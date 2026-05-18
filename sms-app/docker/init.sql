-- SMS schema + seed
SET NAMES utf8mb4;
SET CHARACTER SET utf8mb4;

CREATE DATABASE IF NOT EXISTS sms
  DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE sms;

DROP TABLE IF EXISTS grades;
DROP TABLE IF EXISTS tuitions;
DROP TABLE IF EXISTS enrollments;
DROP TABLE IF EXISTS class_section_sessions;
DROP TABLE IF EXISTS class_sections;
DROP TABLE IF EXISTS subject_textbooks;
DROP TABLE IF EXISTS subjects;
DROP TABLE IF EXISTS sessions;
DROP TABLE IF EXISTS textbooks;
DROP TABLE IF EXISTS students;
DROP TABLE IF EXISTS teachers;
DROP TABLE IF EXISTS majors;
DROP TABLE IF EXISTS faculties;
DROP TABLE IF EXISTS users;

CREATE TABLE users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(64) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    name VARCHAR(255) NOT NULL,
    role VARCHAR(32) NOT NULL,
    status VARCHAR(32) NOT NULL DEFAULT 'active'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE faculties (
    id INT AUTO_INCREMENT PRIMARY KEY,
    code VARCHAR(32) NOT NULL UNIQUE,
    name VARCHAR(255) NOT NULL,
    head VARCHAR(255)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE majors (
    id INT AUTO_INCREMENT PRIMARY KEY,
    code VARCHAR(32) NOT NULL UNIQUE,
    name VARCHAR(255) NOT NULL,
    faculty_id INT NOT NULL,
    FOREIGN KEY (faculty_id) REFERENCES faculties(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE teachers (
    id INT PRIMARY KEY,
    faculty_id INT,
    email VARCHAR(255),
    phone VARCHAR(32),
    FOREIGN KEY (id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (faculty_id) REFERENCES faculties(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE students (
    id INT PRIMARY KEY,
    mssv VARCHAR(32) NOT NULL UNIQUE,
    dob VARCHAR(32),
    gender VARCHAR(16),
    address VARCHAR(255),
    email VARCHAR(255),
    phone VARCHAR(32),
    faculty_id INT,
    major_id INT,
    cohort VARCHAR(16),
    admin_class VARCHAR(64),
    student_status VARCHAR(32) NOT NULL DEFAULT 'đang học',
    FOREIGN KEY (id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (faculty_id) REFERENCES faculties(id),
    FOREIGN KEY (major_id) REFERENCES majors(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE textbooks (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    author VARCHAR(255),
    year INT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE subjects (
    id INT AUTO_INCREMENT PRIMARY KEY,
    code VARCHAR(32) NOT NULL UNIQUE,
    name VARCHAR(255) NOT NULL,
    credits INT NOT NULL,
    content TEXT,
    faculty_id INT,
    status VARCHAR(32) NOT NULL DEFAULT 'active',
    FOREIGN KEY (faculty_id) REFERENCES faculties(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE subject_textbooks (
    subject_id INT NOT NULL,
    textbook_id INT NOT NULL,
    PRIMARY KEY (subject_id, textbook_id),
    FOREIGN KEY (subject_id) REFERENCES subjects(id) ON DELETE CASCADE,
    FOREIGN KEY (textbook_id) REFERENCES textbooks(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE sessions (
    id INT AUTO_INCREMENT PRIMARY KEY,
    day_of_week VARCHAR(32) NOT NULL,
    start_period INT NOT NULL,
    end_period INT NOT NULL,
    room VARCHAR(32)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE class_sections (
    id INT AUTO_INCREMENT PRIMARY KEY,
    code VARCHAR(64) NOT NULL,
    subject_id INT,
    teacher_id INT,
    capacity INT NOT NULL DEFAULT 0,
    enrolled_count INT NOT NULL DEFAULT 0,
    status VARCHAR(32) NOT NULL DEFAULT 'active',
    FOREIGN KEY (subject_id) REFERENCES subjects(id),
    FOREIGN KEY (teacher_id) REFERENCES teachers(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE class_section_sessions (
    class_section_id INT NOT NULL,
    session_id INT NOT NULL,
    PRIMARY KEY (class_section_id, session_id),
    FOREIGN KEY (class_section_id) REFERENCES class_sections(id) ON DELETE CASCADE,
    FOREIGN KEY (session_id) REFERENCES sessions(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE enrollments (
    student_id INT NOT NULL,
    class_section_id INT NOT NULL,
    PRIMARY KEY (student_id, class_section_id),
    FOREIGN KEY (student_id) REFERENCES students(id) ON DELETE CASCADE,
    FOREIGN KEY (class_section_id) REFERENCES class_sections(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE grades (
    id INT AUTO_INCREMENT PRIMARY KEY,
    student_id INT NOT NULL,
    class_section_id INT NOT NULL,
    attendance_score DOUBLE NOT NULL DEFAULT 0,
    midterm_score DOUBLE NOT NULL DEFAULT 0,
    final_score DOUBLE NOT NULL DEFAULT 0,
    semester VARCHAR(32) NOT NULL,
    UNIQUE KEY uq_grade_student_class_semester (student_id, class_section_id, semester),
    FOREIGN KEY (student_id) REFERENCES students(id) ON DELETE CASCADE,
    FOREIGN KEY (class_section_id) REFERENCES class_sections(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE tuitions (
    id INT AUTO_INCREMENT PRIMARY KEY,
    student_id INT NOT NULL,
    semester VARCHAR(32) NOT NULL,
    registered_credits INT NOT NULL,
    price_per_credit DOUBLE NOT NULL,
    paid DOUBLE NOT NULL DEFAULT 0,
    status VARCHAR(32) NOT NULL,
    FOREIGN KEY (student_id) REFERENCES students(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Seed: users
INSERT INTO users (id, username, password, name, role, status) VALUES
    (1, 'admin', 'admin123', 'Quản trị viên',     'ADMIN',   'active'),
    (2, 'pdt01', 'pdt123',   'Nguyễn Văn Phòng',  'PDT',     'active'),
    (3, 'gv01',  'gv123',    'Nguyễn Văn A',      'TEACHER', 'active'),
    (4, 'gv02',  'gv123',    'Trần Thị B',        'TEACHER', 'active'),
    (5, 'sv01',  'sv123',    'Phạm Quang Vinh',   'STUDENT', 'active'),
    (6, 'sv02',  'sv123',    'Đỗ Tiến Thành',     'STUDENT', 'active'),
    (7, 'sv03',  'sv123',    'Nguyễn Chí Ngọc',   'STUDENT', 'active'),
    (8, 'sv04',  'sv123',    'Đỗ Thiện Minh',     'STUDENT', 'active'),
    (9, 'gv03',  'gv123',    'Lê Văn C',          'TEACHER', 'active');

-- Seed: faculties
INSERT INTO faculties (id, code, name, head) VALUES
    (1, 'CNTT', 'Công nghệ Thông tin', 'PGS. TS. Nguyễn Văn X'),
    (2, 'DTVT', 'Điện tử Viễn thông',  'TS. Trần Thị Y'),
    (3, 'KTKT', 'Kỹ thuật Kinh tế',    'TS. Lê Văn Z');

-- Seed: majors
INSERT INTO majors (id, code, name, faculty_id) VALUES
    (1, 'CNPM', 'Công nghệ phần mềm',  1),
    (2, 'HTTT', 'Hệ thống thông tin',  1),
    (3, 'DTVT', 'Điện tử viễn thông',  2);

-- Seed: teachers
INSERT INTO teachers (id, faculty_id, email, phone) VALUES
    (3, 1, 'nva@ptit.edu.vn', '0901000001'),
    (4, 1, 'ttb@ptit.edu.vn', '0901000002'),
    (9, 2, 'lvc@ptit.edu.vn', '0901000003');

-- Seed: students
INSERT INTO students (id, mssv, dob, gender, address, email, phone, faculty_id, major_id, cohort, admin_class, student_status) VALUES
    (5, 'B23DCCE100', '2005-01-15', 'Nam', 'Hà Nội',  'pqv@ptit.edu.vn', '0912000001', 1, 1, '2023', 'D23CQCN01-B', 'đang học'),
    (6, 'B23DCDT239', '2005-03-20', 'Nam', 'Hà Nội',  'dtt@ptit.edu.vn', '0912000002', 1, 2, '2023', 'D23CQCN01-B', 'đang học'),
    (7, 'B23DCCE073', '2005-07-10', 'Nam', 'TP.HCM',  'ncn@ptit.edu.vn', '0912000003', 1, 1, '2023', 'D23CQCN02-B', 'đang học'),
    (8, 'B23DCCE064', '2005-11-05', 'Nam', 'Đà Nẵng', 'dtm@ptit.edu.vn', '0912000004', 1, 1, '2023', 'D23CQCN02-B', 'đang học');

-- Seed: textbooks
INSERT INTO textbooks (id, name, author, year) VALUES
    (1, 'Nhập môn Công nghệ phần mềm', 'Nguyễn Văn A', 2020),
    (2, 'Giải tích 1',                  'Trần Thị B',   2019),
    (3, 'Lập trình Java',               'Lê Văn C',     2021),
    (4, 'Cơ sở dữ liệu',                'Phạm Thị D',   2022);

-- Seed: subjects
INSERT INTO subjects (id, code, name, credits, content, faculty_id, status) VALUES
    (1, 'INT1001', 'Nhập môn CNPM',  3, 'Giới thiệu về công nghệ phần mềm', 1, 'active'),
    (2, 'MAT1001', 'Giải tích 1',    4, 'Giải tích toán học cơ bản',        1, 'active'),
    (3, 'INT1002', 'Lập trình Java', 3, 'Ngôn ngữ lập trình Java',          1, 'active'),
    (4, 'ELT1001', 'Điện tử cơ bản', 3, 'Kiến thức điện tử cơ bản',         2, 'active');

INSERT INTO subject_textbooks (subject_id, textbook_id) VALUES
    (1, 1), (2, 2), (3, 3), (4, 4);

-- Seed: sessions
INSERT INTO sessions (id, day_of_week, start_period, end_period, room) VALUES
    (1, 'Thứ 2', 1, 3, 'A101'),
    (2, 'Thứ 2', 4, 6, 'A102'),
    (3, 'Thứ 3', 1, 3, 'B201'),
    (4, 'Thứ 4', 4, 6, 'B202'),
    (5, 'Thứ 5', 1, 3, 'C301'),
    (6, 'Thứ 6', 4, 6, 'C302');

-- Seed: class_sections
INSERT INTO class_sections (id, code, subject_id, teacher_id, capacity, enrolled_count, status) VALUES
    (1, 'INT1234',    1, 3, 60, 40, 'active'),
    (2, 'INT1234_02', 1, 4, 50, 35, 'active'),
    (3, 'B1',         2, 3, 40, 40, 'active'),
    (4, 'INT1002_01', 3, 4, 45, 30, 'active');

INSERT INTO class_section_sessions (class_section_id, session_id) VALUES
    (1, 1), (2, 3), (3, 2), (4, 4);

-- Seed: enrollments
INSERT INTO enrollments (student_id, class_section_id) VALUES
    (5, 1), (6, 1), (7, 1),
    (5, 3), (6, 3), (7, 3), (8, 3);

-- Seed: grades
INSERT INTO grades (id, student_id, class_section_id, attendance_score, midterm_score, final_score, semester) VALUES
    (1, 5, 1, 7, 8, 9, '2024-2'),
    (2, 6, 1, 8, 9, 8, '2024-2'),
    (3, 7, 1, 7, 7, 8, '2024-2'),
    (4, 5, 3, 8, 7, 9, '2024-2'),
    (5, 6, 3, 9, 8, 7, '2024-2'),
    (6, 7, 3, 7, 8, 8, '2024-2'),
    (7, 8, 3, 8, 9, 9, '2024-2');

-- Seed: tuitions
INSERT INTO tuitions (id, student_id, semester, registered_credits, price_per_credit, paid, status) VALUES
    (1, 5, '2024-2', 15, 850000, 12750000, 'đã đóng'),
    (2, 6, '2024-2', 12, 850000, 0,        'chưa đóng'),
    (3, 7, '2024-2', 18, 850000, 15300000, 'đã đóng'),
    (4, 8, '2024-2', 14, 850000, 0,        'miễn giảm');
