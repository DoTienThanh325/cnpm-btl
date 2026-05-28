package com.sms.view;

import com.sms.dao.ClassSectionDAO;
import com.sms.entity.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.*;
import java.awt.*;
import java.util.List;

/**
 * ManageClassFrm - per spec section C.3.1 and C.3.4
 * Has buttons: Xem danh sách lớp, Mở lớp mới, Cập nhật lớp, Hủy lớp
 */
public class ManageClassFrm extends JFrame {

    private User currentUser;
    private ClassSectionDAO classDAO = new ClassSectionDAO();

    private JTable tblClasses;
    private DefaultTableModel tableModel;
    private JButton btnOpenNew, btnUpdate, btnCancel, btnBack;

    public ManageClassFrm(User user) {
        this.currentUser = user;
        setTitle("Quản lý Lớp học phần");
        setSize(850, 550);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        initComponents();
        loadData(classDAO.getAllClassSections());
    }

    private void initComponents() {
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        mainPanel.setBackground(new Color(236, 240, 241));

        // Button bar per spec: mở lớp mới, cập nhật lớp, hủy lớp
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        topPanel.setBackground(new Color(236, 240, 241));
        btnOpenNew = makeBtn("Mở lớp học phần mới", new Color(46, 204, 113));
        btnUpdate = makeBtn("Cập nhật lớp", new Color(230, 126, 34));
        btnCancel = makeBtn("Hủy lớp", new Color(231, 76, 60));
        btnBack = makeBtn("Quay lại", new Color(127, 140, 141));
        topPanel.add(btnOpenNew);
        topPanel.add(btnUpdate);
        topPanel.add(btnCancel);
        topPanel.add(btnBack);

        // Table: id, mã lớp, môn học, giảng viên, sĩ số - per spec scenario
        String[] cols = {"ID", "Mã lớp", "Môn học", "Giảng viên", "Sĩ số tối đa", "Đã đăng ký", "Trạng thái"};
        tableModel = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tblClasses = new JTable(tableModel);
        tblClasses.setRowHeight(25);
        tblClasses.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        tblClasses.setFont(new Font("Arial", Font.PLAIN, 12));
        JScrollPane sp = new JScrollPane(tblClasses);

        mainPanel.add(topPanel, BorderLayout.NORTH);
        mainPanel.add(sp, BorderLayout.CENTER);
        setContentPane(mainPanel);

        btnOpenNew.addActionListener(e -> {
            JOptionPane.showMessageDialog(this, "Chức năng 'Mở lớp mới' không có trong module này.", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
        });

        // per spec: click Cập nhật lớp → SearchClassFrm
        btnUpdate.addActionListener(e -> {
            new SearchClassFrm(this, classDAO, "UPDATE").setVisible(true);
        });

        btnCancel.addActionListener(e -> {
            int row = tblClasses.getSelectedRow();
            if (row < 0) { JOptionPane.showMessageDialog(this, "Chọn lớp cần hủy."); return; }
            int id = (int) tableModel.getValueAt(row, 0);
            int confirm = JOptionPane.showConfirmDialog(this, "Hủy lớp học phần này?", "Xác nhận", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                classDAO.cancelClass(id);
                loadData(classDAO.getAllClassSections());
                JOptionPane.showMessageDialog(this, "Đã hủy lớp học phần.", "Thành công", JOptionPane.INFORMATION_MESSAGE);
            }
        });

        btnBack.addActionListener(e -> dispose());
    }

    public void loadData(List<ClassSection> classes) {
        tableModel.setRowCount(0);
        for (ClassSection c : classes) {
            tableModel.addRow(new Object[]{
                c.getId(), c.getCode(),
                c.getSubject() != null ? c.getSubject().getName() : "",
                c.getTeacher() != null ? c.getTeacher().getName() : "",
                c.getCapacity(), c.getEnrolledCount(), c.getStatus()
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
