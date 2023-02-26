package vsu.ru.market.services.optional;

import lombok.*;

import java.util.HashMap;
import java.util.Map;



public class ExchangeRate {

    public static Map<String, Map<String, String>> getRate() {
        Map<String, Map<String, String>> rate = new HashMap<>();

        Map<String, String> dolMap = new HashMap<>();
        dolMap.put("RUB", "76.02");
        dolMap.put("EURO", "0.94");
        rate.put("DOL", dolMap);

        Map<String, String> euroMap = new HashMap<>();
        euroMap.put("DOL", "1.06");
        euroMap.put("RUB", "80.18");
        rate.put("EURO", euroMap);

        Map<String, String> rubMap = new HashMap<>();
        rubMap.put("DOL", "0.013");
        rubMap.put("EURO", "0.012");
        rate.put("RUB", rubMap);

        return rate;
    }
}
