package org.ericwubbo.transactionaldemo;

import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class AccountController {
    private final AccountRepository accountRepository;

    private final AccountService accountService;

    @GetMapping
    public List<Account> getAll() {
        return accountRepository.findAll();
    }

    @PatchMapping(consumes = MediaType.TEXT_PLAIN_VALUE, value = "{id}")
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

    @PatchMapping("/block/{id}")
    public ResponseEntity<Account> blockAccount(@PathVariable UUID id) {
        return accountRepository.findById(id).map(account -> {
                            account.block();
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

        try {
            accountService.transfer(payer, receiver, amount);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }

        return ResponseEntity.ok(List.of(payer, receiver));
    }
}
