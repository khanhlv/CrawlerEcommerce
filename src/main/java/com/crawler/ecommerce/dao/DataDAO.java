package com.crawler.ecommerce.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.crawler.ecommerce.core.ConnectionPool;
import com.crawler.ecommerce.core.ShareApplication;
import com.crawler.ecommerce.model.Data;

public class DataDAO {

    public List<Data> queueList(int limit) throws SQLException {

        List<Data> dataList = new ArrayList<>();
        String sqlStory = "SELECT * FROM " + ShareApplication.crawler.getTableData() + " WHERE status = 0 LIMIT ?";
        try (Connection con = ConnectionPool.getTransactional();
             PreparedStatement pStmt = con.prepareStatement(sqlStory)) {

            pStmt.setInt(1, limit);

            ResultSet resultSet = pStmt.executeQuery();
            while(resultSet.next()) {

                Data data = new Data();
                data.setId(resultSet.getInt("id"));
                data.setCode(resultSet.getString("code"));
                data.setName(resultSet.getString("name"));
                data.setImage(resultSet.getString("image"));
                data.setLink(resultSet.getString("link"));
                data.setProperties(resultSet.getString("properties"));
                data.setDescription(resultSet.getString("description"));
                data.setContent(resultSet.getString("content"));
                data.setPrice(resultSet.getDouble("price"));
                data.setRating(resultSet.getDouble("rating"));
                data.setComment_count(resultSet.getInt("comment_count"));
                data.setCategory(resultSet.getString("category"));
                data.setSite(resultSet.getString("site"));
                data.setShop(resultSet.getString("shop"));
                data.setStatus(resultSet.getInt("status"));

                dataList.add(data);
            }

            resultSet.close();
        } catch (Exception ex) {
            throw ex;
        }

        return dataList;
    }

    public void updateData(Data data) throws SQLException {
        String sqlStory = "UPDATE " + ShareApplication.crawler.getTableData() + " SET price = ?, properties = ?, description = ?, shop = ?, rating = ?, comment_count = ?, status = 1, " +
                "updated_date = now() WHERE id = ?";
        try (Connection con = ConnectionPool.getTransactional();
             PreparedStatement pStmt = con.prepareStatement(sqlStory)) {

            pStmt.setDouble(1, data.getPrice());
            pStmt.setString(2, data.getProperties());
            pStmt.setString(3, data.getDescription());
            pStmt.setString(4, data.getShop());
            pStmt.setDouble(5, data.getRating());
            pStmt.setInt(6, data.getComment_count());

            pStmt.setInt(7, data.getId());

            pStmt.executeUpdate();
        } catch (Exception ex) {
            throw ex;
        }
    }

    public void updateDataStatus(int id, int status) throws SQLException {
        String sqlStory = "UPDATE " + ShareApplication.crawler.getTableData() + " SET status = ? WHERE id = ?";
        try (Connection con = ConnectionPool.getTransactional();
             PreparedStatement pStmt = con.prepareStatement(sqlStory)) {

            pStmt.setInt(1, status);
            pStmt.setInt(2, id);

            pStmt.executeUpdate();
        } catch (Exception ex) {
            throw ex;
        }
    }

    public boolean hasExistsCode(String code) throws SQLException {
        String sqlStory = "SELECT code FROM " + ShareApplication.crawler.getTableData() + " WHERE code = ?";
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
            String sqlStory = "INSERT INTO " + ShareApplication.crawler.getTableData() + "(code, name, image, link, price, rating, comment_count, site, category, status) " +
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
