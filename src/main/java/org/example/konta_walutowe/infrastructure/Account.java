package org.example.konta_walutowe.infrastructure;

import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.MapKeyEnumerated;
import jakarta.persistence.SequenceGenerator;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.example.konta_walutowe.domain.Currency;
import org.hibernate.annotations.Immutable;

import java.math.BigDecimal;
import java.util.EnumMap;
import java.util.Map;

import static lombok.AccessLevel.PRIVATE;
import static lombok.AccessLevel.PROTECTED;

@Entity
@NoArgsConstructor(access = PROTECTED)
@AllArgsConstructor(access = PRIVATE)
@Builder
@Getter
@ToString
@EqualsAndHashCode
public class Account {

    @Id
//    @SequenceGenerator(name = "account_seq", sequenceName = "account_id_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.IDENTITY/*, generator = "account_seq"*/)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false, unique=true)
    private String pesel;

    @MapKeyEnumerated(EnumType.STRING)
    @ElementCollection(fetch = FetchType.EAGER)
    @Column(name = "ACCOUNT_BALANCE")
    @JoinTable(name = "ACCOUNT_BALANCES", joinColumns = @JoinColumn(name = "ID"))
    private Map<Currency, BigDecimal> funds;
}
