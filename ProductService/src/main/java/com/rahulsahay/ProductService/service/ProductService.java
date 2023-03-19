package com.rahulsahay.ProductService.service;

import com.rahulsahay.ProductService.model.ProductRequest;
import com.rahulsahay.ProductService.model.ProductResponse;


public interface ProductService {
    long addProduct(ProductRequest productRequest);

    ProductResponse getProductById(long productId);

    void reduceQuantity(long productId, long quantity);
}
