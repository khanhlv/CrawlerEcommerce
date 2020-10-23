package com.crawler.ecommerce.parser;

import java.util.Collections;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.crawler.ecommerce.core.UserAgent;
import com.crawler.ecommerce.model.Data;
import com.crawler.ecommerce.ssl.SSLUtils;

public class AmazonUkParser {
    private static final Logger logger = LoggerFactory.getLogger(AmazonUkParser.class);

    static {
        SSLUtils.trustAllHostnames();
    }

    private List<Data> parserPageOne(Elements elements) throws Exception {

        List<Data> lisData = Collections.emptyList();

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

                lisData.add(toData(id, text, img, price, rating, comment));
            });
        }

        return lisData;
    }

    public List<Data> read(String url) throws Exception {
        List<Data> lisData = Collections.emptyList();

        logger.debug("URL [{}]", url);

        Document doc = Jsoup.connect(url)
                .userAgent(UserAgent.getUserAgent())
                .timeout(30000)
                .get();

        Elements elementsMainResults = doc.select("div#mainResults > ul > li");

        if (elementsMainResults.size() > 0) {
            return parserPageOne(elementsMainResults);
        }

        Elements elements = doc.select("div[data-component-type=\"s-search-result\"]");

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

            lisData.add(toData(id, text, img, price, rating, comment));
        });

        logger.debug("URL [{}] SIZE [{}]", url, elements.size());

        return lisData;
    }

    private Data toData(String id, String text, String img, String price, String rating, String comment) {
        String urlDetail = "https://www.amazon.co.uk/dp/%s/";

        Data dataMap = new Data();
        dataMap.setCode(id);
        dataMap.setName(text);
        dataMap.setImage(img);
        dataMap.setPrice(price);
        dataMap.setRating(rating);
        dataMap.setComment_count(comment);
        dataMap.setLink(String.format(urlDetail, id));

        return dataMap;
    }

    public static void main(String[] args) {
        try {
            AmazonUkParser amazonParser = new AmazonUkParser();
            for (int i = 1; i <= 10; i++) {
                amazonParser.read("https://www.amazon.co.uk/s?rh=n%3A560798%2Cn%3A%21560800%2Cn%3A560834%2Cn%3A376337011&page=" + i);
                Thread.sleep(5000);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}