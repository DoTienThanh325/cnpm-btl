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
 * SearchClassForGradeFrm - used by PDT to search class then edit grades
 * per spec: PDT can view/edit grades, search by lớp hành chính or lớp học phần
 */
public class SearchClassForGradeFrm extends JFrame {

    private User currentUser;
    private boolean teacherMode; // true = teacher, false = PDT

    private ClassSectionDAO classDAO = new ClassSectionDAO();
    private JTextField txtSearch;
    private JButton btnSearch, btnBack;
    private JTable tblResults;
    private DefaultTableModel tableModel;
    private List<ClassSection> currentResults;

    public SearchClassForGradeFrm(User user, boolean teacherMode) {
        this.currentUser = user;
        this.teacherMode = teacherMode;
        setTitle(teacherMode ? "Sửa điểm - Tìm lớp" : "Quản lý Điểm - Tìm lớp");
        setSize(700, 450);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        initComponents();
    }

    private void initComponents() {
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        mainPanel.setBackground(new Color(236, 240, 241));

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        topPanel.setBackground(new Color(236, 240, 241));
        topPanel.add(new JLabel("Nhập tên lớp / mã lớp:"));
        txtSearch = new JTextField(20);
        topPanel.add(txtSearch);
        btnSearch = new JButton("Tìm kiếm");
        btnSearch.setBackground(new Color(41, 128, 185));
        btnSearch.setForeground(Color.WHITE);
        btnSearch.setFocusPainted(false);
        topPanel.add(btnSearch);
        btnBack = new JButton("Quay lại");
        btnBack.setBackground(new Color(127, 140, 141));
        btnBack.setForeground(Color.WHITE);
        btnBack.setFocusPainted(false);
        topPanel.add(btnBack);

        // per spec scenario 1.2: table with STT, Mã lớp, Tên Lớp, Sĩ số
        String[] cols = {"STT", "Mã lớp", "Tên Lớp (Môn học)", "Sĩ số"};
        tableModel = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tblResults = new JTable(tableModel);
        tblResults.setRowHeight(25);
        tblResults.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        tblResults.setFont(new Font("Arial", Font.PLAIN, 12));
        tblResults.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane sp = new JScrollPane(tblResults);

        mainPanel.add(topPanel, BorderLayout.NORTH);
        mainPanel.add(sp, BorderLayout.CENTER);
        setContentPane(mainPanel);

        btnSearch.addActionListener(e -> {
            String kw = txtSearch.getText().trim();
            if (teacherMode) {
                // teacher only sees their classes
                currentResults = classDAO.getByTeacher(currentUser.getId());
                if (!kw.isEmpty()) {
                    currentResults.removeIf(c -> !c.getCode().toLowerCase().contains(kw.toLowerCase())
                            && (c.getSubject() == null || !c.getSubject().getName().toLowerCase().contains(kw.toLowerCase())));
                }
            } else {
                currentResults = classDAO.searchClass(kw);
            }
            loadData(currentResults);
            if (currentResults.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Không tìm thấy lớp học phần tương ứng.", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            }
        });

        // per spec: click vào 1 lớp → EditGradeFrm
        tblResults.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override public void mouseClicked(java.awt.event.MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int row = tblResults.getSelectedRow();
                    if (row < 0 || currentResults == null) return;
                    ClassSection selected = currentResults.get(row);
                    new EditGradeFrm(SearchClassForGradeFrm.this, selected).setVisible(true);
                }
            }
        });

        btnBack.addActionListener(e -> dispose());
        txtSearch.addActionListener(e -> btnSearch.doClick());
    }

    private void loadData(List<ClassSection> classes) {
        tableModel.setRowCount(0);
        int idx = 1;
        for (ClassSection c : classes) {
            tableModel.addRow(new Object[]{
                idx++, c.getCode(),
                c.getSubject() != null ? c.getSubject().getName() : "",
                c.getCapacity()
            });
        }
    }
}
