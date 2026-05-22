package com.sms.view;

import com.sms.dao.UserDAO;
import com.sms.entity.User;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;

public class LoginFrm extends JFrame {

    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private final UserDAO userDAO = new UserDAO();

    public LoginFrm() {
        setTitle("Dang nhap");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 320);
        setLocationRelativeTo(null);
        setResizable(false);
        initComponents();
    }

    private void initComponents() {
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(new Color(45, 62, 80));

        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(new Color(41, 128, 185));
        headerPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        JLabel lblTitle = new JLabel("HE THONG QUAN LY SINH VIEN", JLabel.CENTER);
        lblTitle.setFont(new Font("Arial", Font.BOLD, 16));
        lblTitle.setForeground(Color.WHITE);
        headerPanel.add(lblTitle);

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(new Color(236, 240, 241));
        formPanel.setBorder(new EmptyBorder(30, 40, 30, 40));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(8, 5, 8, 5);

        JLabel lblUser = new JLabel("Ten dang nhap:");
        lblUser.setFont(new Font("Arial", Font.PLAIN, 13));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0.3;
        formPanel.add(lblUser, gbc);

        txtUsername = new JTextField();
        txtUsername.setFont(new Font("Arial", Font.PLAIN, 13));
        txtUsername.setPreferredSize(new Dimension(180, 30));
        gbc.gridx = 1;
        gbc.weightx = 0.7;
        formPanel.add(txtUsername, gbc);

        JLabel lblPass = new JLabel("Mat khau:");
        lblPass.setFont(new Font("Arial", Font.PLAIN, 13));
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0.3;
        formPanel.add(lblPass, gbc);

        txtPassword = new JPasswordField();
        txtPassword.setFont(new Font("Arial", Font.PLAIN, 13));
        txtPassword.setPreferredSize(new Dimension(180, 30));
        gbc.gridx = 1;
        gbc.weightx = 0.7;
        formPanel.add(txtPassword, gbc);

        JButton btnLogin = new JButton("Dang nhap");
        btnLogin.setFont(new Font("Arial", Font.BOLD, 13));
        btnLogin.setBackground(new Color(41, 128, 185));
        btnLogin.setForeground(Color.WHITE);
        btnLogin.setFocusPainted(false);
        btnLogin.setPreferredSize(new Dimension(120, 35));
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        formPanel.add(btnLogin, gbc);

        JLabel lblHint = new JLabel("Goi y: pdt01/pdt123", JLabel.CENTER);
        lblHint.setFont(new Font("Arial", Font.ITALIC, 10));
        lblHint.setForeground(Color.GRAY);
        gbc.gridy = 3;
        formPanel.add(lblHint, gbc);

        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(formPanel, BorderLayout.CENTER);
        setContentPane(mainPanel);

        btnLogin.addActionListener((ActionEvent e) -> actionPerformed());
        txtPassword.addActionListener((ActionEvent e) -> actionPerformed());
    }

    private void actionPerformed() {
        String username = txtUsername.getText().trim();
        String password = new String(txtPassword.getPassword()).trim();

        User user = userDAO.checkLogin(username, password);
        if (user == null) {
            JOptionPane.showMessageDialog(this,
                    "Sai tai khoan hoac mat khau!", "Loi dang nhap", JOptionPane.ERROR_MESSAGE);
            return;
        }

        dispose();
        new AcademicAffairsHomeFrm(user).setVisible(true);
    }
}
