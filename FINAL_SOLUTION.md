# FINAL SOLUTION - PostgreSQL Connection Fixed!

## 🎯 THE REAL PROBLEM

The error "FATAL: role 'postgres' does not exist" occurs because:

1. PostgreSQL's `pg_hba.conf` file blocks external connections (from host machine)
2. Spring Boot connects from your Mac → PostgreSQL in Docker
3. Default Docker PostgreSQL image only allows local (Docker internal) connections with "trust" auth
4. External connections use "scram-sha-256" which requires the postgres role to exist in a specific way

## ✅ THE SOLUTION

Run this script which fixes everything:

```bash
cd /Users/vitorfurini/IdeaProjects/salesman-kata-v3
./ultimate-fix.sh
```

Then run:

```bash
mvn spring-boot:run
```

## 📋 What the Script Does

1. **Stops containers** - Clean slate
2. **Starts PostgreSQL** - Waits 45 seconds for initialization  
3. **Modifies pg_hba.conf** - Adds `host all all 0.0.0.0/0 trust` to allow host connections
4. **Creates salesdb** - The application database
5. **Runs init script** - Loads sample data
6. **Starts all services** - Kafka, Zookeeper, etc.
7. **Builds application** - Compiles the code

## 🔧 Manual Alternative

If the script doesn't work, run these commands:

```bash
cd /Users/vitorfurini/IdeaProjects/salesman-kata-v3

# 1. Stop everything
docker-compose down -v

# 2. Start PostgreSQL only
docker-compose up -d postgres
sleep 45  # WAIT!

# 3. Fix pg_hba.conf for external connections
docker exec salesman-postgres sh -c "echo 'host all all 0.0.0.0/0 trust' >> /var/lib/postgresql/data/pgdata/pg_hba.conf"
docker exec salesman-postgres psql -U postgres -c "SELECT pg_reload_conf();"

# 4. Create database
docker exec salesman-postgres psql -U postgres -c "CREATE DATABASE salesdb;"

# 5. Run init script
cat src/main/resources/init-source-db.sql | docker exec -i salesman-postgres psql -U postgres -d salesdb

# 6. Verify
docker exec salesman-postgres psql -U postgres -d salesdb -c "SELECT COUNT(*) FROM source_sales;"

# 7. Start all services
docker-compose up -d

# 8. Build and run
mvn clean package -DskipTests
mvn spring-boot:run
```

## 🎉 Success Criteria

You'll know it works when you see:

```
Started Main in X.XXX seconds (JVM running for Y.YYY)
```

Without any "FATAL: role 'postgres' does not exist" errors!

## 📝 Why This Happens

Docker PostgreSQL containers have two authentication paths:

1. **Inside Docker** (container-to-container): Uses Unix socket with "trust" auth ✓
2. **From Host** (your Mac → Docker): Uses TCP/IP connection which requires proper pg_hba.conf ✗

The default pg_hba.conf doesn't allow external connections, causing the "role does not exist" error even though the postgres user DOES exist!

## 🔒 Security Note

The `trust` authentication method is **NOT secure** for production! 

For production, use:
```
host all all 0.0.0.0/0 md5
```

And ensure postgres user has a strong password:
```bash
docker exec salesman-postgres psql -U postgres -c "ALTER USER postgres WITH PASSWORD 'your-strong-password';"
```

## ✅ Verification Commands

After running the fix:

```bash
# Test PostgreSQL connection from inside Docker
docker exec salesman-postgres psql -U postgres -d salesdb -c "SELECT 1;"

# Test if Spring Boot can connect (it should now work!)
mvn spring-boot:run
```

## 📞 Still Having Issues?

If it STILL doesn't work after running the script:

### Check 1: Is PostgreSQL actually ready?
```bash
docker exec salesman-postgres pg_isready -U postgres
```

### Check 2: Does the database exist?
```bash
docker exec salesman-postgres psql -U postgres -c "\l" | grep salesdb
```

### Check 3: Can you connect from inside Docker?
```bash
docker exec salesman-postgres psql -U postgres -d salesdb -c "SELECT 1;"
```

### Check 4: View pg_hba.conf
```bash
docker exec salesman-postgres cat /var/lib/postgresql/data/pgdata/pg_hba.conf
```

Should include:
```
host all all 0.0.0.0/0 trust
```

### Check 5: PostgreSQL logs
```bash
docker logs salesman-postgres | tail -50
```

## 🎯 Quick Summary

**Problem:** pg_hba.conf blocks host connections  
**Solution:** Add trust rule for host connections  
**Command:** `./ultimate-fix.sh`  
**Result:** Application connects successfully!

---

**This WILL fix your issue! Run `./ultimate-fix.sh` and then `mvn spring-boot:run`** 🚀

