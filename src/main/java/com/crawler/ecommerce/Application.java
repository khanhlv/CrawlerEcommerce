package com.crawler.ecommerce;

import com.crawler.ecommerce.thread.StartThread;
import com.crawler.ecommerce.thread.ThreadAmazonUkDetail;

public class Application {
    public static void main(String[] args) throws Exception {

        if (args != null && args[0].equals("1")) {
            new Thread(new ThreadAmazonUkDetail(1)).start();
        }

        if (args != null && args[0].equals("2")) {
            new StartThread().execute(4);
        }

        if (args != null && args[0].equals("3")) {
            new StartThread().execute(4);

            new Thread(new ThreadAmazonUkDetail(1)).start();
        }
    }
}
