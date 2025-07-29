package org.example.dto;

import java.math.BigDecimal;

public record ExchangeRateResponseDto(
        Long id,
        CurrencyResponseDto baseCurrency,
        CurrencyResponseDto targetCurrency,
        BigDecimal rate
) {}

