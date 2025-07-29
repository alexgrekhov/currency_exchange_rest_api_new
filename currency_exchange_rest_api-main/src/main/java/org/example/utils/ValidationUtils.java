package org.example.utils;

import org.example.dto.CurrencyRequestDto;
import org.example.dto.ExchangeRateRequestDto;
import org.example.dto.ExchangeRequestDto;
import org.example.exception.InvalidParameterException;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.Set;
import java.util.stream.Collectors;

public class ValidationUtils {

    private static Set<String> currencyCodes;

    public static void validate(CurrencyRequestDto currencyRequestDto) {
        String code = currencyRequestDto.code();
        String name = currencyRequestDto.name();
        String sign = currencyRequestDto.sign();

        if (code == null || code.isBlank()) {
            throw new InvalidParameterException("Missing parameter - code");
        }

        if (name == null || name.isBlank()) {
            throw new InvalidParameterException("Missing parameter - name");
        }

        if (sign == null || sign.isBlank()) {
            throw new InvalidParameterException("Missing parameter - sign");
        }

        validateCurrencyCode(code);
    }

    public static void validate(ExchangeRateRequestDto exchangeRateRequestDto) {
        String baseCurrencyCode = exchangeRateRequestDto.baseCurrencyCode();
        String targetCurrencyCode = exchangeRateRequestDto.targetCurrencyCode();
        BigDecimal rate = exchangeRateRequestDto.rate();

        if (baseCurrencyCode == null || baseCurrencyCode.isBlank()) {
            throw new InvalidParameterException("Missing parameter - baseCurrencyCode");
        }

        if (targetCurrencyCode == null || targetCurrencyCode.isBlank()) {
            throw new InvalidParameterException("Missing parameter - targetCurrencyCode");
        }

        if (rate == null) {
            throw new InvalidParameterException("Missing parameter - rate");
        }

        if (rate.compareTo(BigDecimal.ZERO) < 0) {
            throw new InvalidParameterException("Invalid parameter - rate must be non-negative");
        }

        validateCurrencyCode(baseCurrencyCode);
        validateCurrencyCode(targetCurrencyCode);
    }

    public static void validate(ExchangeRequestDto exchangeRequestDto) {
        String baseCurrencyCode = exchangeRequestDto.baseCurrencyCode();
        String targetCurrencyCode = exchangeRequestDto.targetCurrencyCode();
        BigDecimal amount = exchangeRequestDto.amount();

        if (baseCurrencyCode == null || baseCurrencyCode.isBlank()) {
            throw new InvalidParameterException("Missing parameter - from");
        }

        if (targetCurrencyCode == null || targetCurrencyCode.isBlank()) {
            throw new InvalidParameterException("Missing parameter - to");
        }

        if (amount == null) {
            throw new InvalidParameterException("Missing parameter - amount");
        }

        if (amount.compareTo(BigDecimal.ZERO) < 0) {
            throw new InvalidParameterException("Invalid parameter - amount must be non-negative");
        }

        validateCurrencyCode(baseCurrencyCode);
        validateCurrencyCode(targetCurrencyCode);
    }

    public static void validateCurrencyCode(String code) {
        if (code == null) {
            throw new InvalidParameterException("Currency code must not be null");
        }

        String trimmedCode = code.trim().toUpperCase();

        // Для отладки — выводим реальный код, который проверяем
        System.out.println("🔍 validateCurrencyCode: [" + trimmedCode + "]");

        if (trimmedCode.length() != 3) {
            throw new InvalidParameterException("Currency code must contain exactly 3 letters");
        }

        if (!trimmedCode.matches("[A-Z]{3}")) {
            throw new InvalidParameterException("Currency code must contain only letters");
        }

        // Загружаем допустимые коды валют один раз
        if (currencyCodes == null) {
            currencyCodes = Currency.getAvailableCurrencies()
                    .stream()
                    .map(Currency::getCurrencyCode)
                    .collect(Collectors.toSet());
        }

        if (!currencyCodes.contains(trimmedCode)) {
            throw new InvalidParameterException("Currency code must be in ISO 4217 format");
        }
    }
}


