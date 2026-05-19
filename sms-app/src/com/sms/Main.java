package com.sms;

import com.sms.view.LoginFrm;
import javax.swing.*;

/**
 * Main entry point for SMS - Student Management System
 * Per spec: Hệ thống Quản lý Sinh viên
 */
public class Main {
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
        } catch (Exception e) {
            // use default look and feel
        }
        SwingUtilities.invokeLater(() -> {
            LoginFrm loginFrm = new LoginFrm();
            loginFrm.setVisible(true);
        });
    }
}
