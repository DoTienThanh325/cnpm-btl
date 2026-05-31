package com.sms.test.unit;

import com.sms.dao.SubjectDAO;
import com.sms.entity.Faculty;
import com.sms.entity.Subject;
import com.sms.entity.Textbook;
import org.junit.Assert;
import org.junit.Test;

public class SubjectDAOTest {
    SubjectDAO sd = new SubjectDAO();

    @Test
    public void testCreateSubjectSuccess() {
        String code = "TEST" + System.currentTimeMillis();
        Subject subject = createSubject(code);

        boolean result = sd.createSubject(subject);

        Assert.assertTrue(result);
        Assert.assertTrue(subject.getId() > 0);

        Subject savedSubject = sd.getById(subject.getId());
        Assert.assertNotNull(savedSubject);
        Assert.assertEquals(code, savedSubject.getCode());
        Assert.assertEquals("Mon hoc kiem thu", savedSubject.getName());
        Assert.assertEquals(3, savedSubject.getCredits());
        Assert.assertEquals("active", savedSubject.getStatus());
        Assert.assertEquals(1, savedSubject.getFaculty().getId());
        Assert.assertEquals(1, savedSubject.getTextbooks().size());
    }

    @Test
    public void testCreateSubjectDuplicateCode() {
        String code = "DUP" + System.currentTimeMillis();
        Subject subject = createSubject(code);
        Subject duplicateSubject = createSubject(code);

        boolean result = sd.createSubject(subject);
        boolean duplicateResult = sd.createSubject(duplicateSubject);

        Assert.assertTrue(result);
        Assert.assertFalse(duplicateResult);
    }

    private Subject createSubject(String code) {
        Faculty faculty = new Faculty(1, "CNTT", "Cong nghe Thong tin", "PGS. TS. Nguyen Van X");
        Subject subject = new Subject(0, code, "Mon hoc kiem thu", 3,
                "Noi dung mon hoc kiem thu", faculty, "active");
        subject.getTextbooks().add(new Textbook(1, "Nhap mon Cong nghe phan mem", "Nguyen Van A", 2020));
        return subject;
    }
}
