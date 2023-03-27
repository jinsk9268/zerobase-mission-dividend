package com.zerobase.dividend.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class ScrapedResult {
    private CompanyDto companyDto;
    private List<DividendDto> dividendDtoList;

    public ScrapedResult() {
        this.dividendDtoList = new ArrayList<>();
    }
}
