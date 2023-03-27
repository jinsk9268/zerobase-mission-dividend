package com.zerobase.dividend.service;

import com.zerobase.dividend.domain.CompanyEntity;
import com.zerobase.dividend.domain.DividendEntity;
import com.zerobase.dividend.dto.CompanyDto;
import com.zerobase.dividend.dto.DividendDto;
import com.zerobase.dividend.dto.ScrapedResult;
import com.zerobase.dividend.repository.CompanyRepository;
import com.zerobase.dividend.repository.DividendRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
public class FinanceService {
    private final CompanyRepository companyRepository;
    private final DividendRepository dividendRepository;

    /**
     * 회사의 모든 배당금 정보 조회
     */
    public ScrapedResult getDividendByCompanyName(String companyName) {
        CompanyEntity companyEntity = companyRepository.findByName(companyName)
                .orElseThrow(() -> new RuntimeException("존재하지 않는 회사명입니다. -> " + companyName));

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
