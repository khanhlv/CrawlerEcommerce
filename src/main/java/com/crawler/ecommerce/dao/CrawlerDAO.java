package com.crawler.ecommerce.dao;

import com.crawler.ecommerce.core.ConnectionPool;
import com.crawler.ecommerce.model.Crawler;
import com.crawler.ecommerce.model.Queue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CrawlerDAO {

    private static final Logger logger = LoggerFactory.getLogger(CrawlerDAO.class);

    public List<Crawler> crawlerList() throws SQLException {

        List<Crawler> crawlerList = new ArrayList<>();
        String sqlStory = "SELECT * FROM crawler";
        try (Connection con = ConnectionPool.getTransactional();
             PreparedStatement pStmt = con.prepareStatement(sqlStory);
             ResultSet resultSet = pStmt.executeQuery()) {

            while(resultSet.next()) {
                Crawler crawler = new Crawler();
                crawler.setId(resultSet.getString("id"));
                crawler.setName(resultSet.getString("name"));
                crawler.setSite(resultSet.getString("site"));
                crawler.setUrl(resultSet.getString("url"));
                crawler.setPage(resultSet.getInt("page"));
                crawlerList.add(crawler);
            }
        } catch (Exception ex) {
            throw ex;
        }

        return crawlerList;
    }


    public List<Queue> queueList(int limit) throws SQLException {

        List<Queue> queueList = new ArrayList<>();
        String sqlStory = "SELECT * FROM queue WHERE status = 0 LIMIT ?";
        try (Connection con = ConnectionPool.getTransactional();
             PreparedStatement pStmt = con.prepareStatement(sqlStory)) {

            pStmt.setInt(1, limit);

            ResultSet resultSet = pStmt.executeQuery();
            while(resultSet.next()) {

                Queue queue = new Queue();
                queue.setId(resultSet.getInt("id"));
                queue.setLink(resultSet.getString("link"));
                queue.setNote(resultSet.getString("note"));
                queue.setSize(resultSet.getInt("size"));
                queue.setStatus(resultSet.getInt("link"));

                queue.setCreatedAgent(resultSet.getString("created_agent"));
                queue.setUpdatedAgent(resultSet.getString("updated_agent"));
                queue.setCreatedDate(resultSet.getTimestamp("created_date"));
                queue.setUpdatedDate(resultSet.getTimestamp("updated_date"));

                updateQueueStatus(queue.getId());

                queueList.add(queue);
            }
            resultSet.close();
        } catch (Exception ex) {
            throw ex;
        }

        return queueList;
    }

    public void updateQueueStatus(int id) throws SQLException {
        String sqlStory = "UPDATE queue SET status = 1 WHERE id = ?";
        try (Connection con = ConnectionPool.getTransactional();
             PreparedStatement pStmt = con.prepareStatement(sqlStory)) {

            pStmt.setInt(1, id);

            pStmt.executeUpdate();
        } catch (Exception ex) {
            throw ex;
        }
    }

    public static void main(String[] args) {
        try {
            new CrawlerDAO().queueList(5).stream().forEach(v -> {
                System.out.println(v.getId());
            });
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
