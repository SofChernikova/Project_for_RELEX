package vsu.ru.market.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import vsu.ru.market.models.Wallet;

import java.util.List;
import java.util.Optional;

public interface WalletRepository extends JpaRepository<Wallet, Long> {

    List<Optional<Wallet>> findAllByWalletName(String walletName);
}
