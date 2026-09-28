-- ============================================================
-- EventEase Database Setup Script
-- College Event Registration System
-- ============================================================

CREATE DATABASE IF NOT EXISTS eventease_db;
USE eventease_db;

-- 1. Students Table
CREATE TABLE IF NOT EXISTS students (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    department VARCHAR(255) NOT NULL,
    year INT NOT NULL,
    password VARCHAR(255) NOT NULL
);

-- 2. Organizers Table
CREATE TABLE IF NOT EXISTS organizers (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    department VARCHAR(255) NOT NULL,
    password VARCHAR(255) NOT NULL
);

-- 3. Events Table
CREATE TABLE IF NOT EXISTS events (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    description TEXT NOT NULL,
    category VARCHAR(255) NOT NULL,
    event_date DATE NOT NULL,
    venue VARCHAR(255) NOT NULL,
    max_seats INT NOT NULL,
    organizer_id BIGINT NOT NULL,
    FOREIGN KEY (organizer_id) REFERENCES organizers(id) ON DELETE CASCADE
);

-- 4. Registrations Table
CREATE TABLE IF NOT EXISTS registrations (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    registration_date DATETIME NOT NULL,
    status VARCHAR(50) NOT NULL,
    student_id BIGINT NOT NULL,
    event_id BIGINT NOT NULL,
    FOREIGN KEY (student_id) REFERENCES students(id) ON DELETE CASCADE,
    FOREIGN KEY (event_id) REFERENCES events(id) ON DELETE CASCADE
);

-- ============================================================
-- SAMPLE DATA FOR DEMONSTRATION & TESTING
-- ============================================================

-- Insert Organizers
INSERT INTO organizers (id, name, email, department, password) VALUES
(1, 'Computer Science Club', 'cse@example.com', 'Computer Science', 'org123'),
(2, 'ECE Association', 'ece@example.com', 'Electronics & Communication', 'org123'),
(3, 'Cultural Club', 'cultural@example.com', 'Student Affairs', 'org123')
ON DUPLICATE KEY UPDATE name=VALUES(name);

-- Insert Students
INSERT INTO students (id, name, email, department, year, password) VALUES
(1, 'Arun', 'arun@example.com', 'Computer Science', 3, 'student123'),
(2, 'Priya', 'priya@example.com', 'Information Technology', 2, 'student123'),
(3, 'Karthik', 'karthik@example.com', 'Electrical Engineering', 4, 'student123'),
(4, 'Meena', 'meena@example.com', 'Mechanical Engineering', 1, 'student123')
ON DUPLICATE KEY UPDATE name=VALUES(name);

-- Insert Events (Hackathon maxSeats = 3 for easy capacity testing)
INSERT INTO events (id, title, description, category, event_date, venue, max_seats, organizer_id) VALUES
(1, 'Hackathon', '24-hour coding competition to build innovative projects.', 'Technical', '2026-10-15', 'Main Auditorium', 3, 1),
(2, 'Technical Symposium', 'National level paper presentation and technical workshops.', 'Technical', '2026-11-20', 'Science Block Hall', 50, 1),
(3, 'Robotics Workshop', 'Hands-on training session on microcontrollers, sensor integration, and Arduino.', 'Robotics', '2026-12-05', 'ECE Lab 2', 30, 2),
(4, 'Cultural Fest', 'Annual inter-college cultural festival featuring music, dance, and drama.', 'Cultural', '2026-12-25', 'Open Air Theatre', 200, 3)
ON DUPLICATE KEY UPDATE title=VALUES(title);
