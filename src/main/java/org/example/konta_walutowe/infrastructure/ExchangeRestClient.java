package org.example.konta_walutowe.infrastructure;

import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class ExchangeRestClient {

    public Rates getCurrentExchangeRate() {
        RestClient restClient = RestClient.create();
        return restClient.get()
                .uri("https://api.nbp.pl/api/exchangerates/rates/c/usd/")
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
//                .onStatus(HttpStatusCode::is4xxClientError, (request, response) -> {
//                    throw new MyCustomRuntimeException(response.getStatusCode(), response.getHeaders())
//                })
                .body(Rates.class);
    }
}
