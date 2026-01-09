-- PostgreSQL Database Setup Script for Student API

-- Connect to PostgreSQL and create the database
-- This should be run in psql or a PostgreSQL client

-- Create the database (this command is run from psql as superuser)
CREATE DATABASE studentdb;

-- Connect to the database (in psql you would use \c studentdb)
-- For this example, we'll assume we're already connected to the studentdb database

-- Create the students table
CREATE TABLE IF NOT EXISTS students (
    id SERIAL PRIMARY KEY,
    first_name VARCHAR(255) NOT NULL,
    last_name VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE
);

-- Insert some sample data (optional)
INSERT INTO students (first_name, last_name, email) VALUES
('John', 'Doe', 'john.doe@example.com'),
('Jane', 'Smith', 'jane.smith@example.com');

-- Verify the table was created
\d students;

-- Verify sample data
SELECT * FROM students;