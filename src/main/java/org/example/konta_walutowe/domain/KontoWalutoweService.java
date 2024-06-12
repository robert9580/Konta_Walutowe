package org.example.konta_walutowe.domain;

import org.example.konta_walutowe.api.RegistrationRequest;
import org.example.konta_walutowe.infrastructure.Account;

public interface KontoWalutoweService {

    void registration(RegistrationRequest request);

    Account getAccount(String pesel);

    void exchange(Exchange exchange);
}
