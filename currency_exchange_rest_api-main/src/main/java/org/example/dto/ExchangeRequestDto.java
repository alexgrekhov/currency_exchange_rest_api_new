package org.example.dto;

import java.math.BigDecimal;

public record ExchangeRequestDto(
        String baseCurrencyCode,
        String targetCurrencyCode,
        BigDecimal amount
) {}

