package com.zerobase.dividend.repository;

import com.zerobase.dividend.domain.DividendEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DividendRepository extends JpaRepository<DividendEntity, Long> {
}
