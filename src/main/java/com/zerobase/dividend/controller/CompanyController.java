package com.zerobase.dividend.controller;

import com.zerobase.dividend.dto.CompanyDto;
import com.zerobase.dividend.service.CompanyService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;

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

        CompanyDto companyDto = companyService.addCompany(ticker);

        return ResponseEntity.ok(companyDto);
    }

    /**
     * 모든 회사 조회
     */
    @GetMapping
    public ResponseEntity<?> searchCompany(final Pageable pageable) {
        return ResponseEntity.ok(companyService.getAllCompany(pageable));
    }

    /**
     * 자동 완성을 위한 회사명 목록 조회
     */
    @GetMapping("/autocomplete")
    public ResponseEntity<?> searchAutocomplete(String keyword) {
        return ResponseEntity.ok(companyService.getCompanyNamesByKeyword(keyword));
    }
}
