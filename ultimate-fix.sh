#!/bin/bash

echo "═══════════════════════════════════════════════════════════"
echo "  ULTIMATE FIX - PostgreSQL Connection Issue"
echo "═══════════════════════════════════════════════════════════"
echo ""

cd /Users/vitorfurini/IdeaProjects/salesman-kata-v3

echo "Step 1: Stopping all containers..."
docker-compose down -v
sleep 2

echo "Step 2: Starting PostgreSQL..."
docker-compose up -d postgres
echo "Waiting 45 seconds for PostgreSQL to fully initialize..."
sleep 45

echo "Step 3: Modifying pg_hba.conf for host access..."
docker exec salesman-postgres sh -c "echo 'host all all 0.0.0.0/0 trust' >> /var/lib/postgresql/data/pgdata/pg_hba.conf"
docker exec salesman-postgres psql -U postgres -c "SELECT pg_reload_conf();"

echo "Step 4: Creating salesdb database..."
docker exec salesman-postgres psql -U postgres -c "CREATE DATABASE salesdb;"

echo "Step 5: Running init script..."
cat src/main/resources/init-source-db.sql | docker exec -i salesman-postgres psql -U postgres -d salesdb

echo "Step 6: Verifying setup..."
COUNT=$(docker exec salesman-postgres psql -U postgres -d salesdb -t -c "SELECT COUNT(*) FROM source_sales;" 2>/dev/null | tr -d ' ')
echo "✓ Database has $COUNT records"

echo ""
echo "Step 7: Starting all services..."
docker-compose up -d

echo ""
echo "Step 8: Building application..."
mvn clean package -DskipTests -q

echo ""
echo "═══════════════════════════════════════════════════════════"
echo "  Setup Complete! Now run:"
echo "  mvn spring-boot:run"
echo "═══════════════════════════════════════════════════════════"

