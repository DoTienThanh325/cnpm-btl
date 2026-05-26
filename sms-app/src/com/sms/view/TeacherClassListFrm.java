package com.sms.view;

import com.sms.dao.ClassSectionDAO;
import com.sms.entity.ClassSection;
import com.sms.entity.User;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.*;
import java.awt.*;
import java.util.List;

/**
 * TeacherClassListFrm - per spec: Giảng viên xem danh sách lớp đang giảng dạy
 */
public class TeacherClassListFrm extends JFrame {

    private User currentUser;
    private ClassSectionDAO classDAO = new ClassSectionDAO();

    public TeacherClassListFrm(User user) {
        this.currentUser = user;
        setTitle("Danh sách lớp đang giảng dạy");
        setSize(750, 420);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        initComponents();
    }

    private void initComponents() {
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        mainPanel.setBackground(new Color(236, 240, 241));

        JLabel lblTitle = new JLabel("Danh sách lớp giảng dạy của: " + currentUser.getName());
        lblTitle.setFont(new Font("Arial", Font.BOLD, 13));

        String[] cols = {"Mã lớp", "Môn học", "Sĩ số tối đa", "Đã đăng ký", "Lịch học", "Trạng thái"};
        DefaultTableModel model = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable table = new JTable(model);
        table.setRowHeight(25);
        table.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        table.setFont(new Font("Arial", Font.PLAIN, 12));

        List<ClassSection> classes = classDAO.getByTeacher(currentUser.getId());
        for (ClassSection c : classes) {
            // String sessions = c.getSessions().isEmpty() ? "" : c.getSessions().get(0).toString();
            model.addRow(new Object[]{
                c.getCode(),
                c.getSubject() != null ? c.getSubject().getName() : "",
                c.getCapacity(), c.getEnrolledCount(), c.getStatus()
            });
        }

        JButton btnBack = new JButton("Quay lại");
        btnBack.setBackground(new Color(127, 140, 141));
        btnBack.setForeground(Color.WHITE);
        btnBack.setFocusPainted(false);
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        bottomPanel.add(btnBack);

        mainPanel.add(lblTitle, BorderLayout.NORTH);
        mainPanel.add(new JScrollPane(table), BorderLayout.CENTER);
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);
        setContentPane(mainPanel);

        btnBack.addActionListener(e -> dispose());
    }
}
