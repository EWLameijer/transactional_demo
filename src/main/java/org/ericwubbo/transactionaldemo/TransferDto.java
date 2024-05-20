package org.ericwubbo.transactionaldemo;

import java.math.BigDecimal;
import java.util.UUID;

public record TransferDto(UUID payerId, UUID receiverId, BigDecimal amount) {
}
