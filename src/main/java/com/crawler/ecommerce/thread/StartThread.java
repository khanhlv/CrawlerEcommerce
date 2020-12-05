package com.crawler.ecommerce.thread;

import com.crawler.ecommerce.enums.Crawler;
import com.crawler.ecommerce.enums.ThreadMod;
import com.crawler.ecommerce.proxy.ProxyProvider;
import com.crawler.ecommerce.thread.amazon.co.uk.ThreadAmazonUk;
import com.crawler.ecommerce.thread.amazon.co.uk.ThreadAmazonUkDetail;
import com.crawler.ecommerce.thread.amazon.com.ThreadAmazonCom;
import com.crawler.ecommerce.thread.amazon.com.ThreadAmazonComDetail;
import com.crawler.ecommerce.util.ResourceUtil;
import org.apache.commons.lang3.math.NumberUtils;

public class StartThread {
    public void execute(Crawler crawler, ThreadMod threadMod) throws Exception {

        int threadCategoryCount = NumberUtils.toInt(ResourceUtil.getValue("data.crawler.queue.category.thread"), 1);
        int threadDataCount = NumberUtils.toInt(ResourceUtil.getValue("data.crawler.queue.data.thread"), 1);

        if (Crawler.AMAZON_CO_UK.equals(crawler)) {
            switch (threadMod) {
                case SINGLE_DETAIL:
                    ProxyProvider.startThread();

                    new Thread(new ThreadShareItemQueue()).start();
                    Thread.sleep(5000);

                    for (int i = 1; i <= threadDataCount; i++) {
                        new Thread(new ThreadAmazonUkDetail(i)).start();
                        Thread.sleep(5000);
                    }
                    break;
                case SINGLE_CATEGORY:
                    new Thread(new ThreadShareQueue()).start();
                    Thread.sleep(5000);

                    for (int i = 1; i <= threadCategoryCount; i++) {
                        new Thread(new ThreadAmazonUk(i)).start();
                        Thread.sleep(5000);
                    }
                    break;
            }
        }

        if (Crawler.AMAZON_COM.equals(crawler)) {
            switch (threadMod) {
                case SINGLE_DETAIL:
                    ProxyProvider.startThread();

                    new Thread(new ThreadShareItemQueue()).start();
                    Thread.sleep(5000);

                    for (int i = 1; i <= threadDataCount; i++) {
                        new Thread(new ThreadAmazonComDetail(i)).start();
                        Thread.sleep(5000);
                    }
                    break;
                case SINGLE_CATEGORY:
                    new Thread(new ThreadShareQueue()).start();
                    Thread.sleep(5000);

                    for (int i = 1; i <= threadCategoryCount; i++) {
                        new Thread(new ThreadAmazonCom(i)).start();
                        Thread.sleep(5000);
                    }
                    break;
            }
        }
    }
}
