package com.sms.test.unit;

import com.sms.dao.ClassSectionDAO;
import com.sms.entity.ClassSection;
import com.sms.entity.Subject;
import com.sms.entity.Teacher;
import org.junit.Assert;
import org.junit.Test;

public class ClassSectionDAOTest {

    private ClassSectionDAO dao = new ClassSectionDAO();

    @Test
    public void testUpdateClassValid() {
        ClassSection cs = new ClassSection();
        cs.setId(2);
        cs.setCode("CS002");

        Subject subject = new Subject();
        subject.setId(1);
        cs.setSubject(subject);

        Teacher teacher = new Teacher();
        teacher.setId(4);
        cs.setTeacher(teacher);

        cs.setCapacity(80);
        cs.setEnrolledCount(35);
        cs.setStatus("active");

        boolean result = dao.updateClass(cs);
        Assert.assertTrue("Lỗi: Cập nhật lớp học phần hợp lệ thất bại!", result);
    }

    @Test
    public void testUpdateClassInvalidId() {
        ClassSection cs = new ClassSection();
        cs.setId(9999);
        cs.setCode("CS9999");

        Subject subject = new Subject();
        subject.setId(1);
        cs.setSubject(subject);

        Teacher teacher = new Teacher();
        teacher.setId(1);
        cs.setTeacher(teacher);

        cs.setCapacity(50);
        cs.setEnrolledCount(10);
        cs.setStatus("active");

        boolean result = dao.updateClass(cs);
        Assert.assertFalse("Lỗi: Lớp không tồn tại nhưng hàm vẫn trả về true!", result);
    }
}
