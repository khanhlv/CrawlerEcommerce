package com.crawler.ecommerce;

import com.crawler.ecommerce.thread.StartThread;

public class Application {
    public static void main(String[] args) throws Exception {
        new StartThread().execute(4);
    }
}
