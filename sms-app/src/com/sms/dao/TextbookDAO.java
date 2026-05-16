package com.sms.dao;

import com.sms.entity.Textbook;
import java.util.ArrayList;
import java.util.List;

public class TextbookDAO {
    private static List<Textbook> textbooks = new ArrayList<>();
    private static int nextId = 5;

    static {
        textbooks.add(new Textbook(1, "Nhập môn Công nghệ phần mềm", "Nguyễn Văn A", 2020));
        textbooks.add(new Textbook(2, "Giải tích 1", "Trần Thị B", 2019));
        textbooks.add(new Textbook(3, "Lập trình Java", "Lê Văn C", 2021));
        textbooks.add(new Textbook(4, "Cơ sở dữ liệu", "Phạm Thị D", 2022));
    }

    public List<Textbook> getAllTextbooks() {
        return new ArrayList<>(textbooks);
    }

    public Textbook getById(int id) {
        for (Textbook t : textbooks) if (t.getId() == id) return t;
        return null;
    }
}
