package org.ericwubbo.transactionaldemo;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class AccountService {
    private final AccountRepository accountRepository;

    @Transactional
    public void transfer(Account payer, Account receiver, BigDecimal amount) {
        payer.addMoney(amount.negate());
        accountRepository.save(payer);
        receiver.addMoney(amount);
        accountRepository.save(receiver);
    }
}
