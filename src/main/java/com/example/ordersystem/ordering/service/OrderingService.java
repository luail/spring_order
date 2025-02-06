package com.example.ordersystem.ordering.service;

import com.example.ordersystem.member.domain.Member;
import com.example.ordersystem.member.repository.MemberRepository;
import com.example.ordersystem.ordering.domain.OrderDetail;
import com.example.ordersystem.ordering.domain.Ordering;
import com.example.ordersystem.ordering.dtos.OrderCreateDto;
import com.example.ordersystem.ordering.dtos.OrderDetailDto;
import com.example.ordersystem.ordering.dtos.OrderListResDto;
import com.example.ordersystem.ordering.repository.OrderingDetailRepository;
import com.example.ordersystem.ordering.repository.OrderingRepository;
import com.example.ordersystem.product.domain.Product;
import com.example.ordersystem.product.repository.ProductRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class OrderingService {
    private final OrderingRepository orderingRepository;
    private final MemberRepository memberRepository;
    private final OrderingDetailRepository orderingDetailRepository;
    private final ProductRepository productRepository;

    public OrderingService(OrderingRepository orderingRepository, MemberRepository memberRepository, OrderingDetailRepository orderingDetailRepository, ProductRepository productRepository) {
        this.orderingRepository = orderingRepository;
        this.memberRepository = memberRepository;
        this.orderingDetailRepository = orderingDetailRepository;
        this.productRepository = productRepository;
    }

    public Ordering orderCreate(List<OrderCreateDto> dtos) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Member member = memberRepository.findByEmail(email).orElseThrow(()->new EntityNotFoundException("member is not found"));

////        방법1. cascading 없이 db저장
////        Ordering객체 생성 및 save
//        Ordering ordering = Ordering.builder().member(member).build();
//        orderingRepository.save(ordering);
////        OrderingDetail 객체 생성 및 save
//        for (OrderCreateDto o : dtos) {
//            Product product = productRepository.findById(o.getProductId()).orElseThrow(()->new EntityNotFoundException("product is not found"));
//            if (product.getStockQuantity() < o.getProductCount()) {
//                throw new IllegalArgumentException("재고부족");
//            } else {
////                재고감소 로직.
//                product.updateStockQuantity(o.getProductCount());
//            }
//            OrderDetail orderDetail = OrderDetail.builder()
//                    .ordering(ordering)
//                    .product(product)
//                    .quantity(o.getProductCount())
//                    .build();
//            orderingDetailRepository.save(orderDetail);
//        }

//        방법2. cascading 사용하여 db저장
//        Ordering객체 생성하면서 OrderingDetail객체 같이 생성
        Ordering ordering = Ordering.builder()
                .member(member)
                .build();

        for (OrderCreateDto o : dtos) {
            Product product = productRepository.findById(o.getProductId()).orElseThrow(()->new EntityNotFoundException("product is not found"));
            if (product.getStockQuantity() < o.getProductCount()) {
                throw new IllegalArgumentException("재고부족");
            } else {
//                재고감소 로직.
                product.updateStockQuantity(o.getProductCount());
            }
            OrderDetail orderDetail = OrderDetail.builder()
                    .ordering(ordering)
                    .product(product)
                    .quantity(o.getProductCount())
                    .build();
            ordering.getOrderDetails().add(orderDetail);
        }
        orderingRepository.save(ordering);
        return ordering;
    }

    public List<OrderListResDto> findAll() {
        List<Ordering> orderingList = orderingRepository.findAll();
        List<OrderListResDto> orderListResDtos = new ArrayList<>();
        for (Ordering o : orderingList) {
            List<OrderDetailDto> orderDetailResDtos = new ArrayList<>();
            for (OrderDetail od : o.getOrderDetails()) {
                OrderDetailDto orderDetailDto =OrderDetailDto.builder()
                        .detailId(od.getId())
                        .productName(od.getProduct().getName())
                        .count(od.getQuantity())
                        .build();
                orderDetailResDtos.add(orderDetailDto);
            }
            OrderListResDto orderDto = OrderListResDto
                    .builder()
                    .id(o.getId())
                    .memberEmail(o.getMember().getEmail())
                    .orderStatus(o.getOrderStatus().toString())
                    .orderDetails(orderDetailResDtos)
                    .build();
            orderListResDtos.add(orderDto);
        }
        return orderListResDtos;
    }
}
