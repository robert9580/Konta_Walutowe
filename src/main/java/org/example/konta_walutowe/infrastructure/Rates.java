package org.example.konta_walutowe.infrastructure;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@ToString
@Getter
public class Rates {

    private List<Rate> rates = new ArrayList<>();

    public Rate getRate() {
        return rates.iterator().next();
    }

    @ToString
    @Getter
    @AllArgsConstructor
    public static class Rate {
        private BigDecimal bid;
        private BigDecimal ask;
    }
}
