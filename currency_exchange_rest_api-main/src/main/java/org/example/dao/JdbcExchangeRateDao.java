package org.example.dao;

import org.example.DatabaseConnectionManager;
import org.example.entity.Currency;
import org.example.entity.ExchangeRate;
import org.example.exception.DatabaseOperationException;
import org.example.exception.EntityExistsException;
import org.sqlite.SQLiteErrorCode;
import org.sqlite.SQLiteException;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class JdbcExchangeRateDao implements ExchangeRateDao {

    @Override
    public Optional<ExchangeRate> findById(Long id) {
        final String query = """
            SELECT
                er.id AS id,
                bc.id AS base_id,
                bc.code AS base_code,
                bc.full_name AS base_name,
                bc.sign AS base_sign,
                tc.id AS target_id,
                tc.code AS target_code,
                tc.full_name AS target_name,
                tc.sign AS target_sign,
                er.rate AS rate
            FROM Exchange_rates er
            JOIN Currencies bc ON er.base_currency_id = bc.id
            JOIN Currencies tc ON er.target_currency_id = tc.id
            WHERE er.id = ?
            """;

        try (Connection connection = DatabaseConnectionManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setLong(1, id);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                return Optional.of(getExchangeRate(resultSet));
            }
        } catch (SQLException e) {
            throw new DatabaseOperationException(
                    "Failed to read exchange rate with id '" + id + "' from the database", e
            );
        }
        return Optional.empty();
    }

    @Override
    public List<ExchangeRate> findAll() {
        final String query = """
            SELECT
                er.id AS id,
                bc.id AS base_id,
                bc.code AS base_code,
                bc.full_name AS base_name,
                bc.sign AS base_sign,
                tc.id AS target_id,
                tc.code AS target_code,
                tc.full_name AS target_name,
                tc.sign AS target_sign,
                er.rate AS rate
            FROM Exchange_rates er
            JOIN Currencies bc ON er.base_currency_id = bc.id
            JOIN Currencies tc ON er.target_currency_id = tc.id
            ORDER BY er.id
            """;

        try (Connection connection = DatabaseConnectionManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            ResultSet resultSet = statement.executeQuery();
            List<ExchangeRate> exchangeRates = new ArrayList<>();

            while (resultSet.next()) {
                exchangeRates.add(getExchangeRate(resultSet));
            }

            return exchangeRates;
        } catch (SQLException e) {
            throw new DatabaseOperationException(
                    "Failed to read exchange rates from the database", e
            );
        }
    }

    @Override
    public ExchangeRate save(ExchangeRate entity) {
        final String query = """
            INSERT INTO Exchange_rates (base_currency_id, target_currency_id, rate)
            VALUES (?, ?, ?)
            """;

        try (Connection connection = DatabaseConnectionManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {

            statement.setLong(1, entity.getBaseCurrency().getId());
            statement.setLong(2, entity.getTargetCurrency().getId());
            statement.setBigDecimal(3, entity.getRate());

            int affectedRows = statement.executeUpdate();

            if (affectedRows == 0) {
                throw new DatabaseOperationException(
                        String.format("Failed to save exchange rate '%s' to '%s' to the database",
                                entity.getBaseCurrency().getCode(),
                                entity.getTargetCurrency().getCode())
                );
            }

            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    entity.setId(generatedKeys.getLong(1));
                } else {
                    throw new DatabaseOperationException(
                            "Failed to retrieve ID for the saved exchange rate");
                }
            }

            return entity;

        } catch (SQLException e) {
            if (e instanceof SQLiteException exception) {
                if (exception.getResultCode().code == SQLiteErrorCode.SQLITE_CONSTRAINT_UNIQUE.code) {
                    throw new EntityExistsException(
                            String.format("Exchange rate '%s' to '%s' already exists",
                                    entity.getBaseCurrency().getCode(),
                                    entity.getTargetCurrency().getCode())
                    );
                }
            }
            throw new DatabaseOperationException(
                    String.format("Failed to save exchange rate '%s' to '%s' to the database",
                            entity.getBaseCurrency().getCode(),
                            entity.getTargetCurrency().getCode()), e
            );
        }
    }

    @Override
    public Optional<ExchangeRate> update(ExchangeRate entity) {
        final String query = """
            UPDATE Exchange_rates
            SET rate = ?
            WHERE base_currency_id = ? AND target_currency_id = ?
            """;

        try (Connection connection = DatabaseConnectionManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setBigDecimal(1, entity.getRate());
            statement.setLong(2, entity.getBaseCurrency().getId());
            statement.setLong(3, entity.getTargetCurrency().getId());

            int affectedRows = statement.executeUpdate();

            if (affectedRows > 0) {
                return Optional.of(entity);
            }
        } catch (SQLException e) {
            throw new DatabaseOperationException(
                    "Failed to update exchange rate with id '" + entity.getId() + "' in the database", e
            );
        }
        return Optional.empty();
    }

    @Override
    public void delete(Long id) {
        final String query = """
            DELETE FROM Exchange_rates
            WHERE id = ?
            """;

        try (Connection connection = DatabaseConnectionManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setLong(1, id);
            statement.executeUpdate();

        } catch (SQLException e) {
            throw new DatabaseOperationException(
                    "Failed to delete exchange rate with id '" + id + "' from the database", e
            );
        }
    }

    @Override
    public Optional<ExchangeRate> findByCodes(String baseCurrencyCode, String targetCurrencyCode) {
        final String query = """
            SELECT
                er.id AS id,
                bc.id AS base_id,
                bc.code AS base_code,
                bc.full_name AS base_name,
                bc.sign AS base_sign,
                tc.id AS target_id,
                tc.code AS target_code,
                tc.full_name AS target_name,
                tc.sign AS target_sign,
                er.rate AS rate
            FROM Exchange_rates er
            JOIN Currencies bc ON er.base_currency_id = bc.id
            JOIN Currencies tc ON er.target_currency_id = tc.id
            WHERE bc.code = ? AND tc.code = ?
            """;

        try (Connection connection = DatabaseConnectionManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setString(1, baseCurrencyCode);
            statement.setString(2, targetCurrencyCode);

            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                return Optional.of(getExchangeRate(resultSet));
            }
        } catch (SQLException e) {
            throw new DatabaseOperationException(
                    String.format("Failed to read exchange rate '%s' to '%s' from the database",
                            baseCurrencyCode, targetCurrencyCode), e
            );
        }
        return Optional.empty();
    }

    private static ExchangeRate getExchangeRate(ResultSet resultSet) throws SQLException {
        return new ExchangeRate(
                resultSet.getLong("id"),
                new Currency(
                        resultSet.getLong("base_id"),
                        resultSet.getString("base_code"),
                        resultSet.getString("base_name"),
                        resultSet.getString("base_sign")
                ),
                new Currency(
                        resultSet.getLong("target_id"),
                        resultSet.getString("target_code"),
                        resultSet.getString("target_name"),
                        resultSet.getString("target_sign")
                ),
                resultSet.getBigDecimal("rate")
        );
    }
}
