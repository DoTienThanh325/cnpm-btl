package com.sms.view;

import com.sms.dao.FacultyDAO;
import com.sms.dao.SubjectDAO;
import com.sms.dao.TextbookDAO;
import com.sms.entity.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.List;

/**
 * CreateSubjectFrm - per spec section C.3.3
 * Has: Khoa phụ trách (dropdown), Số tín chỉ, Nội dung cơ bản, Chọn giáo trình (dropdown)
 * Calls: getAllFaculties(), getAllTextbooks(), getAllMajors(), createSubject()
 */
public class CreateSubjectFrm extends JDialog {

    private SubjectFrm parent;
    private Subject editSubject; // null = create mode
    private SubjectDAO subjectDAO;
    private FacultyDAO facultyDAO;
    private TextbookDAO textbookDAO;

    private JTextField txtCode, txtName, txtCredits;
    private JTextArea txtContent;
    private JComboBox<Faculty> cmbFaculty;
    private JComboBox<Textbook> cmbTextbook;
    private JComboBox<String> cmbStatus;
    private JButton btnSave, btnCancel;

    public CreateSubjectFrm(SubjectFrm parent, SubjectDAO subjectDAO,
                             FacultyDAO facultyDAO, TextbookDAO textbookDAO) {
        super(parent, "Tạo môn học mới", true);
        this.parent = parent;
        this.subjectDAO = subjectDAO;
        this.facultyDAO = facultyDAO;
        this.textbookDAO = textbookDAO;
        initComponents();
        loadDropdowns();
    }

    public CreateSubjectFrm(SubjectFrm parent, Subject subject, SubjectDAO subjectDAO,
                             FacultyDAO facultyDAO, TextbookDAO textbookDAO) {
        super(parent, "Chỉnh sửa môn học", true);
        this.parent = parent;
        this.editSubject = subject;
        this.subjectDAO = subjectDAO;
        this.facultyDAO = facultyDAO;
        this.textbookDAO = textbookDAO;
        initComponents();
        loadDropdowns();
        if (subject != null) fillData(subject);
    }

    private void initComponents() {
        setSize(500, 460);
        setLocationRelativeTo(parent);

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(new EmptyBorder(15, 20, 15, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(6, 5, 6, 5);
        int row = 0;

        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0.35; panel.add(new JLabel("Mã môn học:"), gbc);
        txtCode = new JTextField(20);
        gbc.gridx = 1; gbc.weightx = 0.65; panel.add(txtCode, gbc); row++;

        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0.35; panel.add(new JLabel("Tên môn học:"), gbc);
        txtName = new JTextField(20);
        gbc.gridx = 1; gbc.weightx = 0.65; panel.add(txtName, gbc); row++;

        // per spec: Số tín chỉ
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0.35; panel.add(new JLabel("Số tín chỉ:"), gbc);
        txtCredits = new JTextField(20);
        gbc.gridx = 1; gbc.weightx = 0.65; panel.add(txtCredits, gbc); row++;

        // per spec: Nội dung cơ bản môn học
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0.35; panel.add(new JLabel("Nội dung môn học:"), gbc);
        txtContent = new JTextArea(4, 20);
        txtContent.setLineWrap(true);
        gbc.gridx = 1; gbc.weightx = 0.65; panel.add(new JScrollPane(txtContent), gbc); row++;

        // per spec: Khoa phụ trách (dropdown) - getAllFaculties()
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0.35; panel.add(new JLabel("Khoa phụ trách:"), gbc);
        cmbFaculty = new JComboBox<>();
        gbc.gridx = 1; gbc.weightx = 0.65; panel.add(cmbFaculty, gbc); row++;

        // per spec: Chọn giáo trình (dropdown) - getAllTextbooks()
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0.35; panel.add(new JLabel("Chọn giáo trình:"), gbc);
        cmbTextbook = new JComboBox<>();
        gbc.gridx = 1; gbc.weightx = 0.65; panel.add(cmbTextbook, gbc); row++;

        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0.35; panel.add(new JLabel("Trạng thái:"), gbc);
        cmbStatus = new JComboBox<>(new String[]{"active", "closed"});
        gbc.gridx = 1; gbc.weightx = 0.65; panel.add(cmbStatus, gbc); row++;

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 5));
        btnSave = new JButton(editSubject == null ? "Tạo môn học" : "Cập nhật");
        btnSave.setBackground(new Color(46, 204, 113));
        btnSave.setForeground(Color.WHITE);
        btnSave.setFocusPainted(false);
        btnCancel = new JButton("Hủy");
        btnCancel.setBackground(new Color(127, 140, 141));
        btnCancel.setForeground(Color.WHITE);
        btnCancel.setFocusPainted(false);
        btnPanel.add(btnSave);
        btnPanel.add(btnCancel);
        gbc.gridx = 0; gbc.gridy = row; gbc.gridwidth = 2; panel.add(btnPanel, gbc);

        setContentPane(new JScrollPane(panel));

        btnSave.addActionListener(e -> actionPerformed());
        btnCancel.addActionListener(e -> dispose());
    }

