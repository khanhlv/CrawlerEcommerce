package com.crawler.ecommerce.thread;

import com.crawler.ecommerce.enums.Crawler;
import com.crawler.ecommerce.enums.ThreadMod;
import com.crawler.ecommerce.proxy.ProxyProvider;
import com.crawler.ecommerce.thread.amazon.co.uk.ThreadAmazonUk;
import com.crawler.ecommerce.thread.amazon.co.uk.ThreadAmazonUkDetail;
import com.crawler.ecommerce.thread.amazon.com.ThreadAmazonCom;
import com.crawler.ecommerce.thread.amazon.com.ThreadAmazonComDetail;

public class StartThread {
    public void execute(int threadCount, Crawler crawler, ThreadMod threadMod) throws Exception {

        if (Crawler.AMAZON_CO_UK.equals(crawler)) {
            switch (threadMod) {
                case ALL:
                    new Thread(new ThreadShareQueue()).start();
                    Thread.sleep(5000);

                    for (int i = 1; i <= threadCount; i++) {
                        new Thread(new ThreadAmazonUk(i)).start();
                        new Thread(new ThreadAmazonUkDetail(i)).start();
                        Thread.sleep(5000);
                    }
                    break;
                case SINGLE_DETAIL:
                    ProxyProvider.setup();

                    for (int i = 1; i <= threadCount; i++) {
                        new Thread(new ThreadAmazonUkDetail(i)).start();
                        Thread.sleep(5000);
                    }
                    break;
                case SINGLE_CATEGORY:
                    new Thread(new ThreadShareQueue()).start();
                    Thread.sleep(5000);

                    for (int i = 1; i <= threadCount; i++) {
                        new Thread(new ThreadAmazonUk(i)).start();
                        Thread.sleep(5000);
                    }
                    break;
            }
        }

        if (Crawler.AMAZON_COM.equals(crawler)) {
            switch (threadMod) {
                case ALL:
                    new Thread(new ThreadShareQueue()).start();
                    Thread.sleep(5000);

                    for (int i = 1; i <= threadCount; i++) {
                        new Thread(new ThreadAmazonCom(i)).start();
                        new Thread(new ThreadAmazonComDetail(i)).start();
                        Thread.sleep(5000);
                    }
                    break;
                case SINGLE_DETAIL:
                    ProxyProvider.setup();

                    for (int i = 1; i <= threadCount; i++) {
                        new Thread(new ThreadAmazonComDetail(i)).start();
                        Thread.sleep(5000);
                    }
                    break;
                case SINGLE_CATEGORY:
                    new Thread(new ThreadShareQueue()).start();
                    Thread.sleep(5000);

                    for (int i = 1; i <= threadCount; i++) {
                        new Thread(new ThreadAmazonCom(i)).start();
                        Thread.sleep(5000);
                    }
                    break;
            }
        }
    }
}
