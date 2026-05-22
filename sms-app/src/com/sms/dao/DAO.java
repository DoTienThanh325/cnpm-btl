package com.sms.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public abstract class DAO {
    private static final String DEFAULT_URL = "jdbc:mysql://localhost:3306/sms_db?useUnicode=true&characterEncoding=UTF-8&serverTimezone=Asia/Ho_Chi_Minh&useSSL=false&allowPublicKeyRetrieval=true";
    private static final String DEFAULT_USER = "root";
    private static final String DEFAULT_PASSWORD = "12345khongcho";

    protected Connection getConnection() throws SQLException {
        String url = firstNonBlank(System.getProperty("sms.db.url"), System.getenv("SMS_DB_URL"), DEFAULT_URL);
        String user = firstNonBlank(System.getProperty("sms.db.user"), System.getenv("SMS_DB_USER"), DEFAULT_USER);
        String password = firstNonBlank(System.getProperty("sms.db.password"), System.getenv("SMS_DB_PASSWORD"),
                DEFAULT_PASSWORD);
        return DriverManager.getConnection(url, user, password);
    }

    protected RuntimeException dbError(Exception e) {
        return new RuntimeException("Database error: " + e.getMessage(), e);
    }

    protected String call(String procedure, int parameterCount) {
        StringBuilder sql = new StringBuilder("{CALL ").append(procedure).append("(");
        for (int i = 0; i < parameterCount; i++) {
            if (i > 0) {
                sql.append(", ");
            }
            sql.append("?");
        }
        return sql.append(")}").toString();
    }

    private String firstNonBlank(String... values) {
        for (String value : values) {
            if (value != null && !value.trim().isEmpty()) {
                return value;
            }
        }
        return "";
    }
}
