package com.rahulsahay.OrderService.service;

import com.rahulsahay.OrderService.model.OrderRequest;
import com.rahulsahay.OrderService.model.OrderResponse;

public interface OrderService {
    long placeOrder(OrderRequest orderRequest);

    OrderResponse getOrderDetails(long orderId);
}
