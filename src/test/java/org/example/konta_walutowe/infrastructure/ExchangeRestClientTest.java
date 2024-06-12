package org.example.konta_walutowe.infrastructure;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ExtendWith(MockitoExtension.class)
class ExchangeRestClientTest {

    private static final Logger logger = LoggerFactory.getLogger(ExchangeRestClientTest.class);

    private ExchangeRestClient exchangeRestClient = new ExchangeRestClient();

    @Test
    void get() {
        logger.info(exchangeRestClient.getCurrentExchangeRate().toString());
    }
}
