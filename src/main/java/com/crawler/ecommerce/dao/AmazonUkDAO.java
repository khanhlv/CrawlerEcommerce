package com.crawler.ecommerce.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.apache.commons.lang3.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.crawler.ecommerce.core.ConnectionPool;
import com.crawler.ecommerce.model.Data;

public class AmazonUkDAO {

    private static final Logger logger = LoggerFactory.getLogger(AmazonUkDAO.class);

    public boolean hasExistsCode(String code) throws SQLException {
        String sqlStory = "SELECT code FROM data_amazon_co_uk WHERE code = ?";
        try (Connection con = ConnectionPool.getTransactional();
             PreparedStatement pStmt = con.prepareStatement(sqlStory)) {

            pStmt.setString(0, code);

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
            String sqlStory = "INSERT INTO data_amazon_co_uk(code, name, image, link, price, rating, comment_count, category, site, status) " +
                    "VALUES (?,?,?,?,?,?,?,?,?,0)";
            try (Connection con = ConnectionPool.getTransactional();
                 PreparedStatement pStmt = con.prepareStatement(sqlStory)) {

                pStmt.setString(0, data.getCode());
                pStmt.setString(1, data.getName());
                pStmt.setString(2, data.getImage());
                pStmt.setString(3, data.getLink());
                pStmt.setString(4, data.getPrice());
                pStmt.setString(5, data.getRating());
                pStmt.setInt(6, NumberUtils.toInt(data.getComment_count()));
                pStmt.setString(7, data.getCategory());
                pStmt.setString(8, "AMAZON_CO_UK");
                pStmt.setInt(9, 0);

                pStmt.executeUpdate();
            } catch (Exception ex) {
                throw ex;
            }
        }
    }
}
