package vsu.ru.market.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vsu.ru.market.models.Transaction;
import vsu.ru.market.repo.TransactionRepository;

import java.sql.Date;

@Service
@RequiredArgsConstructor
public class TransactionService {

    private final TransactionRepository transactionRepository;

    @Transactional
    public void createTransaction(long time){
        Transaction transaction = new Transaction(new Date(time));
        transactionRepository.save(transaction);
    }

}
