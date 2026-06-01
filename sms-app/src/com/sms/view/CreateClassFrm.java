package com.sms.view;

import com.sms.dao.*;
import com.sms.entity.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.List;

/**
 * CreateClassFrm - per spec section C.3.4
 * Has dropdowns: chọn môn học (getAllSubject), chọn giảng viên (getAllTeacher), chọn lịch học (getAllSession)
 * Has input: sĩ số tối đa
 * Validates: checkSchedule(), validateClassData(), then createClass()
 */
public class CreateClassFrm extends JDialog {

    private ManageClassFrm parent;
    private ClassSectionDAO classDAO;

    private JTextField txtCode, txtCapacity;
    private JComboBox<Subject> cmbSubject;
    private JComboBox<Teacher> cmbTeacher;
    private JComboBox<Session> cmbSession;
    private JButton btnCreate, btnCancel;

    public CreateClassFrm(ManageClassFrm parent, ClassSectionDAO classDAO) {
        super(parent, "Mở lớp học phần mới", true);
        this.parent = parent;
        this.classDAO = classDAO;
        setSize(500, 380);
        setLocationRelativeTo(parent);
        initComponents();
        loadDropdowns();
    }

    private void initComponents() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(new EmptyBorder(20, 30, 20, 30));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(8, 5, 8, 5);
        int row = 0;

        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0.35; panel.add(new JLabel("Mã lớp học phần:"), gbc);
        txtCode = new JTextField(20);
        gbc.gridx = 1; gbc.weightx = 0.65; panel.add(txtCode, gbc); row++;

        // per spec: Chọn môn học (dropdown) - getAllSubject()
        gbc.gridx = 0; gbc.gridy = row; panel.add(new JLabel("Chọn môn học:"), gbc);
        cmbSubject = new JComboBox<>();
        gbc.gridx = 1; panel.add(cmbSubject, gbc); row++;

        // per spec: Chọn giảng viên phụ trách (dropdown) - getAllTeacher()
        gbc.gridx = 0; gbc.gridy = row; panel.add(new JLabel("Chọn giảng viên:"), gbc);
        cmbTeacher = new JComboBox<>();
        gbc.gridx = 1; panel.add(cmbTeacher, gbc); row++;

        // per spec: Thời khóa biểu (dropdown) - getAllSession()
        gbc.gridx = 0; gbc.gridy = row; panel.add(new JLabel("Chọn lịch học:"), gbc);
        cmbSession = new JComboBox<>();
        gbc.gridx = 1; panel.add(cmbSession, gbc); row++;

        // per spec: Sĩ số tối đa
        gbc.gridx = 0; gbc.gridy = row; panel.add(new JLabel("Sĩ số tối đa:"), gbc);
        txtCapacity = new JTextField(20);
        gbc.gridx = 1; panel.add(txtCapacity, gbc); row++;

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 5));
        btnCreate = new JButton("Mở lớp");
        btnCreate.setBackground(new Color(46, 204, 113));
        btnCreate.setForeground(Color.WHITE);
        btnCreate.setFocusPainted(false);
        btnCancel = new JButton("Hủy");
        btnCancel.setBackground(new Color(127, 140, 141));
        btnCancel.setForeground(Color.WHITE);
        btnCancel.setFocusPainted(false);
        btnPanel.add(btnCreate);
        btnPanel.add(btnCancel);
        gbc.gridx = 0; gbc.gridy = row; gbc.gridwidth = 2; panel.add(btnPanel, gbc);

        setContentPane(panel);
        btnCreate.addActionListener(e -> actionPerformed());
        btnCancel.addActionListener(e -> dispose());
    }

    private void loadDropdowns() {
        SubjectDAO sDao = new SubjectDAO();
        TeacherDAO tDao = new TeacherDAO();
        SessionDAO seDao = new SessionDAO();

        // per spec: getAllSubject()
        List<Subject> subjects = sDao.getAllSubject();
        if (subjects.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Hệ thống không tìm thấy môn học nào!", "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
        for (Subject s : subjects) cmbSubject.addItem(s);

        // per spec: getAllTeacher()
        List<Teacher> teachers = tDao.getAllTeacher();
        if (teachers.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Hệ thống không tìm thấy giảng viên nào!", "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
        for (Teacher t : teachers) cmbTeacher.addItem(t);

        // per spec: getAllSession()
        List<Session> sessions = seDao.getAllSession();
        for (Session s : sessions) cmbSession.addItem(s);
    }

    private void actionPerformed() {
        String code = txtCode.getText().trim();
        String capStr = txtCapacity.getText().trim();
        if (code.isEmpty() || capStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng điền đầy đủ thông tin!"); return;
        }
        int capacity;
        try { capacity = Integer.parseInt(capStr); } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Sĩ số không hợp lệ!"); return;
        }

        Subject subject = (Subject) cmbSubject.getSelectedItem();
        Teacher teacher = (Teacher) cmbTeacher.getSelectedItem();
        Session session = (Session) cmbSession.getSelectedItem();

        java.util.List<Session> sessionList = new java.util.ArrayList<>();
        if (session != null) sessionList.add(session);

        // per spec: checkSchedule() - kiểm tra trùng thời khóa biểu
        if (classDAO.checkSchedule(teacher, sessionList)) {
            // per spec exception: "Hệ thống phát hiện trùng thời khóa biểu, giảng viên"
            JOptionPane.showMessageDialog(this, "Giảng viên bị trùng thời khóa biểu!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // per spec: validateClassData() - kiểm tra sĩ số > 0, TKB hợp lệ
        if (!classDAO.validateClassData(capacity, sessionList)) {
            // per spec exception: "sĩ số <= 0 thông báo lỗi"
            JOptionPane.showMessageDialog(this, "Dữ liệu không hợp lệ! (Sĩ số phải > 0, phải có lịch học)", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // per spec: setSubject(), setTeacher(), setSession(), setCapacity()
        ClassSection newClass = new ClassSection();
        newClass.setCode(code);
        newClass.setSubject(subject);
        newClass.setTeacher(teacher);
        newClass.setSession(sessionList);
        newClass.setCapacity(capacity);
        newClass.setEnrolledCount(0);
        newClass.setStatus("active");

        // per spec: createClass()
        classDAO.createClass(newClass);

        // per spec: thông báo mở lớp học phần thành công
        JOptionPane.showMessageDialog(this, "Mở lớp học phần thành công!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
        parent.loadData(classDAO.getAllClassSections());
        dispose();
        // per spec: quay về ManageClassFrm
        parent.setVisible(true);
    }
}
