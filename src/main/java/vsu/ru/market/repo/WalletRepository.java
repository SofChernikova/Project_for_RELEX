package vsu.ru.market.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import vsu.ru.market.models.Wallet;

public interface WalletRepository extends JpaRepository<Wallet, Long> {
}
