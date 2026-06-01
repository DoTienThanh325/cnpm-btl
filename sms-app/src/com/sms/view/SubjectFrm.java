package com.sms.view;

import com.sms.dao.SubjectDAO;
import com.sms.dao.FacultyDAO;
import com.sms.dao.TextbookDAO;
import com.sms.entity.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.*;
import java.awt.*;
import java.util.List;

/**
 * SubjectFrm - per spec section C.3.3
 * Shows list of subjects, has "Tạo môn học mới" button → calls CreateSubjectFrm
 */
public class SubjectFrm extends JFrame {

    private User currentUser;
    private SubjectDAO subjectDAO = new SubjectDAO();
    private FacultyDAO facultyDAO = new FacultyDAO();
    private TextbookDAO textbookDAO = new TextbookDAO();

    private JTextField txtSearch;
    private JButton btnSearch, btnCreate, btnEdit, btnDelete, btnBack;
    private JTable tblSubjects;
    private DefaultTableModel tableModel;

    public SubjectFrm(User user) {
        this.currentUser = user;
        setTitle("Quản lý Môn học");
        setSize(800, 550);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        initComponents();
        loadAllSubjects();
    }

    private void initComponents() {
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        mainPanel.setBackground(new Color(236, 240, 241));

        // Top panel
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        topPanel.setBackground(new Color(236, 240, 241));
        topPanel.add(new JLabel("Tìm kiếm:"));
        txtSearch = new JTextField(18);
        topPanel.add(txtSearch);
        btnSearch = makeBtn("Tìm", new Color(41, 128, 185));
        topPanel.add(btnSearch);
        btnCreate = makeBtn("Tạo môn học mới", new Color(46, 204, 113));
        topPanel.add(btnCreate);
        btnEdit = makeBtn("Chỉnh sửa", new Color(230, 126, 34));
        topPanel.add(btnEdit);
        btnDelete = makeBtn("Xóa", new Color(231, 76, 60));
        topPanel.add(btnDelete);
        btnBack = makeBtn("Quay lại", new Color(127, 140, 141));
        topPanel.add(btnBack);

        // Table
        String[] cols = {"ID", "Mã môn", "Tên môn học", "Tín chỉ", "Khoa phụ trách", "Trạng thái"};
        tableModel = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tblSubjects = new JTable(tableModel);
        tblSubjects.setRowHeight(25);
        tblSubjects.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        tblSubjects.setFont(new Font("Arial", Font.PLAIN, 12));
        JScrollPane sp = new JScrollPane(tblSubjects);

        mainPanel.add(topPanel, BorderLayout.NORTH);
        mainPanel.add(sp, BorderLayout.CENTER);
        setContentPane(mainPanel);

        btnSearch.addActionListener(e -> {
            String kw = txtSearch.getText().trim();
            List<Subject> res = kw.isEmpty() ? subjectDAO.getAllSubjects() : subjectDAO.searchSubjects(kw);
            loadData(res);
        });

        // per spec: click "Tạo môn học mới" → CreateSubjectFrm
        btnCreate.addActionListener(e -> {
            new CreateSubjectFrm(this, subjectDAO, facultyDAO, textbookDAO).setVisible(true);
        });

        btnEdit.addActionListener(e -> {
            int row = tblSubjects.getSelectedRow();
            if (row < 0) { JOptionPane.showMessageDialog(this, "Chọn môn học cần chỉnh sửa."); return; }
            int id = (int) tableModel.getValueAt(row, 0);
            Subject s = subjectDAO.getById(id);
            new CreateSubjectFrm(this, s, subjectDAO, facultyDAO, textbookDAO).setVisible(true);
        });

        btnDelete.addActionListener(e -> {
            int row = tblSubjects.getSelectedRow();
            if (row < 0) { JOptionPane.showMessageDialog(this, "Chọn môn học cần xóa."); return; }
            int id = (int) tableModel.getValueAt(row, 0);
            int confirm = JOptionPane.showConfirmDialog(this, "Xóa môn học này?", "Xác nhận", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                subjectDAO.deleteSubject(id);
                loadAllSubjects();
            }
        });

        btnBack.addActionListener(e -> dispose());
    }

    public void loadAllSubjects() {
        loadData(subjectDAO.getAllSubjects());
    }

    private void loadData(List<Subject> subjects) {
        tableModel.setRowCount(0);
        for (Subject s : subjects) {
            tableModel.addRow(new Object[]{
                s.getId(), s.getCode(), s.getName(), s.getCredits(),
                s.getFaculty() != null ? s.getFaculty().getName() : "", s.getStatus()
            });
        }
    }

    private JButton makeBtn(String text, Color color) {
        JButton btn = new JButton(text);
        btn.setBackground(color);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setFont(new Font("Arial", Font.PLAIN, 12));
        return btn;
    }
}
