package vsu.ru.market.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import vsu.ru.market.models.ExchangeRate;
import vsu.ru.market.services.utils.AdditionalService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CommonService {

    private final ExchangeRateService rateService;

    public Map<String, String> exchangeRate(Map<String, String> request) {
        Map<String, String> result = new HashMap<>();

        String[] requiredParam = {"currency"};
        if (!AdditionalService.areParametersValid(request, requiredParam, 1)) {
            result.put("error", "Нет необходимого параметра!");
            return result;
        }

        List<Optional<ExchangeRate>> rates = rateService.findRateByCurrencyName(request.get("currency"));

        if(rates.isEmpty()){
            result.put("error", "Нет кошелька с таким именем");
            return result;
        }

        for(Optional<ExchangeRate> rate : rates){
            ExchangeRate exchangeRate = rate.get();
            result.put(exchangeRate.getExchangeName(), String.valueOf(exchangeRate.getRate()));
        }

        return result;
    }
}
