-- Database initialization script

-- Create source sales table (simulates external PostgreSQL source)
CREATE TABLE IF NOT EXISTS source_sales (
    id SERIAL PRIMARY KEY,
    sale_id VARCHAR(100) UNIQUE NOT NULL,
    product_type VARCHAR(50),
    product_name VARCHAR(200),
    amount DECIMAL(12, 2),
    salesperson_id VARCHAR(50),
    salesperson_name VARCHAR(100),
    country VARCHAR(100),
    city VARCHAR(100),
    warehouse VARCHAR(50),
    retailer VARCHAR(100),
    sale_date TIMESTAMP NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Create indexes for source table
CREATE INDEX IF NOT EXISTS idx_source_sales_date ON source_sales(sale_date);
CREATE INDEX IF NOT EXISTS idx_source_sales_country_city ON source_sales(country, city);

-- Insert sample data
INSERT INTO source_sales (sale_id, product_type, product_name, amount, salesperson_id, salesperson_name, country, city, warehouse, retailer, sale_date)
VALUES
    ('SALE-001', 'CELL_PHONE', 'iPhone 15 Pro', 1299.99, 'SP-001', 'John Smith', 'USA', 'New York', 'WH-NY-01', 'Apple Store NYC', '2026-03-01 10:30:00'),
    ('SALE-002', 'COMPUTER', 'MacBook Pro M3', 2499.99, 'SP-002', 'Maria Garcia', 'USA', 'Los Angeles', 'WH-LA-01', 'Best Buy LA', '2026-03-02 14:20:00'),
    ('SALE-003', 'ACCESSORY', 'AirPods Pro', 249.99, 'SP-001', 'John Smith', 'USA', 'New York', 'WH-NY-01', 'Apple Store NYC', '2026-03-02 16:45:00'),
    ('SALE-004', 'CELL_PHONE', 'Samsung Galaxy S24', 999.99, 'SP-003', 'Carlos Silva', 'Brazil', 'São Paulo', 'WH-SP-01', 'Samsung Store SP', '2026-03-03 09:15:00'),
    ('SALE-005', 'COMPUTER', 'Dell XPS 15', 1799.99, 'SP-004', 'Anna Mueller', 'Germany', 'Berlin', 'WH-BE-01', 'MediaMarkt Berlin', '2026-03-03 11:30:00'),
    ('SALE-006', 'CELL_PHONE', 'Google Pixel 8', 699.99, 'SP-005', 'Yuki Tanaka', 'Japan', 'Tokyo', 'WH-TK-01', 'BIC Camera Tokyo', '2026-03-04 13:20:00'),
    ('SALE-007', 'ACCESSORY', 'Logitech Mouse', 79.99, 'SP-002', 'Maria Garcia', 'USA', 'Los Angeles', 'WH-LA-01', 'Best Buy LA', '2026-03-04 15:10:00'),
    ('SALE-008', 'COMPUTER', 'HP Pavilion', 899.99, 'SP-006', 'Pierre Dubois', 'France', 'Paris', 'WH-PR-01', 'Fnac Paris', '2026-03-05 10:00:00'),
    ('SALE-009', 'CELL_PHONE', 'iPhone 15', 999.99, 'SP-001', 'John Smith', 'USA', 'New York', 'WH-NY-01', 'Apple Store NYC', '2026-03-05 12:30:00'),
    ('SALE-010', 'ACCESSORY', 'USB-C Cable', 29.99, 'SP-003', 'Carlos Silva', 'Brazil', 'Rio de Janeiro', 'WH-RJ-01', 'Fast Shop RJ', '2026-03-06 08:45:00')
ON CONFLICT (sale_id) DO NOTHING;

-- Create a view for analytics (optional)
CREATE OR REPLACE VIEW monthly_sales_summary AS
SELECT
    country,
    city,
    salesperson_name,
    COUNT(*) as total_sales,
    SUM(amount) as total_revenue,
    DATE_TRUNC('month', sale_date) as month
FROM source_sales
GROUP BY country, city, salesperson_name, DATE_TRUNC('month', sale_date);

