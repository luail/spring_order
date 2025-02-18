package com.example.ordersystem.ordering.service;

import com.example.ordersystem.common.dtos.StockRabbitDto;
import com.example.ordersystem.common.service.StockInventoryService;
import com.example.ordersystem.common.service.StockRabbitmqService;
import com.example.ordersystem.member.domain.Member;
import com.example.ordersystem.member.repository.MemberRepository;
import com.example.ordersystem.ordering.controller.SseController;
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
import org.springframework.security.core.Authentication;
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
    private final StockInventoryService stockInventoryService;
    private final StockRabbitmqService stockRabbitmqService;
    private final SseController sseController;

    public OrderingService(OrderingRepository orderingRepository, MemberRepository memberRepository, OrderingDetailRepository orderingDetailRepository, ProductRepository productRepository, StockInventoryService stockInventoryService, StockRabbitmqService stockRabbitmqService, SseController sseController) {
        this.orderingRepository = orderingRepository;
        this.memberRepository = memberRepository;
        this.orderingDetailRepository = orderingDetailRepository;
        this.productRepository = productRepository;
        this.stockInventoryService = stockInventoryService;
        this.stockRabbitmqService = stockRabbitmqService;
        this.sseController = sseController;
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
            int quantity = o.getProductCount();
//            동시성 이슈 고려 안한 코드.
            if (product.getStockQuantity() < quantity) {
                throw new IllegalArgumentException("재고부족");
            } else {
//                재고감소 로직.
                product.updateStockQuantity(o.getProductCount());
            }

////            동시성이슈를 고려한 코드
////            redis를 통한 재고관리 및 재고잔량 확인
//            int newQuantity = stockInventoryService.decreaseStock(product.getId(), quantity);
//            if (newQuantity < 0) {
//                throw new IllegalArgumentException("재고부족");
//            }
////            rdb동기화(rabbitmq)
//            StockRabbitDto stockRabbitDto = StockRabbitDto.builder().productId(product.getId()).productCount(quantity).build();;
//            stockRabbitmqService.puslish(stockRabbitDto);

            OrderDetail orderDetail = OrderDetail.builder()
                    .ordering(ordering)
                    .product(product)
                    .quantity(o.getProductCount())
                    .build();
            ordering.getOrderDetails().add(orderDetail);
        }
        Ordering ordering1 = orderingRepository.save(ordering);

//          sse를 통한 admin계정에 메시지 발송

        sseController.publishMessage(ordering1.fromEntity(), "admin@naver.com");

        return ordering;
    }

    public List<OrderListResDto> findAll() {
        List<Ordering> orderingList = orderingRepository.findAll();
        List<OrderListResDto> orderListResDtos = new ArrayList<>();
        for (Ordering o : orderingList) {
            orderListResDtos.add(o.fromEntity());
        }
        return orderListResDtos;
    }

    public List<OrderListResDto> myOrders() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Member member = memberRepository.findByEmail(email).orElseThrow(()->new EntityNotFoundException("member is not found"));

        List<OrderListResDto> orderListResDtoList = new ArrayList<>();
        for (Ordering o : member.getOrderingList()) {
            orderListResDtoList.add(o.fromEntity());
        }

        return orderListResDtoList;
    }

    public Ordering orderCancel(Long id) {
        Ordering ordering = orderingRepository.findById(id).orElseThrow(()->new EntityNotFoundException("order is not found"));
        ordering.orderCancel();
        for (OrderDetail od : ordering.getOrderDetails()) {
            od.getProduct().orderCancel(od.getQuantity());
        }
        return ordering;
    }
}
