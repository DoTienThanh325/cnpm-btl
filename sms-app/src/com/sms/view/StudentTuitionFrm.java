package com.sms.view;

import com.sms.dao.TuitionDAO;
import com.sms.entity.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.*;
import java.awt.*;
import java.util.List;

/**
 * StudentTuitionFrm - per spec: sinh viên xem học phí và đóng học phí
 */
public class StudentTuitionFrm extends JFrame {

    private Student student;
    private TuitionDAO tuitionDAO = new TuitionDAO();

    private DefaultTableModel tableModel;
    private List<Tuition> tuitions;

    public StudentTuitionFrm(Student student) {
        this.student = student;
        setTitle("Học phí - " + student.getName());
        setSize(750, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        initComponents();
        loadData();
    }

    private void initComponents() {
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        mainPanel.setBackground(new Color(236, 240, 241));

        JLabel lblTitle = new JLabel("Thông tin học phí của: " + student.getName() + " - " + student.getMssv());
        lblTitle.setFont(new Font("Arial", Font.BOLD, 13));

        // per spec fields: học kỳ, số TC, đơn giá, tổng HP, đã đóng, trạng thái
        String[] cols = {"Học kỳ", "Số TC đăng ký", "Đơn giá/TC (VNĐ)", "Tổng học phí (VNĐ)", "Đã đóng (VNĐ)", "Trạng thái"};
        tableModel = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable table = new JTable(tableModel);
        table.setRowHeight(28);
        table.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        table.setFont(new Font("Arial", Font.PLAIN, 12));
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // per spec: nút Đóng học phí
        JButton btnPay = new JButton("Đóng học phí");
        btnPay.setBackground(new Color(46, 204, 113));
        btnPay.setForeground(Color.WHITE);
        btnPay.setFocusPainted(false);
        btnPay.setFont(new Font("Arial", Font.BOLD, 13));
        JButton btnBack = new JButton("Quay lại");
        btnBack.setBackground(new Color(127, 140, 141));
        btnBack.setForeground(Color.WHITE);
        btnBack.setFocusPainted(false);
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 5));
        bottomPanel.setBackground(new Color(236, 240, 241));
        bottomPanel.add(btnPay);
        bottomPanel.add(btnBack);

        mainPanel.add(lblTitle, BorderLayout.NORTH);
        mainPanel.add(new JScrollPane(table), BorderLayout.CENTER);
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);
        setContentPane(mainPanel);

        btnPay.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row < 0 || tuitions == null) { JOptionPane.showMessageDialog(this, "Chọn học kỳ cần đóng học phí."); return; }
            Tuition t = tuitions.get(row);
            if ("đã đóng".equals(t.getStatus())) {
                JOptionPane.showMessageDialog(this, "Học phí học kỳ này đã được đóng.", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            if ("miễn giảm".equals(t.getStatus())) {
                JOptionPane.showMessageDialog(this, "Học phí học kỳ này được miễn giảm.", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            int confirm = JOptionPane.showConfirmDialog(this,
                    "Đóng học phí " + String.format("%,.0f VNĐ", t.getTotalFee()) + " cho học kỳ " + t.getSemester() + "?",
                    "Xác nhận đóng học phí", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                // per spec: sinh viên đóng học phí
                tuitionDAO.payTuition(t.getId());
                loadData();
                JOptionPane.showMessageDialog(this, "Đóng học phí thành công!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
            }
        });

        btnBack.addActionListener(e -> dispose());
    }

    private void loadData() {
        tableModel.setRowCount(0);
        tuitions = tuitionDAO.getByStudent(student.getId());
        for (Tuition t : tuitions) {
            tableModel.addRow(new Object[]{
                t.getSemester(), t.getRegisteredCredits(),
                String.format("%,.0f", t.getPricePerCredit()),
                String.format("%,.0f", t.getTotalFee()),
                String.format("%,.0f", t.getPaid()),
                t.getStatus()
            });
        }
    }
}
