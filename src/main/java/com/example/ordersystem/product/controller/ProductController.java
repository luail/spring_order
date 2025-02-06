package com.example.ordersystem.product.controller;

import com.example.ordersystem.product.domain.Product;
import com.example.ordersystem.product.dtos.ProductRegisterDto;
import com.example.ordersystem.product.dtos.ProductResDto;
import com.example.ordersystem.product.dtos.ProductSerchDto;
import com.example.ordersystem.product.service.ProductService;
import lombok.Getter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/product")
public class ProductController {
    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @PostMapping("/create")
    @PreAuthorize(("hasRole('ADMIN')"))
    public ResponseEntity<?> productCreate(ProductRegisterDto dto) {
        Product product = productService.productCreate(dto);
        return new ResponseEntity<>(product.getId(), HttpStatus.CREATED);
    }

    @GetMapping("/list")
    public ResponseEntity<?> productList(Pageable pageable, ProductSerchDto dto) {
        Page<ProductResDto> productResDtos = productService.findAll(pageable, dto);
        return new ResponseEntity<>(productResDtos, HttpStatus.OK);
    }

}
