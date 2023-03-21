package com.rahulsahay.OrderService.service;

import com.rahulsahay.OrderService.entity.Order;
import com.rahulsahay.OrderService.exception.CustomException;
import com.rahulsahay.OrderService.external.client.PaymentService;
import com.rahulsahay.OrderService.external.client.ProductService;
import com.rahulsahay.OrderService.external.request.PaymentRequest;
import com.rahulsahay.OrderService.external.response.PaymentResponse;
import com.rahulsahay.OrderService.model.OrderRequest;
import com.rahulsahay.OrderService.model.OrderResponse;
import com.rahulsahay.OrderService.model.PaymentMode;
import com.rahulsahay.OrderService.repository.OrderRepository;
import com.rahulsahay.ProductService.model.ProductResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.time.Instant;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@SpringBootTest
public class OrderServiceImplTest {
    @Mock
    private OrderRepository orderRepository;
    @Mock
    private ProductService productService;
    @Mock
    private PaymentService paymentService;
    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    OrderService orderService = new OrderServiceImpl();

    @DisplayName("Get Order - Success Scenario")
    @Test
    void test_When_Order_Success(){
        //Mocking
        Order order = getMockOrder();
        when(orderRepository.findById(anyLong()))
                .thenReturn(Optional.of(order));
        when(restTemplate.getForObject("http://PRODUCT-SERVICE/product/" + order.getProductId(),
                ProductResponse.class)).thenReturn(getMockProductResponse());
        when(restTemplate.getForObject("http://PAYMENT-SERVICE/payment/order/" + order.getId(),
                PaymentResponse.class)).thenReturn(getMockPaymentResponse());
        //Actual
       OrderResponse orderResponse = orderService.getOrderDetails(1);
        //Verification
        verify(orderRepository, times(1)).findById(anyLong());
        verify(restTemplate, times(1)).getForObject("http://PRODUCT-SERVICE/product/" + order.getProductId(),
                ProductResponse.class);
        verify(restTemplate, times(1)).getForObject("http://PAYMENT-SERVICE/payment/order/" + order.getId(),
                PaymentResponse.class);
        //Assert
        assertNotNull(orderResponse);
        assertEquals(order.getId(), orderResponse.getOrderId());
    }

    @DisplayName("Get Order - Failure Scenario")
    @Test
    void test_When_Get_Order_Not_Found(){
        when(orderRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(null));
        CustomException exception = assertThrows(CustomException.class,()->orderService.getOrderDetails(1));
        assertEquals("NOT_FOUND", exception.getErrorCode());
        assertEquals(404, exception.getStatus());
        verify(orderRepository, times(1)).findById(anyLong());
    }

    @DisplayName("Place Order - Success Scenario")
    @Test
    void test_Place_Order_Success(){
        Order order = getMockOrder();
        OrderRequest orderRequest = getMockOrderRequest();

        when(orderRepository.save(any(Order.class))).thenReturn(order);
        when(productService.reduceQuantity(anyLong(), anyLong())).thenReturn(new ResponseEntity<Void>(HttpStatus.OK));
        when(paymentService.doPayment(any(PaymentRequest.class))).thenReturn(new ResponseEntity<Long>(HttpStatus.OK));
        long orderId = orderService.placeOrder(orderRequest);
        verify(orderRepository, times(2)).save(any());
        verify(productService, times(1)).reduceQuantity(anyLong(), anyLong());
        verify(paymentService, times(1)).doPayment(any(PaymentRequest.class));
        assertEquals(order.getId(), orderId);
    }

    @DisplayName("Place Order - Payment Fail Scenario")
    @Test
    void test_When_Placed_Order_Payment_Fails(){
        Order order = getMockOrder();
        OrderRequest orderRequest = getMockOrderRequest();

        when(orderRepository.save(any(Order.class))).thenReturn(order);
        when(productService.reduceQuantity(anyLong(), anyLong())).thenReturn(new ResponseEntity<Void>(HttpStatus.OK));
        when(paymentService.doPayment(any(PaymentRequest.class))).thenThrow(new RuntimeException());
        long orderId = orderService.placeOrder(orderRequest);
        verify(orderRepository, times(2)).save(any());
        verify(productService, times(1)).reduceQuantity(anyLong(), anyLong());
        verify(paymentService, times(1)).doPayment(any(PaymentRequest.class));
        assertEquals(order.getId(), orderId);
    }

    private OrderRequest getMockOrderRequest() {
        return OrderRequest
                .builder()
                .productId(1)
                .quantity(10)
                .paymentMode(PaymentMode.CASH)
                .totalAmount(100)
                .build();
    }

    private PaymentResponse getMockPaymentResponse() {
        return PaymentResponse
                .builder()
                .orderId(1)
                .paymentId(1)
                .paymentDate(Instant.now())
                .paymentMode(PaymentMode.CASH)
                .amount(75000)
                .status("SUCCESS")
                .build();
    }

    private ProductResponse getMockProductResponse() {
        return ProductResponse
                .builder()
                .productId(2)
                .productName("IPhone")
                .price(75000)
                .quantity(7)
                .build();
    }

    private Order getMockOrder() {
        return Order.builder()
                .orderStatus("PLACED")
                .orderDate(Instant.now())
                .id(1)
                .amount(100)
                .quantity(200)
                .productId(2)
                .build();

    }
}