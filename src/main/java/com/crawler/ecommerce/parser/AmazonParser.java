package com.crawler.ecommerce.parser;

import com.crawler.ecommerce.core.UserAgent;
import com.crawler.ecommerce.util.StringUtil;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

public class AmazonParser {
    public void read(String url) throws Exception {
        Document doc = Jsoup.connect(url)
                .userAgent(UserAgent.getUserAgent())
                .timeout(30000)
                .get();

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

            String urlDetail = "https://www.amazon.co.uk/dp/%s/";
            System.out.println(text);
            System.out.println(id + " - " + img);
            System.out.println(String.format(urlDetail, id));
            System.out.println(price + " - " + rating + " - " + comment);
            System.out.println("-----------------------");
        });

//        System.out.println(doc);
        System.out.println(elements.size());
    }

    public static void main(String[] args) {
        try {
            new AmazonParser().read("https://www.amazon.co.uk/s?bbn=560798&rh=n%3A560798%2Cn%3A%21560800%2Cn%3A560858%2Cn%3A10392531&page=2");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
