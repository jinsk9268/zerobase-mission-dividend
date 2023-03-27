package com.zerobase.dividend.domain;

import com.zerobase.dividend.dto.CompanyDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "company")
public class CompanyEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true)
    private String ticker;
    private String name;

    public CompanyEntity(CompanyDto companyDto) {
        this.ticker = companyDto.getTicker();
        this.name = companyDto.getName();
    }
}
