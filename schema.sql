-- ====================================================================
-- CampusPulse: Campus Issue Management and Detection System
-- Database Setup Script for MySQL 8.x / MySQL Workbench
-- ====================================================================

-- 1. Create Database
DROP DATABASE IF EXISTS campuspulse_db;
CREATE DATABASE campuspulse_db 
CHARACTER SET utf8mb4 
COLLATE utf8mb4_unicode_ci;

USE campuspulse_db;

-- ====================================================================
-- 2. Create Tables
-- ====================================================================

-- Master Table: Users (Students and Administrators)
CREATE TABLE users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    role ENUM('STUDENT', 'ADMIN') NOT NULL DEFAULT 'STUDENT',
    department VARCHAR(50) DEFAULT 'General',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB;

-- Master Table: Categories
CREATE TABLE categories (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE,
    is_critical BOOLEAN DEFAULT FALSE,
    description VARCHAR(255)
) ENGINE=InnoDB;

-- Master Table: Locations
CREATE TABLE locations (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE,
    zone VARCHAR(50) DEFAULT 'Main Campus'
) ENGINE=InnoDB;

-- Core Entity: Aggregated Campus Issues (Issue Groups)
CREATE TABLE issues (
    id INT AUTO_INCREMENT PRIMARY KEY,
    issue_code VARCHAR(20) NOT NULL UNIQUE, -- e.g. CP-001
    title VARCHAR(200) NOT NULL,
    category_id INT NOT NULL,
    location_id INT NOT NULL,
    priority ENUM('LOW', 'MEDIUM', 'HIGH') NOT NULL DEFAULT 'MEDIUM',
    status ENUM('OPEN', 'UNDER_REVIEW', 'IN_PROGRESS', 'RESOLVED', 'REJECTED') NOT NULL DEFAULT 'OPEN',
    report_count INT NOT NULL DEFAULT 1,
    assigned_to VARCHAR(100) DEFAULT 'Campus Facilities / IT Team',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (category_id) REFERENCES categories(id) ON DELETE RESTRICT,
    FOREIGN KEY (location_id) REFERENCES locations(id) ON DELETE RESTRICT
) ENGINE=InnoDB;

-- Core Entity: Individual Student Complaints
CREATE TABLE complaints (
    id INT AUTO_INCREMENT PRIMARY KEY,
    complaint_code VARCHAR(20) NOT NULL UNIQUE, -- e.g. CMP-101
    user_id INT NOT NULL,
    category_id INT NOT NULL,
    location_id INT NOT NULL,
    issue_id INT NULL, -- NULL if independent, or linked to an aggregated issue
    description TEXT NOT NULL,
    is_anonymous BOOLEAN NOT NULL DEFAULT FALSE,
    priority ENUM('LOW', 'MEDIUM', 'HIGH') NOT NULL DEFAULT 'MEDIUM',
    status ENUM('SUBMITTED', 'UNDER_REVIEW', 'IN_PROGRESS', 'RESOLVED', 'REJECTED') NOT NULL DEFAULT 'SUBMITTED',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (category_id) REFERENCES categories(id) ON DELETE RESTRICT,
    FOREIGN KEY (location_id) REFERENCES locations(id) ON DELETE RESTRICT,
    FOREIGN KEY (issue_id) REFERENCES issues(id) ON DELETE SET NULL
) ENGINE=InnoDB;

-- Audit Trail: Complaint Status Change History
CREATE TABLE complaint_status_history (
    id INT AUTO_INCREMENT PRIMARY KEY,
    complaint_id INT NOT NULL,
    changed_by INT NOT NULL,
    old_status VARCHAR(30) NOT NULL,
    new_status VARCHAR(30) NOT NULL,
    remarks TEXT NOT NULL,
    changed_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (complaint_id) REFERENCES complaints(id) ON DELETE CASCADE,
    FOREIGN KEY (changed_by) REFERENCES users(id) ON DELETE CASCADE
) ENGINE=InnoDB;

