package com.rahulsahay.OrderService.service;

import com.rahulsahay.OrderService.model.OrderRequest;

public interface OrderService {
    long placeOrder(OrderRequest orderRequest);
}
