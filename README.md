# ReactJavalinEcommerce

## Database
### PostgresSQL Setup

```bash
# Open PostgreSQL command line
psql
```

```sql
-- Create the database
CREATE DATABASE oasisnourish;

-- Create the user
CREATE USER oasis WITH PASSWORD 'OasisNourish123';

-- Grant privileges to the user
GRANT ALL PRIVILEGES ON DATABASE oasisnourish TO oasis;


-- Change the database owner
ALTER DATABASE oasisnourish OWNER TO oasis;

-- Change the public schema owner
ALTER SCHEMA public OWNER TO oasis;

-- Grant all privileges on the public schema
GRANT ALL PRIVILEGES ON SCHEMA public TO oasis;

# Exit psql
\q
```

```bash
# Connect to the database with the new user
psql -h localhost -U oasis -d oasisnourish
```

### Tables
```bash
# Create tables
psql -h localhost -U oasis -d oasisnourish -f data/create_tables.sql

# Drop all tables
psql -h localhost -U oasis -d oasisnourish -f data/drop_tables.sql
```