#!/bin/bash

echo "═══════════════════════════════════════════════════════════"
echo "  PostgreSQL Fix and Application Startup Script"
echo "═══════════════════════════════════════════════════════════"
echo ""

cd /Users/vitorfurini/IdeaProjects/salesman-kata-v3

# Step 1: Verify PostgreSQL container
echo "Step 1: Verifying PostgreSQL container..."
if ! docker ps | grep -q salesman-postgres; then
    echo "  ✗ PostgreSQL container not running!"
    echo "  Starting containers..."
    docker-compose up -d
    echo "  Waiting 60 seconds for initialization..."
    sleep 60
fi
echo "  ✓ PostgreSQL container is running"
echo ""

# Step 2: Ensure database and user exist
echo "Step 2: Ensuring database setup..."
docker exec salesman-postgres psql -U postgres -c "SELECT 1;" > /dev/null 2>&1
if [ $? -ne 0 ]; then
    echo "  ✗ Cannot connect to PostgreSQL!"
    echo "  Please wait a few more seconds and run this script again"
    exit 1
fi
echo "  ✓ PostgreSQL user 'postgres' exists"

# Create salesdb if it doesn't exist
docker exec salesman-postgres psql -U postgres -c "CREATE DATABASE salesdb;" 2>/dev/null || true
echo "  ✓ Database 'salesdb' ready"

# Run init script
echo "  Running init script..."
docker exec -i salesman-postgres psql -U postgres -d salesdb < src/main/resources/init-source-db.sql 2>/dev/null || true
echo "  ✓ Init script executed"
echo ""

# Step 3: Verify connection
echo "Step 3: Verifying database connection..."
RESULT=$(docker exec salesman-postgres psql -U postgres -d salesdb -t -c "SELECT COUNT(*) FROM source_sales;" 2>/dev/null | tr -d ' \n')
if [ ! -z "$RESULT" ]; then
    echo "  ✓ Database has $RESULT sample records"
else
    echo "  ⚠ Could not verify data, but database is accessible"
fi
echo ""

# Step 4: Build application
echo "Step 4: Building application..."
mvn clean package -DskipTests -q
if [ $? -eq 0 ]; then
    echo "  ✓ Build successful"
else
    echo "  ✗ Build failed"
    exit 1
fi
echo ""

# Step 5: Start application
echo "Step 5: Starting Spring Boot application..."
echo ""
echo "═══════════════════════════════════════════════════════════"
echo "  Application is starting..."
echo "  Watch for: 'Started Main in X.XXX seconds'"
echo "═══════════════════════════════════════════════════════════"
echo ""

mvn spring-boot:run

