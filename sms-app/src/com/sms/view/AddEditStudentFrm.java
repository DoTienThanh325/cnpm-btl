package com.sms.view;

import com.sms.dao.FacultyDAO;
import com.sms.dao.MajorDAO;
import com.sms.dao.StudentDAO;
import com.sms.entity.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.List;

/**
 * AddEditStudentFrm - form to add/edit student, per spec fields
 */
public class AddEditStudentFrm extends JDialog {

    private StudentManagementFrm parent;
    private Student student; // null = add mode
    private StudentDAO studentDAO;
    private FacultyDAO facultyDAO;
    private MajorDAO majorDAO;

    private JTextField txtName, txtDob, txtAddress, txtEmail, txtPhone, txtCohort, txtAdminClass;
    private JComboBox<String> cmbGender, cmbStatus;
    private JComboBox<Faculty> cmbFaculty;
    private JComboBox<Major> cmbMajor;
    private JButton btnSave, btnCancel;

    public AddEditStudentFrm(StudentManagementFrm parent, Student student,
                              StudentDAO studentDAO, FacultyDAO facultyDAO, MajorDAO majorDAO) {
        super(parent, student == null ? "Thêm Sinh viên mới" : "Cập nhật Sinh viên", true);
        this.parent = parent;
        this.student = student;
        this.studentDAO = studentDAO;
        this.facultyDAO = facultyDAO;
        this.majorDAO = majorDAO;
        setSize(500, 520);
        setLocationRelativeTo(parent);
        initComponents();
        if (student != null) fillData();
    }

