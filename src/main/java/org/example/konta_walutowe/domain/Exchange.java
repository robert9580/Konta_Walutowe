package org.example.konta_walutowe.domain;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

@Builder
@Getter
public class Exchange {
    private String pesel;
    private Currency from;
    private Currency to;
    private BigDecimal quantity;
}
