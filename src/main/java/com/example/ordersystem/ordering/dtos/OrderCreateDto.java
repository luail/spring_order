package com.example.ordersystem.ordering.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class OrderCreateDto {
    private Long productId;
    private Integer productCount;
}
