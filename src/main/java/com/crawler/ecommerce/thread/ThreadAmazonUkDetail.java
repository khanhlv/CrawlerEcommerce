package com.crawler.ecommerce.thread;


import com.crawler.ecommerce.dao.DataDAO;
import com.crawler.ecommerce.model.Data;
import com.crawler.ecommerce.parser.AmazonUkParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class ThreadAmazonUkDetail implements Runnable {

    private static final Logger logger = LoggerFactory.getLogger(ThreadAmazonUkDetail.class);
    private AmazonUkParser amazonParser = new AmazonUkParser();
    private DataDAO dataDAO = new DataDAO();

    private String threadName = "THREAD_DATA_";

    public ThreadAmazonUkDetail(int threadCount) {
        this.threadName = this.threadName + threadCount;
        System.out.println("START_THREAD_DATA_" + threadCount);
    }

    @Override
    public void run() {
        try {
            while (true) {
                List<Data> contentList = dataDAO.queueList(10);

                for(Data data : contentList){
                    try {
                        logger.debug(this.threadName + "## GET_START [URL=" + data.getLink() + "]");

                        long start = System.currentTimeMillis();

                        Data content = amazonParser.readDetail(data.getLink(), data.getCode(), data.getId());

                        long end = System.currentTimeMillis() - start;

                        logger.debug(this.threadName + "## GET_END [URL=" + data.getLink() + "][TIME=" + end  + "]");

                        if (content.getPrice() <= 0) {
                            dataDAO.updateDataStatus(data.getId(), 2);
                        } else {
                            dataDAO.updateData(content);
                        }

                        dataDAO.updateData(content);

                        Thread.sleep(200);
                    } catch (Exception ex) {
                        logger.error(this.threadName + " ## ERROR[" + data.getLink() + "]", ex);
                        dataDAO.updateDataStatus(data.getId(), -1);
                    }
                }

                Thread.sleep(1 * 30 * 1000);
            }
        } catch (Exception ex) {
            logger.error(this.threadName + " ## ERROR[ThreadAmazonUKDetail]", ex);
        }
    }
}
