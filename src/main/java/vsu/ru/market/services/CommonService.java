package vsu.ru.market.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import vsu.ru.market.services.optional.AdditionalService;
import vsu.ru.market.services.optional.ExchangeRate;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CommonService {

    public Map<String, String> exchangeRate(Map<String, String> request) {
        Map<String, String> result = new HashMap<>();

        String[] requiredParam = {"currency"};
        if (!AdditionalService.areParametersValid(request, requiredParam, 1)) {
            result.put("error", "Нет необходимого параметра!");
            return result;
        }

        boolean found = false;
        for (String s : new String[]{"RUB", "DOL", "EURO"}) {
            if (request.containsKey(s)) {
                found = true;
                break;
            }
        }
        

        if (!found) {
            result.put("error", "Нет кошелька с таким именем");
            return result;
        }

        result = ExchangeRate.getRate().get(request.get("currency"));

        return result;
    }
}
