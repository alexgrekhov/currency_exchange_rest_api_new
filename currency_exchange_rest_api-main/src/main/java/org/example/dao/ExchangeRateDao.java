package org.example.dao;

import org.example.entity.ExchangeRate;

import java.util.Optional;

public interface ExchangeRateDao extends CrudDao<ExchangeRate, Long> {

    Optional<ExchangeRate> findByCodes(String baseCurrencyCode, String targetCurrencyCode);
}
