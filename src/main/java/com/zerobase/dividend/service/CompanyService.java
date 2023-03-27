package com.zerobase.dividend.service;

import com.zerobase.dividend.domain.CompanyEntity;
import com.zerobase.dividend.domain.DividendEntity;
import com.zerobase.dividend.dto.CompanyDto;
import com.zerobase.dividend.dto.ScrapedResult;
import com.zerobase.dividend.repository.CompanyRepository;
import com.zerobase.dividend.repository.DividendRepository;
import com.zerobase.dividend.scraper.Scraper;
import lombok.AllArgsConstructor;
import org.apache.commons.collections4.Trie;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.List;
import java.util.stream.Collectors;

@AllArgsConstructor
@Service
public class CompanyService {
    private final Scraper yahooFinanceScraper;
    private final Trie trie;

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
            throw new RuntimeException("스크랩에 실패했습니다. 회사명을 다시 확인하거나 잠시 후 다시 시도해주세요-> " + ticker);
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

    /**
     * 모든 회사 조회
     */
    public Page<CompanyEntity> getAllCompany(Pageable pageable) {
        return companyRepository.findAll(pageable);
    }

    /**
     * 자동 검색을 위한 키워드
     */
    public void addAutocompleteKeyword(String keyword) {
        trie.put(keyword, null);
    }

    /**
     * trie에 keyword로 시작하는 회사들 리스트로 반환
     */
    public List<String> autocompleteList(String keyword) {
        return (List<String>) trie.prefixMap(keyword).keySet()
                .stream().collect(Collectors.toList());
    }

    /**
     * trie에 저장된 회사명 삭제
     */
    public void deleteAutocompleteKeyword(String keyword) {
        trie.remove(keyword);
    }
}
