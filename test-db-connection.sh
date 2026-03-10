#!/bin/bash

echo "Testing PostgreSQL Connection..."
echo "================================"
echo ""

# Test 1: Container is running
echo "1. Checking if container is running..."
if docker ps | grep -q salesman-postgres; then
    echo "   ✓ Container is running"
else
    echo "   ✗ Container is NOT running"
    echo "   Run: docker-compose up -d"
    exit 1
fi

# Test 2: pg_isready
echo "2. Checking PostgreSQL readiness..."
if docker exec salesman-postgres pg_isready -U postgres > /dev/null 2>&1; then
    echo "   ✓ PostgreSQL is ready"
else
    echo "   ✗ PostgreSQL is NOT ready"
    echo "   Wait a few seconds and try again"
    exit 1
fi

# Test 3: Connect to postgres database
echo "3. Testing connection to 'postgres' database..."
if docker exec salesman-postgres psql -U postgres -d postgres -c "SELECT 1;" > /dev/null 2>&1; then
    echo "   ✓ Can connect to postgres database"
else
    echo "   ✗ Cannot connect to postgres database"
    exit 1
fi

# Test 4: Check if salesdb exists
echo "4. Checking if 'salesdb' database exists..."
DB_EXISTS=$(docker exec salesman-postgres psql -U postgres -t -c "SELECT 1 FROM pg_database WHERE datname='salesdb';" 2>/dev/null | tr -d ' \n')
if [ "$DB_EXISTS" = "1" ]; then
    echo "   ✓ Database 'salesdb' exists"
else
    echo "   ✗ Database 'salesdb' does NOT exist"
    echo "   Creating database..."
    docker exec salesman-postgres psql -U postgres -c "CREATE DATABASE salesdb;" 2>&1
    if [ $? -eq 0 ]; then
        echo "   ✓ Database created successfully"
    else
        echo "   ✗ Failed to create database"
        exit 1
    fi
fi

# Test 5: Connect to salesdb
echo "5. Testing connection to 'salesdb'..."
if docker exec salesman-postgres psql -U postgres -d salesdb -c "SELECT 1;" > /dev/null 2>&1; then
    echo "   ✓ Can connect to salesdb"
else
    echo "   ✗ Cannot connect to salesdb"
    exit 1
fi

# Test 6: Check for source_sales table
echo "6. Checking for source_sales table..."
TABLE_EXISTS=$(docker exec salesman-postgres psql -U postgres -d salesdb -t -c "SELECT 1 FROM information_schema.tables WHERE table_name='source_sales';" 2>/dev/null | tr -d ' \n')
if [ "$TABLE_EXISTS" = "1" ]; then
    COUNT=$(docker exec salesman-postgres psql -U postgres -d salesdb -t -c "SELECT COUNT(*) FROM source_sales;" 2>/dev/null | tr -d ' \n')
    echo "   ✓ Table 'source_sales' exists with $COUNT records"
else
    echo "   ⚠ Table 'source_sales' does NOT exist"
    echo "   Running init script..."
    docker exec -i salesman-postgres psql -U postgres -d salesdb < src/main/resources/init-source-db.sql
    if [ $? -eq 0 ]; then
        echo "   ✓ Init script executed successfully"
    else
        echo "   ✗ Failed to run init script"
    fi
fi

echo ""
echo "================================"
echo "All checks passed! ✓"
echo "================================"
echo ""
echo "PostgreSQL is ready for Spring Boot connection!"
echo ""
echo "Connection details:"
echo "  URL: jdbc:postgresql://localhost:5432/salesdb"
echo "  User: postgres"
echo "  Password: postgres"
echo ""
echo "You can now run: mvn spring-boot:run"

