package org.example.dao;

import org.example.DatabaseConnectionManager;
import org.example.entity.Currency;
import org.example.exception.DatabaseOperationException;
import org.example.exception.EntityExistsException;
import org.sqlite.SQLiteErrorCode;
import org.sqlite.SQLiteException;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class JdbcCurrencyDao implements CurrencyDao {

    @Override
    public Optional<Currency> findById(Long id) {
        final String query = """
            SELECT *
            FROM Currencies
            WHERE id = ?
            """;

        try (Connection connection = DatabaseConnectionManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setLong(1, id);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                return Optional.of(getCurrency(resultSet));
            }
        } catch (SQLException e) {
            throw new DatabaseOperationException(
                    "Failed to read currency with id '" + id + "' from the database", e
            );
        }
        return Optional.empty();
    }

    @Override
    public List<Currency> findAll() {
        final String query = """
            SELECT *
            FROM Currencies
            """;

        try (Connection connection = DatabaseConnectionManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            ResultSet resultSet = statement.executeQuery();
            List<Currency> currencies = new ArrayList<>();

            while (resultSet.next()) {
                currencies.add(getCurrency(resultSet));
            }

            return currencies;
        } catch (SQLException e) {
            throw new DatabaseOperationException(
                    "Failed to read currencies from the database", e
            );
        }
    }

    @Override
    public Currency save(Currency entity) {
        final String query = """
            INSERT INTO Currencies (code, full_name, sign)
            VALUES (?, ?, ?)
            """;

        try (Connection connection = DatabaseConnectionManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {

            statement.setString(1, entity.getCode());
            statement.setString(2, entity.getFullName());
            statement.setString(3, entity.getSign());

            int affectedRows = statement.executeUpdate();

            if (affectedRows == 0) {
                throw new DatabaseOperationException(
                        "Failed to save currency with code '" + entity.getCode() + "' to the database"
                );
            }

            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return new Currency(
                            generatedKeys.getLong(1),
                            entity.getCode(),
                            entity.getFullName(),
                            entity.getSign()
                    );
                } else {
                    throw new DatabaseOperationException(
                            "Failed to retrieve ID for the saved currency");
                }
            }

        } catch (SQLException e) {
            if (e instanceof SQLiteException exception) {
                if (exception.getResultCode().code == SQLiteErrorCode.SQLITE_CONSTRAINT_UNIQUE.code) {
                    throw new EntityExistsException(
                            "Currency with code '" + entity.getCode() + "' already exists"
                    );
                }
            }
            throw new DatabaseOperationException(
                    "Failed to save currency with code '" + entity.getCode() + "' to the database", e
            );
        }
    }

    @Override
    public Optional<Currency> update(Currency entity) {
        final String query = """
            UPDATE Currencies
            SET code = ?, full_name = ?, sign = ?
            WHERE id = ?
            """;

        try (Connection connection = DatabaseConnectionManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setString(1, entity.getCode());
            statement.setString(2, entity.getFullName());
            statement.setString(3, entity.getSign());
            statement.setLong(4, entity.getId());

            int affectedRows = statement.executeUpdate();

            if (affectedRows > 0) {
                return Optional.of(entity);
            }
        } catch (SQLException e) {
            throw new DatabaseOperationException(
                    "Failed to update currency with id '" + entity.getId() + "' in the database", e
            );
        }
        return Optional.empty();
    }

    @Override
    public void delete(Long id) {
        final String query = """
            DELETE FROM Currencies
            WHERE id = ?
            """;

        try (Connection connection = DatabaseConnectionManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setLong(1, id);
            statement.executeUpdate();

        } catch (SQLException e) {
            throw new DatabaseOperationException(
                    "Failed to delete currency with id '" + id + "' from the database", e
            );
        }
    }

    @Override
    public Optional<Currency> findByCode(String code) {
        final String query = """
            SELECT *
            FROM Currencies
            WHERE code = ?
            """;

        try (Connection connection = DatabaseConnectionManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setString(1, code);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                return Optional.of(getCurrency(resultSet));
            }
        } catch (SQLException e) {
            throw new DatabaseOperationException(
                    "Failed to read currency with code '" + code + "' from the database", e
            );
        }
        return Optional.empty();
    }

    private static Currency getCurrency(ResultSet resultSet) throws SQLException {
        return new Currency(
                resultSet.getLong("id"),
                resultSet.getString("code"),
                resultSet.getString("full_name"),
                resultSet.getString("sign")
        );
    }
}

