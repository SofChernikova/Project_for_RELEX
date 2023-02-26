package vsu.ru.market.models;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "wallet")
public class Wallet {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "wallet_id", nullable = false, unique = true)
    private Long id;

    @Column(name = "wallet_name", nullable = false,
            columnDefinition = "varchar(128)")
    private String walletName;

    @Column(name = "total", nullable = false,
            columnDefinition = "decimal(10, 4)")
    private BigDecimal sum;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
}
