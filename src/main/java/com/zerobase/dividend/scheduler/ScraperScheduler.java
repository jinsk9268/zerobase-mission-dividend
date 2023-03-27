package com.zerobase.dividend.scheduler;

import com.zerobase.dividend.domain.CompanyEntity;
import com.zerobase.dividend.domain.DividendEntity;
import com.zerobase.dividend.dto.CompanyDto;
import com.zerobase.dividend.dto.ScrapedResult;
import com.zerobase.dividend.repository.CompanyRepository;
import com.zerobase.dividend.repository.DividendRepository;
import com.zerobase.dividend.scraper.Scraper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@EnableCaching
@AllArgsConstructor
@Slf4j
public class ScraperScheduler {
    private final CompanyRepository companyRepository;
    private final DividendRepository dividendRepository;
    private final Scraper yahooFinanceScrapper;

    @CacheEvict(value = "finance", allEntries = true)
    @Scheduled(cron = "${scheduler.scrap.yahoo}")
    public void yahooFinanceScheduling() {
        List<CompanyEntity> companyEntityList = companyRepository.findAll();

        for (CompanyEntity c : companyEntityList) {
            log.info("scraping scheduler is started -> " + c.getName());

            ScrapedResult scrapedResult = yahooFinanceScrapper.scrap(
                    new CompanyDto(c.getTicker(), c.getName())
            );

            scrapedResult.getDividendDtoList().stream()
                    .map(d -> new DividendEntity(c.getId(), d))
                    .forEach(d -> {
                        if (!dividendRepository.existsByCompanyIdAndDate(
                                d.getCompanyId(), d.getDate()
                        )) {
                            dividendRepository.save(d);
                        }
                    });

            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                log.error("scraping scheduler error -> " + e);
                Thread.currentThread().interrupt();
            }

            log.info("scraping scheduler is succeed -> " + c.getName());
        }
    }
}
