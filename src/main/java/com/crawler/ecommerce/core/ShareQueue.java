package com.crawler.ecommerce.core;

import com.crawler.ecommerce.model.Queue;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedDeque;

public final class ShareQueue {
    public static ConcurrentLinkedDeque<String> shareQueue = new ConcurrentLinkedDeque<>();
    public final static int QUEUE_SIZE = 1;
    public final static int QUEUE_SIZE_LIMIT = 1;

    public static void addItem(List<Queue> queueList) {
        if (shareQueue.size() < QUEUE_SIZE_LIMIT) {
            for (Queue queue : queueList) {
                String link = queue.getId() + "|" + queue.getLink();
                if (!shareQueue.contains(link)) {
                    shareQueue.add(link);
                }
            }
        }
    }

    public static List<String> getItem() {
        List<String> listItem = new ArrayList<>();

        int size = shareQueue.size() > QUEUE_SIZE ? QUEUE_SIZE : shareQueue.size();

        for (int i = 0; i < size; i++) {
            listItem.add(shareQueue.poll());
        }

        return listItem;
    }
}
