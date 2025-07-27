package org.example.dto;

import java.math.BigDecimal;

public record ExchangeRateRequestDto(
        String baseCurrencyCode,
        String targetCurrencyCode,
        BigDecimal rate
) {}

