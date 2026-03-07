# Quick Start Guide

## 🚀 Get Running in 5 Minutes

### Step 1: Start Infrastructure (2 minutes)

```bash
# Clone or navigate to project
cd salesman-kata-v3

# Start PostgreSQL and Kafka
docker-compose up -d

# Wait for services to be healthy
docker-compose ps
```

You should see all services with status "Up":
- salesman-postgres
- salesman-kafka
- salesman-zookeeper
- salesman-kafka-ui
- salesman-pgadmin

### Step 2: Build Application (1 minute)

```bash
# Build with Maven
mvn clean package -DskipTests
```

### Step 3: Run Application (1 minute)

```bash
# Start Spring Boot application
mvn spring-boot:run
```

Wait for log message:
```
Started Main in X.XXX seconds
```

### Step 4: Test the Pipeline (1 minute)

**Trigger data ingestion:**
```bash
curl -X POST http://localhost:8080/api/ingestion/trigger
```

**Get top cities report:**
```bash
curl http://localhost:8080/api/report/cities | jq
```

**Get top salespeople report:**
```bash
curl http://localhost:8080/api/report/salespeople | jq
```

**Get full report:**
```bash
curl http://localhost:8080/api/report/full | jq
```

---

## 📊 Access UIs

### Kafka UI (Monitor Messages)
- URL: http://localhost:8090
- View topics: raw-sales, processed-sales, data-lineage
- Monitor consumer groups
- See message payloads

### pgAdmin (Database Management)
- URL: http://localhost:5050
- Email: `admin@admin.com`
- Password: `admin`

**Add PostgreSQL Server:**
1. Right-click "Servers" → "Register" → "Server"
2. Name: `SalesDB`
3. Connection tab:
   - Host: `postgres` (or `localhost` if not in Docker network)
   - Port: `5432`
   - Database: `salesdb`
   - Username: `postgres`
   - Password: `postgres`
4. Save

---

## 📁 Sample Data

### PostgreSQL (Pre-loaded)
- Table: `source_sales`
- Records: 10 sample sales
- Location: See `src/main/resources/init-source-db.sql`

### CSV Files
- Directory: `./data/csv/`
- File: `sales_march_2026.csv`
- Records: 12 sample sales

### Add Your Own CSV
1. Create file in `./data/csv/` directory
2. Use this format:

```csv
sale_id,product_type,product_name,amount,salesperson_id,salesperson_name,country,city,warehouse,retailer,sale_date
SALE-001,CELL_PHONE,iPhone 15,999.99,SP-001,John Doe,USA,Boston,WH-01,Best Buy,2026-03-06 10:00:00
```

3. Trigger ingestion:
```bash
curl -X POST http://localhost:8080/api/ingestion/trigger
```

---

## 🧪 Testing the Flow

### Test 1: End-to-End Pipeline

```bash
# 1. Add a new CSV file
cat > data/csv/test_sales.csv << EOF
sale_id,product_type,product_name,amount,salesperson_id,salesperson_name,country,city,warehouse,retailer,sale_date
TEST-001,COMPUTER,Test Laptop,1999.99,SP-TEST,Test Person,USA,Seattle,WH-SE,Test Store,2026-03-06 12:00:00
EOF

# 2. Trigger ingestion
curl -X POST http://localhost:8080/api/ingestion/trigger

# 3. Check Kafka UI
# Open http://localhost:8090 → Topics → raw-sales
# You should see the new message

# 4. Check processed data
curl http://localhost:8080/api/report/cities | jq '.[] | select(.city == "SEATTLE")'
```

### Test 2: Data Lineage

```bash
# Access database
docker exec -it salesman-postgres psql -U postgres -d salesdb

# Query lineage for a specific sale
SELECT * FROM data_lineage WHERE record_id = 'SALE-001' ORDER BY timestamp;

# Exit
\q
```

### Test 3: Scheduled Job

The application runs ingestion every Monday at 6:00 AM.

