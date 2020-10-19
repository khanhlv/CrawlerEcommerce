package com.crawler.ecommerce.core;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

public class ConnectionPool {
    private static HikariConfig config = new HikariConfig();
    private static HikariDataSource ds;

    static {
        InputStream resourceAsStream = Thread.currentThread().getContextClassLoader().getResourceAsStream("config.properties");

        Properties properties = new Properties();
        if (resourceAsStream == null) {
            throw new IllegalArgumentException("Config file config.properties not found in classpath");
        } else {
            try {
                properties.load(resourceAsStream);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        config.setPoolName("E-commerceHikariCP");
        config.setConnectionTestQuery("SELECT 1");
        config.setMaximumPoolSize(20);
        config.setMinimumIdle(5);

        config.setDriverClassName(properties.getProperty("datasource.driver-class-name"));
        config.setJdbcUrl(properties.getProperty("datasource.url"));
        config.setUsername(properties.getProperty("datasource.username"));
        config.setPassword(properties.getProperty("datasource.password"));

        config.addDataSourceProperty("cachePrepStmts", "true");
        config.addDataSourceProperty("prepStmtCacheSize", "250");
        config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
        config.addDataSourceProperty("useServerPrepStmts", "true");


        ds = new HikariDataSource(config);
    }

    private ConnectionPool() { }

    public static Connection getTransactional() throws SQLException {
        return ds.getConnection();
    }
}

