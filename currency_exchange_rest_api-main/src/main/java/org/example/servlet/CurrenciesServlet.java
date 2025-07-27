package org.example.servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.dao.CurrencyDao;
import org.example.dao.JdbcCurrencyDao;
import org.example.dto.CurrencyRequestDto;
import org.example.dto.CurrencyResponseDto;
import org.example.entity.Currency;
import org.example.utils.MappingUtils;
import org.example.utils.ValidationUtils;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@WebServlet("/currencies")
public class CurrenciesServlet extends HttpServlet {

    private final CurrencyDao currencyDao = new JdbcCurrencyDao();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        List<Currency> currencies = currencyDao.findAll();

        List<CurrencyResponseDto> currenciesDto = currencies.stream()
                .map(MappingUtils::convertToDto)
                .collect(Collectors.toList());

        resp.setContentType("application/json;charset=UTF-8");
        objectMapper.writeValue(resp.getWriter(), currenciesDto);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        // Получаем параметры запроса и нормализуем
        String code = req.getParameter("code");
        String name = req.getParameter("name");
        String sign = req.getParameter("sign");

        String normalizedCode = code != null ? code.trim().toUpperCase() : null;
        String normalizedName = name != null ? name.trim() : null;
        String normalizedSign = sign != null ? sign.trim() : null;

        // Логируем для отладки
        System.out.println("POST /currencies received:");
        System.out.println("code = '" + normalizedCode + "'");
        System.out.println("name = '" + normalizedName + "'");
        System.out.println("sign = '" + normalizedSign + "'");

        // Создаем DTO
        CurrencyRequestDto currencyRequestDto = new CurrencyRequestDto(normalizedCode, normalizedName, normalizedSign);

        // Валидируем данные
        ValidationUtils.validate(currencyRequestDto);

        // Конвертируем DTO в сущность и сохраняем
        Currency currency = currencyDao.save(MappingUtils.convertToEntity(currencyRequestDto));

        // Отправляем ответ
        resp.setStatus(HttpServletResponse.SC_CREATED);
        resp.setContentType("application/json;charset=UTF-8");
        objectMapper.writeValue(resp.getWriter(), MappingUtils.convertToDto(currency));
    }
}

