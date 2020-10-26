package com.crawler.ecommerce.thread;


import com.crawler.ecommerce.core.ShareQueue;
import com.crawler.ecommerce.dao.AmazonUkDAO;
import com.crawler.ecommerce.dao.CrawlerDAO;
import com.crawler.ecommerce.model.Data;
import com.crawler.ecommerce.parser.AmazonUkParser;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class ThreadAmazonUk implements Runnable {

    private static final Logger logger = LoggerFactory.getLogger(ThreadAmazonUk.class);
    private AmazonUkParser amazonParser = new AmazonUkParser();
    private AmazonUkDAO amazonUkDAO = new AmazonUkDAO();
    private CrawlerDAO crawlerDAO = new CrawlerDAO();

    private String threadName = "THREAD_";

    public ThreadAmazonUk(int threadCount) {
        this.threadName = this.threadName + threadCount;
        System.out.println("START_THREAD_" + threadCount);
    }

    @Override
    public void run() {
        try {
            while (true) {
                List<String> listContent = ShareQueue.getItem();

                System.out.println(this.threadName + " ## LINK_SIZE [" + listContent.size() + "]");

                for(String data : listContent){
                    String[] str = StringUtils.split(data,"\\|");
                    int id = NumberUtils.toInt(str[0]);
                    String link = str[1];
                    try {

                        logger.debug(this.threadName + " ## GET_START [URL=" + link + "]");

                        link = link.replaceAll("https://www\\.amazon\\.co\\.uk/s", "https://www.amazon.co.uk/s/query");

                        List<Data> listData = amazonParser.readQuery(link);

                        if (listData.size() > 0) {

                            for (Data result : listData) {
                                amazonUkDAO.insert(result);
                            }

                            logger.debug(this.threadName + " ## GET_END [URL={}] [SIZE={}]", link, listData.size());
                        } else {
                            crawlerDAO.updateQueueStatus(id, 2);
                        }
                    } catch (Exception ex) {
                        logger.error(this.threadName + " ## ERROR[" + link + "]", ex);
                        crawlerDAO.updateQueueStatus(id, -1);
                    }
                    Thread.sleep( 1000);
                }

                Thread.sleep( 5000);
            }
        } catch (Exception ex) {
            logger.error(this.threadName + " ## ERROR[ThreadAmazonUK]", ex);
        }
    }
}
