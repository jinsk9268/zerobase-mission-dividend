package com.zerobase.dividend.repository;

import com.zerobase.dividend.domain.DividendEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DividendRepository extends JpaRepository<DividendEntity, Long> {
    List<DividendEntity> findAllByCompanyId(Long companyId);
}
