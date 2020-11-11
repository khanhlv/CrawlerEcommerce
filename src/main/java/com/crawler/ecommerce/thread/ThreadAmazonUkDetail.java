package com.crawler.ecommerce.thread;


import java.util.List;

import org.apache.commons.lang3.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.crawler.ecommerce.dao.DataDAO;
import com.crawler.ecommerce.model.Data;
import com.crawler.ecommerce.parser.AmazonUkParser;
import com.crawler.ecommerce.util.ResourceUtil;

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
                int limit = NumberUtils.toInt(ResourceUtil.getValue("data.crawler.limit"));

                List<Data> contentList = dataDAO.queueList(limit);

                for(Data data : contentList){
                    try {
                        logger.debug(this.threadName + "## GET_START [URL=" + data.getLink() + "]");

                        long start = System.currentTimeMillis();

                        Data content = amazonParser.readDetail(data.getLink(), data.getCode(), data.getId());

                        long end = System.currentTimeMillis() - start;

                        logger.debug(this.threadName + "## GET_END [URL=" + data.getLink() + "][TIME=" + end  + "]");

                        if (content.getPrice() > 0) {
                            dataDAO.updateData(content);
                        } else {
                            dataDAO.updateDataStatus(data.getId(), 2);
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
