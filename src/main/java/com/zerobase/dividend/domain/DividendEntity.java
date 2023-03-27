package com.zerobase.dividend.domain;

import com.zerobase.dividend.dto.DividendDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "dividend")
@Table(uniqueConstraints = {
        @UniqueConstraint(
                columnNames = {"companyId", "date"}
        )}
)
public class DividendEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long companyId;
    private LocalDateTime date;
    private String dividend;

    public DividendEntity(Long companyId, DividendDto dividendDto) {
        this.companyId = companyId;
        this.date = dividendDto.getDate();
        this.dividend = dividendDto.getDividend();
    }
}
