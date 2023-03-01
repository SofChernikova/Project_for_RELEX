package vsu.ru.market.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vsu.ru.market.models.ExchangeRate;
import vsu.ru.market.repo.ExchangeRateRepository;


import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ExchangeRateService {

    private final ExchangeRateRepository rateRepository;

    @Transactional
    public void saveRate(ExchangeRate rate) {
        rateRepository.save(rate);
    }

    public List<Optional<ExchangeRate>> findRateByCurrencyName(String name) {
        return rateRepository.findAllByCurrencyName(name);
    }

    public Optional<ExchangeRate> findByCurrencyNameAndExchangeName(String currency, String exchange){
       return rateRepository.findByCurrencyNameAndExchangeName(currency, exchange);
    }
}
