package com.sms.view;

import com.sms.dao.ClassSectionDAO;
import com.sms.entity.ClassSection;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.*;
import java.awt.*;
import java.util.List;

/**
 * SearchClassFrm - per spec section C.3.1 (search + table of results)
 * Has: ô nhập mã lớp, nút tìm, bảng danh sách kết quả
 * mode: "UPDATE" → click row → EditClassFrm
 *       "GRADE"  → click row → EditGradeFrm (used by teacher too)
 */
public class SearchClassFrm extends JFrame {

    private JFrame parentFrm;
    private ClassSectionDAO classDAO;
    private String mode; // "UPDATE" or "GRADE"

    private JTextField txtSearch;
    private JButton btnSearch, btnBack;
    private JTable tblResults;
    private DefaultTableModel tableModel;
    private List<ClassSection> currentResults;

    public SearchClassFrm(JFrame parent, ClassSectionDAO classDAO, String mode) {
        this.parentFrm = parent;
        this.classDAO = classDAO;
        this.mode = mode;
        setTitle(mode.equals("UPDATE") ? "Tìm lớp để cập nhật" : "Tìm lớp để sửa điểm");
        setSize(700, 420);
        setLocationRelativeTo(parent);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        initComponents();
    }

    private void initComponents() {
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        mainPanel.setBackground(new Color(236, 240, 241));

        // per spec: ô nhập mã lớp + nút Tìm kiếm
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        topPanel.setBackground(new Color(236, 240, 241));
        topPanel.add(new JLabel("Nhập mã lớp / tên lớp:"));
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

        // per spec: bảng danh sách kết quả with columns: id, mã lớp, môn học, giảng viên, sĩ số
        String[] cols = {"id", "Mã lớp", "Môn học", "Giảng viên", "Sĩ số"};
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

        // per spec: click tìm → searchClass()
        btnSearch.addActionListener(e -> {
            String kw = txtSearch.getText().trim();
            // per spec: searchClass() of ClassSection
            currentResults = classDAO.searchClass(kw);
            loadData(currentResults);
            if (currentResults.isEmpty()) {
                // per spec exception: "Hệ thống báo không có lớp nào trong kết quả tìm kiếm"
                JOptionPane.showMessageDialog(this, "Không có lớp nào trong kết quả tìm kiếm.", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            }
        });

        // per spec: PĐT click vào dòng → EditClassFrm / EditGradeFrm
        tblResults.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override public void mouseClicked(java.awt.event.MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int row = tblResults.getSelectedRow();
                    if (row < 0 || currentResults == null) return;
                    ClassSection selected = currentResults.get(row);
                    if ("UPDATE".equals(mode)) {
                        // per spec: → EditClassFrm
                        new EditClassFrm(SearchClassFrm.this, selected, classDAO).setVisible(true);
                        dispose();
                    } else {
                        JOptionPane.showMessageDialog(SearchClassFrm.this,
                                "Chức năng này không có trong module này.",
                                "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                    }
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
                c.getTeacher() != null ? c.getTeacher().getName() : "",
                c.getCapacity()
            });
        }
    }
}
