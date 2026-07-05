package com.example.SISproject.utils;

import org.springframework.beans.factory.annotation.Value;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;


public class Database {
    private final String url;
    private final String username;
    private final String password;

    public String getPassword() {
        return password;
    }

    public String getUrl() {
        return url;
    }

    public String getUsername() {
        return username;
    }

    public Database() {
        Properties properties = new Properties();

        // Load the properties file from the classpath
        try (InputStream input = getClass().getClassLoader().getResourceAsStream("application.properties")) {
            if (input == null) {
                throw new RuntimeException("Unable to find application.properties");
            }
            properties.load(input);
        } catch (IOException e) {
            throw new RuntimeException("Error loading application.properties", e);
        }

        // Assign the values from the properties file
        this.url = properties.getProperty("db.url");
        this.username = properties.getProperty("db.username");
        this.password = properties.getProperty("db.password");
    }
    protected Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url,username,password);
    }
}
