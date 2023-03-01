package vsu.ru.market.services;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vsu.ru.market.models.ExchangeRate;
import vsu.ru.market.models.User;
import vsu.ru.market.models.Wallet;
import vsu.ru.market.repo.UserRepository;
import vsu.ru.market.repo.WalletRepository;
import vsu.ru.market.services.utils.AdditionalService;


import java.math.BigDecimal;

import java.util.*;


@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final WalletRepository walletRepository;
    private final TransactionService transactionService;
    private final ExchangeRateService rateService;

    public User extractUser(String secretKey) {
        User user = null;
        try {
            user = userRepository.findBySecretKey(secretKey).orElseThrow();
        } catch (UsernameNotFoundException e) {
        } finally {
            return user;
        }
    }

    public Map<String, String> getAllWallets(User user) {
        Map<String, String> result = new HashMap<>();

        Set<Wallet> wallets = user.getWallets();

        if (wallets.isEmpty()) {
            result.put("error", "Нет активных кошельков!");
            return result;
        }

        for (Wallet wallet : wallets) {
            result.put(wallet.getWalletName() + "_wallet", wallet.getSum().toString());
        }
        return result;
    }

    @Transactional
    public Map<String, String> replenishBalance(User user, Map<String, String> request) {
        Map<String, String> result = new HashMap<>();

        String[] requiredParam = {"RUB", "DOL", "EURO"};
        if (!AdditionalService.areParametersValid(request, requiredParam, 0)) {
            result.put("error", "Нет запрашиваемого кошелька или тело запроса пустое!");
            return result;
        }

        Set<Wallet> wallets = user.getWallets();

        if (wallets.isEmpty()) {
            result.put("error", "Нет активных кошельков!");
            return result;
        }

        List<String> requiredSum = new ArrayList<>(request.values());

        if(requiredSum.get(0).length() == 0){
            result.put("error", "Нет значения параметра!");
            return result;
        }
        String walletName = request.keySet().toString().substring(1, 4);

        for (Wallet curr : wallets) {
            if (curr.getWalletName().equals(walletName)) {
                curr.setSum(curr.getSum().add(new BigDecimal(requiredSum.get(0))));
                result.put(curr.getWalletName() + "_wallet", curr.getSum().toString());
                walletRepository.save(curr);

                long now = System.currentTimeMillis();
                transactionService.createTransaction(now);

                break;
            }
        }
        return result;
    }

    @Transactional
    public Map<String, String> withdrawMoney(User user, Map<String, String> request) {
        Map<String, String> result = new HashMap<>();

        String[] requiredParam = {"currency", "count"};
        if (!AdditionalService.areParametersValid(request, requiredParam, 1)) {
            result.put("error", "Нет необходимого параметра!");
            return result;
        }
        if (!request.containsKey("card") && !request.containsKey("wallet")) {
            result.put("error", "Нет необходимого параметра!");
            return result;
        }

        Set<Wallet> wallets = user.getWallets();

        if (wallets.isEmpty()) {
            result.put("error", "Нет активных кошельков!");
            return result;
        }

        List<String> values = new ArrayList<>(request.values());

        if(values.get(1).length() == 0 || values.get(2).length() == 0){
            result.put("error", "Нет значения параметра!");
            return result;
        }

        String currency = values.get(0);
        BigDecimal sum = new BigDecimal(values.get(1));



        boolean found = false;
        for (Wallet curr : wallets) {
            if (curr.getWalletName().equals(currency)) {
                found = true;
                BigDecimal newSum = curr.getSum().subtract(sum);

                if (!isEnoughMoney(newSum, new BigDecimal(0))) {
                    result.put("error", "Недостаточно средств!");
                    return result;
                }

                curr.setSum(newSum);
                result.put(currency + "_wallet", newSum.toString());
                walletRepository.save(curr);

                long now = System.currentTimeMillis();
                transactionService.createTransaction(now);
                break;
            }
        }

        if (!found) {
            result.put("error", "Нет запрашиваемого кошелька!");
            return result;
        }

        return result;
    }

    @Transactional
    public Map<String, String> exchangeMoney(User user, Map<String, String> request) {
        Map<String, String> result = new TreeMap<>();

        String[] requiredParam = {"from", "to", "amount"};
        if (!AdditionalService.areParametersValid(request, requiredParam, 1)) {
            result.put("error", "Нет необходимого параметра!");
            return result;
        }

        Set<Wallet> wallets = user.getWallets();

        if (wallets.isEmpty()) {
            result.put("error", "Нет активных кошельков!");
            return result;
        }

        List<String> values = new ArrayList<>(request.values());
        String currencyFrom = values.get(0);
        String currencyTo = values.get(1);

        if(values.get(2).length() == 0){
            result.put("error", "Нет значения параметра!");
            return result;
        }
        BigDecimal amount = new BigDecimal(values.get(2));

        Optional<ExchangeRate> exchangeRate = rateService.findByCurrencyNameAndExchangeName(currencyFrom, currencyTo);

        if (exchangeRate.isEmpty()) {
            result.put("error", "Нет кошелька с таким именем");
            return result;
        }

        Wallet walletTo = new Wallet();

        for (Wallet wallet : wallets) {
            if (wallet.getWalletName().equals(currencyFrom)) {
                if (!isEnoughMoney(wallet.getSum(), amount)) {
                    result.put("error", "Недостаточно средств!");
                    return result;
                }
                wallet.setSum(wallet.getSum().subtract(amount));
                walletRepository.save(wallet);
                continue;
            }
            if (wallet.getWalletName().equals(currencyTo)) walletTo = wallet;
        }

        BigDecimal rateDec = new BigDecimal(exchangeRate.get().getRate());
        BigDecimal previous = walletTo.getSum();
        BigDecimal current = amount.multiply(rateDec);
        walletTo.setSum(previous.add(current));
        walletRepository.save(walletTo);

        long now = System.currentTimeMillis();
        transactionService.createTransaction(now);


        result.put("currency_from", currencyFrom + "_wallet");
        result.put("currency_to", currencyTo + "_wallet");
        result.put("amount_from", amount.toString());
        result.put("amount_to", String.valueOf(current.doubleValue()));

        return result;
    }

    private boolean isEnoughMoney(BigDecimal value1, BigDecimal value2) {
        if (value1.compareTo(value2) < 0) return false;
        return true;
    }

}
