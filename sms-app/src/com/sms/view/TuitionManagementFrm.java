package com.sms.view;

import com.sms.dao.StudentDAO;
import com.sms.dao.TuitionDAO;
import com.sms.entity.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.*;
import java.awt.*;
import java.util.List;

/**
 * TuitionManagementFrm - per spec: PDT manages tuition
 * Tìm sinh viên, xem học phí, tính học phí theo tín chỉ, miễn giảm
 */
public class TuitionManagementFrm extends JFrame {

    private User currentUser;
    private StudentDAO studentDAO = new StudentDAO();
    private TuitionDAO tuitionDAO = new TuitionDAO();

    private JTextField txtSearch;
    private JButton btnSearch, btnDiscount, btnBack;
    private JTable tblTuition;
    private DefaultTableModel tableModel;
    private List<Tuition> currentTuitions;

    public TuitionManagementFrm(User user) {
        this.currentUser = user;
        setTitle("Quản lý Học phí");
        setSize(850, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        initComponents();
        loadAll();
    }

    private void initComponents() {
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        mainPanel.setBackground(new Color(236, 240, 241));

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        topPanel.setBackground(new Color(236, 240, 241));
        topPanel.add(new JLabel("Tìm sinh viên (tên/MSSV):"));
        txtSearch = new JTextField(18);
        topPanel.add(txtSearch);
        btnSearch = makeBtn("Tìm kiếm", new Color(41, 128, 185));
        topPanel.add(btnSearch);
        btnDiscount = makeBtn("Miễn giảm học phí", new Color(155, 89, 182));
        topPanel.add(btnDiscount);
        btnBack = makeBtn("Quay lại", new Color(127, 140, 141));
        topPanel.add(btnBack);

        // per spec fields: mã SV, học kỳ, số TC, đơn giá, tổng HP, đã đóng, trạng thái
        String[] cols = {"ID", "MSSV", "Họ tên", "Học kỳ", "Số TC", "Đơn giá/TC (VNĐ)", "Tổng HP (VNĐ)", "Đã đóng (VNĐ)", "Trạng thái"};
        tableModel = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tblTuition = new JTable(tableModel);
        tblTuition.setRowHeight(25);
        tblTuition.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        tblTuition.setFont(new Font("Arial", Font.PLAIN, 12));
        JScrollPane sp = new JScrollPane(tblTuition);

        mainPanel.add(topPanel, BorderLayout.NORTH);
        mainPanel.add(sp, BorderLayout.CENTER);
        setContentPane(mainPanel);

        btnSearch.addActionListener(e -> {
            String kw = txtSearch.getText().trim();
            if (kw.isEmpty()) { loadAll(); return; }
            List<Student> students = studentDAO.searchStudents(kw);
            tableModel.setRowCount(0);
            currentTuitions = new java.util.ArrayList<>();
            for (Student s : students) {
                List<Tuition> t = tuitionDAO.getByStudent(s.getId());
                currentTuitions.addAll(t);
                for (Tuition tuition : t) loadRow(tuition, s);
            }
        });

        btnDiscount.addActionListener(e -> {
            int row = tblTuition.getSelectedRow();
            if (row < 0) { JOptionPane.showMessageDialog(this, "Chọn bản ghi học phí cần miễn giảm."); return; }
            int id = (int) tableModel.getValueAt(row, 0);
            int confirm = JOptionPane.showConfirmDialog(this, "Áp dụng miễn giảm học phí?", "Xác nhận", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                tuitionDAO.applyDiscount(id, "Miễn giảm theo quyết định");
                loadAll();
                JOptionPane.showMessageDialog(this, "Đã áp dụng miễn giảm học phí.", "Thành công", JOptionPane.INFORMATION_MESSAGE);
            }
        });

        btnBack.addActionListener(e -> dispose());
    }

    private void loadAll() {
        tableModel.setRowCount(0);
        currentTuitions = tuitionDAO.getAllTuitions();
        for (Tuition t : currentTuitions) {
            loadRow(t, t.getStudent());
        }
    }

    private void loadRow(Tuition t, Student s) {
        tableModel.addRow(new Object[]{
            t.getId(),
            s != null ? s.getMssv() : "",
            s != null ? s.getName() : "",
            t.getSemester(),
            t.getRegisteredCredits(),
            String.format("%,.0f", t.getPricePerCredit()),
            String.format("%,.0f", t.getTotalFee()),
            String.format("%,.0f", t.getPaid()),
            t.getStatus()
        });
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
