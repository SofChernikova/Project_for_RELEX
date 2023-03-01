package vsu.ru.market.models;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "rate")
public class ExchangeRate {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "rate_id", nullable = false, unique = true)
    private Long id;

    @Column(name = "currency_name", nullable = false,
            columnDefinition = "varchar(10)")
    private String currencyName;

    @Column(name = "exchange_name", nullable = false,
            columnDefinition = "varchar(10)")
    private String exchangeName;

    @Column(name = "rate", nullable = false,
            columnDefinition = "decimal(7, 3))")
    private double rate;


}
