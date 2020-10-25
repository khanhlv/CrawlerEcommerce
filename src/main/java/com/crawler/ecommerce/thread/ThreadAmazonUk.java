package com.crawler.ecommerce.thread;


import com.crawler.ecommerce.core.ShareQueue;
import com.crawler.ecommerce.parser.AmazonUkParser;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class ThreadAmazonUk implements Runnable {

    private static final Logger logger = LoggerFactory.getLogger(ThreadAmazonUk.class);
    private AmazonUkParser amazonParser = new AmazonUkParser();

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
//                    try {
//
//                        logger.info(this.threadName + " ## GET_START [URL=" + link + "]");
//
//                        long start = System.currentTimeMillis();
//                        Content content = parseTruyenFull.readContent(link);
//                        content.setId(id);
//                        long end = System.currentTimeMillis() - start;
//
//                        logger.info(this.threadName + " ## GET_END [URL=" + link + "][TIME=" + end  + "]");
//
//                        if (StringUtils.isBlank(content.getContent())) {
//                            chapterDAO.updateStatus(id, 2);
//                        } else {
//                            InputStream inputStream = GZipUtil.compress(content.getContent());
//                            int fileSize = inputStream.available();
//
//                            String fileId = GoogleDriverUtil.uploadFile(driveFiles, inputStream, id + ".txt.gz", "1-WvE-4xcD81drSN_5bnOFpAj73rX_RHN");
//
//                            System.out.println(this.threadName + " ## [SIZE=" + fileSize + "][FILE_ID=" + fileId + "]");
//
//                            content.setFileName(fileId);
//                            content.setContent(fileId);
//
//                            chapterDAO.updateChapterContent(content);
//
//                            chapterDAO.updateStatus(id, 1);
//                        }
//
//                    } catch (Exception ex) {
//                        logger.error(this.threadName + " ## ERROR[" + link + "]", ex);
//                        chapterDAO.updateStatus(id, -1);
//                    }
                }

                Thread.sleep( 5000);
            }
        } catch (Exception ex) {
            logger.error(this.threadName + " ## ERROR[ThreadChapter]", ex);
        }
    }
}
