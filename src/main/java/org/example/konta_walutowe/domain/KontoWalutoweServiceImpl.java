package org.example.konta_walutowe.domain;

import lombok.RequiredArgsConstructor;
import org.example.konta_walutowe.api.RegistrationRequest;
import org.example.konta_walutowe.infrastructure.Account;
import org.example.konta_walutowe.infrastructure.ExchangeRestClient;
import org.example.konta_walutowe.infrastructure.Rates;
import org.example.konta_walutowe.infrastructure.KontoWalutoweRepository;
import org.example.konta_walutowe.library.BusinessException;
import org.springframework.stereotype.Service;
import pl.foltak.polishidnumbers.pesel.InvalidPeselException;
import pl.foltak.polishidnumbers.pesel.Pesel;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Clock;
import java.time.LocalDate;

import static java.util.Objects.isNull;

@Service
@RequiredArgsConstructor
class KontoWalutoweServiceImpl implements KontoWalutoweService {

    private final Clock clock;
    private final KontoWalutoweRepository kontoWalutoweRepository;
    private final ExchangeRestClient exchangeRestClient;

    private static final int ADULT_YEARS = 18;

    @Override
    public void registration(RegistrationRequest request) {
        Pesel pesel;
        try {
            pesel = new Pesel(request.getPesel());

        } catch (InvalidPeselException e) {
            throw new RuntimeException(e);  // 500
        }
        if (isNotAdult(pesel.getBirthDate())) {
            throw new BusinessException("Is not adult");
        }
        kontoWalutoweRepository.saveInitial(request);
    }

    @Override
    public Account getAccount(String pesel) {
        return kontoWalutoweRepository.findByPesel(pesel);
    }

    @Override
    public void exchange(Exchange exchange) {
        Account account = kontoWalutoweRepository.findByPesel(exchange.getPesel());
        if (exchange.getFrom() == Currency.USD) {
            checkEnoughFunds(account, exchange, Currency.USD);
            Rates rates = exchangeRestClient.getCurrentExchangeRate();
            BigDecimal zloty = exchange.getQuantity().multiply(rates.getRate().getBid()).setScale(2, RoundingMode.HALF_UP);
            BigDecimal currentDollar = isNull(account.getFunds().get(Currency.USD)) ? new BigDecimal("0.00") : account.getFunds().get(Currency.USD);
            BigDecimal nextDollar = currentDollar.subtract(exchange.getQuantity());
            BigDecimal currentZloty = account.getFunds().get(Currency.PLN);
            BigDecimal nextZloty = currentZloty.add(zloty);
            account.getFunds().put(Currency.USD, nextDollar);
            account.getFunds().put(Currency.PLN, nextZloty);
            kontoWalutoweRepository.saveExchange(account);
            return;

        }
        if (exchange.getFrom() == Currency.PLN) {
            checkEnoughFunds(account, exchange, Currency.PLN);
            Rates rates = exchangeRestClient.getCurrentExchangeRate();
            BigDecimal dollar = exchange.getQuantity().divide(rates.getRate().getAsk(), RoundingMode.HALF_UP);
            BigDecimal currentZloty = account.getFunds().get(Currency.PLN);
            BigDecimal nextZloty = currentZloty.subtract(exchange.getQuantity());
            BigDecimal currentDollar = isNull(account.getFunds().get(Currency.USD)) ? new BigDecimal("0.00") : account.getFunds().get(Currency.USD);
            BigDecimal nextDollar = currentDollar.add(dollar);
            account.getFunds().put(Currency.PLN, nextZloty);
            account.getFunds().put(Currency.USD, nextDollar);
            kontoWalutoweRepository.saveExchange(account);
            return;

        }
        throw new IllegalStateException("Currency pair not supported");
    }

    private void checkEnoughFunds(Account account, Exchange exchange, Currency currency) {
        if (isNull(account.getFunds().get(currency)) || account.getFunds().get(currency).compareTo(exchange.getQuantity()) < 0) {
            throw new BusinessException("Account is not enough funds");
        }
    }

    private boolean isNotAdult(LocalDate birthDate) {
        LocalDate adult = birthDate.plusYears(ADULT_YEARS);
        return adult.isAfter(LocalDate.now(clock));
    }
}
