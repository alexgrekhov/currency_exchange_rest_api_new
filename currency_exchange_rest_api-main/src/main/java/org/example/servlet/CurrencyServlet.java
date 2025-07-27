package org.example.servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.dao.CurrencyDao;
import org.example.dao.JdbcCurrencyDao;
import org.example.entity.Currency;
import org.example.exception.NotFoundException;
import org.example.utils.ValidationUtils;

import java.io.IOException;

import static org.example.utils.MappingUtils.convertToDto;

@WebServlet("/currency/*")
public class CurrencyServlet extends HttpServlet {

    private final CurrencyDao currencyDao = new JdbcCurrencyDao();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String code = req.getPathInfo().replaceFirst("/", "");

        ValidationUtils.validateCurrencyCode(code);

        Currency currency = currencyDao.findByCode(code)
                .orElseThrow(() -> new NotFoundException("Currency with code '" + code + "' not found"));

        objectMapper.writeValue(resp.getWriter(), convertToDto(currency));
    }
}
