package org.example.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


public record CurrencyResponseDto(Long id, String code, String name, String sign) {}

