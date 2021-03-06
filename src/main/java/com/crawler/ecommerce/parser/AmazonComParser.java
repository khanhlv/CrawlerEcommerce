package com.crawler.ecommerce.parser;

import com.crawler.ecommerce.core.Consts;
import com.crawler.ecommerce.core.UserAgent;
import com.crawler.ecommerce.enums.Crawler;
import com.crawler.ecommerce.model.Data;
import com.crawler.ecommerce.proxy.ProxyProvider;
import com.crawler.ecommerce.util.AmazonUtil;
import com.crawler.ecommerce.util.ResourceUtil;
import com.google.gson.Gson;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.util.*;

public class AmazonComParser {
    private static final Logger logger = LoggerFactory.getLogger(AmazonComParser.class);

    public Data readDetail(String url, String code, int id, InetSocketAddress socketAddress) throws Exception {
        Data dataMap = new Data();

        Connection connection = Jsoup.connect(url)
                .userAgent(UserAgent.getUserAgent())
                .timeout(Consts.TIMEOUT)
                .maxBodySize(0);

        if (socketAddress != null) {
            connection.proxy(new Proxy(Proxy.Type.HTTP, socketAddress));
        }

        Document doc = connection.get();

        if (ResourceUtil.getValue("debug").equals("true")) {
            FileUtils.writeStringToFile(new File("data/html/" + code + ".html"), doc.html());
        }

        if (doc.select("form").attr("action").equals("/errors/validateCaptcha")) {
//            logger.debug("PROXY [{}] URL_DETAIL [{}] - VALIDATE_CAPTCHA", (socketAddress != null ? socketAddress.toString() : "N/A"), url);
            return null;
        }

        String priceText = doc.select("span#priceblock_saleprice").text().trim();

        if (StringUtils.isBlank(priceText)) {
            priceText = doc.select("span#priceblock_ourprice").text().trim();
        }

        priceText = StringUtils.isNotBlank(priceText) ? StringUtils.split(priceText, "-")[0] : "0";

        double price = NumberUtils.toDouble(priceText.replaceAll("\\s+", "").replaceAll("\\$", ""));
        String description = doc.select("div#feature-bullets").text();

        String shop = doc.select("span#tabular-buybox-truncate-1 span.tabular-buybox-text").text().trim();
        double rating = NumberUtils.toDouble(doc.select("span#acrPopover").attr("title").trim()
                .replaceAll(" out of 5 stars", "").replaceAll("\\s+", ""));

        int count_comment = 0;

        Elements elementsComment = doc.select("span#acrCustomerReviewText");
        if (elementsComment.size() > 0) {
            count_comment = NumberUtils.toInt(elementsComment.get(0).text().trim().replaceAll("[^0-9]+", ""));
        }

        String propertiesElements = doc.select("script").toString();

        StringBuffer content = new StringBuffer();

        Elements elementsProductDetail = doc.select("div#prodDetails");
        elementsProductDetail.select("script").remove();
        elementsProductDetail.select("style").remove();
        Elements elementsProductContent = doc.select("div#aplus");
        elementsProductContent.select("script").remove();
        elementsProductContent.select("style").remove();

        content.append(elementsProductDetail.html());
        content.append(elementsProductContent.html());

        Elements elementsProductCategory = doc.select("div#wayfinding-breadcrumbs_feature_div ul li a");
        ArrayList<String> categoryList = new ArrayList<>();
        elementsProductCategory.stream().forEach(v -> categoryList.add(v.text().trim()));

        Map<String, Object> mapData = AmazonUtil.properties(propertiesElements);

        if (mapData.size() > 0) {
            dataMap.setProperties(new Gson().toJson(mapData));
        } else {
            dataMap.setProperties("");
        }

        dataMap.setId(id);
        dataMap.setCode(code);
        dataMap.setPrice(price);
        dataMap.setRating(rating);
        dataMap.setComment_count(count_comment);
        dataMap.setShop(shop);
        dataMap.setContent(content.toString());
        dataMap.setCategory(StringUtils.join(categoryList, "|"));

        String characterFilter = "[^\\p{L}\\p{M}\\p{N}\\p{P}\\p{Z}\\p{Cf}\\p{Cs}\\s]";
        String emotionless = description.replaceAll(characterFilter,"");

        dataMap.setDescription(StringUtils.substring(emotionless, 0, 4000));

//        System.out.println("--------------------");
//        System.out.println(dataMap.getCode());
//        System.out.println(dataMap.getPrice());
//        System.out.println(dataMap.getRating());
//        System.out.println(dataMap.getComment_count());
//        System.out.println(dataMap.getShop());
//        System.out.println(dataMap.getDescription());
//        System.out.println(dataMap.getProperties());
//        System.out.println(dataMap.getContent());
//        System.out.println(dataMap.getCategory());

        logger.debug("PROXY [{}] URL_DETAIL [{}] PRICE[{}] RATE[{} - {}] SHOP[{}] PROPERTIES[{}]", (socketAddress != null ? socketAddress.toString() : "N/A"),
                url, dataMap.getPrice(), dataMap.getRating(), dataMap.getComment_count(), dataMap.getShop(), StringUtils.isNotBlank(dataMap.getProperties()));

        return dataMap;
    }

