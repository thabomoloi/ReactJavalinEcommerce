# ReactJavalinEcommerce

## PostgresSQL Setup

```bash
-- Open PostgreSQL command line
psql
```

```sql
-- Create the database
CREATE DATABASE oasisnourish;

-- Create the user
CREATE USER oasis WITH PASSWORD 'OasisNourish123';

-- Grant privileges to the user
GRANT ALL PRIVILEGES ON DATABASE oasisnourish TO oasis;

# Exit psql
\q
```

```bash
# Connect to the database with the new user
psql -h localhost -U oasis -d oasisnourish
```

