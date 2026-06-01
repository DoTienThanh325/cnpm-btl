package com.sms.view;

import com.sms.dao.ClassSectionDAO;
import com.sms.dao.TeacherDAO;
import com.sms.dao.SubjectDAO;
import com.sms.entity.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.List;

/**
 * EditClassFrm - per spec section C.3.1
 * Fields: id (read-only), mã lớp (read-only), môn học (dropdown - read-only per spec),
 *         giảng viên (dropdown - editable), sĩ số (editable)
 * Buttons: Cập nhật, Hủy
 * Calls: getAllTeacher(), getAllSubject(), updateClass()
 */
public class EditClassFrm extends JFrame {

    private JFrame parentFrm;
    private ClassSection classSection;
    private ClassSectionDAO classDAO;

    private JTextField txtId, txtCode, txtCapacity;
    private JComboBox<Subject> cmbSubject;
    private JComboBox<Teacher> cmbTeacher;
    private JButton btnUpdate, btnCancel;

    public EditClassFrm(JFrame parent, ClassSection cs, ClassSectionDAO classDAO) {
        this.parentFrm = parent;
        this.classSection = cs;
        this.classDAO = classDAO;
        setTitle("Cập nhật Lớp học phần - " + cs.getCode());
        setSize(480, 340);
        setLocationRelativeTo(parent);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        initComponents();
        fillData();
    }

    private void initComponents() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(new EmptyBorder(20, 30, 20, 30));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(8, 5, 8, 5);
        int row = 0;

        // per spec: id - không sửa được
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0.35; panel.add(new JLabel("id:"), gbc);
        txtId = new JTextField();
        txtId.setEditable(false);
        txtId.setBackground(new Color(220, 220, 220));
        gbc.gridx = 1; gbc.weightx = 0.65; panel.add(txtId, gbc); row++;

        // per spec: Mã lớp - không sửa được
        gbc.gridx = 0; gbc.gridy = row; panel.add(new JLabel("Mã lớp:"), gbc);
        txtCode = new JTextField();
        txtCode.setEditable(false);
        txtCode.setBackground(new Color(220, 220, 220));
        gbc.gridx = 1; panel.add(txtCode, gbc); row++;

        // per spec: Môn học (danh sách thả xuống - không sửa được)
        gbc.gridx = 0; gbc.gridy = row; panel.add(new JLabel("Môn học:"), gbc);
        SubjectDAO sDao = new SubjectDAO();
        List<Subject> subjects = sDao.getAllSubject();
        cmbSubject = new JComboBox<>(subjects.toArray(new Subject[0]));
        cmbSubject.setEnabled(false); // per spec: not editable
        gbc.gridx = 1; panel.add(cmbSubject, gbc); row++;

        // per spec: Giảng viên (danh sách thả xuống - editable) → getAllTeacher()
        gbc.gridx = 0; gbc.gridy = row; panel.add(new JLabel("Giảng viên:"), gbc);
        TeacherDAO tDao = new TeacherDAO();
        List<Teacher> teachers = tDao.getAllTeacher();
        cmbTeacher = new JComboBox<>(teachers.toArray(new Teacher[0]));
        gbc.gridx = 1; panel.add(cmbTeacher, gbc); row++;

        // per spec: Sĩ số - editable
        gbc.gridx = 0; gbc.gridy = row; panel.add(new JLabel("Sĩ số:"), gbc);
        txtCapacity = new JTextField();
        gbc.gridx = 1; panel.add(txtCapacity, gbc); row++;

        // per spec: nút Cập nhật và nút Hủy
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 5));
        btnUpdate = new JButton("Cập nhật");
        btnUpdate.setBackground(new Color(46, 204, 113));
        btnUpdate.setForeground(Color.WHITE);
        btnUpdate.setFocusPainted(false);
        btnCancel = new JButton("Hủy");
        btnCancel.setBackground(new Color(127, 140, 141));
        btnCancel.setForeground(Color.WHITE);
        btnCancel.setFocusPainted(false);
        btnPanel.add(btnUpdate);
        btnPanel.add(btnCancel);
        gbc.gridx = 0; gbc.gridy = row; gbc.gridwidth = 2; panel.add(btnPanel, gbc);

        setContentPane(panel);

        btnUpdate.addActionListener(e -> actionPerformed());
        btnCancel.addActionListener(e -> {
            dispose();
            if (parentFrm instanceof ManageClassFrm) {
                ManageClassFrm mf = (ManageClassFrm) parentFrm;
                mf.setVisible(true);
            }
        });
    }

    private void fillData() {
        txtId.setText(String.valueOf(classSection.getId()));
        txtCode.setText(classSection.getCode());
        txtCapacity.setText(String.valueOf(classSection.getCapacity()));

        // set selected subject
        for (int i = 0; i < cmbSubject.getItemCount(); i++) {
            Subject s = cmbSubject.getItemAt(i);
            if (classSection.getSubject() != null && s.getId() == classSection.getSubject().getId()) {
                cmbSubject.setSelectedIndex(i); break;
            }
        }
        // set selected teacher
        for (int i = 0; i < cmbTeacher.getItemCount(); i++) {
            Teacher t = cmbTeacher.getItemAt(i);
            if (classSection.getTeacher() != null && t.getId() == classSection.getTeacher().getId()) {
                cmbTeacher.setSelectedIndex(i); break;
            }
        }
    }

    private void actionPerformed() {
        String capStr = txtCapacity.getText().trim();
        if (capStr.isEmpty()) { JOptionPane.showMessageDialog(this, "Sĩ số không được để trống!"); return; }
        int capacity;
        try { capacity = Integer.parseInt(capStr); } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Sĩ số không hợp lệ!"); return;
        }
        Teacher teacher = (Teacher) cmbTeacher.getSelectedItem();
        // per spec: setCapacity(), setTeacher()
        classSection.setCapacity(capacity);
        classSection.setTeacher(teacher);
        // per spec: updateClass()
        classDAO.updateClass(classSection);
        // per spec: thông báo thành công
        JOptionPane.showMessageDialog(this, "Cập nhật lớp học phần thành công!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
        dispose();
        // per spec: quay về ManageClassFrm
        if (parentFrm instanceof ManageClassFrm) {
            ManageClassFrm mf = (ManageClassFrm) parentFrm;
            mf.loadData(new ClassSectionDAO().getAllClassSections());
            mf.setVisible(true);
        }
    }
}
