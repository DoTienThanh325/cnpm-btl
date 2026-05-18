package com.sms.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {
    private static final String HOST = envOrDefault("SMS_DB_HOST", "localhost");
    private static final String PORT = envOrDefault("SMS_DB_PORT", "3306");
    private static final String NAME = envOrDefault("SMS_DB_NAME", "sms");
    private static final String USER = envOrDefault("SMS_DB_USER", "sms");
    private static final String PASS = envOrDefault("SMS_DB_PASSWORD", "sms");

    private static final String URL =
            "jdbc:mysql://" + HOST + ":" + PORT + "/" + NAME
                    + "?useUnicode=true&characterEncoding=UTF-8&useSSL=false"
                    + "&allowPublicKeyRetrieval=true&serverTimezone=UTC";

    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("MySQL JDBC driver not on classpath", e);
        }
    }

    public static Connection get() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASS);
    }

    private static String envOrDefault(String key, String fallback) {
        String v = System.getenv(key);
        return (v == null || v.isEmpty()) ? fallback : v;
    }
}
