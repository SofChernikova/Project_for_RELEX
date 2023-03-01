package vsu.ru.market.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import vsu.ru.market.models.ExchangeRate;
import vsu.ru.market.models.User;


import java.util.List;
import java.util.Optional;

public interface ExchangeRateRepository extends JpaRepository<ExchangeRate, Long> {

    List<Optional<ExchangeRate>> findAllByCurrencyName(String currencyName);
    Optional<ExchangeRate> findByCurrencyNameAndExchangeName(String currencyName, String exchangeName);
}
