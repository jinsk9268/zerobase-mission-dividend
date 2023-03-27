package com.zerobase.dividend.scraper;

import com.zerobase.dividend.dto.CompanyDto;
import com.zerobase.dividend.dto.DividendDto;
import com.zerobase.dividend.dto.ScrapedResult;
import com.zerobase.dividend.type.Month;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Component
public class YahooFinanceScraper extends ScraperConnection implements ScraperInterface {
    private final String SCRAPED_URL = "https://finance.yahoo.com/quote/%s/history?period1=%d&period2=%d&interval=1mo";
    private final String SUMMARY_URL = "https://finance.yahoo.com/quote/%s?p=%s";
    private final long START_DATE = 86400;

    @Override
    public ScrapedResult scrap(CompanyDto companyDto) {
        String url = String.format(
                SCRAPED_URL,
                companyDto.getTicker(),
                START_DATE,
                System.currentTimeMillis() / 1000
        );

        Elements parsingDivs = getScrapedDocument(url)
                .getElementsByAttributeValue("data-test", "historical-prices");
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

        return new ScrapedResult(companyDto, dividendDtoList);
    }

    @Override
    public CompanyDto scrapCompanyByTicker(String ticker) {
        String url = String.format(SUMMARY_URL, ticker, ticker);

        Element h1 = getScrapedDocument(url)
                .getElementsByTag("h1").get(0);
        String title = h1.text().split(" - ")[1].trim();

        return new CompanyDto(ticker, title);
    }
}
