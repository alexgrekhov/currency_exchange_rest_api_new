package org.example.utils;

import org.example.dto.CurrencyRequestDto;
import org.example.dto.CurrencyResponseDto;
import org.example.dto.ExchangeRateResponseDto;
import org.example.entity.Currency;
import org.example.entity.ExchangeRate;

public class MappingUtils {

    public static Currency convertToEntity(CurrencyRequestDto dto) {
        Currency currency = new Currency();

        String code = dto.code() != null ? dto.code().trim().toUpperCase() : null;
        String name = dto.name() != null ? dto.name().trim() : null;
        String sign = dto.sign() != null ? dto.sign().trim() : null;

        currency.setCode(code);
        currency.setFullName(name);
        currency.setSign(sign);

        return currency;
    }

    public static CurrencyResponseDto convertToDto(Currency currency) {
        return new CurrencyResponseDto(
                currency.getId(),
                currency.getCode(),
                currency.getFullName(),
                currency.getSign()
        );
    }

    public static ExchangeRateResponseDto convertToDto(ExchangeRate exchangeRate) {
        return new ExchangeRateResponseDto(
                exchangeRate.getId(),
                convertToDto(exchangeRate.getBaseCurrency()),
                convertToDto(exchangeRate.getTargetCurrency()),
                exchangeRate.getRate()
        );
    }
}


