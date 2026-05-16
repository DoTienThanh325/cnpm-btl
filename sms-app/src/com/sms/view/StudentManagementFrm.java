package com.sms.view;

import com.sms.dao.StudentDAO;
import com.sms.dao.FacultyDAO;
import com.sms.dao.MajorDAO;
import com.sms.entity.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.util.List;

/**
 * StudentManagementFrm - per spec: add student (auto MSSV), update, soft delete, search by name/ID
 */
public class StudentManagementFrm extends JFrame {

    private User currentUser;
    private StudentDAO studentDAO = new StudentDAO();
    private FacultyDAO facultyDAO = new FacultyDAO();
    private MajorDAO majorDAO = new MajorDAO();

    private JTextField txtSearch;
    private JButton btnSearch, btnAdd, btnEdit, btnSoftDelete, btnBack;
    private JTable tblStudents;
    private DefaultTableModel tableModel;

    public StudentManagementFrm(User user) {
        this.currentUser = user;
        setTitle("Quản lý Sinh viên");
        setSize(900, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        initComponents();
        loadData(studentDAO.getAllStudents());
    }

    private void initComponents() {
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        mainPanel.setBackground(new Color(236, 240, 241));

        // Top: search + buttons
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        topPanel.setBackground(new Color(236, 240, 241));
        topPanel.add(new JLabel("Tìm kiếm (tên/MSSV):"));
        txtSearch = new JTextField(20);
        topPanel.add(txtSearch);
        btnSearch = new JButton("Tìm");
        styleButton(btnSearch, new Color(41, 128, 185));
        topPanel.add(btnSearch);
        btnAdd = new JButton("Thêm SV mới");
        styleButton(btnAdd, new Color(46, 204, 113));
        topPanel.add(btnAdd);
        btnEdit = new JButton("Cập nhật");
        styleButton(btnEdit, new Color(230, 126, 34));
        topPanel.add(btnEdit);
        btnSoftDelete = new JButton("Xóa (mềm)");
        styleButton(btnSoftDelete, new Color(231, 76, 60));
        topPanel.add(btnSoftDelete);
        btnBack = new JButton("Quay lại");
        styleButton(btnBack, new Color(127, 140, 141));
        topPanel.add(btnBack);

        // Table
        String[] columns = {"ID", "MSSV", "Họ và tên", "Ngày sinh", "Giới tính", "Khoa", "Lớp HC", "Trạng thái"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override public boolean isCellEditable(int row, int col) { return false; }
        };
        tblStudents = new JTable(tableModel);
        tblStudents.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tblStudents.setRowHeight(25);
        tblStudents.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        tblStudents.setFont(new Font("Arial", Font.PLAIN, 12));
        JScrollPane scrollPane = new JScrollPane(tblStudents);

        mainPanel.add(topPanel, BorderLayout.NORTH);
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        setContentPane(mainPanel);

        // Events
        btnSearch.addActionListener(e -> {
            String kw = txtSearch.getText().trim();
            List<Student> results = kw.isEmpty() ? studentDAO.getAllStudents() : studentDAO.searchStudents(kw);
            loadData(results);
            if (results.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Không tìm thấy sinh viên nào.", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            }
        });

        btnAdd.addActionListener(e -> {
            new AddEditStudentFrm(this, null, studentDAO, facultyDAO, majorDAO).setVisible(true);
        });

        btnEdit.addActionListener(e -> {
            int row = tblStudents.getSelectedRow();
            if (row < 0) { JOptionPane.showMessageDialog(this, "Chọn sinh viên cần cập nhật.", "Thông báo", JOptionPane.WARNING_MESSAGE); return; }
            int id = (int) tableModel.getValueAt(row, 0);
            Student s = studentDAO.getById(id);
            new AddEditStudentFrm(this, s, studentDAO, facultyDAO, majorDAO).setVisible(true);
        });

        btnSoftDelete.addActionListener(e -> {
            int row = tblStudents.getSelectedRow();
            if (row < 0) { JOptionPane.showMessageDialog(this, "Chọn sinh viên cần xóa.", "Thông báo", JOptionPane.WARNING_MESSAGE); return; }
            int id = (int) tableModel.getValueAt(row, 0);
            int confirm = JOptionPane.showConfirmDialog(this, "Xóa mềm sinh viên này? Dữ liệu điểm/học phí vẫn được giữ lại.", "Xác nhận", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                studentDAO.softDeleteStudent(id);
                loadData(studentDAO.getAllStudents());
                JOptionPane.showMessageDialog(this, "Đã đánh dấu sinh viên là 'nghỉ học'.", "Thành công", JOptionPane.INFORMATION_MESSAGE);
            }
        });

        btnBack.addActionListener(e -> this.dispose());
    }

    public void loadData(List<Student> students) {
        tableModel.setRowCount(0);
        for (Student s : students) {
            tableModel.addRow(new Object[]{
                s.getId(), s.getMssv(), s.getName(), s.getDob(), s.getGender(),
                s.getFaculty() != null ? s.getFaculty().getName() : "",
                s.getAdminClass(), s.getStudentStatus()
            });
        }
    }

    private void styleButton(JButton btn, Color color) {
        btn.setBackground(color);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setFont(new Font("Arial", Font.PLAIN, 12));
    }
}
