package org.ericwubbo.transactionaldemo;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
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

    private boolean isBlocked = false;

    public void addMoney(BigDecimal transfer) throws Exception {
        if (isBlocked) throw new Exception("You cannot transfer money to a blocked account");
        amount = amount.add(transfer);
    }

    public Account(String amount) {
        this.amount = new BigDecimal(amount);
    }

    public void block() {
        isBlocked = true;
    }
}
