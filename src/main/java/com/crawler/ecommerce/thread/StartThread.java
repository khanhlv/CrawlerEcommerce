package com.crawler.ecommerce.thread;


public class StartThread {
    public void execute(int threadCount) throws Exception {
        new Thread(new ThreadShareQueue()).start();
        Thread.sleep(5000);

        for (int i = 1; i <= threadCount; i++) {
            new Thread(new ThreadAmazonUk(i)).start();
            Thread.sleep(5000);
        }

        new Thread(new ThreadAmazonUkDetail(1)).start();
    }
}
