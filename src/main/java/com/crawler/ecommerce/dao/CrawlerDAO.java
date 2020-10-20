package com.crawler.ecommerce.dao;

import com.crawler.ecommerce.core.ConnectionPool;
import com.crawler.ecommerce.model.Crawler;
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
                crawlerList.add(crawler);
            }
        } catch (Exception ex) {
            throw ex;
        }

        return crawlerList;
    }
}
