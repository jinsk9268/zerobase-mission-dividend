package com.zerobase.dividend.scraper;

import com.zerobase.dividend.dto.CompanyDto;
import com.zerobase.dividend.dto.DividendDto;
import com.zerobase.dividend.dto.ScrapedResult;
import com.zerobase.dividend.type.Month;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class YahooFinanceScraper {
    private final String SCRAPED_URL = "https://finance.yahoo.com/quote/%s/history?period1=%d&period2=%d&interval=1mo";
    private final long START_DATE = 86400;

    public ScrapedResult scrap(CompanyDto companyDto) {
        ScrapedResult scrapedResult = new ScrapedResult();
        scrapedResult.setCompanyDto(companyDto);

        try {
            long now = System.currentTimeMillis() / 1000;
            String url = String.format(SCRAPED_URL, companyDto.getTicker(), START_DATE, now);

            Connection connection = Jsoup.connect(url);
            Document document = connection.get();

            Elements parsingDivs = document.getElementsByAttributeValue("data-test", "historical-prices");
            Element table = parsingDivs.get(0);
            Element tbody = table.children().get(1);

            List<DividendDto> dividendDtoList = new ArrayList<>();
            for (Element e : tbody.children()) {
                String txt = e.text();

                if (!txt.endsWith("Dividend")) {
                    continue;
                }

                String[] splits = txt.split(" ");
                int month = Month.strToNumber(splits[0]);
                int day = Integer.parseInt(splits[1].replace(",", ""));
                int year = Integer.parseInt(splits[2]);
                String dividend = splits[3];

                if (month < 0) {
                    throw new RuntimeException("매칭되는 월이 없습니다. -> " + splits[0]);
                }

                dividendDtoList.add(new DividendDto(
                        LocalDateTime.of(year, month, day, 0, 0),
                        dividend
                ));
            }

            scrapedResult.setDividendDtoList(dividendDtoList);
        } catch (IOException e) {
            throw new RuntimeException("배당금 정보 스크래핑중 에러가 발생했습니다. -> " + e);
        }

        return scrapedResult;
    }
}
