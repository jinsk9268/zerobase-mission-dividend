package com.zerobase.dividend.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class DividendDto {
    private LocalDateTime date;
    private String dividend;
}
