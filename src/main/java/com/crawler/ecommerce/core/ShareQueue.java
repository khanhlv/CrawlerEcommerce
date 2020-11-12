package com.crawler.ecommerce.core;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedDeque;

import org.apache.commons.lang3.math.NumberUtils;

import com.crawler.ecommerce.model.Queue;
import com.crawler.ecommerce.util.ResourceUtil;

public final class ShareQueue {
    public static ConcurrentLinkedDeque<String> shareQueue = new ConcurrentLinkedDeque<>();
    public final static int QUEUE_SIZE = NumberUtils.toInt(ResourceUtil.getValue("data.crawler.queue.size"));
    public final static int QUEUE_SIZE_LIMIT = NumberUtils.toInt(ResourceUtil.getValue("data.crawler.queue.limit"));

    public static void addItem(List<Queue> queueList) {
        if (shareQueue.size() < QUEUE_SIZE_LIMIT) {
            for (Queue queue : queueList) {
                String link = queue.getId() + "|" + queue.getLink() + "|" + queue.getName();
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
