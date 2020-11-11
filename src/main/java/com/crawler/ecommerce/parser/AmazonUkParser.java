package com.crawler.ecommerce.parser;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.crawler.ecommerce.core.UserAgent;
import com.crawler.ecommerce.model.Data;
import com.google.gson.Gson;

public class AmazonUkParser {
    private static final Logger logger = LoggerFactory.getLogger(AmazonUkParser.class);

    private List<Data> parserPageOne(Elements elements, String site) {

        List<Data> lisData = new ArrayList<>();

        if (elements.size() > 0) {
            elements.stream().forEach(e -> {
                String id = e.attr("data-asin");
                String img = e.select("img.s-access-image").attr("src");
                String text = e.select("h2.a-size-base.s-inline.s-access-title.a-text-normal").text();

                Elements elePrice = e.select("a.a-link-normal.a-text-normal > span.a-size-base.a-color-price.s-price.a-text-bold");
                String price = elePrice.text();

                Elements eleRating = e.select("div.a-row.a-spacing-none > span[name=" + id +"]").select("span.a-icon-alt");
                String rating = eleRating.text();

                Elements eleComment = e.select("a[href*='customerReviews']");
                String comment = eleComment.text();

                lisData.add(toData(id, text, img, price, rating, comment, site));
            });
        }

        return lisData;
    }

    private List<Data> parserPage(Elements elements, String site) {

        List<Data> lisData = new ArrayList<>();

        if (elements.size() > 0) {
            elements.stream().forEach(e -> {
                String id = e.attr("data-asin");
                String img = e.select("img.s-image").attr("src");
                String text = e.select("span.a-size-medium.a-color-base.a-text-normal").text();

                Elements elePrice = e.select("span.a-price > span.a-offscreen");
                String price;
                if (elePrice.size() > 1) {
                    price = elePrice.get(0).text();
                } else {
                    price = elePrice.text();
                }

                Elements eleRatingComment = e.select("div.a-row.a-size-small span[aria-label]");

                String rating = StringUtils.EMPTY;
                String comment = StringUtils.EMPTY;

                if (eleRatingComment.size() > 0) {
                    rating = eleRatingComment.get(0).attr("aria-label");
                    comment = eleRatingComment.get(1).attr("aria-label");
                }

                lisData.add(toData(id, text, img, price, rating, comment, site));
            });
        }

        return lisData;
    }

    public List<Data> read(String url) throws Exception {
        List<Data> lisData;

        logger.debug("URL [{}]", url);

        Document doc = Jsoup.connect(url)
                .userAgent(UserAgent.getUserAgent())
                .timeout(30000)
                .get();

        Elements elementsMainResults = doc.select("div#mainResults > ul > li");

        if (elementsMainResults.size() > 0) {
            lisData = parserPageOne(elementsMainResults, url);
        } else {
            Elements elements = doc.select("div[data-component-type=\"s-search-result\"]");

            lisData = parserPage(elements, url);
        }

        logger.debug("URL [{}] SIZE [{}]", url, lisData.size());

        return lisData;
    }

