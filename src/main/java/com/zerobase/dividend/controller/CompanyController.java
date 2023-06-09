package com.zerobase.dividend.controller;

import com.zerobase.dividend.dto.CompanyDto;
import com.zerobase.dividend.service.CompanyService;
import com.zerobase.dividend.type.CacheKey;
import lombok.AllArgsConstructor;
import org.springframework.cache.CacheManager;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/company")
@AllArgsConstructor
public class CompanyController {
    private final CompanyService companyService;
    private final CacheManager redisCacheManager;

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
    @PreAuthorize("hasRole('READ')")
    public ResponseEntity<?> searchCompany(final Pageable pageable) {
        return ResponseEntity.ok(companyService.getAllCompany(pageable));
    }

    /**
     * 자동 완성을 위한 회사명 목록 조회
     */
    @GetMapping("/autocomplete")
    @PreAuthorize("hasRole('WRITE')")
    public ResponseEntity<?> searchAutocomplete(String keyword) {
        return ResponseEntity.ok(companyService.getCompanyNamesByKeyword(keyword));
    }

    /**
     * 회사 삭제 (회사와 관련된 모든 정보 삭제)
     */
    @DeleteMapping("/{ticker}")
    @PreAuthorize("hasRole('WRITE')")
    public ResponseEntity<?> deleteCompany(@PathVariable String ticker) {
        String companyName = companyService.deleteCompany(ticker);

        clearFinanceCache(companyName);

        return ResponseEntity.ok(companyName);
    }

    public void clearFinanceCache(String companyName) {
        redisCacheManager.getCache(CacheKey.KEY_FINANCE).evict(companyName );
    }
}
