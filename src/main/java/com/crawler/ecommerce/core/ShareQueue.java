package com.crawler.ecommerce.core;

import java.util.concurrent.ConcurrentLinkedDeque;

public final class ShareQueue {
    public static ConcurrentLinkedDeque<String> shareQueue = new ConcurrentLinkedDeque<>();
}
