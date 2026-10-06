package com.example.projectcalctool.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * Bro mellem Spring og vores vanilla JDBC-repositories.
 * Faar DataSource ind fra Spring (konfigureret via application-*.properties)
 * og gemmer den statisk saa repositories kan kalde getConnection() direkte.
 */
@Component
public class DBUtil {

    private static DataSource dataSource;

    @Autowired
    public DBUtil(DataSource dataSource) {
        DBUtil.dataSource = dataSource;
    }

    public static Connection getConnection() throws SQLException {
        if (dataSource == null) {
            throw new SQLException("DataSource not initialized yet");
        }
        return dataSource.getConnection();
    }
}
