package org.example.konta_walutowe.infrastructure;

import org.example.konta_walutowe.api.RegistrationRequest;

public interface KontoWalutoweRepository {

    Long saveInitial(RegistrationRequest request);

    Account findByPesel(String pesel);

    Account saveExchange(Account account);
}
