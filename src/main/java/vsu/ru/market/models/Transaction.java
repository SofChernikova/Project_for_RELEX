package vsu.ru.market.models;

import jakarta.persistence.*;
import lombok.*;

import java.sql.Date;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "my_transaction")
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "trans_id", nullable = false, unique = true)
    private Long id;

    @Column(name = "trans_date", nullable = false,
            columnDefinition = "date")
    private Date date;

    public Transaction(Date date) {
        this.date = date;
    }
}
