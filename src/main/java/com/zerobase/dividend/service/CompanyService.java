package com.zerobase.dividend.service;

import com.zerobase.dividend.domain.CompanyEntity;
import com.zerobase.dividend.domain.DividendEntity;
import com.zerobase.dividend.dto.CompanyDto;
import com.zerobase.dividend.dto.ScrapedResult;
import com.zerobase.dividend.repository.CompanyRepository;
import com.zerobase.dividend.repository.DividendRepository;
import com.zerobase.dividend.scraper.Scraper;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.List;
import java.util.stream.Collectors;

@AllArgsConstructor
@Service
public class CompanyService {
    private final Scraper yahooFinanceScraper;

    private final CompanyRepository companyRepository;
    private final DividendRepository dividendRepository;

    /**
     * ticker 기준으로 회사, 배당금 정보 저장
     */
    public CompanyDto addCompany(String ticker) {
        if (companyRepository.existsByTicker(ticker)) {
            throw new RuntimeException("이미 존재하는 회사 입니다. -> " + ticker);
        }

        return storeCompanyAndDividend(ticker);
    }

    private CompanyDto storeCompanyAndDividend(String ticker) {
        // 스크래핑 결과 가져오기
        CompanyDto companyDto = yahooFinanceScraper.scrapCompanyByTicker(ticker);

        if (ObjectUtils.isEmpty(companyDto)) {
            throw new RuntimeException("스크랩에 실패했습니다. -> " + ticker);
        }

        ScrapedResult scrapedResult = yahooFinanceScraper.scrap(companyDto);

        // 회사 저장
        CompanyEntity companyEntity = companyRepository.save(new CompanyEntity(companyDto));

        // 회사의 배당금 정보 저장
        List<DividendEntity> dividendEntities
                = scrapedResult.getDividendDtoList().stream()
                .map(e -> new DividendEntity(companyEntity.getId(), e))
                .collect(Collectors.toList());
        dividendRepository.saveAll(dividendEntities);

        return companyDto;
    }
}
