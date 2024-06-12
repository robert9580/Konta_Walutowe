package org.example.konta_walutowe.infrastructure;

import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountRepo extends JpaRepository<Account, Long> {

    Account findByPesel(String pesel);

    boolean existsByPesel(String pesel);
}
