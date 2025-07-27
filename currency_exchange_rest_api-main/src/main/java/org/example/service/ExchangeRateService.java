package org.example.service;

import org.example.dao.CurrencyDao;
import org.example.dao.ExchangeRateDao;
import org.example.dao.JdbcCurrencyDao;
import org.example.dao.JdbcExchangeRateDao;
import org.example.dto.ExchangeRateRequestDto;
import org.example.entity.Currency;
import org.example.entity.ExchangeRate;
import org.example.exception.NotFoundException;

public class ExchangeRateService {

    private final CurrencyDao currencyDao = new JdbcCurrencyDao();
    private final ExchangeRateDao exchangeRateDao = new JdbcExchangeRateDao();

    public ExchangeRate save(ExchangeRateRequestDto exchangeRateRequestDto) {
        String baseCurrencyCode = exchangeRateRequestDto.baseCurrencyCode();   // ✅
        String targetCurrencyCode = exchangeRateRequestDto.targetCurrencyCode(); // ✅

        Currency baseCurrency = currencyDao.findByCode(baseCurrencyCode)
                .orElseThrow(() -> new NotFoundException("Currency with code '" + baseCurrencyCode + "' not found"));
        Currency targetCurrency = currencyDao.findByCode(targetCurrencyCode)
                .orElseThrow(() -> new NotFoundException("Currency with code '" + targetCurrencyCode + "' not found"));

        ExchangeRate exchangeRate = new ExchangeRate(
                baseCurrency,
                targetCurrency,
                exchangeRateRequestDto.rate() // ✅
        );

        return exchangeRateDao.save(exchangeRate);
    }

    public ExchangeRate update(ExchangeRateRequestDto exchangeRateRequestDto) {
        String baseCurrencyCode = exchangeRateRequestDto.baseCurrencyCode();   // ✅
        String targetCurrencyCode = exchangeRateRequestDto.targetCurrencyCode(); // ✅

        Currency baseCurrency = currencyDao.findByCode(baseCurrencyCode)
                .orElseThrow(() -> new NotFoundException("Currency with code '" + baseCurrencyCode + "' not found"));
        Currency targetCurrency = currencyDao.findByCode(targetCurrencyCode)
                .orElseThrow(() -> new NotFoundException("Currency with code '" + targetCurrencyCode + "' not found"));

        ExchangeRate exchangeRate = new ExchangeRate(
                baseCurrency,
                targetCurrency,
                exchangeRateRequestDto.rate() // ✅
        );

        return exchangeRateDao.update(exchangeRate)
                .orElseThrow(() -> new NotFoundException(
                        "Failed to update exchange rate '" + baseCurrencyCode + "' - '" + targetCurrencyCode + "', no such exchange rate found")
                );
    }
}
