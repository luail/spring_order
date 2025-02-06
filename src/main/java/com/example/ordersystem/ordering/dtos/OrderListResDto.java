package com.example.ordersystem.ordering.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderListResDto {
    private Long id;
    private String memberEmail;
    private String orderStatus;
    private List<OrderDetailDto> orderDetails;
}
