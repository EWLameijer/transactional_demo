package org.ericwubbo.transactionaldemo;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@NoArgsConstructor
@Getter
public class Account {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private BigDecimal amount;

    public void addMoney(BigDecimal transfer) {
        amount = amount.add(transfer);
    }

    public Account(String amount) {
        this.amount = new BigDecimal(amount);
    }
}
