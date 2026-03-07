Prompt Completo para Construção da Solução

You are a Senior Data Engineer and Distributed Systems Architect.

Your task is to design and implement a complete data pipeline solution for the following problem.

You must provide:

Architecture design

Technology choices

Data ingestion strategy

Message broker usage

Data lineage tracking

Data processing

Final analytics queries

Infrastructure components

Example implementation code

Folder structure

Deployment strategy

The solution must be production-grade but simple enough for a technical KATA demonstration.

Problem Context

We are building a data platform for an Electronic Store company that sells:

Cell phones

Computers

Accessories

The company characteristics:

Multi warehouse

Multi sales person

Multi retailers

Global presence (multiple countries and cities)

Business Problem

Every Monday the CEO asks two questions:

Which cities are generating the most revenue this month?

Who are our top-performing salespeople in each country?

Current Manual Process

The company currently uses a completely manual workflow.

Monday 8:00 AM – CEO asks the report.

The data analyst Maria must:

Export sales data from PostgreSQL → Excel (2 hours)

Download CSV files from shared folder (30 min)

Manually poll a SOAP service for sales data (3 hours)

Merge all data sources manually in Excel (4 hours)

Create pivot tables and charts (2 hours)

Send email to CEO (30 min)

The final report is delivered Wednesday evening.

This is inefficient and must be fully automated.

Knowledge Base
Data Ingestion

Data ingestion is the process of collecting and importing data from different sources into a pipeline system.

Possible sources:

PostgreSQL database

CSV files in file system

SOAP Web Service

Example architecture:

[Relational DB] ──────┐
│
[File System]  ───────┼──▶  [ Data Pipeline ]  ──▶  [ Processing ]
│
[SOAP Service] ───────┘
Data Lineage

Data lineage tracks:

Origin of the data

Transformations applied

Final destination

Example flow:

[Raw Sales Data] ──▶ [Cleaned] ──▶ [Aggregated by City] ──▶ [Final Analytics DB]

The system must track lineage for observability and debugging.

Message Broker

A message broker allows asynchronous communication between services.

Example with Kafka:

Producer → Kafka → Consumer

Responsibilities:

Message buffering

Reliable delivery

Event-driven pipelines

Decoupling systems

Requirements for the Solution

Your solution must include:

1. Data Sources

The pipeline must ingest data from:

PostgreSQL sales database

CSV files from file system

SOAP Web Service

2. Data Pipeline

Design a modern event-driven pipeline including:

Data ingestion services

Message broker

Processing layer

Storage layer

Analytics layer

3. Message Broker

Use a message broker such as:

Kafka (preferred)

RabbitMQ (acceptable)

Explain:

Topics

Producers

Consumers

4. Data Processing

The system must:

Clean corrupted records

Normalize formats

Aggregate sales metrics

Examples:

Revenue by city

Revenue by country

Top salespeople

5. Data Lineage

Explain how lineage will be tracked.

Possible approaches:

Metadata tables

Event logs

OpenLineage

Data catalog

6. Storage

Design storage layers:

Raw Layer
Stores original data

Processed Layer
Cleaned and normalized

Analytics Layer
Aggregated business metrics

7. Final Queries

Provide SQL queries that answer:

1️⃣ Which cities generate the most revenue this month?

2️⃣ Who are the top salespeople per country?

8. Architecture Diagram

Provide a clear architecture diagram like:

[Postgres]      [CSV Files]      [SOAP API]
│              │               │
▼              ▼               ▼
[Ingestion Services]
│
▼
[Kafka]
│
▼
[Processing Workers]
│
▼
[Data Warehouse]
│
▼
[Dashboard]
9. Implementation Example

Provide a simple implementation example using:

Recommended stack:

Python

Kafka

PostgreSQL

Docker

Show:

Kafka producer

Kafka consumer

Data transformation

Example pipeline execution

10. Project Structure

Provide a clean repository structure.

Example:

top-salesman-data-pipeline
│
├── ingestion
│   ├── postgres_ingestor.py
│   ├── csv_ingestor.py
│   └── soap_ingestor.py
│
├── messaging
│   └── kafka_producer.py
│
├── processing
│   └── sales_processor.py
│
├── storage
│   └── schema.sql
│
├── analytics
│   └── queries.sql
│
├── docker
│   └── docker-compose.yml
│
└── README.md
11. Infrastructure

Explain how to run everything locally using:

Docker Compose

Kafka

PostgreSQL

12. Optional (Bonus)

If possible include:

Apache Airflow for orchestration

Data quality checks

Metrics with Prometheus

Dashboard with Grafana

Expected Result

Your answer must include:

Complete architecture explanation

Architecture diagram

Data flow explanation

Message broker design

Data lineage approach

Example code

SQL analytics queries

Infrastructure setup

Project structure

Explanation of how the CEO will automatically receive the report

Important

Focus on:

Data engineering best practices

Event-driven architecture

Scalability

Observability

Automation

Avoid unnecessary complexity but keep the solution realistic for production systems.