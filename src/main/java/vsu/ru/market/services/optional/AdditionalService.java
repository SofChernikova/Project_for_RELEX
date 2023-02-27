package vsu.ru.market.services.optional;


import java.util.Map;

/**
 * Дополнительный сервис, реализующий проверку параметров*/
public class AdditionalService {

    /**
     * @param requiredParam - массив ожидаемых ключей
     * @param special       - числовое значение:
     *                      1 - все requiredParam должны быть обязательно
     *                      0 - хотя бы один requiredParam должен быть
     */
    public static boolean areParametersValid(Map<String, String> requestParam, String[] requiredParam, int special) {
        if (requestParam.isEmpty()) return false;

        if (special == 1) {
            for (String s : requiredParam) {
                if (!requestParam.containsKey(s)) return false;
            }
            return true;
        }

        for (String s : requiredParam) {
            if (requestParam.containsKey(s)) return true;
        }
        return false;
    }
}
