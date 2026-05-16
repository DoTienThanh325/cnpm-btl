package com.sms.dao;

import com.sms.entity.Student;
import com.sms.entity.Tuition;
import java.util.*;

public class TuitionDAO {
    private static List<Tuition> tuitions = new ArrayList<>();
    private static int nextId = 6;

    static {
        StudentDAO sDao = new StudentDAO();
        Student sv01 = sDao.getById(5);
        Student sv02 = sDao.getById(6);
        Student sv03 = sDao.getById(7);
        Student sv04 = sDao.getById(8);

        tuitions.add(new Tuition(1, sv01, "2024-2", 15, 850000, 12750000, "đã đóng"));
        tuitions.add(new Tuition(2, sv02, "2024-2", 12, 850000, 0, "chưa đóng"));
        tuitions.add(new Tuition(3, sv03, "2024-2", 18, 850000, 15300000, "đã đóng"));
        tuitions.add(new Tuition(4, sv04, "2024-2", 14, 850000, 0, "miễn giảm"));
    }

    public List<Tuition> getAllTuitions() {
        return new ArrayList<>(tuitions);
    }

    public List<Tuition> getByStudent(int studentId) {
        List<Tuition> result = new ArrayList<>();
        for (Tuition t : tuitions) {
            if (t.getStudent().getId() == studentId) result.add(t);
        }
        return result;
    }

    public boolean updateTuition(Tuition tuition) {
        for (int i = 0; i < tuitions.size(); i++) {
            if (tuitions.get(i).getId() == tuition.getId()) {
                tuitions.set(i, tuition);
                return true;
            }
        }
        return false;
    }

    // miễn giảm per spec
    public boolean applyDiscount(int tuitionId, String reason) {
        for (Tuition t : tuitions) {
            if (t.getId() == tuitionId) {
                t.setStatus("miễn giảm");
                t.setPaid(0);
                return true;
            }
        }
        return false;
    }

    // đóng học phí per spec
    public boolean payTuition(int tuitionId) {
        for (Tuition t : tuitions) {
            if (t.getId() == tuitionId) {
                t.setPaid(t.getTotalFee());
                t.setStatus("đã đóng");
                return true;
            }
        }
        return false;
    }
}
