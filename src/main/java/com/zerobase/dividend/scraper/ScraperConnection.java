package com.zerobase.dividend.scraper;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;

public class ScraperConnection {
    public Document getScrapedDocument(String url) {
        Document document;

        try {
            Connection connection = Jsoup.connect(url);
            document = connection.get();
        } catch (IOException e) {
            throw new RuntimeException( url + " 스크래핑 중 오류가 발생했습니다. -> " + e);
        }

        return document;
    }
}
