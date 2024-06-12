package org.example.konta_walutowe.api;

import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.konta_walutowe.domain.Currency;

import java.math.BigDecimal;

@NoArgsConstructor
@Getter
@Setter
public class ExchangeRequest {
    @NotNull
    private Currency from;
    @NotNull
    private Currency to;
    @NotNull
    @Digits(integer = 6, fraction = 2)
    private BigDecimal quantity;

}
