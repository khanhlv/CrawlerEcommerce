package com.crawler.ecommerce.proxy;

import com.crawler.ecommerce.core.ShareQueue;

public class ProxyProvider {

    public static void setup() {
        FPLNetSource fplNetSource =  new FPLNetSource();
        HideMySource hideMySource  =  new HideMySource();
        SSLProxiesOrgSource sslProxiesOrgSource =  new SSLProxiesOrgSource();

        ShareQueue.socketAddressList.addAll(sslProxiesOrgSource.proxy());
        ShareQueue.socketAddressList.addAll(hideMySource.proxy());
        ShareQueue.socketAddressList.addAll(fplNetSource.proxy());

        System.out.println("PROXY_SIZE=" + ShareQueue.socketAddressList.size());
    }

    public static void main(String[] args) {
        ProxyProvider.setup();
    }
}
