package org.ericwubbo.transactionaldemo;

import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.springframework.web.bind.annotation.RequestMethod.PATCH;

@RestController
@RequiredArgsConstructor
public class AccountController {
    private final AccountRepository accountRepository;

    @GetMapping
    public List<Account> getAll() {
        return accountRepository.findAll();
    }

    @RequestMapping(consumes = MediaType.TEXT_PLAIN_VALUE, method = PATCH, value = "{id}")
    public ResponseEntity<Account> depositMoney(@PathVariable UUID id, @RequestBody String amountAsString) {
        var amount = new BigDecimal(amountAsString);
        if (amount.compareTo(BigDecimal.ZERO) <= 0) return ResponseEntity.badRequest().build();
        return accountRepository.findById(id).map(account -> {
                            account.addMoney(amount);
                            return accountRepository.save(account);
                        }
                ).map(ResponseEntity::ok).
                orElse(ResponseEntity.notFound().build());
    }

    @PatchMapping
    public ResponseEntity<List<Account>> transferMoney(@RequestBody TransferDto transfer) {
        BigDecimal amount = transfer.amount();
        if (amount.compareTo(BigDecimal.ZERO) <= 0) return ResponseEntity.badRequest().build();
        Optional<Account> possiblePayer = accountRepository.findById(transfer.payerId());
        if (possiblePayer.isEmpty()) return ResponseEntity.notFound().build();
        Account payer = possiblePayer.get();
        if (payer.getAmount().compareTo(amount) <= 0) return ResponseEntity.badRequest().build();
        Optional<Account> possibleReceiver = accountRepository.findById(transfer.receiverId());
        if (possibleReceiver.isEmpty()) return ResponseEntity.notFound().build();
        Account receiver = possibleReceiver.get();

        payer.addMoney(amount.negate());
        accountRepository.save(payer);
        receiver.addMoney(amount);
        accountRepository.save(receiver); // of course, I could have used saveAll here, but to demonstrate @Transactional...

        return ResponseEntity.ok(List.of(payer, receiver));
    }
}
