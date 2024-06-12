package org.example.konta_walutowe.infrastructure;

import lombok.RequiredArgsConstructor;
import org.example.konta_walutowe.api.RegistrationRequest;
import org.example.konta_walutowe.domain.Currency;
import org.example.konta_walutowe.library.BusinessException;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.EnumMap;

@Repository
@RequiredArgsConstructor
class KontoWalutoweRepositoryImpl implements KontoWalutoweRepository {

    private final AccountRepo accountRepo;

    @Override
    public Long saveInitial(RegistrationRequest request) {

        if (accountRepo.existsByPesel(request.getPesel())) {
            throw new BusinessException("Account already exists");
        }

        EnumMap<Currency, BigDecimal> funds = new EnumMap<>(Currency.class);
        funds.put(Currency.PLN, request.getInitialBalance());

        Account account = Account.builder()
                .name(request.getName())
                .pesel(request.getPesel())
                .funds(funds)
                .build();

        return accountRepo.save(account).getId();
    }

    @Override
    public Account findByPesel(String pesel) {
        return accountRepo.findByPesel(pesel);
    }

    @Override
    public Account saveExchange(Account account) {
       return accountRepo.save(account);
    }
}