    private void loadDropdowns() {
        // per spec: getAllFaculties()
        List<Faculty> faculties = facultyDAO.getAllFaculties();
        if (faculties.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Hệ thống không tìm thấy khoa nào!", "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
        for (Faculty f : faculties) cmbFaculty.addItem(f);

        // per spec: getAllTextbooks()
        List<Textbook> textbooks = textbookDAO.getAllTextbooks();
        if (textbooks.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Hệ thống không tìm thấy giáo trình nào!", "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
        for (Textbook t : textbooks) cmbTextbook.addItem(t);
    }

    private void fillData(Subject s) {
        txtCode.setText(s.getCode());
        txtCode.setEditable(false);
        txtName.setText(s.getName());
        txtCredits.setText(String.valueOf(s.getCredits()));
        txtContent.setText(s.getContent());
        cmbStatus.setSelectedItem(s.getStatus());
    }

    private void actionPerformed() {
        String code = txtCode.getText().trim();
        String name = txtName.getText().trim();
        String creditsStr = txtCredits.getText().trim();
        if (code.isEmpty() || name.isEmpty() || creditsStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng điền đầy đủ thông tin!"); return;
        }
        int credits;
        try { credits = Integer.parseInt(creditsStr); } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Số tín chỉ không hợp lệ!"); return;
        }
        Faculty faculty = (Faculty) cmbFaculty.getSelectedItem();
        Textbook textbook = (Textbook) cmbTextbook.getSelectedItem();

        if (editSubject == null) {
            // per spec: createSubject()
            Subject newSubject = new Subject();
            newSubject.setCode(code);
            newSubject.setName(name);
            newSubject.setCredits(credits);
            newSubject.setContent(txtContent.getText().trim());
            newSubject.setFaculty(faculty);
            newSubject.setStatus((String) cmbStatus.getSelectedItem());
            if (textbook != null) newSubject.getTextbooks().add(textbook);
            boolean ok = subjectDAO.createSubject(newSubject);
            if (ok) {
                JOptionPane.showMessageDialog(this, "Tạo môn học thành công!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
                parent.loadAllSubjects();
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Mã môn đã tồn tại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            editSubject.setName(name);
            editSubject.setCredits(credits);
            editSubject.setContent(txtContent.getText().trim());
            editSubject.setFaculty(faculty);
            editSubject.setStatus((String) cmbStatus.getSelectedItem());
            if (textbook != null && !editSubject.getTextbooks().contains(textbook))
                editSubject.getTextbooks().add(textbook);
            subjectDAO.updateSubject(editSubject);
            JOptionPane.showMessageDialog(this, "Cập nhật môn học thành công!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
            parent.loadAllSubjects();
            dispose();
        }
    }
}
