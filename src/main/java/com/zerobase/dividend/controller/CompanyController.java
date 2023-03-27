package com.zerobase.dividend.controller;

import com.zerobase.dividend.dto.CompanyDto;
import com.zerobase.dividend.service.CompanyService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/company")
@AllArgsConstructor
public class CompanyController {
    private final CompanyService companyService;

    /**
     * 회사 추가 (회사 정보 및 배당금)
     */
    @PostMapping
    public ResponseEntity<?> addCompany(@RequestBody CompanyDto request) {
        String ticker = request.getTicker().trim();

        if (ObjectUtils.isEmpty(ticker)) {
            throw new RuntimeException("입력값이 없습니다.");
        }

        return ResponseEntity.ok(companyService.addCompany(ticker));
    }
}
