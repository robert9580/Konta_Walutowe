package org.example.konta_walutowe.api;

import org.example.konta_walutowe.domain.Exchange;
import org.mapstruct.Mapper;

@Mapper
public interface ApiMapper {
    Exchange map(String pesel, ExchangeRequest request);
}
