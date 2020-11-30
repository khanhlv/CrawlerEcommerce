package com.crawler.ecommerce;

import org.apache.commons.lang3.math.NumberUtils;

import com.crawler.ecommerce.core.ShareApplication;
import com.crawler.ecommerce.enums.Crawler;
import com.crawler.ecommerce.enums.ThreadMod;
import com.crawler.ecommerce.thread.StartThread;
import com.crawler.ecommerce.util.ResourceUtil;

public class Application {

    public static void main(String[] args) throws Exception {

        if (args != null && args.length == 3) {
            String crawler = args[0];
            String threadMod = args[1];
            String crawlerAgent = args[2];
            System.out.println(String.format("CRAWLER [%s] THREAD_MOD [%s] CRAWLER_AGENT [%s]", crawler, threadMod, crawlerAgent));

            ShareApplication.crawler = Crawler.valueOf(crawler);
            ShareApplication.crawlerAgent = crawlerAgent;

            new StartThread().execute(Crawler.valueOf(crawler), ThreadMod.valueOf(threadMod));
        } else {
            System.out.println("CRAWLER: AMAZON_CO_UK, AMAZON_COM");
            System.out.println("THREAD_MOD:  ALL, SINGLE_DETAIL, SINGLE_CATEGORY");
            System.out.println("CRAWLER_AGENT:  AGENT_WINDOW....");
        }
    }
}
