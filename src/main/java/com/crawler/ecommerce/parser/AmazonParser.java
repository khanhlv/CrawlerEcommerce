package com.crawler.ecommerce.parser;

import com.crawler.ecommerce.core.UserAgent;
import com.crawler.ecommerce.ssl.SSLUtils;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

public class AmazonParser {
    static {
        SSLUtils.trustAllHostnames();
    }

    private void parserPageOne(String url) throws Exception {
        System.out.println("#####################" + url);
        Document doc = Jsoup.connect(url)
                .userAgent(UserAgent.getUserAgent())
                .timeout(30000)
                .get();

        Elements elements = doc.select("div#mainResults > ul > li");

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

            String urlDetail = "https://www.amazon.co.uk/dp/%s/";
            System.out.println(text);
            System.out.println(id + " - " + img);
            System.out.println(String.format(urlDetail, id));
            System.out.println(price + " - " + rating + " - " + comment);
            System.out.println("-----------------------");
        });

        System.out.println("[" + elements.size() + "]#####################" + url);
    }

    public void read(String url) throws Exception {
        System.out.println("#####################" + url);

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

        System.out.println("[" + elements.size() + "]#####################" + url);
    }

    public static void main(String[] args) {
        try {
            AmazonParser amazonParser = new AmazonParser();
            for (int i = 1; i <= 10; i++) {
                if (i == 1) {
                    amazonParser.parserPageOne("https://www.amazon.co.uk/s?rh=n%3A560798%2Cn%3A%21560800%2Cn%3A560834%2Cn%3A376337011&page=" + i);
                } else {
                    amazonParser.read("https://www.amazon.co.uk/s?rh=n%3A560798%2Cn%3A%21560800%2Cn%3A560834%2Cn%3A376337011&page=" + i);
                }
                Thread.sleep(5000);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
