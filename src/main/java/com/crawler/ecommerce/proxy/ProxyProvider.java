package com.crawler.ecommerce.proxy;

import com.crawler.ecommerce.core.ShareQueue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.ArrayList;

public class ProxyProvider {
    private static final Logger logger = LoggerFactory.getLogger(ProxyProvider.class);
    static  ArrayList<String> duplicateStrings;

    public static void setup() {
        duplicateStrings = new ArrayList();

        FPLNetSource fplNetSource =  new FPLNetSource();
        HideMySource hideMySource  =  new HideMySource();
        SSLProxiesOrgSource sslProxiesOrgSource =  new SSLProxiesOrgSource();

        sslProxiesOrgSource.proxy().stream().forEach(data -> addProxyItem(data));

        hideMySource.proxy().stream().forEach(data -> addProxyItem(data));

        fplNetSource.proxy().stream().forEach(data -> addProxyItem(data));

        logger.debug("PROXY_SIZE [{}]" , ShareQueue.socketAddressList.size());
    }

    public static void addProxyItem(InetSocketAddress data) {
        if (!duplicateStrings.contains(data.toString())) {
            duplicateStrings.add(data.toString());

            ShareQueue.socketAddressList.add(data);
        }
    }

    public static void main(String[] args) {
        ProxyProvider.setup();
    }
}
