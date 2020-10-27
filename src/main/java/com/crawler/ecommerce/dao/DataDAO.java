package com.crawler.ecommerce.dao;

import com.crawler.ecommerce.core.ConnectionPool;
import com.crawler.ecommerce.model.Data;
import com.crawler.ecommerce.util.ResourceUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class DataDAO {

    public boolean hasExistsCode(String code) throws SQLException {
        String sqlStory = "SELECT code FROM " + ResourceUtil.getValue("data.crawler.table") + " WHERE code = ?";
        try (Connection con = ConnectionPool.getTransactional();
             PreparedStatement pStmt = con.prepareStatement(sqlStory)) {

            pStmt.setString(1, code);

            if (pStmt.executeQuery().next()) {
                return true;
            }
        } catch (Exception ex) {
            throw ex;
        }

        return false;
    }

    public void insert(Data data) throws SQLException {

        if (!hasExistsCode(data.getCode())) {
            String sqlStory = "INSERT INTO " + ResourceUtil.getValue("data.crawler.table") + "(code, name, image, link, price, rating, comment_count, site, category, status) " +
                    "VALUES (?,?,?,?,?,?,?,?,?,0)";
            try (Connection con = ConnectionPool.getTransactional();
                 PreparedStatement pStmt = con.prepareStatement(sqlStory)) {

                pStmt.setString(1, data.getCode());
                pStmt.setString(2, data.getName());
                pStmt.setString(3, data.getImage());
                pStmt.setString(4, data.getLink());
                pStmt.setDouble(5, data.getPrice());
                pStmt.setDouble(6, data.getRating());
                pStmt.setInt(7, data.getComment_count());
                pStmt.setString(8, data.getSite());
                pStmt.setString(9, data.getCategory());

                pStmt.executeUpdate();
            } catch (Exception ex) {
                throw ex;
            }
        }
    }
}