-- Administrative Logs: Issue Updates and Resolution Remarks
CREATE TABLE issue_updates (
    id INT AUTO_INCREMENT PRIMARY KEY,
    issue_id INT NOT NULL,
    admin_id INT NOT NULL,
    status_update VARCHAR(30) NOT NULL,
    remarks TEXT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (issue_id) REFERENCES issues(id) ON DELETE CASCADE,
    FOREIGN KEY (admin_id) REFERENCES users(id) ON DELETE CASCADE
) ENGINE=InnoDB;

-- ====================================================================
-- 3. Indexes for Optimized JDBC Queries
-- ====================================================================
CREATE INDEX idx_complaints_user ON complaints(user_id);
CREATE INDEX idx_complaints_issue ON complaints(issue_id);
CREATE INDEX idx_complaints_status ON complaints(status);
CREATE INDEX idx_complaints_category ON complaints(category_id);
CREATE INDEX idx_complaints_location ON complaints(location_id);
CREATE INDEX idx_issues_status ON issues(status);
CREATE INDEX idx_issues_priority ON issues(priority);

-- ====================================================================
-- 4. Seed Data: Master Lookups
-- ====================================================================

-- Categories (Includes critical category flags for priority escalation)
INSERT INTO categories (id, name, is_critical, description) VALUES
(1, 'Internet / WiFi', FALSE, 'Campus network, Wi-Fi hotspots, and LAN connectivity'),
(2, 'Electricity', TRUE, 'Power outages, broken switches, laboratory equipment supply'),
(3, 'Water', TRUE, 'Drinking water coolers, washrooms, pipeline leaks'),
(4, 'Classroom', FALSE, 'Benches, whiteboards, projectors, ventilation'),
(5, 'Laboratory', FALSE, 'PC hardware, software licensing, instruments, lab consumables'),
(6, 'Cleanliness', FALSE, 'Garbage disposal, campus hygiene, restroom maintenance'),
(7, 'Transport', FALSE, 'College bus schedules, bus passes, route issues'),
(8, 'Library', FALSE, 'Book availability, reading room silence, e-library portals'),
(9, 'Security', TRUE, 'Gate passes, security personnel, night safety, lost & found'),
(10, 'Hostel', FALSE, 'Hostel mess, room repair, corridor lighting'),
(11, 'Other', FALSE, 'General campus amenities');

-- Locations
INSERT INTO locations (id, name, zone) VALUES
(1, 'Block A', 'Academic Zone'),
(2, 'Block B', 'Academic Zone'),
(3, 'Block C', 'Academic Zone'),
(4, 'Main Building', 'Administrative Zone'),
(5, 'Library', 'Academic Zone'),
(6, 'Computer Lab', 'IT & Computer Science Wing'),
(7, 'Mechanical Lab', 'Workshop Area'),
(8, 'Canteen', 'Student Activity Zone'),
(9, 'Hostel', 'Residential Zone'),
(10, 'Parking', 'Campus Gate & Exterior');

-- ====================================================================
-- 5. Seed Users (Passwords hashed with SHA-256)
-- Password for all seed users: "Password@123"
-- SHA-256 hash: "ef92b778bafe771e89245b89ecbc08a44a4e166c06659911881f383d4473e94f"
-- ====================================================================

INSERT INTO users (id, name, email, password_hash, role, department) VALUES
(1, 'Prof. Sharma (Admin)', 'admin@college.edu', 'ef92b778bafe771e89245b89ecbc08a44a4e166c06659911881f383d4473e94f', 'ADMIN', 'Administration'),
(2, 'Rahul Verma', 'rahul@college.edu', 'ef92b778bafe771e89245b89ecbc08a44a4e166c06659911881f383d4473e94f', 'STUDENT', 'Computer Science'),
(3, 'Priya Nair', 'priya@college.edu', 'ef92b778bafe771e89245b89ecbc08a44a4e166c06659911881f383d4473e94f', 'STUDENT', 'Information Technology'),
(4, 'Amit Patel', 'amit@college.edu', 'ef92b778bafe771e89245b89ecbc08a44a4e166c06659911881f383d4473e94f', 'STUDENT', 'Electronics'),
(5, 'Sneha Roy', 'sneha@college.edu', 'ef92b778bafe771e89245b89ecbc08a44a4e166c06659911881f383d4473e94f', 'STUDENT', 'Mechanical Engineering'),
(6, 'Rohit Kulkarni', 'rohit@college.edu', 'ef92b778bafe771e89245b89ecbc08a44a4e166c06659911881f383d4473e94f', 'STUDENT', 'Civil Engineering');