    private void initComponents() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(new EmptyBorder(15, 20, 15, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);

        String[] labels = {"Họ và tên:", "Ngày sinh (YYYY-MM-DD):", "Giới tính:", "Địa chỉ:",
                "Email:", "Số điện thoại:", "Khoa:", "Ngành:", "Khóa học:", "Lớp HC:", "Trạng thái SV:"};
        int row = 0;

        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0.35; panel.add(new JLabel(labels[row]), gbc);
        txtName = new JTextField(20);
        gbc.gridx = 1; gbc.weightx = 0.65; panel.add(txtName, gbc); row++;

        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0.35; panel.add(new JLabel(labels[row]), gbc);
        txtDob = new JTextField(20);
        gbc.gridx = 1; gbc.weightx = 0.65; panel.add(txtDob, gbc); row++;

        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0.35; panel.add(new JLabel(labels[row]), gbc);
        cmbGender = new JComboBox<>(new String[]{"Nam", "Nữ", "Khác"});
        gbc.gridx = 1; gbc.weightx = 0.65; panel.add(cmbGender, gbc); row++;

        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0.35; panel.add(new JLabel(labels[row]), gbc);
        txtAddress = new JTextField(20);
        gbc.gridx = 1; gbc.weightx = 0.65; panel.add(txtAddress, gbc); row++;

        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0.35; panel.add(new JLabel(labels[row]), gbc);
        txtEmail = new JTextField(20);
        gbc.gridx = 1; gbc.weightx = 0.65; panel.add(txtEmail, gbc); row++;

        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0.35; panel.add(new JLabel(labels[row]), gbc);
        txtPhone = new JTextField(20);
        gbc.gridx = 1; gbc.weightx = 0.65; panel.add(txtPhone, gbc); row++;

        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0.35; panel.add(new JLabel(labels[row]), gbc);
        List<Faculty> faculties = facultyDAO.getAllFaculties();
        cmbFaculty = new JComboBox<>(faculties.toArray(new Faculty[0]));
        gbc.gridx = 1; gbc.weightx = 0.65; panel.add(cmbFaculty, gbc); row++;

        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0.35; panel.add(new JLabel(labels[row]), gbc);
        List<Major> majors = majorDAO.getAllMajors();
        cmbMajor = new JComboBox<>(majors.toArray(new Major[0]));
        gbc.gridx = 1; gbc.weightx = 0.65; panel.add(cmbMajor, gbc); row++;

        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0.35; panel.add(new JLabel(labels[row]), gbc);
        txtCohort = new JTextField(20);
        gbc.gridx = 1; gbc.weightx = 0.65; panel.add(txtCohort, gbc); row++;

        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0.35; panel.add(new JLabel(labels[row]), gbc);
        txtAdminClass = new JTextField(20);
        gbc.gridx = 1; gbc.weightx = 0.65; panel.add(txtAdminClass, gbc); row++;

        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0.35; panel.add(new JLabel(labels[row]), gbc);
        cmbStatus = new JComboBox<>(new String[]{"đang học", "nghỉ học", "tốt nghiệp"});
        gbc.gridx = 1; gbc.weightx = 0.65; panel.add(cmbStatus, gbc); row++;

        // Buttons
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 5));
        btnSave = new JButton(student == null ? "Thêm mới" : "Cập nhật");
        btnSave.setBackground(new Color(46, 204, 113));
        btnSave.setForeground(Color.WHITE);
        btnSave.setFocusPainted(false);
        btnCancel = new JButton("Hủy");
        btnCancel.setBackground(new Color(127, 140, 141));
        btnCancel.setForeground(Color.WHITE);
        btnCancel.setFocusPainted(false);
        btnPanel.add(btnSave);
        btnPanel.add(btnCancel);

        gbc.gridx = 0; gbc.gridy = row; gbc.gridwidth = 2;
        panel.add(btnPanel, gbc);

        setContentPane(new JScrollPane(panel));

        btnSave.addActionListener(e -> actionPerformed());
        btnCancel.addActionListener(e -> dispose());
    }

    private void fillData() {
        txtName.setText(student.getName());
        txtDob.setText(student.getDob());
        cmbGender.setSelectedItem(student.getGender());
        txtAddress.setText(student.getAddress());
        txtEmail.setText(student.getEmail());
        txtPhone.setText(student.getPhone());
        txtCohort.setText(student.getCohort());
        txtAdminClass.setText(student.getAdminClass());
        cmbStatus.setSelectedItem(student.getStudentStatus());
    }

    private void actionPerformed() {
        String name = txtName.getText().trim();
        if (name.isEmpty()) { JOptionPane.showMessageDialog(this, "Họ tên không được để trống!"); return; }

        Faculty faculty = (Faculty) cmbFaculty.getSelectedItem();
        Major major = (Major) cmbMajor.getSelectedItem();

        if (student == null) {
            // ADD mode - per spec: system auto-generates MSSV
            Student newStudent = new Student();
            newStudent.setName(name);
            newStudent.setDob(txtDob.getText().trim());
            newStudent.setGender((String) cmbGender.getSelectedItem());
            newStudent.setAddress(txtAddress.getText().trim());
            newStudent.setEmail(txtEmail.getText().trim());
            newStudent.setPhone(txtPhone.getText().trim());
            newStudent.setFaculty(faculty);
            newStudent.setMajor(major);
            newStudent.setCohort(txtCohort.getText().trim());
            newStudent.setAdminClass(txtAdminClass.getText().trim());
            newStudent.setStudentStatus((String) cmbStatus.getSelectedItem());
            newStudent.setRole("STUDENT");
            newStudent.setStatus("active");
            newStudent.setUsername("sv_" + name.replaceAll("\\s+", "").toLowerCase());
            newStudent.setPassword("sv123");
            studentDAO.addStudent(newStudent);
            JOptionPane.showMessageDialog(this, "Thêm sinh viên thành công! MSSV: " + newStudent.getMssv());
        } else {
            // EDIT mode
            student.setName(name);
            student.setDob(txtDob.getText().trim());
            student.setGender((String) cmbGender.getSelectedItem());
            student.setAddress(txtAddress.getText().trim());
            student.setEmail(txtEmail.getText().trim());
            student.setPhone(txtPhone.getText().trim());
            student.setFaculty(faculty);
            student.setMajor(major);
            student.setCohort(txtCohort.getText().trim());
            student.setAdminClass(txtAdminClass.getText().trim());
            student.setStudentStatus((String) cmbStatus.getSelectedItem());
            studentDAO.updateStudent(student);
            JOptionPane.showMessageDialog(this, "Cập nhật sinh viên thành công!");
        }
        parent.loadData(studentDAO.getAllStudents());
        dispose();
    }
}
