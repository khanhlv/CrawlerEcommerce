package com.crawler.ecommerce.parser;

import com.crawler.ecommerce.core.UserAgent;
import com.crawler.ecommerce.enums.Crawler;
import com.crawler.ecommerce.model.Data;
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
import java.util.*;

public class AmazonComParser {
    private static final Logger logger = LoggerFactory.getLogger(AmazonComParser.class);

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

        priceText = StringUtils.split(priceText, "-")[0];

        double price = NumberUtils.toDouble(priceText.replaceAll("\\s+", "").replaceAll("\\$", ""));
        String description = doc.select("div#feature-bullets").text();

        String shop = doc.select("a#sellerProfileTriggerId").text().trim();
        double rating = NumberUtils.toDouble(doc.select("span#acrPopover").attr("title").trim()
                .replaceAll(" out of 5 stars", "").replaceAll("\\s+", ""));
        int count_comment = NumberUtils.toInt(doc.select("span#acrCustomerReviewText").get(0).text().trim().replaceAll("[^0-9]+", ""));

        String propertiesElements = doc.select("script").toString();

        Map<String, Object> mapData = properties(propertiesElements);

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

    private Map<String, Object> properties(String dataInput) {
        String dataAttribute = dataInput.substring(dataInput.indexOf("P.register('twister-js-init-dpx-data'"),
                dataInput.indexOf("useDesktopTwisterMetaAsset"));
        dataAttribute = dataAttribute.substring(dataAttribute.indexOf("var dataToReturn = ") + 19, dataAttribute.indexOf("return dataToReturn;") - 2);
        dataAttribute = dataAttribute.replaceFirst("num_total_variations", UUID.randomUUID().toString());


        String dataParseJSON = dataInput.substring(dataInput.indexOf("var obj = jQuery.parseJSON('") + 28, dataInput.indexOf("data" +
                "[\"alwaysIncludeVideo\"]") - 4);

        Map<String, Object> mapDataParseJSON = new Gson().fromJson(dataParseJSON, Map.class);

        Map<String, Object> mapDataAttribute = new Gson().fromJson(dataAttribute, Map.class);

        Map<String, Object> colorImages =  (Map<String, Object>) mapDataParseJSON.get("colorImages");

        List<String> dimensionValues =  (List<String>) mapDataAttribute.get("dimensions");
        Map<String, List<String>> dimensionValuesDisplayData =  (Map<String, List<String>>) mapDataAttribute.get("dimensionValuesDisplayData");

        List<Map<String, Object>> dimensionValuesList = new ArrayList<>();

        dimensionValuesDisplayData.forEach((key, value) -> {
            Map<String, Object> data = new LinkedHashMap<>();
            data.put("asin", key);
            for(int i = 0; i < dimensionValues.size(); i++) {
                data.put(dimensionValues.get(i), value.get(i));
            }

            String name = StringUtils.join(value, " ");

            dimensionValuesList.add(data);
            data.put("images", colorImages.get(name));
        });

        Map<String, Object> mapData = new LinkedHashMap<>();
        mapData.put("variationValues", mapDataAttribute.get("variationValues"));
        mapData.put("selectedVariations", mapDataAttribute.get("selected_variations"));
        mapData.put("dimensionValuesData", dimensionValuesList);

        return mapData;
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
            String setting = "{\n" +
                    "  \"userAgent\" : \"Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/86.0.4240.198 Safari/537.36\",\n" +
                    "  \"x-amz-captcha-2\" : \"PEce+/DomX9LQnj3wgdtWQ==\",\n" +
                    "  \"x-amz-captcha-1\" : \"1605337900184777\"\n" +
                    "}";
            AmazonComParser amazonParser = new AmazonComParser();
//            amazonParser.readQuery("https://www.amazon.com/s/query?bbn=16225009011&rh=n%3A16225009011%2Cn%3A281407&page=2");
            Data content = amazonParser.readDetail("https://www.amazon.com/dp/B077ZMKWVM/", "B077ZMKWVM", 1, setting);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
