package com.zerobase.dividend.service;

import com.zerobase.dividend.domain.CompanyEntity;
import com.zerobase.dividend.domain.DividendEntity;
import com.zerobase.dividend.dto.CompanyDto;
import com.zerobase.dividend.dto.DividendDto;
import com.zerobase.dividend.dto.ScrapedResult;
import com.zerobase.dividend.error.impl.NoCompanyException;
import com.zerobase.dividend.repository.CompanyRepository;
import com.zerobase.dividend.repository.DividendRepository;
import com.zerobase.dividend.type.CacheKey;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
@Slf4j
public class FinanceService {
    private final CompanyRepository companyRepository;
    private final DividendRepository dividendRepository;

    /**
     * 회사의 모든 배당금 정보 조회
     */
    @Cacheable(key = "#companyName", value = CacheKey.KEY_FINANCE)
    public ScrapedResult getDividendByCompanyName(String companyName) {
        log.info("search company -> " + companyName);

        CompanyEntity companyEntity = companyRepository.findByName(companyName)
                .orElseThrow(() -> new NoCompanyException());

        List<DividendEntity> dividendEntityList =
                dividendRepository.findAllByCompanyId(companyEntity.getId());

        List<DividendDto> dividendDtoList = new ArrayList<>();
        for (DividendEntity d : dividendEntityList) {
            dividendDtoList.add(
                    new DividendDto(d.getDate(), d.getDividend())
            );
        }

        return new ScrapedResult(
                new CompanyDto(companyEntity.getTicker(), companyEntity.getName()),
                dividendDtoList
        );
    }
}
