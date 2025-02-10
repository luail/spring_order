package com.example.ordersystem.common.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.context.annotation.Bean;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StockRabbitDto {
    private Long productId;
    private Integer productCount;
}
