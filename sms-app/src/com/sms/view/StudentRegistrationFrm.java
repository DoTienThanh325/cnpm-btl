package com.sms.view;

import com.sms.dao.ClassSectionDAO;
import com.sms.entity.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.*;
import java.awt.*;
import java.util.List;

/**
 * StudentRegistrationFrm - per spec: sinh viên duyệt khóa sẵn có, đăng ký, hủy
 */
public class StudentRegistrationFrm extends JFrame {

    private Student student;
    private ClassSectionDAO classDAO = new ClassSectionDAO();

    private JTable tblAvailable, tblEnrolled;
    private DefaultTableModel availModel, enrolledModel;
    private JButton btnEnroll, btnCancel, btnBack;

    public StudentRegistrationFrm(Student student) {
        this.student = student;
        setTitle("Đăng ký môn học - " + student.getName());
        setSize(900, 580);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        initComponents();
        loadData();
    }

    private void initComponents() {
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        mainPanel.setBackground(new Color(236, 240, 241));

        // Available classes
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(new Color(236, 240, 241));
        JLabel lblAvail = new JLabel("Các lớp học phần sẵn có:");
        lblAvail.setFont(new Font("Arial", Font.BOLD, 12));
        String[] availCols = {"ID", "Mã lớp", "Môn học", "Giảng viên", "Còn chỗ", "Trạng thái"};
        availModel = new DefaultTableModel(availCols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tblAvailable = new JTable(availModel);
        tblAvailable.setRowHeight(25);
        tblAvailable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        topPanel.add(lblAvail, BorderLayout.NORTH);
        topPanel.add(new JScrollPane(tblAvailable), BorderLayout.CENTER);

        // Buttons
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 5));
        btnPanel.setBackground(new Color(236, 240, 241));
        btnEnroll = new JButton("Đăng ký ↓");
        btnEnroll.setBackground(new Color(46, 204, 113));
        btnEnroll.setForeground(Color.WHITE);
        btnEnroll.setFocusPainted(false);
        btnCancel = new JButton("Hủy đăng ký ↑");
        btnCancel.setBackground(new Color(231, 76, 60));
        btnCancel.setForeground(Color.WHITE);
        btnCancel.setFocusPainted(false);
        btnBack = new JButton("Quay lại");
        btnBack.setBackground(new Color(127, 140, 141));
        btnBack.setForeground(Color.WHITE);
        btnBack.setFocusPainted(false);
        btnPanel.add(btnEnroll);
        btnPanel.add(btnCancel);
        btnPanel.add(btnBack);

        // Enrolled classes
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setBackground(new Color(236, 240, 241));
        JLabel lblEnrolled = new JLabel("Lớp học phần đã đăng ký:");
        lblEnrolled.setFont(new Font("Arial", Font.BOLD, 12));
        String[] enrolledCols = {"Mã lớp", "Môn học", "Giảng viên", "Sĩ số"};
        enrolledModel = new DefaultTableModel(enrolledCols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tblEnrolled = new JTable(enrolledModel);
        tblEnrolled.setRowHeight(25);
        bottomPanel.add(lblEnrolled, BorderLayout.NORTH);
        bottomPanel.add(new JScrollPane(tblEnrolled), BorderLayout.CENTER);

        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, topPanel, bottomPanel);
        splitPane.setDividerLocation(230);

        mainPanel.add(splitPane, BorderLayout.CENTER);
        mainPanel.add(btnPanel, BorderLayout.SOUTH);
        setContentPane(mainPanel);

        btnEnroll.addActionListener(e -> {
            int row = tblAvailable.getSelectedRow();
            if (row < 0) { JOptionPane.showMessageDialog(this, "Chọn lớp cần đăng ký."); return; }
            int id = (int) availModel.getValueAt(row, 0);
            // per spec: hệ thống kiểm tra số lượng SV tối đa
            boolean ok = classDAO.enrollStudent(student.getId(), id);
            if (ok) {
                JOptionPane.showMessageDialog(this, "Đăng ký thành công!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
                loadData();
            } else {
                JOptionPane.showMessageDialog(this, "Đăng ký thất bại! Lớp đã đầy hoặc đã đăng ký.", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        });

        btnCancel.addActionListener(e -> {
            int row = tblEnrolled.getSelectedRow();
            if (row < 0) { JOptionPane.showMessageDialog(this, "Chọn lớp cần hủy đăng ký."); return; }
            // find class section id from code
            String code = (String) enrolledModel.getValueAt(row, 0);
            List<ClassSection> enrolled = classDAO.getByStudent(student.getId());
            for (ClassSection c : enrolled) {
                if (c.getCode().equals(code)) {
                    classDAO.cancelEnrollment(student.getId(), c.getId());
                    JOptionPane.showMessageDialog(this, "Đã hủy đăng ký lớp " + code, "Thành công", JOptionPane.INFORMATION_MESSAGE);
                    loadData();
                    return;
                }
            }
        });

        btnBack.addActionListener(e -> dispose());
    }

    private void loadData() {
        // available classes
        availModel.setRowCount(0);
        List<ClassSection> enrolled = classDAO.getByStudent(student.getId());
        for (ClassSection c : classDAO.getAllClassSections()) {
            boolean alreadyIn = enrolled.stream().anyMatch(e -> e.getId() == c.getId());
            if (!alreadyIn && "active".equals(c.getStatus())) {
                int available = c.getCapacity() - c.getEnrolledCount();
                availModel.addRow(new Object[]{
                    c.getId(), c.getCode(),
                    c.getSubject() != null ? c.getSubject().getName() : "",
                    c.getTeacher() != null ? c.getTeacher().getName() : "",
                    available, c.getStatus()
                });
            }
        }
        // enrolled classes
        enrolledModel.setRowCount(0);
        for (ClassSection c : enrolled) {
            enrolledModel.addRow(new Object[]{
                c.getCode(),
                c.getSubject() != null ? c.getSubject().getName() : "",
                c.getTeacher() != null ? c.getTeacher().getName() : "",
                c.getCapacity()
            });
        }
    }
}
