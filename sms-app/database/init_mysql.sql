CREATE DATABASE IF NOT EXISTS sms_db
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE sms_db;

SET FOREIGN_KEY_CHECKS = 0;
DROP TABLE IF EXISTS grades;
DROP TABLE IF EXISTS tuitions;
DROP TABLE IF EXISTS student_enrollments;
DROP TABLE IF EXISTS class_section_sessions;
DROP TABLE IF EXISTS class_sections;
DROP TABLE IF EXISTS subject_textbooks;
DROP TABLE IF EXISTS subjects;
DROP TABLE IF EXISTS textbooks;
DROP TABLE IF EXISTS sessions;
DROP TABLE IF EXISTS students;
DROP TABLE IF EXISTS teachers;
DROP TABLE IF EXISTS majors;
DROP TABLE IF EXISTS faculties;
DROP TABLE IF EXISTS users;
SET FOREIGN_KEY_CHECKS = 1;

CREATE TABLE users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    name VARCHAR(255) NOT NULL,
    role ENUM('ADMIN', 'PDT', 'TEACHER', 'STUDENT') NOT NULL,
    status VARCHAR(50) NOT NULL DEFAULT 'active'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE faculties (
    id INT AUTO_INCREMENT PRIMARY KEY,
    code VARCHAR(50) NOT NULL UNIQUE,
    name VARCHAR(255) NOT NULL,
    head VARCHAR(255)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE majors (
    id INT AUTO_INCREMENT PRIMARY KEY,
    code VARCHAR(50) NOT NULL UNIQUE,
    name VARCHAR(255) NOT NULL,
    faculty_id INT NOT NULL,
    CONSTRAINT fk_majors_faculty FOREIGN KEY (faculty_id) REFERENCES faculties(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE teachers (
    user_id INT PRIMARY KEY,
    faculty_id INT NOT NULL,
    email VARCHAR(255),
    phone VARCHAR(50),
    CONSTRAINT fk_teachers_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_teachers_faculty FOREIGN KEY (faculty_id) REFERENCES faculties(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE students (
    user_id INT PRIMARY KEY,
    mssv VARCHAR(50) NOT NULL UNIQUE,
    dob VARCHAR(20),
    gender VARCHAR(20),
    address VARCHAR(255),
    email VARCHAR(255),
    phone VARCHAR(50),
    faculty_id INT NOT NULL,
    major_id INT NOT NULL,
    cohort VARCHAR(20),
    admin_class VARCHAR(100),
    student_status VARCHAR(50),
    CONSTRAINT fk_students_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_students_faculty FOREIGN KEY (faculty_id) REFERENCES faculties(id),
    CONSTRAINT fk_students_major FOREIGN KEY (major_id) REFERENCES majors(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE textbooks (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    author VARCHAR(255),
    publish_year INT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE subjects (
    id INT AUTO_INCREMENT PRIMARY KEY,
    code VARCHAR(50) NOT NULL UNIQUE,
    name VARCHAR(255) NOT NULL,
    credits INT NOT NULL,
    content TEXT,
    faculty_id INT NOT NULL,
    status VARCHAR(50) NOT NULL DEFAULT 'active',
    CONSTRAINT fk_subjects_faculty FOREIGN KEY (faculty_id) REFERENCES faculties(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE subject_textbooks (
    subject_id INT NOT NULL,
    textbook_id INT NOT NULL,
    PRIMARY KEY (subject_id, textbook_id),
    CONSTRAINT fk_subject_textbooks_subject FOREIGN KEY (subject_id) REFERENCES subjects(id) ON DELETE CASCADE,
    CONSTRAINT fk_subject_textbooks_textbook FOREIGN KEY (textbook_id) REFERENCES textbooks(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE sessions (
    id INT AUTO_INCREMENT PRIMARY KEY,
    day_of_week VARCHAR(50) NOT NULL,
    start_period INT NOT NULL,
    end_period INT NOT NULL,
    room VARCHAR(50) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE class_sections (
    id INT AUTO_INCREMENT PRIMARY KEY,
    code VARCHAR(100) NOT NULL,
    subject_id INT NOT NULL,
    teacher_id INT NOT NULL,
    capacity INT NOT NULL,
    enrolled_count INT NOT NULL DEFAULT 0,
    status VARCHAR(50) NOT NULL DEFAULT 'active',
    CONSTRAINT fk_class_sections_subject FOREIGN KEY (subject_id) REFERENCES subjects(id),
    CONSTRAINT fk_class_sections_teacher FOREIGN KEY (teacher_id) REFERENCES teachers(user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE class_section_sessions (
    class_section_id INT NOT NULL,
    session_id INT NOT NULL,
    PRIMARY KEY (class_section_id, session_id),
    CONSTRAINT fk_class_section_sessions_class FOREIGN KEY (class_section_id) REFERENCES class_sections(id) ON DELETE CASCADE,
    CONSTRAINT fk_class_section_sessions_session FOREIGN KEY (session_id) REFERENCES sessions(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE student_enrollments (
    student_id INT NOT NULL,
    class_section_id INT NOT NULL,
    PRIMARY KEY (student_id, class_section_id),
    CONSTRAINT fk_student_enrollments_student FOREIGN KEY (student_id) REFERENCES students(user_id) ON DELETE CASCADE,
    CONSTRAINT fk_student_enrollments_class FOREIGN KEY (class_section_id) REFERENCES class_sections(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE grades (
    id INT AUTO_INCREMENT PRIMARY KEY,
    student_id INT NOT NULL,
    class_section_id INT NOT NULL,
    attendance_score DOUBLE NOT NULL DEFAULT 0,
    midterm_score DOUBLE NOT NULL DEFAULT 0,
    final_score DOUBLE NOT NULL DEFAULT 0,
    semester VARCHAR(20) NOT NULL,
    UNIQUE KEY uk_grades_student_class (student_id, class_section_id),
    CONSTRAINT fk_grades_student FOREIGN KEY (student_id) REFERENCES students(user_id) ON DELETE CASCADE,
    CONSTRAINT fk_grades_class FOREIGN KEY (class_section_id) REFERENCES class_sections(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE tuitions (
    id INT AUTO_INCREMENT PRIMARY KEY,
    student_id INT NOT NULL,
    semester VARCHAR(20) NOT NULL,
    registered_credits INT NOT NULL,
    price_per_credit DOUBLE NOT NULL,
    paid DOUBLE NOT NULL DEFAULT 0,
    status VARCHAR(50) NOT NULL,
    CONSTRAINT fk_tuitions_student FOREIGN KEY (student_id) REFERENCES students(user_id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

INSERT INTO users (id, username, password, name, role, status) VALUES
(1, 'admin', 'admin123', 'Quản trị viên', 'ADMIN', 'active'),
(2, 'pdt01', 'pdt123', 'Nguyễn Văn Phòng', 'PDT', 'active'),
(3, 'gv01', 'gv123', 'Nguyễn Văn A', 'TEACHER', 'active'),
(4, 'gv02', 'gv123', 'Trần Thị B', 'TEACHER', 'active'),
(5, 'sv01', 'sv123', 'Phạm Quang Vinh', 'STUDENT', 'active'),
(6, 'sv02', 'sv123', 'Đỗ Tiến Thành', 'STUDENT', 'active'),
(7, 'sv03', 'sv123', 'Nguyễn Chí Ngọc', 'STUDENT', 'active'),
(8, 'sv04', 'sv123', 'Đỗ Thiện Minh', 'STUDENT', 'active'),
(9, 'gv03', 'gv123', 'Lê Văn C', 'TEACHER', 'active');

INSERT INTO faculties (id, code, name, head) VALUES
(1, 'CNTT', 'Công nghệ Thông tin', 'PGS. TS. Nguyễn Văn X'),
(2, 'DTVT', 'Điện tử Viễn thông', 'TS. Trần Thị Y'),
(3, 'KTKT', 'Kỹ thuật Kinh tế', 'TS. Lê Văn Z');

INSERT INTO majors (id, code, name, faculty_id) VALUES
(1, 'CNPM', 'Công nghệ phần mềm', 1),
(2, 'HTTT', 'Hệ thống thông tin', 1),
(3, 'DTVT', 'Điện tử viễn thông', 2);

INSERT INTO teachers (user_id, faculty_id, email, phone) VALUES
(3, 1, 'nva@ptit.edu.vn', '0901000001'),
(4, 1, 'ttb@ptit.edu.vn', '0901000002'),
(9, 2, 'lvc@ptit.edu.vn', '0901000003');

INSERT INTO students (user_id, mssv, dob, gender, address, email, phone, faculty_id, major_id, cohort, admin_class, student_status) VALUES
(5, 'B23DCCE100', '2005-01-15', 'Nam', 'Hà Nội', 'pqv@ptit.edu.vn', '0912000001', 1, 1, '2023', 'D23CQCN01-B', 'đang học'),
(6, 'B23DCDT239', '2005-03-20', 'Nam', 'Hà Nội', 'dtt@ptit.edu.vn', '0912000002', 1, 2, '2023', 'D23CQCN01-B', 'đang học'),
(7, 'B23DCCE073', '2005-07-10', 'Nam', 'TP.HCM', 'ncn@ptit.edu.vn', '0912000003', 1, 1, '2023', 'D23CQCN02-B', 'đang học'),
(8, 'B23DCCE064', '2005-11-05', 'Nam', 'Đà Nẵng', 'dtm@ptit.edu.vn', '0912000004', 1, 1, '2023', 'D23CQCN02-B', 'đang học');

INSERT INTO textbooks (id, name, author, publish_year) VALUES
(1, 'Nhập môn Công nghệ phần mềm', 'Nguyễn Văn A', 2020),
(2, 'Giải tích 1', 'Trần Thị B', 2019),
(3, 'Lập trình Java', 'Lê Văn C', 2021),
(4, 'Cơ sở dữ liệu', 'Phạm Thị D', 2022);

INSERT INTO subjects (id, code, name, credits, content, faculty_id, status) VALUES
(1, 'INT1001', 'Nhập môn CNPM', 3, 'Giới thiệu về công nghệ phần mềm', 1, 'active'),
(2, 'MAT1001', 'Giải tích 1', 4, 'Giải tích toán học cơ bản', 1, 'active'),
(3, 'INT1002', 'Lập trình Java', 3, 'Ngôn ngữ lập trình Java', 1, 'active'),
(4, 'ELT1001', 'Điện tử cơ bản', 3, 'Kiến thức điện tử cơ bản', 2, 'active');

INSERT INTO subject_textbooks (subject_id, textbook_id) VALUES
(1, 1), (2, 2), (3, 3), (4, 4);

INSERT INTO sessions (id, day_of_week, start_period, end_period, room) VALUES
(1, 'Thứ 2', 1, 3, 'A101'),
(2, 'Thứ 2', 4, 6, 'A102'),
(3, 'Thứ 3', 1, 3, 'B201'),
(4, 'Thứ 4', 4, 6, 'B202'),
(5, 'Thứ 5', 1, 3, 'C301'),
(6, 'Thứ 6', 4, 6, 'C302');

INSERT INTO class_sections (id, code, subject_id, teacher_id, capacity, enrolled_count, status) VALUES
(1, 'INT1234', 1, 3, 60, 40, 'active'),
(2, 'INT1234_02', 1, 4, 50, 35, 'active'),
(3, 'B1', 2, 3, 40, 40, 'active'),
(4, 'INT1002_01', 3, 4, 45, 30, 'active');

INSERT INTO class_section_sessions (class_section_id, session_id) VALUES
(1, 1), (2, 3), (3, 2), (4, 4);

INSERT INTO student_enrollments (student_id, class_section_id) VALUES
(5, 1), (6, 1), (7, 1),
(5, 3), (6, 3), (7, 3), (8, 3);

INSERT INTO grades (id, student_id, class_section_id, attendance_score, midterm_score, final_score, semester) VALUES
(1, 5, 1, 7, 8, 9, '2024-2'),
(2, 6, 1, 8, 9, 8, '2024-2'),
(3, 7, 1, 7, 7, 8, '2024-2'),
(4, 5, 3, 8, 7, 9, '2024-2'),
(5, 6, 3, 9, 8, 7, '2024-2'),
(6, 7, 3, 7, 8, 8, '2024-2'),
(7, 8, 3, 8, 9, 9, '2024-2');

INSERT INTO tuitions (id, student_id, semester, registered_credits, price_per_credit, paid, status) VALUES
(1, 5, '2024-2', 15, 850000, 12750000, 'đã đóng'),
(2, 6, '2024-2', 12, 850000, 0, 'chưa đóng'),
(3, 7, '2024-2', 18, 850000, 15300000, 'đã đóng'),
(4, 8, '2024-2', 14, 850000, 0, 'miễn giảm');

ALTER TABLE users AUTO_INCREMENT = 10;
ALTER TABLE faculties AUTO_INCREMENT = 4;
ALTER TABLE majors AUTO_INCREMENT = 4;
ALTER TABLE textbooks AUTO_INCREMENT = 5;
ALTER TABLE subjects AUTO_INCREMENT = 5;
ALTER TABLE sessions AUTO_INCREMENT = 7;
ALTER TABLE class_sections AUTO_INCREMENT = 5;
ALTER TABLE grades AUTO_INCREMENT = 8;
ALTER TABLE tuitions AUTO_INCREMENT = 5;

DROP PROCEDURE IF EXISTS sp_check_login;
DROP PROCEDURE IF EXISTS sp_get_all_users;
DROP PROCEDURE IF EXISTS sp_search_users;
DROP PROCEDURE IF EXISTS sp_add_user;
DROP PROCEDURE IF EXISTS sp_delete_user;
DROP PROCEDURE IF EXISTS sp_update_user_role;
DROP PROCEDURE IF EXISTS sp_get_all_faculties;
DROP PROCEDURE IF EXISTS sp_get_faculty_by_id;
DROP PROCEDURE IF EXISTS sp_get_all_majors;
DROP PROCEDURE IF EXISTS sp_get_major_by_id;
DROP PROCEDURE IF EXISTS sp_get_all_sessions;
DROP PROCEDURE IF EXISTS sp_get_session_by_id;
DROP PROCEDURE IF EXISTS sp_get_all_textbooks;
DROP PROCEDURE IF EXISTS sp_get_textbook_by_id;
DROP PROCEDURE IF EXISTS sp_get_all_teachers;
DROP PROCEDURE IF EXISTS sp_get_teacher_by_id;
DROP PROCEDURE IF EXISTS sp_search_teachers;
DROP PROCEDURE IF EXISTS sp_select_students_base;
DROP PROCEDURE IF EXISTS sp_get_all_students;
DROP PROCEDURE IF EXISTS sp_get_student_by_id;
DROP PROCEDURE IF EXISTS sp_get_student_by_mssv;
DROP PROCEDURE IF EXISTS sp_get_students_by_class;
DROP PROCEDURE IF EXISTS sp_search_students;
DROP PROCEDURE IF EXISTS sp_add_student;
DROP PROCEDURE IF EXISTS sp_update_student;
DROP PROCEDURE IF EXISTS sp_soft_delete_student;
DROP PROCEDURE IF EXISTS sp_get_all_subjects;
DROP PROCEDURE IF EXISTS sp_get_subject_by_id;
DROP PROCEDURE IF EXISTS sp_search_subjects;
DROP PROCEDURE IF EXISTS sp_create_subject;
DROP PROCEDURE IF EXISTS sp_update_subject;
DROP PROCEDURE IF EXISTS sp_delete_subject;
DROP PROCEDURE IF EXISTS sp_get_textbooks_by_subject;
DROP PROCEDURE IF EXISTS sp_delete_subject_textbooks;
DROP PROCEDURE IF EXISTS sp_add_subject_textbook;
DROP PROCEDURE IF EXISTS sp_get_all_class_sections;
DROP PROCEDURE IF EXISTS sp_get_class_section_by_id;
DROP PROCEDURE IF EXISTS sp_search_class_sections;
DROP PROCEDURE IF EXISTS sp_get_class_sections_by_teacher;
DROP PROCEDURE IF EXISTS sp_get_class_sections_by_student;
DROP PROCEDURE IF EXISTS sp_get_active_sessions_by_teacher;
DROP PROCEDURE IF EXISTS sp_create_class_section;
DROP PROCEDURE IF EXISTS sp_update_class_section;
DROP PROCEDURE IF EXISTS sp_cancel_class_section;
DROP PROCEDURE IF EXISTS sp_get_sessions_by_class_section;
DROP PROCEDURE IF EXISTS sp_delete_class_section_sessions;
DROP PROCEDURE IF EXISTS sp_add_class_section_session;
DROP PROCEDURE IF EXISTS sp_get_student_enrollments;
DROP PROCEDURE IF EXISTS sp_enroll_student;
DROP PROCEDURE IF EXISTS sp_cancel_enrollment;
DROP PROCEDURE IF EXISTS sp_get_grade_by_student_and_class;
DROP PROCEDURE IF EXISTS sp_get_grades_by_student;
DROP PROCEDURE IF EXISTS sp_get_grades_by_class;
DROP PROCEDURE IF EXISTS sp_select_grades_base;
DROP PROCEDURE IF EXISTS sp_update_grade;
DROP PROCEDURE IF EXISTS sp_add_grade;
DROP PROCEDURE IF EXISTS sp_ensure_grade_for_student_class;
DROP PROCEDURE IF EXISTS sp_get_all_tuitions;
DROP PROCEDURE IF EXISTS sp_get_tuitions_by_student;
DROP PROCEDURE IF EXISTS sp_update_tuition;
DROP PROCEDURE IF EXISTS sp_apply_tuition_discount;
DROP PROCEDURE IF EXISTS sp_pay_tuition;

DELIMITER $$

CREATE PROCEDURE sp_check_login(IN p_username VARCHAR(100), IN p_password VARCHAR(255))
BEGIN
    SELECT id, username, password, name, role, status FROM users
    WHERE username = p_username AND password = p_password AND status = 'active';
END$$

CREATE PROCEDURE sp_get_all_users()
BEGIN
    SELECT id, username, password, name, role, status FROM users ORDER BY id;
END$$

CREATE PROCEDURE sp_search_users(IN p_keyword VARCHAR(255))
BEGIN
    SELECT id, username, password, name, role, status FROM users
    WHERE LOWER(name) LIKE CONCAT('%', p_keyword, '%')
       OR LOWER(username) LIKE CONCAT('%', p_keyword, '%')
       OR CAST(id AS CHAR) LIKE CONCAT('%', p_keyword, '%')
    ORDER BY id;
END$$

CREATE PROCEDURE sp_add_user(
    IN p_username VARCHAR(100),
    IN p_password VARCHAR(255),
    IN p_name VARCHAR(255),
    IN p_role VARCHAR(20),
    IN p_status VARCHAR(50),
    OUT p_id INT
)
BEGIN
    INSERT INTO users(username, password, name, role, status)
    VALUES (p_username, p_password, p_name, p_role, p_status);
    SET p_id = LAST_INSERT_ID();
END$$

CREATE PROCEDURE sp_delete_user(IN p_id INT)
BEGIN
    DELETE FROM users WHERE id = p_id;
END$$

CREATE PROCEDURE sp_update_user_role(IN p_id INT, IN p_role VARCHAR(20))
BEGIN
    UPDATE users SET role = p_role WHERE id = p_id;
END$$

CREATE PROCEDURE sp_get_all_faculties()
BEGIN
    SELECT id, code, name, head FROM faculties ORDER BY id;
END$$

CREATE PROCEDURE sp_get_faculty_by_id(IN p_id INT)
BEGIN
    SELECT id, code, name, head FROM faculties WHERE id = p_id;
END$$

CREATE PROCEDURE sp_get_all_majors()
BEGIN
    SELECT m.id, m.code, m.name, f.id faculty_id, f.code faculty_code, f.name faculty_name, f.head
    FROM majors m JOIN faculties f ON f.id = m.faculty_id ORDER BY m.id;
END$$

CREATE PROCEDURE sp_get_major_by_id(IN p_id INT)
BEGIN
    SELECT m.id, m.code, m.name, f.id faculty_id, f.code faculty_code, f.name faculty_name, f.head
    FROM majors m JOIN faculties f ON f.id = m.faculty_id WHERE m.id = p_id;
END$$

CREATE PROCEDURE sp_get_all_sessions()
BEGIN
    SELECT id, day_of_week, start_period, end_period, room FROM sessions ORDER BY id;
END$$

CREATE PROCEDURE sp_get_session_by_id(IN p_id INT)
BEGIN
    SELECT id, day_of_week, start_period, end_period, room FROM sessions WHERE id = p_id;
END$$

CREATE PROCEDURE sp_get_all_textbooks()
BEGIN
    SELECT id, name, author, publish_year FROM textbooks ORDER BY id;
END$$

CREATE PROCEDURE sp_get_textbook_by_id(IN p_id INT)
BEGIN
    SELECT id, name, author, publish_year FROM textbooks WHERE id = p_id;
END$$

CREATE PROCEDURE sp_get_all_teachers()
BEGIN
    SELECT u.id, u.username, u.password, u.name, u.status, t.email, t.phone,
           f.id faculty_id, f.code faculty_code, f.name faculty_name, f.head
    FROM teachers t JOIN users u ON u.id = t.user_id
    JOIN faculties f ON f.id = t.faculty_id
    ORDER BY u.id;
END$$

CREATE PROCEDURE sp_get_teacher_by_id(IN p_id INT)
BEGIN
    SELECT u.id, u.username, u.password, u.name, u.status, t.email, t.phone,
           f.id faculty_id, f.code faculty_code, f.name faculty_name, f.head
    FROM teachers t JOIN users u ON u.id = t.user_id
    JOIN faculties f ON f.id = t.faculty_id
    WHERE u.id = p_id;
END$$

CREATE PROCEDURE sp_search_teachers(IN p_keyword VARCHAR(255))
BEGIN
    SELECT u.id, u.username, u.password, u.name, u.status, t.email, t.phone,
           f.id faculty_id, f.code faculty_code, f.name faculty_name, f.head
    FROM teachers t JOIN users u ON u.id = t.user_id
    JOIN faculties f ON f.id = t.faculty_id
    WHERE LOWER(u.name) LIKE CONCAT('%', p_keyword, '%')
    ORDER BY u.id;
END$$

CREATE PROCEDURE sp_select_students_base()
BEGIN
    SELECT u.id, u.username, u.password, u.name, u.status, st.mssv, st.dob, st.gender, st.address, st.email, st.phone,
           st.cohort, st.admin_class, st.student_status,
           f.id faculty_id, f.code faculty_code, f.name faculty_name, f.head,
           m.id major_id, m.code major_code, m.name major_name
    FROM students st JOIN users u ON u.id = st.user_id
    JOIN faculties f ON f.id = st.faculty_id
    JOIN majors m ON m.id = st.major_id
    ORDER BY u.id;
END$$

CREATE PROCEDURE sp_get_all_students()
BEGIN
    CALL sp_select_students_base();
END$$

CREATE PROCEDURE sp_get_student_by_id(IN p_id INT)
BEGIN
    SELECT u.id, u.username, u.password, u.name, u.status, st.mssv, st.dob, st.gender, st.address, st.email, st.phone,
           st.cohort, st.admin_class, st.student_status,
           f.id faculty_id, f.code faculty_code, f.name faculty_name, f.head,
           m.id major_id, m.code major_code, m.name major_name
    FROM students st JOIN users u ON u.id = st.user_id
    JOIN faculties f ON f.id = st.faculty_id
    JOIN majors m ON m.id = st.major_id
    WHERE u.id = p_id;
END$$

CREATE PROCEDURE sp_get_student_by_mssv(IN p_mssv VARCHAR(50))
BEGIN
    SELECT u.id, u.username, u.password, u.name, u.status, st.mssv, st.dob, st.gender, st.address, st.email, st.phone,
           st.cohort, st.admin_class, st.student_status,
           f.id faculty_id, f.code faculty_code, f.name faculty_name, f.head,
           m.id major_id, m.code major_code, m.name major_name
    FROM students st JOIN users u ON u.id = st.user_id
    JOIN faculties f ON f.id = st.faculty_id
    JOIN majors m ON m.id = st.major_id
    WHERE st.mssv = p_mssv;
END$$

CREATE PROCEDURE sp_get_students_by_class(IN p_class_section_id INT)
BEGIN
    SELECT u.id, u.username, u.password, u.name, u.status, st.mssv, st.dob, st.gender, st.address, st.email, st.phone,
           st.cohort, st.admin_class, st.student_status,
           f.id faculty_id, f.code faculty_code, f.name faculty_name, f.head,
           m.id major_id, m.code major_code, m.name major_name
    FROM students st JOIN users u ON u.id = st.user_id
    JOIN faculties f ON f.id = st.faculty_id
    JOIN majors m ON m.id = st.major_id
    JOIN student_enrollments se ON se.student_id = st.user_id
    WHERE se.class_section_id = p_class_section_id
    ORDER BY u.id;
END$$

CREATE PROCEDURE sp_search_students(IN p_keyword VARCHAR(255))
BEGIN
    SELECT u.id, u.username, u.password, u.name, u.status, st.mssv, st.dob, st.gender, st.address, st.email, st.phone,
           st.cohort, st.admin_class, st.student_status,
           f.id faculty_id, f.code faculty_code, f.name faculty_name, f.head,
           m.id major_id, m.code major_code, m.name major_name
    FROM students st JOIN users u ON u.id = st.user_id
    JOIN faculties f ON f.id = st.faculty_id
    JOIN majors m ON m.id = st.major_id
    WHERE LOWER(u.name) LIKE CONCAT('%', p_keyword, '%')
       OR LOWER(st.mssv) LIKE CONCAT('%', p_keyword, '%')
    ORDER BY u.id;
END$$

CREATE PROCEDURE sp_add_student(
    IN p_username VARCHAR(100), IN p_password VARCHAR(255), IN p_name VARCHAR(255), IN p_status VARCHAR(50),
    IN p_dob VARCHAR(20), IN p_gender VARCHAR(20), IN p_address VARCHAR(255), IN p_email VARCHAR(255), IN p_phone VARCHAR(50),
    IN p_faculty_id INT, IN p_major_id INT, IN p_cohort VARCHAR(20), IN p_admin_class VARCHAR(100), IN p_student_status VARCHAR(50),
    OUT p_user_id INT, OUT p_mssv VARCHAR(50)
)
BEGIN
    INSERT INTO users(username, password, name, role, status)
    VALUES (p_username, p_password, p_name, 'STUDENT', p_status);
    SET p_user_id = LAST_INSERT_ID();
    SELECT CONCAT('B', COALESCE(MAX(CASE WHEN mssv REGEXP '^B[0-9]+$' THEN CAST(SUBSTRING(mssv, 2) AS UNSIGNED) END), 24000) + 1)
    INTO p_mssv FROM students;
    INSERT INTO students(user_id, mssv, dob, gender, address, email, phone, faculty_id, major_id, cohort, admin_class, student_status)
    VALUES (p_user_id, p_mssv, p_dob, p_gender, p_address, p_email, p_phone, p_faculty_id, p_major_id, p_cohort, p_admin_class, p_student_status);
END$$

CREATE PROCEDURE sp_update_student(
    IN p_id INT, IN p_username VARCHAR(100), IN p_password VARCHAR(255), IN p_name VARCHAR(255), IN p_status VARCHAR(50),
    IN p_mssv VARCHAR(50), IN p_dob VARCHAR(20), IN p_gender VARCHAR(20), IN p_address VARCHAR(255), IN p_email VARCHAR(255),
    IN p_phone VARCHAR(50), IN p_faculty_id INT, IN p_major_id INT, IN p_cohort VARCHAR(20),
    IN p_admin_class VARCHAR(100), IN p_student_status VARCHAR(50)
)
BEGIN
    UPDATE users SET username = p_username, password = p_password, name = p_name, status = p_status WHERE id = p_id;
    UPDATE students
    SET mssv = p_mssv, dob = p_dob, gender = p_gender, address = p_address, email = p_email, phone = p_phone,
        faculty_id = p_faculty_id, major_id = p_major_id, cohort = p_cohort, admin_class = p_admin_class, student_status = p_student_status
    WHERE user_id = p_id;
END$$

CREATE PROCEDURE sp_soft_delete_student(IN p_id INT)
BEGIN
    UPDATE students SET student_status = 'nghỉ học' WHERE user_id = p_id;
END$$

CREATE PROCEDURE sp_get_all_subjects()
BEGIN
    SELECT s.id, s.code, s.name, s.credits, s.content, s.status,
           f.id faculty_id, f.code faculty_code, f.name faculty_name, f.head
    FROM subjects s JOIN faculties f ON f.id = s.faculty_id
    ORDER BY s.id;
END$$

CREATE PROCEDURE sp_get_subject_by_id(IN p_id INT)
BEGIN
    SELECT s.id, s.code, s.name, s.credits, s.content, s.status,
           f.id faculty_id, f.code faculty_code, f.name faculty_name, f.head
    FROM subjects s JOIN faculties f ON f.id = s.faculty_id
    WHERE s.id = p_id;
END$$

CREATE PROCEDURE sp_search_subjects(IN p_keyword VARCHAR(255))
BEGIN
    SELECT s.id, s.code, s.name, s.credits, s.content, s.status,
           f.id faculty_id, f.code faculty_code, f.name faculty_name, f.head
    FROM subjects s JOIN faculties f ON f.id = s.faculty_id
    WHERE LOWER(s.name) LIKE CONCAT('%', p_keyword, '%')
       OR LOWER(s.code) LIKE CONCAT('%', p_keyword, '%')
    ORDER BY s.id;
END$$

CREATE PROCEDURE sp_create_subject(
    IN p_code VARCHAR(50), IN p_name VARCHAR(255), IN p_credits INT, IN p_content TEXT,
    IN p_faculty_id INT, IN p_status VARCHAR(50), OUT p_id INT
)
BEGIN
    INSERT INTO subjects(code, name, credits, content, faculty_id, status)
    VALUES (p_code, p_name, p_credits, p_content, p_faculty_id, p_status);
    SET p_id = LAST_INSERT_ID();
END$$

CREATE PROCEDURE sp_update_subject(
    IN p_id INT, IN p_name VARCHAR(255), IN p_credits INT, IN p_content TEXT, IN p_faculty_id INT, IN p_status VARCHAR(50)
)
BEGIN
    UPDATE subjects SET name = p_name, credits = p_credits, content = p_content, faculty_id = p_faculty_id, status = p_status WHERE id = p_id;
END$$

CREATE PROCEDURE sp_delete_subject(IN p_id INT)
BEGIN
    DELETE FROM subjects WHERE id = p_id;
END$$

CREATE PROCEDURE sp_get_textbooks_by_subject(IN p_subject_id INT)
BEGIN
    SELECT t.id, t.name, t.author, t.publish_year FROM textbooks t
    JOIN subject_textbooks st ON st.textbook_id = t.id
    WHERE st.subject_id = p_subject_id
    ORDER BY t.id;
END$$

CREATE PROCEDURE sp_delete_subject_textbooks(IN p_subject_id INT)
BEGIN
    DELETE FROM subject_textbooks WHERE subject_id = p_subject_id;
END$$

CREATE PROCEDURE sp_add_subject_textbook(IN p_subject_id INT, IN p_textbook_id INT)
BEGIN
    INSERT IGNORE INTO subject_textbooks(subject_id, textbook_id) VALUES (p_subject_id, p_textbook_id);
END$$

CREATE PROCEDURE sp_get_all_class_sections()
BEGIN
    SELECT c.id, c.code, c.capacity, c.enrolled_count, c.status,
           s.id subject_id, s.code subject_code, s.name subject_name, s.credits, s.content, s.status subject_status,
           sf.id subject_faculty_id, sf.code subject_faculty_code, sf.name subject_faculty_name, sf.head subject_faculty_head,
           u.id teacher_id, u.username, u.password, u.name teacher_name, u.status teacher_status, t.email teacher_email, t.phone teacher_phone,
           tf.id teacher_faculty_id, tf.code teacher_faculty_code, tf.name teacher_faculty_name, tf.head teacher_faculty_head
    FROM class_sections c
    JOIN subjects s ON s.id = c.subject_id JOIN faculties sf ON sf.id = s.faculty_id
    JOIN teachers t ON t.user_id = c.teacher_id JOIN users u ON u.id = t.user_id
    JOIN faculties tf ON tf.id = t.faculty_id
    ORDER BY c.id;
END$$

CREATE PROCEDURE sp_get_class_section_by_id(IN p_id INT)
BEGIN
    SELECT c.id, c.code, c.capacity, c.enrolled_count, c.status,
           s.id subject_id, s.code subject_code, s.name subject_name, s.credits, s.content, s.status subject_status,
           sf.id subject_faculty_id, sf.code subject_faculty_code, sf.name subject_faculty_name, sf.head subject_faculty_head,
           u.id teacher_id, u.username, u.password, u.name teacher_name, u.status teacher_status, t.email teacher_email, t.phone teacher_phone,
           tf.id teacher_faculty_id, tf.code teacher_faculty_code, tf.name teacher_faculty_name, tf.head teacher_faculty_head
    FROM class_sections c
    JOIN subjects s ON s.id = c.subject_id JOIN faculties sf ON sf.id = s.faculty_id
    JOIN teachers t ON t.user_id = c.teacher_id JOIN users u ON u.id = t.user_id
    JOIN faculties tf ON tf.id = t.faculty_id
    WHERE c.id = p_id;
END$$

CREATE PROCEDURE sp_search_class_sections(IN p_keyword VARCHAR(255))
BEGIN
    SELECT c.id, c.code, c.capacity, c.enrolled_count, c.status,
           s.id subject_id, s.code subject_code, s.name subject_name, s.credits, s.content, s.status subject_status,
           sf.id subject_faculty_id, sf.code subject_faculty_code, sf.name subject_faculty_name, sf.head subject_faculty_head,
           u.id teacher_id, u.username, u.password, u.name teacher_name, u.status teacher_status, t.email teacher_email, t.phone teacher_phone,
           tf.id teacher_faculty_id, tf.code teacher_faculty_code, tf.name teacher_faculty_name, tf.head teacher_faculty_head
    FROM class_sections c
    JOIN subjects s ON s.id = c.subject_id JOIN faculties sf ON sf.id = s.faculty_id
    JOIN teachers t ON t.user_id = c.teacher_id JOIN users u ON u.id = t.user_id
    JOIN faculties tf ON tf.id = t.faculty_id
    WHERE LOWER(c.code) LIKE CONCAT('%', p_keyword, '%')
       OR LOWER(s.name) LIKE CONCAT('%', p_keyword, '%')
    ORDER BY c.id;
END$$

CREATE PROCEDURE sp_get_class_sections_by_teacher(IN p_teacher_id INT)
BEGIN
    SELECT c.id, c.code, c.capacity, c.enrolled_count, c.status,
           s.id subject_id, s.code subject_code, s.name subject_name, s.credits, s.content, s.status subject_status,
           sf.id subject_faculty_id, sf.code subject_faculty_code, sf.name subject_faculty_name, sf.head subject_faculty_head,
           u.id teacher_id, u.username, u.password, u.name teacher_name, u.status teacher_status, t.email teacher_email, t.phone teacher_phone,
           tf.id teacher_faculty_id, tf.code teacher_faculty_code, tf.name teacher_faculty_name, tf.head teacher_faculty_head
    FROM class_sections c
    JOIN subjects s ON s.id = c.subject_id JOIN faculties sf ON sf.id = s.faculty_id
    JOIN teachers t ON t.user_id = c.teacher_id JOIN users u ON u.id = t.user_id
    JOIN faculties tf ON tf.id = t.faculty_id
    WHERE c.teacher_id = p_teacher_id
    ORDER BY c.id;
END$$

CREATE PROCEDURE sp_get_class_sections_by_student(IN p_student_id INT)
BEGIN
    SELECT c.id, c.code, c.capacity, c.enrolled_count, c.status,
           s.id subject_id, s.code subject_code, s.name subject_name, s.credits, s.content, s.status subject_status,
           sf.id subject_faculty_id, sf.code subject_faculty_code, sf.name subject_faculty_name, sf.head subject_faculty_head,
           u.id teacher_id, u.username, u.password, u.name teacher_name, u.status teacher_status, t.email teacher_email, t.phone teacher_phone,
           tf.id teacher_faculty_id, tf.code teacher_faculty_code, tf.name teacher_faculty_name, tf.head teacher_faculty_head
    FROM class_sections c
    JOIN subjects s ON s.id = c.subject_id JOIN faculties sf ON sf.id = s.faculty_id
    JOIN teachers t ON t.user_id = c.teacher_id JOIN users u ON u.id = t.user_id
    JOIN faculties tf ON tf.id = t.faculty_id
    JOIN student_enrollments se ON se.class_section_id = c.id
    WHERE se.student_id = p_student_id
    ORDER BY c.id;
END$$

CREATE PROCEDURE sp_get_active_sessions_by_teacher(IN p_teacher_id INT)
BEGIN
    SELECT se.day_of_week, se.start_period, se.end_period FROM class_sections c
    JOIN class_section_sessions css ON css.class_section_id = c.id
    JOIN sessions se ON se.id = css.session_id
    WHERE c.teacher_id = p_teacher_id AND c.status = 'active';
END$$

CREATE PROCEDURE sp_create_class_section(
    IN p_code VARCHAR(100), IN p_subject_id INT, IN p_teacher_id INT, IN p_capacity INT,
    IN p_enrolled_count INT, IN p_status VARCHAR(50), OUT p_id INT
)
BEGIN
    INSERT INTO class_sections(code, subject_id, teacher_id, capacity, enrolled_count, status)
    VALUES (p_code, p_subject_id, p_teacher_id, p_capacity, p_enrolled_count, p_status);
    SET p_id = LAST_INSERT_ID();
END$$

CREATE PROCEDURE sp_update_class_section(
    IN p_id INT, IN p_code VARCHAR(100), IN p_subject_id INT, IN p_teacher_id INT,
    IN p_capacity INT, IN p_enrolled_count INT, IN p_status VARCHAR(50)
)
BEGIN
    UPDATE class_sections
    SET code = p_code, subject_id = p_subject_id, teacher_id = p_teacher_id,
        capacity = p_capacity, enrolled_count = p_enrolled_count, status = p_status
    WHERE id = p_id;
END$$

CREATE PROCEDURE sp_cancel_class_section(IN p_id INT)
BEGIN
    UPDATE class_sections SET status = 'cancelled' WHERE id = p_id;
END$$

CREATE PROCEDURE sp_get_sessions_by_class_section(IN p_class_section_id INT)
BEGIN
    SELECT se.id, se.day_of_week, se.start_period, se.end_period, se.room FROM sessions se
    JOIN class_section_sessions css ON css.session_id = se.id
    WHERE css.class_section_id = p_class_section_id
    ORDER BY se.id;
END$$

CREATE PROCEDURE sp_delete_class_section_sessions(IN p_class_section_id INT)
BEGIN
    DELETE FROM class_section_sessions WHERE class_section_id = p_class_section_id;
END$$

CREATE PROCEDURE sp_add_class_section_session(IN p_class_section_id INT, IN p_session_id INT)
BEGIN
    INSERT INTO class_section_sessions(class_section_id, session_id) VALUES (p_class_section_id, p_session_id);
END$$

CREATE PROCEDURE sp_get_student_enrollments()
BEGIN
    SELECT student_id, class_section_id FROM student_enrollments;
END$$

CREATE PROCEDURE sp_enroll_student(IN p_student_id INT, IN p_class_section_id INT)
BEGIN
    INSERT INTO student_enrollments(student_id, class_section_id)
    SELECT p_student_id, p_class_section_id
    FROM class_sections
    WHERE id = p_class_section_id AND enrolled_count < capacity;
    UPDATE class_sections
    SET enrolled_count = enrolled_count + 1
    WHERE id = p_class_section_id AND ROW_COUNT() > 0;
END$$

CREATE PROCEDURE sp_cancel_enrollment(IN p_student_id INT, IN p_class_section_id INT)
BEGIN
    DELETE FROM student_enrollments WHERE student_id = p_student_id AND class_section_id = p_class_section_id;
    UPDATE class_sections
    SET enrolled_count = GREATEST(enrolled_count - 1, 0)
    WHERE id = p_class_section_id AND ROW_COUNT() > 0;
END$$

CREATE PROCEDURE sp_select_grades_base(IN p_student_id INT, IN p_class_section_id INT, IN p_grade_id INT)
BEGIN
    SELECT g.id, g.attendance_score, g.midterm_score, g.final_score, g.semester,
           su.id student_user_id, su.username student_username, su.password student_password, su.name student_name, su.status student_status_user,
           st.mssv, st.dob, st.gender, st.address, st.email student_email, st.phone student_phone, st.cohort, st.admin_class, st.student_status,
           sf.id student_faculty_id, sf.code student_faculty_code, sf.name student_faculty_name, sf.head student_faculty_head,
           m.id major_id, m.code major_code, m.name major_name,
           c.id class_id, c.code class_code, c.capacity, c.enrolled_count, c.status class_status,
           sub.id subject_id, sub.code subject_code, sub.name subject_name, sub.credits, sub.content, sub.status subject_status,
           subf.id subject_faculty_id, subf.code subject_faculty_code, subf.name subject_faculty_name, subf.head subject_faculty_head,
           tu.id teacher_id, tu.username teacher_username, tu.password teacher_password, tu.name teacher_name, tu.status teacher_status,
           t.email teacher_email, t.phone teacher_phone, tf.id teacher_faculty_id, tf.code teacher_faculty_code, tf.name teacher_faculty_name, tf.head teacher_faculty_head
    FROM grades g
    JOIN students st ON st.user_id = g.student_id JOIN users su ON su.id = st.user_id
    JOIN faculties sf ON sf.id = st.faculty_id JOIN majors m ON m.id = st.major_id
    JOIN class_sections c ON c.id = g.class_section_id
    JOIN subjects sub ON sub.id = c.subject_id JOIN faculties subf ON subf.id = sub.faculty_id
    JOIN teachers t ON t.user_id = c.teacher_id JOIN users tu ON tu.id = t.user_id JOIN faculties tf ON tf.id = t.faculty_id
    WHERE (p_student_id IS NULL OR g.student_id = p_student_id)
      AND (p_class_section_id IS NULL OR g.class_section_id = p_class_section_id)
      AND (p_grade_id IS NULL OR g.id = p_grade_id)
    ORDER BY g.id;
END$$

CREATE PROCEDURE sp_get_grade_by_student_and_class(IN p_student_id INT, IN p_class_section_id INT)
BEGIN
    CALL sp_select_grades_base(p_student_id, p_class_section_id, NULL);
END$$

CREATE PROCEDURE sp_get_grades_by_student(IN p_student_id INT)
BEGIN
    CALL sp_select_grades_base(p_student_id, NULL, NULL);
END$$

CREATE PROCEDURE sp_get_grades_by_class(IN p_class_section_id INT)
BEGIN
    CALL sp_select_grades_base(NULL, p_class_section_id, NULL);
END$$

CREATE PROCEDURE sp_update_grade(
    IN p_id INT, IN p_attendance_score DOUBLE, IN p_midterm_score DOUBLE, IN p_final_score DOUBLE, IN p_semester VARCHAR(20)
)
BEGIN
    UPDATE grades SET attendance_score = p_attendance_score, midterm_score = p_midterm_score, final_score = p_final_score, semester = p_semester WHERE id = p_id;
END$$

CREATE PROCEDURE sp_add_grade(
    IN p_student_id INT, IN p_class_section_id INT, IN p_attendance_score DOUBLE, IN p_midterm_score DOUBLE,
    IN p_final_score DOUBLE, IN p_semester VARCHAR(20), OUT p_id INT
)
BEGIN
    INSERT INTO grades(student_id, class_section_id, attendance_score, midterm_score, final_score, semester)
    VALUES (p_student_id, p_class_section_id, p_attendance_score, p_midterm_score, p_final_score, p_semester);
    SET p_id = LAST_INSERT_ID();
END$$

CREATE PROCEDURE sp_ensure_grade_for_student_class(IN p_student_id INT, IN p_class_section_id INT)
BEGIN
    INSERT IGNORE INTO grades(student_id, class_section_id, attendance_score, midterm_score, final_score, semester)
    VALUES (p_student_id, p_class_section_id, 0, 0, 0, '2024-2');
END$$

CREATE PROCEDURE sp_get_all_tuitions()
BEGIN
    SELECT tu.id tuition_id, tu.semester, tu.registered_credits, tu.price_per_credit, tu.paid, tu.status tuition_status,
           u.id, u.username, u.password, u.name, u.status, st.mssv, st.dob, st.gender, st.address, st.email, st.phone,
           st.cohort, st.admin_class, st.student_status,
           f.id faculty_id, f.code faculty_code, f.name faculty_name, f.head,
           m.id major_id, m.code major_code, m.name major_name
    FROM tuitions tu JOIN students st ON st.user_id = tu.student_id JOIN users u ON u.id = st.user_id
    JOIN faculties f ON f.id = st.faculty_id JOIN majors m ON m.id = st.major_id
    ORDER BY tu.id;
END$$

CREATE PROCEDURE sp_get_tuitions_by_student(IN p_student_id INT)
BEGIN
    SELECT tu.id tuition_id, tu.semester, tu.registered_credits, tu.price_per_credit, tu.paid, tu.status tuition_status,
           u.id, u.username, u.password, u.name, u.status, st.mssv, st.dob, st.gender, st.address, st.email, st.phone,
           st.cohort, st.admin_class, st.student_status,
           f.id faculty_id, f.code faculty_code, f.name faculty_name, f.head,
           m.id major_id, m.code major_code, m.name major_name
    FROM tuitions tu JOIN students st ON st.user_id = tu.student_id JOIN users u ON u.id = st.user_id
    JOIN faculties f ON f.id = st.faculty_id JOIN majors m ON m.id = st.major_id
    WHERE tu.student_id = p_student_id
    ORDER BY tu.id;
END$$

CREATE PROCEDURE sp_update_tuition(
    IN p_id INT, IN p_semester VARCHAR(20), IN p_registered_credits INT, IN p_price_per_credit DOUBLE, IN p_paid DOUBLE, IN p_status VARCHAR(50)
)
BEGIN
    UPDATE tuitions SET semester = p_semester, registered_credits = p_registered_credits, price_per_credit = p_price_per_credit, paid = p_paid, status = p_status WHERE id = p_id;
END$$

CREATE PROCEDURE sp_apply_tuition_discount(IN p_id INT)
BEGIN
    UPDATE tuitions SET status = 'miễn giảm', paid = 0 WHERE id = p_id;
END$$

CREATE PROCEDURE sp_pay_tuition(IN p_id INT)
BEGIN
    UPDATE tuitions SET paid = registered_credits * price_per_credit, status = 'đã đóng' WHERE id = p_id;
END$$

DELIMITER ;

select * from subjects;
