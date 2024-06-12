package org.example.konta_walutowe.api;


import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.example.konta_walutowe.domain.KontoWalutoweService;
import org.example.konta_walutowe.infrastructure.Account;
import org.hibernate.validator.constraints.pl.PESEL;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class KontaWalutoweController {

    private final KontoWalutoweService kontoWalutoweService;
    private final ApiMapper mapper;

    @PostMapping("/registration")
    public void registration(@RequestBody @NotNull @Valid RegistrationRequest request) {
        kontoWalutoweService.registration(request);
    }

    @GetMapping("/account/{pesel}")
    public Account account(@PathVariable("pesel") @NotNull @PESEL String pesel) {
        return kontoWalutoweService.getAccount(pesel);
    }

    @PostMapping("/account/{pesel}/exchange")
    public void exchange(@PathVariable("pesel") @NotNull @PESEL String pesel, @RequestBody @NotNull @Valid ExchangeRequest request) {
        kontoWalutoweService.exchange(mapper.map(pesel, request));
    }

}
