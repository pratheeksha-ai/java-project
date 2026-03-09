CREATE DATABASE IF NOT EXISTS library_db;
USE library_db;

-- Drop existing tables if they exist (to ensure clean schema)
DROP TABLE IF EXISTS transactions;
DROP TABLE IF EXISTS members;
DROP TABLE IF EXISTS books;

CREATE TABLE IF NOT EXISTS books (
    id INT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(255),
    author VARCHAR(255),
    isbn VARCHAR(255) UNIQUE NOT NULL,
    is_available BOOLEAN DEFAULT TRUE
);

CREATE TABLE IF NOT EXISTS members (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255),
    member_id VARCHAR(255) UNIQUE NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL
);

CREATE TABLE IF NOT EXISTS transactions (
    id INT AUTO_INCREMENT PRIMARY KEY,
    isbn VARCHAR(255) NOT NULL,
    member_id VARCHAR(255) NOT NULL,
    borrow_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    return_date TIMESTAMP NULL,
    FOREIGN KEY (isbn) REFERENCES books(isbn),
    FOREIGN KEY (member_id) REFERENCES members(member_id)
);
