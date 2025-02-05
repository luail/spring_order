package com.example.ordersystem.product.dtos;

import com.example.ordersystem.member.domain.Member;
import com.example.ordersystem.product.domain.Product;
import jakarta.persistence.Entity;
import lombok.*;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.multipart.MultipartFile;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class ProductRegisterDto {
    private String name;
    private String category;
    private Integer price;
    private Integer stockQuantity;
    private MultipartFile productImage;

    public Product toEntity(Member member) {
        return Product.builder().name(this.name).category(this.category)
                .price(this.price).stockQuantity(this.stockQuantity)
                .member(member)
                .build();
    }
}
