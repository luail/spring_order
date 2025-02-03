package com.example.ordersystem.product.controller;

import com.example.ordersystem.product.service.ProductService;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ProductController {
    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }
}
