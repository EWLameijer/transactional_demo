package org.ericwubbo.transactionaldemo;

import java.math.BigDecimal;
import java.util.UUID;

public record DepositDto(UUID accountId, BigDecimal amount) {
}