    private Data toData(String id, String text, String img, String price, String rating, String comment, String site) {
        String urlDetail = Crawler.AMAZON_COM.getSite() + "/dp/%s/";

        Data dataMap = new Data();
        dataMap.setCode(id);
        dataMap.setName(text);
        dataMap.setImage(img);
        dataMap.setPrice(NumberUtils.toDouble(
                price.replaceAll("\\s+", "").replaceAll("\\$", "")));
        dataMap.setRating(NumberUtils.toDouble(
                rating.replaceAll(" out of 5 stars", "").replaceAll("\\s+", "")));
        dataMap.setComment_count(NumberUtils.toInt(comment.replaceAll(",", "")));
        dataMap.setLink(String.format(urlDetail, id));
        dataMap.setSite(site);

//        System.out.println("--------------------");
//        System.out.println(dataMap.getCode());
//        System.out.println(dataMap.getImage());
//        System.out.println(dataMap.getLink());
//        System.out.println(dataMap.getPrice());
//        System.out.println(dataMap.getName());
//        System.out.println(dataMap.getRating());
//        System.out.println(dataMap.getComment_count());

        return dataMap;
    }

    public List<Data> readQuery(String url) throws Exception {
        List<Data> lisData = new ArrayList<>();

        Map<String, String> mapHeader = new LinkedHashMap<>();
        mapHeader.put("origin", Crawler.AMAZON_COM.getSite());
        mapHeader.put("referer", url);
        mapHeader.put("sec-fetch-dest", "empty");
        mapHeader.put("sec-fetch-mode", "cors");
        mapHeader.put("x-amazon-s-fallback-url", "");
        mapHeader.put("x-amazon-s-mismatch-behavior", "ALLOW");
        mapHeader.put("x-requested-with", "XMLHttpRequest");

        Connection.Response resp = Jsoup.connect(url)
                .userAgent(UserAgent.getUserAgent())
                .timeout(Consts.TIMEOUT_CATEGORY)
                .headers(mapHeader)
                .ignoreContentType(true)
                .method(Connection.Method.POST)
                .maxBodySize(0)
                .execute();

        String body = resp.body();

        if (ResourceUtil.getValue("debug").equals("true")) {
            FileUtils.writeStringToFile(new File("data/category/" + url.substring(url.lastIndexOf("?") + 1) + ".html"), body);
        }

        String[] data = body.split("&&&");

        for (String value : data) {

            if (value.indexOf("{") > 0) {
                String d = value.substring(value.indexOf("{"), value.lastIndexOf("}") + 1);

                Query query = new Gson().fromJson(d, Query.class);

                if (StringUtils.isNotBlank(query.getAsin())) {

                    Document doc = Jsoup.parse(query.getHtml());

                    String id = query.getAsin();

                    String img = doc.select("img").attr("src");
                    String text = doc.select("h2").text();

                    Elements elePrice = doc.select("span.a-price > span.a-offscreen");

                    String price = "0";

                    if (elePrice.size() > 0) {
                        price = elePrice.get(0).text();
                    }

                    Elements eleRating = doc.select("a.a-popover-trigger.a-declarative").select("span.a-icon-alt");
                    String rating = eleRating.text();

                    Elements eleComment = doc.select("a[href*='customerReviews']");
                    String comment = eleComment.text();

                    lisData.add(toData(id, text, img, price, rating, comment, url));
                }
            }
        }
        return lisData;
    }

    public static void main(String[] args) {
        try {
            AmazonComParser amazonParser = new AmazonComParser();
//            amazonParser.readQuery("https://www.amazon.com/s/query?bbn=16225009011&rh=n%3A16225009011%2Cn%3A281407&page=2");

            ProxyProvider.setup();

            List<InetSocketAddress> inetSocketAddresses = ProxyProvider.proxyList();
            for (InetSocketAddress socketAddress : inetSocketAddresses) {
                try {
                    Data content = amazonParser.readDetail("https://www.amazon.com/dp/B086DBR6T7/", "B086DBR6T7", 1, socketAddress);
                    break;
                } catch (Exception ex) {
                    System.out.println("ERROR_" + socketAddress.toString());
                }


            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
