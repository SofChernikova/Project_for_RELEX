package vsu.ru.market.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import vsu.ru.market.models.Transaction;

import java.util.List;
import java.util.Optional;
import java.sql.Date;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    List<Optional<Transaction>> findAllByDateBetween(Date start, Date end);
}