    public Data readDetail(String url, String code, int id, String settingValue) throws Exception {
        Data dataMap = new Data();

        Map<String, String> mapCookies = new Gson().fromJson(settingValue, Map.class);

        String userAgent = mapCookies.get("userAgent");

        Document doc = Jsoup.connect(url)
                .userAgent(userAgent)
                .cookie("x-amz-captcha-2", mapCookies.get("x-amz-captcha-2"))
                .cookie("x-amz-captcha-1", mapCookies.get("x-amz-captcha-1"))
                .timeout(30000)
                .get();

        FileUtils.writeStringToFile(new File("data/html/" + code + ".html"), doc.html());

        if (doc.select("form").attr("action").equals("/errors/validateCaptcha")) {
            logger.debug("URL_DETAIL [{}] - VALIDATE_CAPTCHA", url);

            return null;
        }

        String priceText = doc.select("span#priceblock_saleprice").text().trim();

        if (StringUtils.isBlank(priceText)) {
            priceText = doc.select("span#priceblock_ourprice").text().trim();
        }

        double price = NumberUtils.toDouble(priceText.replaceAll("\\s+", "").replaceAll("\\£", ""));
        String description = doc.select("div#feature-bullets").text();

        String shop = doc.select("a#sellerProfileTriggerId").text().trim();
        double rating = NumberUtils.toDouble(doc.select("span#acrPopover").attr("title").trim()
                .replaceAll(" out of 5 stars", "").replaceAll("\\s+", ""));
        int count_comment = NumberUtils.toInt(doc.select("span#acrCustomerReviewText").text().trim().replaceAll("[^0-9]+", ""));

        Elements propertiesElements = doc.select("div#twisterContainer ul");

        String urlDetail = "https://www.amazon.co.uk/dp/%s/";

        if (propertiesElements.size() > 0) {
            Map<String, List<Map>> mapData = new LinkedHashMap<>();
            propertiesElements.forEach(els -> {
                String key = els.attr("data-a-button-group")
                        .replaceAll("\\{\"name\":\"twister_", "")
                        .replaceAll("\"}", "");

                Elements elementsLi = els.select("li");

                List<Map> listAttr = new ArrayList<>();
                elementsLi.forEach(dataEle -> {
                    Map<String, String> mapAttr = new LinkedHashMap<>();
                    String textAttr = dataEle.select("img.imgSwatch").attr("alt").trim();

                    if (StringUtils.isBlank(textAttr)) {
                        textAttr = dataEle.select("button div.twisterTextDiv").text().trim();
                    }

                    String priceAttr = dataEle.select("button span.twisterSwatchPrice").text().trim();

                    String codeAttr = dataEle.attr("data-defaultasin").trim();
                    String urlAttr = String.format(urlDetail, codeAttr);

                    mapAttr.put("text", textAttr);
                    mapAttr.put("price", priceAttr);
                    mapAttr.put("code", codeAttr);
                    mapAttr.put("urlAttr", urlAttr);

                    listAttr.add(mapAttr);
                });

                mapData.put(key, listAttr);
            });
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

        String characterFilter = "[^\\p{L}\\p{M}\\p{N}\\p{P}\\p{Z}\\p{Cf}\\p{Cs}\\s]";
        String emotionless = description.replaceAll(characterFilter,"");

        dataMap.setDescription(emotionless);

//        System.out.println("--------------------");
//        System.out.println(dataMap.getCode());
//        System.out.println(dataMap.getPrice());
//        System.out.println(dataMap.getRating());
//        System.out.println(dataMap.getComment_count());
//        System.out.println(dataMap.getShop());
//        System.out.println(dataMap.getDescription());
//        System.out.println(dataMap.getProperties());

        logger.debug("URL_DETAIL [{}] PRICE[{}] RATE[{} - {}] SHOP[{}] PROPERTIES[{}]", url, dataMap.getPrice(), dataMap.getRating(),
                dataMap.getComment_count(), dataMap.getShop(), StringUtils.isNotBlank(dataMap.getProperties()));

        return dataMap;
    }

    private Data toData(String id, String text, String img, String price, String rating, String comment, String site) {
        String urlDetail = "https://www.amazon.co.uk/dp/%s/";

        Data dataMap = new Data();
        dataMap.setCode(id);
        dataMap.setName(text);
        dataMap.setImage(img);
        dataMap.setPrice(NumberUtils.toDouble(
                price.replaceAll("\\s+", "").replaceAll("\\£", "")));
        dataMap.setRating(NumberUtils.toDouble(
                rating.replaceAll(" out of 5 stars", "").replaceAll("\\s+", "")));
        dataMap.setComment_count(NumberUtils.toInt(comment));
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
//        mapHeader.put("downlink", "2.75");
//        mapHeader.put("ect", "4g");
        mapHeader.put("origin", "https://www.amazon.co.uk");
        mapHeader.put("referer", url);
//        mapHeader.put("rtt", "100");
        mapHeader.put("sec-fetch-dest", "empty");
        mapHeader.put("sec-fetch-mode", "cors");
        mapHeader.put("x-amazon-s-fallback-url", "");
//        mapHeader.put("x-amazon-rush-fingerprints", "AmazonRushAssetLoader:C19C2781A9CA71D093CC78D5B0E5D2EC34BBAA7B|AmazonRushFramework:7A9E1F79C14CA1CCB0640E2990FD1B4EF2FE4509|AmazonRushRouter:3CD20ED06C633A93E0B890D2CAF8E9C37C1E8C03|SearchAssets:FC9F7601DC4DAE1847E06565E0EB8A6AAC92A950|DynamicImageLoader:D3F0A3BA29C00086BD6D4686D2C79BBB78969B01|SearchPartnerAssets:D220327554E8509335FDCD0DC1BA7F441B605601");
        mapHeader.put("x-amazon-s-mismatch-behavior", "ALLOW");
//        mapHeader.put("x-amazon-s-swrs-version", "15078BB72D30898381D55329B6223804");
        mapHeader.put("x-requested-with", "XMLHttpRequest");

        Connection.Response resp = Jsoup.connect(url)
                .userAgent(UserAgent.getUserAgent())
                .timeout(30000)
                .headers(mapHeader)
                .ignoreContentType(true)
                .method(Connection.Method.POST)
                .execute();

        String body = resp.body();

        String[] data = body.split("&&&");

        for (String value : data) {

            if (value.indexOf("{") > 0) {
                String d = value.substring(value.indexOf("{"), value.lastIndexOf("}") + 1);

                Query query = new Gson().fromJson(d, Query.class);

                if (StringUtils.isNotBlank(query.getAsin())) {

//                    FileUtils.writeStringToFile(new File("data/data.html"), query.getHtml());

                    Document doc = Jsoup.parse(query.getHtml());

                    String id = query.getAsin();

                    String img = doc.select("img").attr("src");
                    String text = doc.select("span.a-color-base.a-text-normal").text();

                    Elements elePrice = doc.select("span.a-price > span.a-offscreen");
                    String price = elePrice.text();

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
            AmazonUkParser amazonParser = new AmazonUkParser();
//            amazonParser.read("https://www.amazon.co.uk/s?rh=n%3A560798%2Cn%3A%21560800%2Cn%3A560834%2Cn%3A376337011&page=" + 1);
//            amazonParser.readQuery("https://www.amazon.co.uk/s/query?rh=n%3A560798%2Cn%3A%21560800%2Cn%3A1345763031&page=32");
            Data content = amazonParser.readDetail("https://www.amazon.co.uk/dp/B07ZG8W8B4", "B07ZG8W8B4", 1, "");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
