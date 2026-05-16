package com.sms.view;

import com.sms.dao.UserDAO;
import com.sms.entity.User;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.*;
import java.awt.*;
import java.util.List;

/**
 * AdminHomeFrm - per spec: quản lý tài khoản người dùng, phân quyền, cấu hình hệ thống
 */
public class AdminHomeFrm extends JFrame {

    private User currentUser;
    private UserDAO userDAO = new UserDAO();

    private JTextField txtSearch;
    private JButton btnSearch, btnAdd, btnDelete, btnChangeRole, btnLogout;
    private JTable tblUsers;
    private DefaultTableModel tableModel;

    public AdminHomeFrm(User user) {
        this.currentUser = user;
        setTitle("Admin - Quản lý tài khoản");
        setSize(800, 520);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        initComponents();
        loadAll();
    }

    private void initComponents() {
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBackground(new Color(236, 240, 241));
        mainPanel.setBorder(new EmptyBorder(0, 0, 0, 0));

        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(44, 62, 80));
        headerPanel.setBorder(new EmptyBorder(15, 20, 15, 20));
        JLabel lblTitle = new JLabel("ADMIN - QUẢN LÝ TÀI KHOẢN NGƯỜI DÙNG");
        lblTitle.setFont(new Font("Arial", Font.BOLD, 15));
        lblTitle.setForeground(Color.WHITE);
        JLabel lblUser = new JLabel("Xin chào: " + currentUser.getName());
        lblUser.setFont(new Font("Arial", Font.PLAIN, 12));
        lblUser.setForeground(Color.WHITE);
        headerPanel.add(lblTitle, BorderLayout.WEST);
        headerPanel.add(lblUser, BorderLayout.EAST);

        // Toolbar
        JPanel toolPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 8));
        toolPanel.setBackground(new Color(236, 240, 241));
        toolPanel.setBorder(new EmptyBorder(5, 10, 5, 10));
        toolPanel.add(new JLabel("Tìm kiếm (tên/ID):"));
        txtSearch = new JTextField(18);
        toolPanel.add(txtSearch);
        btnSearch = makeBtn("Tìm", new Color(41, 128, 185));
        toolPanel.add(btnSearch);
        btnAdd = makeBtn("Thêm tài khoản", new Color(46, 204, 113));
        toolPanel.add(btnAdd);
        btnDelete = makeBtn("Xóa", new Color(231, 76, 60));
        toolPanel.add(btnDelete);
        btnChangeRole = makeBtn("Phân quyền", new Color(155, 89, 182));
        toolPanel.add(btnChangeRole);
        btnLogout = makeBtn("Đăng xuất", new Color(127, 140, 141));
        toolPanel.add(btnLogout);

        // Table - per spec fields: mã tài khoản, tên đăng nhập, vai trò, trạng thái
        String[] cols = {"ID", "Tên đăng nhập", "Họ tên", "Vai trò", "Trạng thái"};
        tableModel = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tblUsers = new JTable(tableModel);
        tblUsers.setRowHeight(25);
        tblUsers.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        tblUsers.setFont(new Font("Arial", Font.PLAIN, 12));
        tblUsers.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        mainPanel.add(headerPanel, BorderLayout.NORTH);
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.add(toolPanel, BorderLayout.NORTH);
        centerPanel.add(new JScrollPane(tblUsers), BorderLayout.CENTER);
        mainPanel.add(centerPanel, BorderLayout.CENTER);
        setContentPane(mainPanel);

        // per spec: tìm kiếm theo tên/ID
        btnSearch.addActionListener(e -> {
            String kw = txtSearch.getText().trim();
            if (kw.isEmpty()) { loadAll(); return; }
            loadData(userDAO.searchUsers(kw));
        });

        // per spec: thêm tài khoản
        btnAdd.addActionListener(e -> {
            String username = JOptionPane.showInputDialog(this, "Tên đăng nhập:");
            if (username == null || username.trim().isEmpty()) return;
            String password = JOptionPane.showInputDialog(this, "Mật khẩu:");
            if (password == null || password.trim().isEmpty()) return;
            String name = JOptionPane.showInputDialog(this, "Họ tên:");
            if (name == null || name.trim().isEmpty()) return;
            String[] roles = {"STUDENT", "TEACHER", "PDT", "ADMIN"};
            String role = (String) JOptionPane.showInputDialog(this, "Vai trò:", "Chọn vai trò",
                    JOptionPane.QUESTION_MESSAGE, null, roles, roles[0]);
            if (role == null) return;
            User newUser = new User(0, username.trim(), password.trim(), name.trim(), role, "active");
            boolean ok = userDAO.addUser(newUser);
            if (ok) { loadAll(); JOptionPane.showMessageDialog(this, "Thêm tài khoản thành công!"); }
            else JOptionPane.showMessageDialog(this, "Tên đăng nhập đã tồn tại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
        });

        // per spec: xóa tài khoản
        btnDelete.addActionListener(e -> {
            int row = tblUsers.getSelectedRow();
            if (row < 0) { JOptionPane.showMessageDialog(this, "Chọn tài khoản cần xóa."); return; }
            int id = (int) tableModel.getValueAt(row, 0);
            int confirm = JOptionPane.showConfirmDialog(this, "Xóa tài khoản này?", "Xác nhận", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                userDAO.deleteUser(id);
                loadAll();
                JOptionPane.showMessageDialog(this, "Đã xóa tài khoản.");
            }
        });

        // per spec: phân quyền người dùng theo vai trò
        btnChangeRole.addActionListener(e -> {
            int row = tblUsers.getSelectedRow();
            if (row < 0) { JOptionPane.showMessageDialog(this, "Chọn tài khoản cần phân quyền."); return; }
            int id = (int) tableModel.getValueAt(row, 0);
            String[] roles = {"STUDENT", "TEACHER", "PDT", "ADMIN"};
            String role = (String) JOptionPane.showInputDialog(this, "Chọn vai trò mới:", "Phân quyền",
                    JOptionPane.QUESTION_MESSAGE, null, roles, tableModel.getValueAt(row, 3));
            if (role != null) {
                userDAO.updateUserRole(id, role);
                loadAll();
                JOptionPane.showMessageDialog(this, "Phân quyền thành công!");
            }
        });

        btnLogout.addActionListener(e -> { dispose(); new LoginFrm().setVisible(true); });

        txtSearch.addActionListener(e -> btnSearch.doClick());
    }

    private void loadAll() { loadData(userDAO.getAllUsers()); }

    private void loadData(List<User> users) {
        tableModel.setRowCount(0);
        for (User u : users) {
            tableModel.addRow(new Object[]{ u.getId(), u.getUsername(), u.getName(), u.getRole(), u.getStatus() });
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
