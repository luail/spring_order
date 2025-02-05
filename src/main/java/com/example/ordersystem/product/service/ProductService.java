package com.example.ordersystem.product.service;

import com.example.ordersystem.member.domain.Member;
import com.example.ordersystem.member.repository.MemberRepository;
import com.example.ordersystem.product.domain.Product;
import com.example.ordersystem.product.dtos.ProductRegisterDto;
import com.example.ordersystem.product.dtos.ProductResDto;
import com.example.ordersystem.product.dtos.ProductSerchDto;
import com.example.ordersystem.product.repository.ProductRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.awt.print.Pageable;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.List;

@Service
@Transactional
public class ProductService {
    private final ProductRepository productRepository;
    private final MemberRepository memberRepository;
    private final S3Client s3Client;
    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    public ProductService(ProductRepository productRepository, MemberRepository memberRepository, S3Client s3Client) {
        this.productRepository = productRepository;
        this.memberRepository = memberRepository;
        this.s3Client = s3Client;
    }

    public Product productCreate(ProductRegisterDto dto) {
        try {
            //        member조회
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            Member member = memberRepository.findByEmail(authentication.getName()).orElseThrow(()->new EntityNotFoundException("member is not found"));

            Product product = productRepository.save(dto.toEntity(member));

//        aws에 image 저장 후에 url추출
//        aws의 s3접근가능한 iam계정생성, iam계정을 통해 aws에 접근가능한 접근객체 생성
            MultipartFile image = dto.getProductImage();
            byte[] bytes = image.getBytes();
            String filename = product.getId() + "_" + image.getOriginalFilename();
//        먼저 local에 저장.
            Path path = Paths.get("C:/Temp/temp", filename);
            Files.write(path, bytes, StandardOpenOption.CREATE, StandardOpenOption.WRITE);

//            저장객체
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucket)
                    .key(filename)
                    .build();
//            저장실행
            s3Client.putObject(putObjectRequest, RequestBody.fromFile(path));
            String s3Url = s3Client.utilities().getUrl(a->a.bucket(bucket).key(filename)).toExternalForm();
            product.updateImagePath(s3Url);
            return product;
        } catch (IOException e) {
            throw new RuntimeException("이미지 저장 실패");
        }

    }

    public Page<ProductResDto> findAll(Pageable pageable, ProductSerchDto serchDto) {
//        검색을 위해 Specification 객체 사용
//        Specification객체는 복잡한 쿼리를 명세를 이용하여 정의하는 방식으로, 쿼리를 쉽게 생성.
    }
}
