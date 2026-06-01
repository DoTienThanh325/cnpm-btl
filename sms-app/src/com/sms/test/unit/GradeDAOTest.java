package com.sms.test.unit;

import com.sms.dao.GradeDAO;
import com.sms.entity.ClassSection;
import com.sms.entity.Grade;
import com.sms.entity.Student;
import org.junit.Assert;
import org.junit.Test;

public class GradeDAOTest {

    GradeDAO dao = new GradeDAO();

    // TEST 1: Sửa điểm thành 9.0 (Hợp lệ -> Mong đợi lưu THÀNH CÔNG)
    @Test
    public void testSuaDiemHopLe() {
        // 1. Tạo đối tượng Sinh viên (Ánh xạ đúng user_id = 5 trong DB)
        Student s = new Student(); 
        s.setId(5); 

        // 2. Tạo đối tượng Lớp môn học (Ánh xạ đúng class_section_id = 1)
        ClassSection cs = new ClassSection(1, "INT1234", null, null, 60, 40, "active"); 
        cs.setId(1); 
        // (Nếu ClassSection không có hàm set trống, bạn dùng: new ClassSection(1, "INT1234", null, null, 60, 40, "active"))

        // 3. Tạo đối tượng Điểm (Ánh xạ đúng bản ghi grades id = 1)
        // Đổi điểm chuyên cần từ 10.0 -> 9.0
        Grade g = new Grade(1, s, cs, 9.0, 8.0, 9.0, "2024-2");

        // 4. Gọi hàm lưu vào DB
        boolean ketQua = dao.updateGrade(g);

        // 5. Kiểm tra kết quả
        Assert.assertTrue(ketQua);
    }

    // TEST 2: Sửa điểm thành 11.0 (Không hợp lệ -> Mong đợi lưu THẤT BẠI)
    @Test
    public void testSuaDiemKhongHopLe() {
        Student s = new Student(); 
        s.setId(5);

        ClassSection cs = new ClassSection(1, "INT1234", null, null, 60, 40, "active"); 
        cs.setId(1);

        // Tạo dữ liệu điểm chuyên cần = 11.0 (Vượt quá 10.0)
        Grade g = new Grade(1, s, cs, 11.0, 8.0, 9.0, "2024-2");

        // Gọi hàm lưu vào DB
        boolean ketQua = dao.updateGrade(g);

        // Kiểm tra kết quả (Phải trả về false do bị chặn)
        Assert.assertFalse(ketQua); 
    }
}