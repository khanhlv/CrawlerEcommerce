package com.crawler.ecommerce.thread;

import com.crawler.ecommerce.core.ShareQueue;
import com.crawler.ecommerce.dao.CrawlerDAO;
import com.crawler.ecommerce.model.Queue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class ThreadShareQueue implements Runnable {

    private static final Logger logger = LoggerFactory.getLogger(ThreadShareQueue.class);
    private CrawlerDAO crawlerDAO = new CrawlerDAO();

    public ThreadShareQueue(){
        System.out.println("START_THREAD_QUEUE");
    }

    @Override
    public void run() {
        try {
            while (true) {

                if (ShareQueue.shareQueue.size() < ShareQueue.QUEUE_SIZE_LIMIT) {
                    List<Queue> queueList = crawlerDAO.queueList(ShareQueue.QUEUE_SIZE_LIMIT);

                    if (queueList != null && queueList.size() > 0) {
                        ShareQueue.addItem(queueList);
                    }
                }

                System.out.println("SHARE_QUEUE=" + ShareQueue.shareQueue.size());

                Thread.sleep(1 * 60 * 1000);
            }
        } catch (Exception ex) {
            logger.error("ERROR[ThreadShareQueue]", ex);
        }
    }
}
