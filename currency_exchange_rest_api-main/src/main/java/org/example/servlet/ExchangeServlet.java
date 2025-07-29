package org.example.servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.dto.ExchangeRequestDto;
import org.example.dto.ExchangeResponseDto;
import org.example.service.ExchangeService;
import org.example.utils.ValidationUtils;
import java.io.IOException;
import java.math.BigDecimal;
import java.security.InvalidParameterException;

@WebServlet("/exchange")
public class ExchangeServlet extends HttpServlet {

    private final ExchangeService exchangeService = new ExchangeService();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String baseCurrencyCode = req.getParameter("from");
        String targetCurrencyCode = req.getParameter("to");
        String amountStr = req.getParameter("amount");

        if (baseCurrencyCode == null || baseCurrencyCode.isBlank()) {
            throw new InvalidParameterException("Missing parameter - from");
        }

        if (targetCurrencyCode == null || targetCurrencyCode.isBlank()) {

            throw new org.example.exception.InvalidParameterException("Missing parameter - to");

        }

        if (amountStr == null || amountStr.isBlank()) {
            throw new InvalidParameterException("Missing parameter - amount");
        }

        BigDecimal amount = convertToNumber(amountStr);

        ExchangeRequestDto exchangeRequestDto = new ExchangeRequestDto(
                baseCurrencyCode.trim().toUpperCase(),
                targetCurrencyCode.trim().toUpperCase(),
                amount
        );

        ValidationUtils.validate(exchangeRequestDto);

        ExchangeResponseDto exchangeResponseDto = exchangeService.exchange(exchangeRequestDto);

        objectMapper.writeValue(resp.getWriter(), exchangeResponseDto);
    }

    private static BigDecimal convertToNumber(String input) {
        try {
            return new BigDecimal(input.trim());
        } catch (NumberFormatException e) {
            throw new InvalidParameterException("Parameter amount must be a valid number");
        }
    }
}

