package com.sms.test.unit;

import com.sms.dao.ClassSectionDAO;
import com.sms.dao.SessionDAO;
import com.sms.entity.*;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;

public class ClassSectionDAOTest {

    ClassSectionDAO dao = new ClassSectionDAO();
    SessionDAO sessionDAO = new SessionDAO();

    // TC-CS-01: Mở lớp học phần với dữ liệu hợp lệ (giảng viên không trùng lịch)
    @Test
    public void testCreateClassSuccess() {
        String code = "CS" + System.currentTimeMillis();
        ClassSection classSection = buildValidClassSection(code);

        // Tiền điều kiện: giảng viên không bị trùng lịch với session được chọn
        boolean hasConflict = dao.checkSchedule(classSection.getTeacher(), classSection.getSessions());
        Assert.assertFalse("Giang vien khong duoc trung lich", hasConflict);

        boolean result = dao.createClass(classSection);

        Assert.assertTrue("Tao lop hoc phan thanh cong", result);
        Assert.assertTrue("ID lop hoc phan phai duoc sinh ra", classSection.getId() > 0);

        ClassSection saved = dao.getById(classSection.getId());
        Assert.assertNotNull("Lop hoc phan phai ton tai trong CSDL", saved);
        Assert.assertEquals(code, saved.getCode());
        Assert.assertEquals(1, saved.getSubject().getId());
        Assert.assertEquals(1, saved.getTeacher().getId());
        Assert.assertEquals(40, saved.getCapacity());
        Assert.assertEquals("active", saved.getStatus());
        Assert.assertFalse("Lop hoc phan phai co it nhat mot buoi hoc", saved.getSessions().isEmpty());
    }

    private ClassSection buildValidClassSection(String code) {
        Faculty faculty = new Faculty(1, "CNTT", "Cong nghe Thong tin", "PGS. TS. Nguyen Van X");

        Subject subject = new Subject(1, "INT1001", "Nhap mon lap trinh", 3,
                "Noi dung co ban", faculty, "active");

        Teacher teacher = new Teacher(1, "teacher01", "", "Nguyen Van A", "active",
                faculty, "teacher01@sms.edu.vn", "0901000001");

        // Lay session co san tu CSDL (session ID = 1, kiem tra truoc bang checkSchedule)
        List<Session> allSessions = sessionDAO.getAllSession();
        Assert.assertFalse("Phai co it nhat mot session trong CSDL", allSessions.isEmpty());
        Session session = allSessions.get(0);

        ClassSection cs = new ClassSection(0, code, subject, teacher, 40, 0, "active");
        cs.getSessions().add(session);
        return cs;
    }
}
