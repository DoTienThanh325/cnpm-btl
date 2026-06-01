package com.sms.view;

import com.sms.dao.ClassSectionDAO;
import com.sms.dao.GradeDAO;
import com.sms.dao.StudentDAO;
import com.sms.entity.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.*;
import java.awt.*;
import java.util.List;

/**
 * EditGradeFrm - per spec section C.3.2 (B.1.2)
 * Shows: danh sách sinh viên trong lớp dưới dạng bảng
 * Columns: STT, Mã sinh viên, Họ và tên, Điểm chuyên cần, Điểm giữa kỳ, Điểm cuối kỳ
 * Button: Lưu
 * Calls: getStudentByClass(), getGradeByStudentAndClass(), updateGrade()
 */
public class EditGradeFrm extends JFrame {

    private JFrame parentFrm;
    private ClassSection classSection;

    private StudentDAO studentDAO = new StudentDAO();
    private GradeDAO gradeDAO = new GradeDAO();
    private ClassSectionDAO classDAO = new ClassSectionDAO();

    private JTable tblGrades;
    private DefaultTableModel tableModel;
    private JButton btnSave, btnBack;
    private List<Student> students;
    private List<Grade> grades;

    public EditGradeFrm(JFrame parent, ClassSection cs) {
        this.parentFrm = parent;
        this.classSection = cs;
        setTitle("Sửa điểm - Lớp " + cs.getCode());
        setSize(750, 480);
        setLocationRelativeTo(parent);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        initComponents();
        loadData();
    }

    private void initComponents() {
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        mainPanel.setBackground(new Color(236, 240, 241));

        JLabel lblTitle = new JLabel("Lớp: " + classSection.getCode() + " | Môn: " +
                (classSection.getSubject() != null ? classSection.getSubject().getName() : ""));
        lblTitle.setFont(new Font("Arial", Font.BOLD, 13));
        lblTitle.setBorder(new EmptyBorder(0, 0, 5, 0));

        // per spec: table with columns STT, Mã sinh viên, Họ và tên, Điểm chuyên cần, Điểm giữa kỳ, Điểm cuối kỳ
        String[] cols = {"STT", "Mã sinh viên", "Họ và tên", "Điểm chuyên cần", "Điểm giữa kỳ", "Điểm cuối kỳ"};
        tableModel = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int row, int col) {
                // per spec: only score columns 3,4,5 are editable
                return col >= 3;
            }
            @Override public Class<?> getColumnClass(int col) {
                return col >= 3 ? Double.class : Object.class;
            }
        };
        tblGrades = new JTable(tableModel);
        tblGrades.setRowHeight(28);
        tblGrades.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        tblGrades.setFont(new Font("Arial", Font.PLAIN, 12));

        // highlight editable columns
        for (int i = 3; i <= 5; i++) {
            tblGrades.getColumnModel().getColumn(i).setCellRenderer(new DefaultTableCellRenderer() {
                @Override public Component getTableCellRendererComponent(JTable t, Object val, boolean sel, boolean foc, int r, int c) {
                    Component comp = super.getTableCellRendererComponent(t, val, sel, foc, r, c);
                    if (!sel) comp.setBackground(new Color(255, 255, 220));
                    return comp;
                }
            });
        }

        JScrollPane sp = new JScrollPane(tblGrades);

        // per spec: nút Lưu
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 5));
        bottomPanel.setBackground(new Color(236, 240, 241));
        btnSave = new JButton("Lưu");
        btnSave.setBackground(new Color(46, 204, 113));
        btnSave.setForeground(Color.WHITE);
        btnSave.setFocusPainted(false);
        btnSave.setFont(new Font("Arial", Font.BOLD, 13));
        btnBack = new JButton("Quay lại");
        btnBack.setBackground(new Color(127, 140, 141));
        btnBack.setForeground(Color.WHITE);
        btnBack.setFocusPainted(false);
        bottomPanel.add(btnSave);
        bottomPanel.add(btnBack);

        mainPanel.add(lblTitle, BorderLayout.NORTH);
        mainPanel.add(sp, BorderLayout.CENTER);
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);
        setContentPane(mainPanel);

        btnSave.addActionListener(e -> actionPerformed());
        btnBack.addActionListener(e -> dispose());
    }

    private void loadData() {
        tableModel.setRowCount(0);
        // per spec: getStudentByClass()
        students = studentDAO.getStudentByClass(classSection.getId());
        // per spec: ensure grade records exist
        gradeDAO.ensureGradesForClass(classSection.getId(), students, classSection);

        int idx = 1;
        for (Student s : students) {
            // per spec: getGradeByStudentAndClass()
            Grade g = gradeDAO.getGradeByStudentAndClass(s.getId(), classSection.getId());
            tableModel.addRow(new Object[]{
                idx++, s.getMssv(), s.getName(),
                g != null ? g.getAttendanceScore() : 0.0,
                g != null ? g.getMidtermScore() : 0.0,
                g != null ? g.getFinalScore() : 0.0
            });
        }
    }

    private void actionPerformed() {
        // per spec: save scores → updateGrade()
        if (tblGrades.isEditing()) tblGrades.getCellEditor().stopCellEditing();

        for (int i = 0; i < students.size(); i++) {
            Student s = students.get(i);
            try {
                double attendance = Double.parseDouble(tableModel.getValueAt(i, 3).toString());
                double midterm = Double.parseDouble(tableModel.getValueAt(i, 4).toString());
                double finalScore = Double.parseDouble(tableModel.getValueAt(i, 5).toString());

                if (attendance < 0 || attendance > 10 || midterm < 0 || midterm > 10 || finalScore < 0 || finalScore > 10) {
                    JOptionPane.showMessageDialog(this, "Điểm phải trong khoảng 0-10!", "Lỗi", JOptionPane.ERROR_MESSAGE); return;
                }

                Grade g = gradeDAO.getGradeByStudentAndClass(s.getId(), classSection.getId());
                if (g != null) {
                    // per spec: updateGrade()
                    g.updateGrade(attendance, midterm, finalScore);
                    gradeDAO.updateGrade(g);
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Điểm không hợp lệ tại dòng " + (i + 1), "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }
        }
        // per spec: thông báo thành công
        JOptionPane.showMessageDialog(this, "Lưu điểm thành công!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
    }
}
