package org.example;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.dto.ErrorResponseDto;
import org.example.exception.DatabaseOperationException;
import org.example.exception.EntityExistsException;
import org.example.exception.InvalidParameterException;
import org.example.exception.NotFoundException;

import java.io.IOException;

import static jakarta.servlet.http.HttpServletResponse.*;

@WebFilter("/*")
public class ExceptionHandlingFilter extends HttpFilter {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    protected void doFilter(HttpServletRequest req, HttpServletResponse res, FilterChain chain)
            throws IOException, ServletException {
        try {
            super.doFilter(req, res, chain);
        } catch (RuntimeException e) {
            int status = switch (e) {
                case DatabaseOperationException ignored -> HttpServletResponse.SC_INTERNAL_SERVER_ERROR;
                case EntityExistsException ignored      -> HttpServletResponse.SC_CONFLICT;
                case InvalidParameterException ignored  -> HttpServletResponse.SC_BAD_REQUEST;
                case NotFoundException ignored          -> HttpServletResponse.SC_NOT_FOUND;
                default -> HttpServletResponse.SC_INTERNAL_SERVER_ERROR;
            };

            writeErrorResponse(res, status, e);
        }
    }

    private void writeErrorResponse(HttpServletResponse response, int errorCode, RuntimeException e) throws IOException {
        response.setStatus(errorCode);
        objectMapper.writeValue(response.getWriter(), new ErrorResponseDto(errorCode, e.getMessage()));
    }
}

