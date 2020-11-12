package com.crawler.ecommerce;

import com.crawler.ecommerce.core.ShareApplication;
import com.crawler.ecommerce.enums.Crawler;
import com.crawler.ecommerce.enums.ThreadMod;
import com.crawler.ecommerce.thread.StartThread;

public class Application {


    public static void main(String[] args) throws Exception {
        System.out.println("Crawler: AMAZON_CO_UK, AMAZON_COM");
        System.out.println("ThreadMod:  ALL, SINGLE_DETAIL, SINGLE_CATEGORY");

        if (args != null && args.length == 2) {
            String crawler = args[0];
            String threadMod = args[1];

            ShareApplication.crawler = Crawler.valueOf(crawler);

            new StartThread().execute(4, Crawler.valueOf(crawler), ThreadMod.valueOf(threadMod));
        }
    }
}
