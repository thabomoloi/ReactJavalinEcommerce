CREATE TABLE users (
  id SERIAL PRIMARY KEY,                     -- Auto-incremented unique identifier
  name VARCHAR(255) NOT NULL,                -- User's name, adjusted size as needed
  role VARCHAR(50) NOT NULL,                 -- User's role, to store enum values
  email VARCHAR(255) UNIQUE NOT NULL,        -- User's email, must be unique
  email_verified TIMESTAMP,                  -- Date and time when the email was verified
  password VARCHAR(255) NOT NULL             -- User's password (hashed)
);