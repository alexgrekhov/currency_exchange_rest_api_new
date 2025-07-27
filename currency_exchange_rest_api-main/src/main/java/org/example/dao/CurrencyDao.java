package org.example.dao;

import org.example.entity.Currency;

import java.util.Optional;

public interface CurrencyDao extends CrudDao<Currency, Long> {

    Optional<Currency> findByCode(String code);
}
