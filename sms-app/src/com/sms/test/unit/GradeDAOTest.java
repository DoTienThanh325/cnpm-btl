package com.sms.test.unit;

import com.sms.dao.GradeDAO;
import com.sms.entity.ClassSection;
import com.sms.entity.Grade;
import com.sms.entity.Student;
import org.junit.Assert;
import org.junit.Test;

public class GradeDAOTest {

    GradeDAO dao = new GradeDAO();

    @Test
    public void testSuaDiemHopLe() {
        Student s = new Student(); 
        s.setId(5); 

        ClassSection cs = new ClassSection(1, "INT1234", null, null, 60, 40, "active"); 
        cs.setId(1); 

        Grade g = new Grade(1, s, cs, 9.0, 8.0, 9.0, "2024-2");

        boolean ketQua = dao.updateGrade(g);

        Assert.assertTrue(ketQua);
    }

    @Test
    public void testSuaDiemKhongHopLe() {
        Student s = new Student(); 
        s.setId(5);

        ClassSection cs = new ClassSection(1, "INT1234", null, null, 60, 40, "active"); 
        cs.setId(1);

        Grade g = new Grade(1, s, cs, 11.0, 8.0, 9.0, "2024-2");

        boolean ketQua = dao.updateGrade(g);

        Assert.assertFalse(ketQua); 
    }
}