To test scheduling:
1. Edit `DataIngestionOrchestrator.java`
2. Change cron: `@Scheduled(cron = "0 * * * * *")` (every minute)
3. Restart application
4. Watch logs for scheduled execution

---

## 🔧 Common Commands

### Docker Management

```bash
# View logs
docker-compose logs -f kafka
docker-compose logs -f postgres

# Restart services
docker-compose restart

# Stop services
docker-compose down

# Stop and remove data
docker-compose down -v

# Check resource usage
docker stats
```

### Application Management

```bash
# Run in dev mode (hot reload)
mvn spring-boot:run

# Run with specific profile
mvn spring-boot:run -Dspring-boot.run.profiles=dev

# Check application logs
tail -f logs/application.log

# Stop application
Ctrl+C or kill PID
```

### Database Queries

```bash
# Connect to PostgreSQL
docker exec -it salesman-postgres psql -U postgres -d salesdb

# Useful queries
SELECT COUNT(*) FROM raw_sales;
SELECT COUNT(*) FROM processed_sales;
SELECT source, COUNT(*) FROM raw_sales GROUP BY source;
SELECT stage, status, COUNT(*) FROM data_lineage GROUP BY stage, status;

# Exit
\q
```

---

## 📈 Verify Everything Works

**Checklist:**

- [ ] Docker containers are running
- [ ] Application starts without errors
- [ ] Can trigger ingestion via API
- [ ] Kafka messages appear in Kafka UI
- [ ] Data appears in PostgreSQL tables
- [ ] API returns reports with data
- [ ] Lineage records are created

**Run this verification script:**

```bash
#!/bin/bash
echo "=== Verification Script ==="

echo "1. Checking Docker services..."
docker-compose ps | grep "Up"

echo "2. Checking application health..."
curl -s http://localhost:8080/actuator/health 2>/dev/null || echo "App not running"

echo "3. Triggering ingestion..."
curl -s -X POST http://localhost:8080/api/ingestion/trigger

echo "4. Waiting 5 seconds for processing..."
sleep 5

echo "5. Checking data..."
CITIES=$(curl -s http://localhost:8080/api/report/cities | jq 'length')
echo "Number of cities with sales: $CITIES"

echo "6. Checking Kafka topics..."
docker exec salesman-kafka kafka-topics --bootstrap-server localhost:9092 --list

echo "=== Verification Complete ==="
```

---

## 🐛 Troubleshooting

### Problem: Port already in use

```bash
# Find process using port
lsof -i :8080  # Application
lsof -i :5432  # PostgreSQL
lsof -i :9092  # Kafka

# Kill process
kill -9 <PID>
```

### Problem: Kafka won't start

```bash
# Remove Kafka data and restart
docker-compose down -v
docker-compose up -d kafka
```

### Problem: Database connection failed

```bash
# Check PostgreSQL is running
docker-compose ps postgres

# Check connection
docker exec salesman-postgres pg_isready -U postgres

# View logs
docker-compose logs postgres
```

### Problem: No data in reports

```bash
# 1. Check if data was ingested
docker exec -it salesman-postgres psql -U postgres -d salesdb -c "SELECT COUNT(*) FROM raw_sales;"

# 2. Check Kafka messages
# Open http://localhost:8090 and verify messages in topics

# 3. Check application logs for errors
docker-compose logs app  # If running in Docker
# or
# Check console output if running with mvn spring-boot:run
```

---

## 🎯 Next Steps

1. **Explore the code:**
   - Start with `Main.java`
   - Check `DataIngestionOrchestrator.java` for scheduling
   - Review `KafkaProducer.java` and `KafkaConsumer.java`

2. **Modify the pipeline:**
   - Add a new data source
   - Create a new analytics endpoint
   - Implement data quality checks

3. **Deploy to production:**
   - Review `ARCHITECTURE.md` for deployment strategies
   - Set up monitoring and alerting
   - Configure production database

4. **Scale the system:**
   - Add more Kafka partitions
   - Increase consumer instances
   - Implement caching

---

**Enjoy your automated data pipeline! 🚀**

