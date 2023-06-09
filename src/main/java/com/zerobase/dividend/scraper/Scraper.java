package com.zerobase.dividend.scraper;

import com.zerobase.dividend.dto.CompanyDto;
import com.zerobase.dividend.dto.ScrapedResult;

public interface Scraper {
    ScrapedResult scrap(CompanyDto companyDto);
    CompanyDto scrapCompanyByTicker(String ticker);
}
