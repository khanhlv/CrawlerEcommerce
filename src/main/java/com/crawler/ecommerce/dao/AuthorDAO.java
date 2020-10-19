package com.crawler.ecommerce.dao;

import com.crawler.ecommerce.core.ConnectionPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;

public class AuthorDAO {

    private static final Logger logger = LoggerFactory.getLogger(AuthorDAO.class);

    public boolean checkExistsAuthor(int authorId, int storyId) throws SQLException {
        String sqlStory = "SELECT * FROM STORY_AUTHOR WHERE AUTHOR_ID = ? AND STORY_ID = ?";
        try (Connection con = ConnectionPool.getTransactional();
             PreparedStatement pStmt = con.prepareStatement(sqlStory)) {
            pStmt.setInt(1, authorId);
            pStmt.setInt(2, storyId);

            if (pStmt.executeQuery().next()) {
                return true;
            }
        } catch (Exception ex) {
            throw ex;
        }
        return false;
    }

    public void insertStoryAuthor(int authorId, int storyId) throws SQLException {
        String sqlStory = "INSERT INTO STORY_AUTHOR(AUTHOR_ID,STORY_ID) VALUES(?,?)";
        try (Connection con = ConnectionPool.getTransactional();
             PreparedStatement pStmt = con.prepareStatement(sqlStory)) {

            pStmt.setInt(1, authorId);
            pStmt.setInt(2, storyId);

            int affectedRows = pStmt.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Creating user failed, no rows affected.");
            }
        } catch (Exception ex) {
            throw ex;
        }
    }

    public int checkExists(String authorName) throws SQLException {
        String sqlStory = "SELECT * FROM AUTHOR WHERE AUTHOR_NAME = ?";
        try (Connection con = ConnectionPool.getTransactional();
             PreparedStatement pStmt = con.prepareStatement(sqlStory)) {

            pStmt.setString(1, authorName);

            ResultSet rs = pStmt.executeQuery();

            if (rs.next()) {
                return rs.getInt("AUTHOR_ID");
            }
        } catch (Exception ex) {
            throw ex;
        }

        return -1;
    }
}
