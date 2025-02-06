package com.example.ordersystem.ordering.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class OrderDetailDto {
    private Long detailId;
    private String productName;
    private Integer count;
}
