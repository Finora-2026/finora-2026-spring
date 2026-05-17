package com.bellamyphan.finora_2026_spring.postgres.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record AccountDailyBalanceDto(LocalDate date, BigDecimal pendingBalance, BigDecimal postedBalance) {
}
