package vsu.ru.market.services;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import vsu.ru.market.controllers.requests.AllWalletsRequest;
import vsu.ru.market.models.User;
import vsu.ru.market.models.Wallet;
import vsu.ru.market.repo.UserRepository;
import vsu.ru.market.repo.WalletRepository;
import vsu.ru.market.services.optional.ExchangeRate;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final WalletRepository walletRepository;

    public User extractUser(String secretKey) {
        User user = userRepository.findBySecretKey(secretKey).orElseThrow(() -> new UsernameNotFoundException("No user found :( "));
        return user;
    }

    public Map<String, String> getAllWallets(AllWalletsRequest allWalletsRequest) {
        var user = userRepository.findBySecretKey(allWalletsRequest.getKey());
        Map<String, String> map = new HashMap<>();

        if (!user.isEmpty()) {
            Set<Wallet> wallets = user.get().getWallets();
            for (Wallet wallet : wallets) {
                map.put(wallet.getWalletName(), wallet.getSum().toString());
            }
        } else {
            map.put("error:", "Ошибка в значении ключа!");
        }
        return map;
    }


    public Map<String, String> replenishBalance(Map<String, String> request) {
        String key = request.get("key");
        var user = userRepository.findBySecretKey(key);
        Map<String, String> result = new HashMap<>();

        if (!user.isEmpty()) {
            request.remove("key");
            Set<Wallet> wallets = user.get().getWallets();

            for (Map.Entry<String, String> requestWalletEntry : request.entrySet()) {
                for (Wallet userWallet : wallets) {
                    if (userWallet.getWalletName().equals(requestWalletEntry.getKey())) {
                        userWallet.setSum(userWallet.getSum().add(new BigDecimal(requestWalletEntry.getValue())));
                        result.put(userWallet.getWalletName(), userWallet.getSum().toString());
                        walletRepository.save(userWallet);
                        break;
                    }
                }
            }
        } else {
            result.put("error:", "Ошибка в значении ключа!");
        }
        return result;
    }

    public Map<String, String> withdrawMoney(Map<String, String> request) {
        ArrayList<String> values = new ArrayList<>(request.values());
        String key = values.get(0);
        var user = userRepository.findBySecretKey(key);
        Map<String, String> result = new HashMap<>();

        if (!user.isEmpty()) {
            String currencyWallet = values.get(1);
            BigDecimal sum = new BigDecimal(values.get(2));
            String withdrawalName = values.get(3);
            var withdrawalWallet = Wallet.builder().walletName(withdrawalName).build();

            Set<Wallet> wallets = user.get().getWallets();

            for (Wallet userWallet : wallets) {
                if (userWallet.getWalletName().equals(currencyWallet)) {
                    BigDecimal newSum = userWallet.getSum().subtract(sum);
                    if (check(newSum, new BigDecimal(0))) {
                        withdrawalWallet.setSum(sum);
                        userWallet.setSum(newSum);
                        result.put(currencyWallet, newSum.toString());
                        walletRepository.save(userWallet);
                    } else {
                        result.put("error:", "Недостаточно средств!");
                    }
                    break;
                }
            }
        } else {
            result.put("error:", "Ошибка в значении ключа!");
        }
        return null;
    }

    public Map<String, String> exchangeRate(Map<String, String> request) {
        ArrayList<String> values = new ArrayList<>(request.values());
        String key = values.get(0);
        var user = userRepository.findBySecretKey(key);
        Map<String, String> rates = new HashMap<>();
        if (!user.isEmpty()) {
            String currencyWallet = values.get(1);
            rates = ExchangeRate.getRate().get(currencyWallet);
        } else {
            rates.put("error:", "Ошибка в значении ключа!");
        }
        return rates;
    }

    public Map<String, String> exchangeMoney(Map<String, String> request) {
        ArrayList<String> values = new ArrayList<>(request.values());
        String key = values.get(0);
        var user = userRepository.findBySecretKey(key);
        Map<String, String> result = new TreeMap<>();

        if (!user.isEmpty()) {
            Set<Wallet> wallets = user.get().getWallets();

            String currencyFrom = values.get(1);
            String currencyTo = values.get(2);
            BigDecimal amount = new BigDecimal(values.get(3));

            Wallet walletTo = new Wallet();

            boolean isEnough = false;
            for (Wallet wallet : wallets) {
                if (wallet.getWalletName().equals(currencyFrom)) {
                    if (check(wallet.getSum(), amount)) {
                        wallet.setSum(wallet.getSum().subtract(amount));
                        isEnough = true;
                        walletRepository.save(wallet);
                        continue;
                    } else {
                        result.put("error:", "Недостаточно средств!");
                        break;
                    }
                }
                if (wallet.getWalletName().equals(currencyTo)) walletTo = wallet;
            }

            if (isEnough) {
                String rateStr = ExchangeRate.getRate().get(currencyFrom).get(currencyTo);
                BigDecimal rateDec = new BigDecimal(rateStr);
                BigDecimal previous = walletTo.getSum();
                BigDecimal current = amount.multiply(rateDec);
                walletTo.setSum(previous.add(current));
                walletRepository.save(walletTo);

                result.put("currency_from", currencyFrom);
                result.put("currency_to", currencyTo);
                result.put("amount_from", amount.toString());
                result.put("amount_to", current.toString());
            }
        } else {
            result.put("error:", "Ошибка в значении ключа!");
        }
        return result;
    }

    private boolean check(BigDecimal value1, BigDecimal value2) {
        if (value1.compareTo(value2) < 0) return false;
        return true;
    }
}
