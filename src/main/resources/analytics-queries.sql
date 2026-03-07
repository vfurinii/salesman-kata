-- Analytics Queries for CEO Report

-- Query 1: Which cities are generating the most revenue this month?
SELECT
    country,
    city,
    SUM(amount) as total_revenue,
    COUNT(*) as sales_count,
    AVG(amount) as average_sale_value
FROM processed_sales
WHERE EXTRACT(YEAR FROM sale_date) = EXTRACT(YEAR FROM CURRENT_DATE)
  AND EXTRACT(MONTH FROM sale_date) = EXTRACT(MONTH FROM CURRENT_DATE)
GROUP BY country, city
ORDER BY total_revenue DESC
LIMIT 20;

-- Query 2: Who are our top-performing salespeople in each country?
WITH ranked_salespeople AS (
    SELECT
        country,
        salesperson_id,
        salesperson_name,
        SUM(amount) as total_revenue,
        COUNT(*) as sales_count,
        AVG(amount) as average_sale_value,
        ROW_NUMBER() OVER (PARTITION BY country ORDER BY SUM(amount) DESC) as rank
    FROM processed_sales
    WHERE EXTRACT(YEAR FROM sale_date) = EXTRACT(YEAR FROM CURRENT_DATE)
      AND EXTRACT(MONTH FROM sale_date) = EXTRACT(MONTH FROM CURRENT_DATE)
    GROUP BY country, salesperson_id, salesperson_name
)
SELECT
    country,
    salesperson_id,
    salesperson_name,
    total_revenue,
    sales_count,
    average_sale_value
FROM ranked_salespeople
WHERE rank <= 5
ORDER BY country, total_revenue DESC;

-- Additional Query: Revenue by Product Type
SELECT
    product_type,
    SUM(amount) as total_revenue,
    COUNT(*) as sales_count,
    AVG(amount) as average_price
FROM processed_sales
WHERE EXTRACT(YEAR FROM sale_date) = EXTRACT(YEAR FROM CURRENT_DATE)
  AND EXTRACT(MONTH FROM sale_date) = EXTRACT(MONTH FROM CURRENT_DATE)
GROUP BY product_type
ORDER BY total_revenue DESC;

-- Additional Query: Revenue by Country
SELECT
    country,
    SUM(amount) as total_revenue,
    COUNT(*) as sales_count,
    COUNT(DISTINCT city) as cities_count,
    COUNT(DISTINCT salesperson_id) as salespeople_count
FROM processed_sales
WHERE EXTRACT(YEAR FROM sale_date) = EXTRACT(YEAR FROM CURRENT_DATE)
  AND EXTRACT(MONTH FROM sale_date) = EXTRACT(MONTH FROM CURRENT_DATE)
GROUP BY country
ORDER BY total_revenue DESC;

-- Additional Query: Sales Performance by Warehouse
SELECT
    warehouse,
    country,
    COUNT(*) as sales_count,
    SUM(amount) as total_revenue
FROM processed_sales
WHERE EXTRACT(YEAR FROM sale_date) = EXTRACT(YEAR FROM CURRENT_DATE)
  AND EXTRACT(MONTH FROM sale_date) = EXTRACT(MONTH FROM CURRENT_DATE)
GROUP BY warehouse, country
ORDER BY total_revenue DESC;

-- Data Quality Check: Compare Raw vs Processed Records
SELECT
    'RAW' as layer,
    COUNT(*) as record_count,
    COUNT(DISTINCT sale_id) as unique_sales
FROM raw_sales
WHERE EXTRACT(YEAR FROM ingested_at) = EXTRACT(YEAR FROM CURRENT_DATE)
  AND EXTRACT(MONTH FROM ingested_at) = EXTRACT(MONTH FROM CURRENT_DATE)
UNION ALL
SELECT
    'PROCESSED' as layer,
    COUNT(*) as record_count,
    COUNT(DISTINCT sale_id) as unique_sales
FROM processed_sales
WHERE EXTRACT(YEAR FROM processed_at) = EXTRACT(YEAR FROM CURRENT_DATE)
  AND EXTRACT(MONTH FROM processed_at) = EXTRACT(MONTH FROM CURRENT_DATE);

-- Data Lineage Query: Track record journey
SELECT
    dl.record_id,
    dl.source,
    dl.stage,
    dl.transformation,
    dl.status,
    dl.timestamp,
    dl.error_message
FROM data_lineage dl
WHERE dl.record_id = 'SALE-001'  -- Replace with specific sale_id
ORDER BY dl.timestamp;

