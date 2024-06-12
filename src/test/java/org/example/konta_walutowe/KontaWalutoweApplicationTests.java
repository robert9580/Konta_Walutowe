package org.example.konta_walutowe;

import org.example.konta_walutowe.api.ExchangeRequest;
import org.example.konta_walutowe.api.RegistrationRequest;
import org.example.konta_walutowe.domain.Currency;
import org.example.konta_walutowe.infrastructure.Account;
import org.example.konta_walutowe.infrastructure.KontoWalutoweRepository;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;

import java.math.BigDecimal;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class KontaWalutoweApplicationTests {

    private static final Logger logger = LoggerFactory.getLogger(KontaWalutoweApplicationTests.class);

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private KontoWalutoweRepository kontoWalutoweRepository;


    @Test
    void exchange() {

        //registration
        RegistrationRequest request = new RegistrationRequest();
        request.setPesel("06222722364");
        request.setName("Jan Kowalski");
        request.setInitialBalance(new BigDecimal("100.00"));
        restTemplate.postForObject("http://localhost:" + port + "/registration", request, Void.class);
        //exchange PLN -> USD
        ExchangeRequest exchangeRequest = new ExchangeRequest();
        exchangeRequest.setFrom(Currency.PLN);
        exchangeRequest.setTo(Currency.USD);
        exchangeRequest.setQuantity(new BigDecimal("25.00"));
        restTemplate.postForObject("http://localhost:" + port + "/account/{pesel}/exchange", exchangeRequest, Account.class, "06222722364");
        //read account
        Account account = restTemplate.getForObject("http://localhost:" + port + "/account/{pesel}", Account.class, "06222722364");
        logger.atInfo().log(account.toString());
        //exchange USD -> PLN
        ExchangeRequest exchangeRequest2 = new ExchangeRequest();
        exchangeRequest2.setFrom(Currency.USD);
        exchangeRequest2.setTo(Currency.PLN);
        exchangeRequest2.setQuantity(new BigDecimal("1.00"));
        restTemplate.postForObject("http://localhost:" + port + "/account/{pesel}/exchange", exchangeRequest2, Account.class, "06222722364");
        //read account
        Account account2 = restTemplate.getForObject("http://localhost:" + port + "/account/{pesel}", Account.class, "06222722364");
        logger.atInfo().log(account2.toString());  //TODO RS asercję zrobić a tzn trzeba zmokowć NBP

    }
}