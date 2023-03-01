package vsu.ru.market.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vsu.ru.market.models.ExchangeRate;
import vsu.ru.market.models.Transaction;
import vsu.ru.market.models.Wallet;
import vsu.ru.market.repo.TransactionRepository;
import vsu.ru.market.repo.WalletRepository;
import vsu.ru.market.services.utils.AdditionalService;

import java.math.BigDecimal;
import java.util.*;
import java.sql.Date;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final WalletRepository walletRepository;
    private final TransactionRepository transactionRepository;
    private final ExchangeRateService rateService;

    @Transactional
    public Map<String, String> changeExchangeRate(Map<String, String> request) {
        Map<String, String> result = new HashMap<>();

        String[] requiredParam = {"currency"};
        if (!AdditionalService.areParametersValid(request, requiredParam, 1) || request.size() < 3) {
            result.put("error", "Нет необходимого параметра!");
            return result;
        }

        String currName = request.get("currency");
        List<Optional<ExchangeRate>> current = rateService.findRateByCurrencyName(currName);

        if(current.isEmpty()){
            result.put("error", "Нет запрашиваемого кошелька!");
            return result;
        }

        for (Optional<ExchangeRate> curr : current) {
            String name = curr.get().getExchangeName();

            if (!request.containsKey(name)) {
                result.clear();
                result.put("error", "Нет запрашиваемого кошелька!");
                return result;
            }

            if(request.get(name).length() == 0){
                result.clear();
                result.put("error", "Нет значения!");
                return result;
            }

            result.put(name, request.get(name));
            curr.get().setRate(Double.parseDouble(request.get(name)));
            rateService.saveRate(curr.get());
        }
        return result;
    }

    public Map<String, String> totalAmount(Map<String, String> request) {
        Map<String, String> result = new HashMap<>();

        String[] requiredParam = {"currency"};
        if (!AdditionalService.areParametersValid(request, requiredParam, 1)) {
            result.put("error", "Нет необходимого параметра!");
            return result;
        }

        String currency = request.get("currency");
        List<Optional<Wallet>> wallets = walletRepository.findAllByWalletName(currency);

        if (wallets.isEmpty()) {
            result.put("error", "Нет активных кошельков!");
            return result;
        }

        BigDecimal sum = new BigDecimal(0);
        for (Optional<Wallet> wallet : wallets) {
            sum = sum.add(wallet.get().getSum());
        }
        result.put(currency, sum.toString());

        return result;
    }

    public Map<String, String> totalTransactions(Map<String, String> request) {
        Map<String, String> result = new HashMap<>();

        String[] requiredParam = {"from", "to"};
        if (!AdditionalService.areParametersValid(request, requiredParam, 1)) {
            result.put("error", "Нет необходимого параметра!");
            return result;
        }

        Date fromDate;
        Date toDate;
        try {
            fromDate = Date.valueOf(request.get("from"));
            toDate = Date.valueOf(request.get("to"));
        } catch (IllegalArgumentException e) {
            result.put("error", "Формат даты должен быть yyyy-[m]m-[d]d");
            return result;
        }

        List<Optional<Transaction>> transactions = transactionRepository
                .findAllByDateBetween(fromDate, toDate);

        if (transactions.isEmpty()) {
            result.put("error", "Нет транзакций за данный период!");
            return result;
        }

        result.put("total_transactions", String.valueOf(transactions.size()));

        return result;
    }
}
