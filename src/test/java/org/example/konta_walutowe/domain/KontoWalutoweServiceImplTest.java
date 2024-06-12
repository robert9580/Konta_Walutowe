package org.example.konta_walutowe.domain;

import org.example.konta_walutowe.api.RegistrationRequest;
import org.example.konta_walutowe.infrastructure.Account;
import org.example.konta_walutowe.infrastructure.ExchangeRestClient;
import org.example.konta_walutowe.infrastructure.KontoWalutoweRepository;
import org.example.konta_walutowe.infrastructure.Rates;
import org.example.konta_walutowe.library.BusinessException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.util.EnumMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.ThrowableAssert.catchThrowable;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class KontoWalutoweServiceImplTest {

    private static final Clock CLOCK = Clock.fixed(Instant.parse("2024-02-28T12:00:00Z"), ZoneId.of("Europe/Warsaw"));

    @Mock
    private KontoWalutoweRepository kontoWalutoweRepository;

    @Mock
    private ExchangeRestClient exchangeRestClient;

    private KontoWalutoweService kontoWalutoweService;

    @BeforeEach
    void setup() {
        kontoWalutoweService = new KontoWalutoweServiceImpl(CLOCK, kontoWalutoweRepository, exchangeRestClient);
    }

    @Nested
    class Registration {

        @ParameterizedTest
        @CsvSource(value = {"06222722364",
                "06222824332",
                "04222988283"})
        void registrationAdult(String pesel) {
            //given
            RegistrationRequest request = new RegistrationRequest();
            request.setPesel(pesel);

            //when
            assertDoesNotThrow(() -> kontoWalutoweService.registration(request));
            verifyNoMoreInteractions();

        }

        @ParameterizedTest
        @CsvSource(value = {"06230154342",
                "08222985226"})
        void registrationNotAdult(String pesel) {
            //given
            RegistrationRequest request = new RegistrationRequest();
            request.setPesel(pesel);

            //when
            Throwable thrown = catchThrowable(() -> kontoWalutoweService.registration(request));

            //then
            assertThat(thrown).isInstanceOf(BusinessException.class)
                    .hasMessage("Is not adult");
            verifyNoMoreInteractions();
        }
    }

    @Nested
    class Exchange {

        @Captor
        ArgumentCaptor<Account> captor;

        @Test
        void exchangePLNUSD() {
            //given
            String pesel = "06222722364";
            org.example.konta_walutowe.domain.Exchange exchange = org.example.konta_walutowe.domain.Exchange.builder()
                    .pesel(pesel)
                    .from(Currency.PLN)
                    .quantity(new BigDecimal("5.23"))
                    .build();
            Map<Currency, BigDecimal> funds = new EnumMap<>(Currency.class);
            funds.put(Currency.PLN, new BigDecimal("6.57"));
            Account account = Account.builder()
                    .id(1L)
                    .name("name")
                    .pesel(pesel)
                    .funds(funds)
                    .build();
            when(kontoWalutoweRepository.findByPesel(exchange.getPesel())).thenReturn(account);
            Rates rates = new Rates();
            Rates.Rate rate = new Rates.Rate(new BigDecimal("3.992"), new BigDecimal("4.0726"));
            rates.getRates().add(rate);
            when(exchangeRestClient.getCurrentExchangeRate()).thenReturn(rates);

            //when
            kontoWalutoweService.exchange(exchange);

            //then
            verify(kontoWalutoweRepository).saveExchange(captor.capture());
            Account accountCaptor = captor.getValue();
            assertThat(accountCaptor.getId()).isEqualTo(1L);
            assertThat(accountCaptor.getName()).isEqualTo("name");
            assertThat(accountCaptor.getPesel()).isEqualTo(pesel);
            assertThat(accountCaptor.getFunds().get(Currency.PLN)).isEqualTo(new BigDecimal("1.34"));
            assertThat(accountCaptor.getFunds().get(Currency.USD)).isEqualTo(new BigDecimal("1.28"));
            verifyNoMoreInteractions();
        }

        @Test
        void exchangeUSDPLN() {
            //given
            String pesel = "06222722364";
            org.example.konta_walutowe.domain.Exchange exchange = org.example.konta_walutowe.domain.Exchange.builder()
                    .pesel(pesel)
                    .from(Currency.USD)
                    .quantity(new BigDecimal("0.23"))
                    .build();
            Map<Currency, BigDecimal> funds = new EnumMap<>(Currency.class);
            funds.put(Currency.PLN, new BigDecimal("42.87"));
            funds.put(Currency.USD, new BigDecimal("9.99"));
            Account account = Account.builder()
                    .id(1L)
                    .name("name")
                    .pesel(pesel)
                    .funds(funds)
                    .build();
            when(kontoWalutoweRepository.findByPesel(exchange.getPesel())).thenReturn(account);
            Rates rates = new Rates();
            Rates.Rate rate = new Rates.Rate(new BigDecimal("3.992"), new BigDecimal("4.0726"));
            rates.getRates().add(rate);
            when(exchangeRestClient.getCurrentExchangeRate()).thenReturn(rates);

            //when
            kontoWalutoweService.exchange(exchange);

            //then
            verify(kontoWalutoweRepository).saveExchange(captor.capture());
            Account accountCaptor = captor.getValue();
            assertThat(accountCaptor.getId()).isEqualTo(1L);
            assertThat(accountCaptor.getName()).isEqualTo("name");
            assertThat(accountCaptor.getPesel()).isEqualTo(pesel);
            assertThat(accountCaptor.getFunds().get(Currency.PLN)).isEqualTo(new BigDecimal("43.79"));
            assertThat(accountCaptor.getFunds().get(Currency.USD)).isEqualTo(new BigDecimal("9.76"));
            verifyNoMoreInteractions();
        }

        @Test
        void exchangeNotEnoughFundsPLN() {
            //given
            String pesel = "06222722364";
            org.example.konta_walutowe.domain.Exchange exchange = org.example.konta_walutowe.domain.Exchange.builder()
                    .pesel(pesel)
                    .from(Currency.PLN)
                    .quantity(new BigDecimal("88.89"))
                    .build();
            Map<Currency, BigDecimal> funds = new EnumMap<>(Currency.class);
            funds.put(Currency.PLN, new BigDecimal("88.88"));
            funds.put(Currency.USD, new BigDecimal("9.99"));
            Account account = Account.builder()
                    .id(1L)
                    .name("name")
                    .pesel(pesel)
                    .funds(funds)
                    .build();
            when(kontoWalutoweRepository.findByPesel(exchange.getPesel())).thenReturn(account);

            //when
            Throwable thrown = catchThrowable(() -> kontoWalutoweService.exchange(exchange));

            //then
            assertThat(thrown).isInstanceOf(BusinessException.class)
                    .hasMessage("Account is not enough funds");
            verifyNoMoreInteractions();
        }

        @Test
        void exchangeNotEnoughFundsUSD() {
            //given
            String pesel = "06222722364";
            org.example.konta_walutowe.domain.Exchange exchange = org.example.konta_walutowe.domain.Exchange.builder()
                    .pesel(pesel)
                    .from(Currency.USD)
                    .quantity(new BigDecimal("10.00"))
                    .build();
            Map<Currency, BigDecimal> funds = new EnumMap<>(Currency.class);
            funds.put(Currency.PLN, new BigDecimal("88.88"));
            funds.put(Currency.USD, new BigDecimal("9.99"));
            Account account = Account.builder()
                    .id(1L)
                    .name("name")
                    .pesel(pesel)
                    .funds(funds)
                    .build();
            when(kontoWalutoweRepository.findByPesel(exchange.getPesel())).thenReturn(account);

            //when
            Throwable thrown = catchThrowable(() -> kontoWalutoweService.exchange(exchange));

            //then
            assertThat(thrown).isInstanceOf(BusinessException.class)
                    .hasMessage("Account is not enough funds");
            verifyNoMoreInteractions();
        }
    }

    private void verifyNoMoreInteractions() {
        Mockito.verifyNoMoreInteractions(kontoWalutoweRepository, exchangeRestClient);
    }
}
