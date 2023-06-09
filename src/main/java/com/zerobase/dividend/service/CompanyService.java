package com.zerobase.dividend.service;

import com.zerobase.dividend.domain.CompanyEntity;
import com.zerobase.dividend.domain.DividendEntity;
import com.zerobase.dividend.dto.CompanyDto;
import com.zerobase.dividend.dto.ScrapedResult;
import com.zerobase.dividend.error.impl.NoCompanyException;
import com.zerobase.dividend.repository.CompanyRepository;
import com.zerobase.dividend.repository.DividendRepository;
import com.zerobase.dividend.scraper.Scraper;
import lombok.AllArgsConstructor;
import org.apache.commons.collections4.Trie;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
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
     * LIKE 문을 사용한 자동 완성 검색을 위한 리스트 반환
     */
    public List<String> getCompanyNamesByKeyword(String keyword) {
        Pageable limit = PageRequest.of(0, 10);
        Page<CompanyEntity> companyEntityPage =
                companyRepository.findByNameStartingWithIgnoreCase(keyword, limit);

        return companyEntityPage.stream()
                .map(e -> e.getName())
                .collect(Collectors.toList());
    }

    /**
     * 자동 완성 trie에 회사명 저장
     */
    public void addAutocompleteKeyword(String keyword) {
        trie.put(keyword, null);
    }

    /**
     * 자동 완성 trie에 keyword로 시작하는 회사들 리스트로 반환
     */
    public List<String> autocompleteList(String keyword) {
        return (List<String>) trie.prefixMap(keyword).keySet()
                .stream().limit(10).collect(Collectors.toList());
    }

    /**
     * 자동 완성 trie에 저장된 회사명 삭제
     */
    public void deleteAutocompleteKeyword(String keyword) {
        trie.remove(keyword);
    }

    /**
     * 회사 삭제
     */
    public String deleteCompany(String ticker) {
        CompanyEntity companyEntity = companyRepository.findByTicker(ticker)
                .orElseThrow(() -> new NoCompanyException());

        dividendRepository.deleteAllByCompanyId(companyEntity.getId());

        companyRepository.delete(companyEntity);

        return companyEntity.getName();
    }
}
