package com.crawler.ecommerce.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.crawler.ecommerce.core.ConnectionPool;

public class SettingDAO {

    public String getValue(String key) throws SQLException {
        String result = null;

        String sqlStory = "SELECT * FROM `setting` where `setting_key` = ? and `status` = 1";
        try (Connection con = ConnectionPool.getTransactional();
             PreparedStatement pStmt = con.prepareStatement(sqlStory)) {

            pStmt.setString(1, key);

            ResultSet resultSet = pStmt.executeQuery();

            if (resultSet.next()) {
                result = resultSet.getString("setting_value");
            }

            resultSet.close();
        } catch (Exception ex) {
            throw ex;
        }

        return result;
    }

    public void updateStatus(String key, int status) throws SQLException {
        String sqlStory = "UPDATE `setting` SET `status` = ? WHERE `setting_key` = ?";
        try (Connection con = ConnectionPool.getTransactional();
             PreparedStatement pStmt = con.prepareStatement(sqlStory)) {

            pStmt.setInt(1, status);
            pStmt.setString(2, key);

            pStmt.executeUpdate();
        } catch (Exception ex) {
            throw ex;
        }
    }
}
