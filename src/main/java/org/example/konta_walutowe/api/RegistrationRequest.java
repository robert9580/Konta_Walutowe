package org.example.konta_walutowe.api;

import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.pl.PESEL;

import java.math.BigDecimal;

@NoArgsConstructor
@Getter
@Setter
public class RegistrationRequest {
    @Size(min = 2, max = 100)
    String name;
    @NotNull
    @PESEL
    String pesel;
    @NotNull
    @Digits(integer = 6, fraction = 2)
    BigDecimal initialBalance;
}
