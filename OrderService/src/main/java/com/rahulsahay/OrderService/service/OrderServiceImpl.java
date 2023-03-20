package com.rahulsahay.OrderService.service;

import com.rahulsahay.OrderService.entity.Order;
import com.rahulsahay.OrderService.external.client.PaymentService;
import com.rahulsahay.OrderService.external.client.ProductService;
import com.rahulsahay.OrderService.external.request.PaymentRequest;
import com.rahulsahay.OrderService.model.OrderRequest;
import com.rahulsahay.OrderService.repository.OrderRepository;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@Log4j2
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private ProductService productService;

    @Autowired
    private PaymentService paymentService;
    @Override
    public long placeOrder(OrderRequest orderRequest) {
        //Create Order Entity
        //Save the data
        //Then call product service to block our product and reduce the quantity
        //Payment service -> complete payment -> success -> complete else cancel
        log.info("Placing Order Request: {}", orderRequest);
        productService.reduceQuantity(orderRequest.getProductId(), orderRequest.getQuantity());
        log.info("Creating Order with status CREATED");
        Order order = Order.builder()
                .amount(orderRequest.getTotalAmount())
                .orderStatus("CREATED")
                .productId(orderRequest.getProductId())
                .orderDate(Instant.now())
                .quantity(orderRequest.getQuantity())
                .build();
        order = orderRepository.save(order);
        log.info("Calling Payment service to complete the order!!!");
        PaymentRequest paymentRequest = PaymentRequest.builder()
                .orderId(order.getId())
                .paymentMode(orderRequest.getPaymentMode())
                .amount(orderRequest.getTotalAmount())
                .build();
        String orderStatus = null;
        try{
            paymentService.doPayment(paymentRequest);
            log.info("Payment done successfully. Changing the orderStatus = PLACED");
            orderStatus = "PLACED";
        }catch (Exception e){
            log.info("Payment FAILED. Changing the orderStatus = FAILED");
            orderStatus = "PAYMENT_FAILED";
        }
        order.setOrderStatus(orderStatus);
        //Now this time with OrderStatus
        orderRepository.save(order);
        log.info("Order placed successfully with Order Id : {}", order.getId());
        return order.getId();
    }
}
