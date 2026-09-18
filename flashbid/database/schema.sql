CREATE DATABASE IF NOT EXISTS flashbid_db;
USE flashbid_db;

-- 1. Items Table
CREATE TABLE IF NOT EXISTS items (
    item_id INT PRIMARY KEY AUTO_INCREMENT,
    title VARCHAR(100) NOT NULL,
    item_type VARCHAR(20) NOT NULL, -- 'STANDARD' or 'RESERVE'
    reserve_price DOUBLE NOT NULL,
    current_bid DOUBLE NOT NULL,
    highest_bidder VARCHAR(100) DEFAULT 'None',
    status ENUM('OPEN', 'CLOSED') DEFAULT 'OPEN',
    end_time BIGINT NOT NULL -- Unix epoch timestamp in seconds
);

-- 2. Bid History Table
CREATE TABLE IF NOT EXISTS bid_history (
    bid_id INT PRIMARY KEY AUTO_INCREMENT,
    item_id INT NOT NULL,
    bidder_name VARCHAR(100) NOT NULL,
    bid_amount DOUBLE NOT NULL,
    bid_timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (item_id) REFERENCES items(item_id) ON DELETE CASCADE
);

-- Seed an initial test item: ReserveItem active for 10 minutes (600 seconds)
INSERT INTO items (title, item_type, reserve_price, current_bid, status, end_time) 
VALUES ('Vintage Campus Watch', 'RESERVE', 2000.0, 1500.0, 'OPEN', UNIX_TIMESTAMP() + 600)
ON DUPLICATE KEY UPDATE item_id=item_id;