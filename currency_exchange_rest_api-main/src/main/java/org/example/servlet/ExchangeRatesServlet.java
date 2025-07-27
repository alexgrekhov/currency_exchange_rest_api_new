package org.example.servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.dao.ExchangeRateDao;
import org.example.dao.JdbcExchangeRateDao;
import org.example.dto.ExchangeRateRequestDto;
import org.example.dto.ExchangeRateResponseDto;
import org.example.entity.ExchangeRate;
import org.example.exception.InvalidParameterException;
import org.example.service.ExchangeRateService;
import org.example.utils.MappingUtils;
import org.example.utils.ValidationUtils;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

import static jakarta.servlet.http.HttpServletResponse.SC_CREATED;
import static org.example.utils.MappingUtils.convertToDto;

@WebServlet("/exchangeRates")
public class ExchangeRatesServlet extends HttpServlet {

    private final ExchangeRateDao exchangeRateDao = new JdbcExchangeRateDao();
    private final ExchangeRateService exchangeRateService = new ExchangeRateService();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        List<ExchangeRate> exchangeRates = exchangeRateDao.findAll();

        List<ExchangeRateResponseDto> exchangeRatesDto = exchangeRates.stream()
                .map(MappingUtils::convertToDto)
                .collect(Collectors.toList());

        objectMapper.writeValue(resp.getWriter(), exchangeRatesDto);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String baseCurrencyCode = req.getParameter("baseCurrencyCode");
        String targetCurrencyCode = req.getParameter("targetCurrencyCode");
        String rate = req.getParameter("rate");

        if (rate == null || rate.isBlank()) {
            throw new InvalidParameterException("Missing parameter - rate");
        }

        if (baseCurrencyCode.equalsIgnoreCase(targetCurrencyCode)) {
            throw new InvalidParameterException("Base currency and target currency cannot be the same");
        }

        ExchangeRateRequestDto exchangeRateRequestDto = new ExchangeRateRequestDto(baseCurrencyCode, targetCurrencyCode, convertToNumber(rate));

        ValidationUtils.validate(exchangeRateRequestDto);

        ExchangeRate exchangeRate = exchangeRateService.save(exchangeRateRequestDto);

        resp.setStatus(SC_CREATED);
        objectMapper.writeValue(resp.getWriter(), convertToDto(exchangeRate));
    }

    private static BigDecimal convertToNumber(String rate) {
        try {
            return BigDecimal.valueOf(Double.parseDouble(rate));
        }
        catch (NumberFormatException e) {
            throw new InvalidParameterException("Parameter rate must be a number");
        }
    }
}
