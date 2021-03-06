package com.crawler.ecommerce.thread.amazon.co.uk;


import java.util.List;

import org.apache.commons.lang3.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.crawler.ecommerce.core.ShareQueue;
import com.crawler.ecommerce.dao.DataDAO;
import com.crawler.ecommerce.dao.SettingDAO;
import com.crawler.ecommerce.enums.Crawler;
import com.crawler.ecommerce.model.Data;
import com.crawler.ecommerce.parser.AmazonUkParser;
import com.crawler.ecommerce.util.ResourceUtil;

public class ThreadAmazonUkDetail implements Runnable {

    private static final Logger logger = LoggerFactory.getLogger(ThreadAmazonUkDetail.class);
    private AmazonUkParser amazonParser = new AmazonUkParser();
    private DataDAO dataDAO = new DataDAO();
    private SettingDAO settingDAO = new SettingDAO();

    private String threadName = "THREAD_DATA_";

    public ThreadAmazonUkDetail(int threadCount) {
        this.threadName = this.threadName + threadCount;
        System.out.println("START_THREAD_DATA_" + threadCount);
    }

    @Override
    public void run() {
        try {
            while (true) {

                if (ShareQueue.shareQueueItem.size() == 0) {
                    int limit = NumberUtils.toInt(ResourceUtil.getValue("data.crawler.limit"));

                    List<Data> contentList = dataDAO.queueList(limit);

                    ShareQueue.shareQueueItem.addAll(contentList);
                }

                String settingValue = settingDAO.getValue(Crawler.AMAZON_CO_UK.name());

                if (settingValue != null) {

                    Data data = ShareQueue.shareQueueItem.poll();

                    try {
                        logger.debug(this.threadName + "## GET_START [URL=" + data.getLink() + "]");

                        long start = System.currentTimeMillis();

                        Data content = amazonParser.readDetail(data.getLink(), data.getCode(), data.getId(), settingValue);

                        long end = System.currentTimeMillis() - start;

                        logger.debug(this.threadName + "## GET_END [URL=" + data.getLink() + "][TIME=" + end  + "]");

                        if (content == null) {
                            settingDAO.updateStatus(Crawler.AMAZON_CO_UK.name(), 0);

                            dataDAO.updateDataStatus(data.getId(), -1);

                            break;
                        } else {
                            if (content.getPrice() > 0) {
                                dataDAO.updateData(content, 1);
                            } else {
                                content.setPrice(data.getPrice());

                                dataDAO.updateData(content, 1);
                            }
                        }
                    } catch (Exception ex) {
                        logger.error(this.threadName + " ## ERROR[" + data.getLink() + "]", ex);
                        dataDAO.updateDataStatus(data.getId(), -1);
                    }
                } else {
                    System.out.println(this.threadName + "## SETTING_WAITING ...");
                }

                Thread.sleep(500);
            }
        } catch (Exception ex) {
            logger.error(this.threadName + " ## ERROR[ThreadAmazonUKDetail]", ex);
        }
    }
}
