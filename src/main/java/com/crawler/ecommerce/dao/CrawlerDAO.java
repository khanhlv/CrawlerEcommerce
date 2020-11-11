package com.crawler.ecommerce.dao;

import com.crawler.ecommerce.core.ConnectionPool;
import com.crawler.ecommerce.model.Crawler;
import com.crawler.ecommerce.model.Queue;
import com.crawler.ecommerce.util.ResourceUtil;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
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
        String sqlStory = "SELECT * FROM " + ResourceUtil.getValue("data.crawler");
        try (Connection con = ConnectionPool.getTransactional();
             PreparedStatement pStmt = con.prepareStatement(sqlStory);
             ResultSet resultSet = pStmt.executeQuery()) {

            while(resultSet.next()) {
                Crawler crawler = new Crawler();
                crawler.setId(resultSet.getString("id"));
                crawler.setName(resultSet.getString("name"));
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
        String sqlStory = "SELECT * FROM " + ResourceUtil.getValue("data.crawler.queue") + " WHERE status = 0 LIMIT ?";
        try (Connection con = ConnectionPool.getTransactional();
             PreparedStatement pStmt = con.prepareStatement(sqlStory)) {

            pStmt.setInt(1, limit);

            ResultSet resultSet = pStmt.executeQuery();
            while(resultSet.next()) {

                Queue queue = new Queue();
                queue.setId(resultSet.getInt("id"));
                queue.setLink(resultSet.getString("link"));
                queue.setStatus(resultSet.getInt("status"));
                queue.setName(resultSet.getString("name"));

                updateQueueStatus(queue.getId(), 1);

                queueList.add(queue);
            }
            resultSet.close();
        } catch (Exception ex) {
            throw ex;
        }

        return queueList;
    }

    public void updateQueueStatus(int id, int status) throws SQLException {
        String sqlStory = "UPDATE " + ResourceUtil.getValue("data.crawler.queue") + " SET status = ? WHERE id = ?";
        try (Connection con = ConnectionPool.getTransactional();
             PreparedStatement pStmt = con.prepareStatement(sqlStory)) {

            pStmt.setInt(1, status);
            pStmt.setInt(2, id);

            pStmt.executeUpdate();
        } catch (Exception ex) {
            throw ex;
        }
    }

    public void insertQueueFile() throws Exception {
        try {
            StringBuilder data = new StringBuilder();
            crawlerList().stream().forEach(v -> {
                for (int i = 2; i <= v.getPage(); i++) {
                    String url = v.getUrl().replaceAll("#\\{page\\}", i + "");
                    System.out.println(url);

                    data.append(String.format("INSERT INTO %s (link, name, status) VALUES ('%s','%s',0);\n", ResourceUtil.getValue("data.crawler.queue"), url, v.getName()));
                }
            });

            FileUtils.writeStringToFile(new File("data/queue.sql"), data.toString());
        } catch (Exception e) {
            throw e;
        }
    }

    public static void main(String[] args) {
        try {
            CrawlerDAO crawlerDAO = new CrawlerDAO();

            crawlerDAO.insertQueueFile();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