-- ====================================================================
-- 6. Seed Data: The 5-Student Real-World Scenario
-- Five students report WiFi issues in Block A.
-- System groups them into Issue CP-001 with High priority and Under Review status.
-- ====================================================================

INSERT INTO issues (id, issue_code, title, category_id, location_id, priority, status, report_count, assigned_to, created_at) VALUES
(1, 'CP-001', 'Block A WiFi Connectivity Problem', 1, 1, 'HIGH', 'UNDER_REVIEW', 5, 'Campus IT Network Team', DATE_SUB(NOW(), INTERVAL 1 DAY));

INSERT INTO complaints (id, complaint_code, user_id, category_id, location_id, issue_id, description, is_anonymous, priority, status, created_at) VALUES
(1, 'CMP-101', 2, 1, 1, 1, 'WiFi is not working in Block A.', FALSE, 'MEDIUM', 'UNDER_REVIEW', DATE_SUB(NOW(), INTERVAL 20 HOUR)),
(2, 'CMP-102', 3, 1, 1, 1, 'Internet connection is unavailable in Block A.', FALSE, 'MEDIUM', 'UNDER_REVIEW', DATE_SUB(NOW(), INTERVAL 16 HOUR)),
(3, 'CMP-103', 4, 1, 1, 1, 'Block A WiFi keeps disconnecting continuously.', TRUE, 'MEDIUM', 'UNDER_REVIEW', DATE_SUB(NOW(), INTERVAL 12 HOUR)),
(4, 'CMP-104', 5, 1, 1, 1, 'Unable to connect to campus WiFi in Block A lecture halls.', FALSE, 'MEDIUM', 'UNDER_REVIEW', DATE_SUB(NOW(), INTERVAL 8 HOUR)),
(5, 'CMP-105', 6, 1, 1, 1, 'Very poor internet connection in Block A 2nd floor.', TRUE, 'HIGH', 'UNDER_REVIEW', DATE_SUB(NOW(), INTERVAL 4 HOUR));

-- Status History for Complaint 1
INSERT INTO complaint_status_history (complaint_id, changed_by, old_status, new_status, remarks, changed_at) VALUES
(1, 1, 'SUBMITTED', 'UNDER_REVIEW', 'Associated with Issue CP-001 (Block A WiFi Connectivity Problem). Assigned to Network Admin.', DATE_SUB(NOW(), INTERVAL 15 HOUR));

-- Issue Update Log
INSERT INTO issue_updates (issue_id, admin_id, status_update, remarks, created_at) VALUES
(1, 1, 'UNDER_REVIEW', 'Aggregated 5 reports from students. Network administrator dispatched to check 2nd floor access point.', DATE_SUB(NOW(), INTERVAL 15 HOUR));

-- Another independent complaint for comparison
INSERT INTO complaints (id, complaint_code, user_id, category_id, location_id, issue_id, description, is_anonymous, priority, status, created_at) VALUES
(6, 'CMP-106', 2, 3, 5, NULL, 'Drinking water cooler on Library 1st floor is leaking water on the floor.', FALSE, 'MEDIUM', 'SUBMITTED', DATE_SUB(NOW(), INTERVAL 2 HOUR));

-- Verification query
SELECT 'Database setup complete. Total Users:' AS status, COUNT(*) AS count FROM users
UNION ALL
SELECT 'Total Categories:', COUNT(*) FROM categories
UNION ALL
SELECT 'Total Locations:', COUNT(*) FROM locations
UNION ALL
SELECT 'Total Issues:', COUNT(*) FROM issues
UNION ALL
SELECT 'Total Complaints:', COUNT(*) FROM complaints;
