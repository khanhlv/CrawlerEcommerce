package com.crawler.ecommerce.thread;

import java.io.File;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.crawler.ecommerce.core.ShareQueue;
import com.crawler.ecommerce.dao.CrawlerDAO;
import com.crawler.ecommerce.model.Queue;

public class ThreadShareQueue implements Runnable {

    private static final Logger logger = LoggerFactory.getLogger(ThreadShareQueue.class);
    private CrawlerDAO crawlerDAO = new CrawlerDAO();
    private static final String PATH_QUEUE = "data/queue.txt";

    public ThreadShareQueue(){
        System.out.println("START_THREAD_QUEUE");
    }

    @Override
    public void run() {
        try {
            File fileQueue = new File(PATH_QUEUE);

            if (fileQueue.exists()) {
                List<String> stringList = FileUtils.readLines(fileQueue, "UTF-8");

                ShareQueue.shareQueue.addAll(stringList);
            }

            while (true) {
                if (ShareQueue.shareQueue.size() < ShareQueue.QUEUE_SIZE_LIMIT) {
                    int limit = ShareQueue.QUEUE_SIZE_LIMIT - ShareQueue.shareQueue.size();

                    List<Queue> queueList = crawlerDAO.queueList(limit);

                    if (queueList != null && queueList.size() > 0) {
                        ShareQueue.addItem(queueList);
                    }

                    FileUtils.writeLines(fileQueue, "UTF-8", ShareQueue.shareQueue);
                }

                System.out.println("SHARE_QUEUE=" + ShareQueue.shareQueue.size());

                Thread.sleep(2 * 60 * 1000);
            }
        } catch (Exception ex) {
            logger.error("ERROR[ThreadShareQueue]", ex);
        }
    }
}
