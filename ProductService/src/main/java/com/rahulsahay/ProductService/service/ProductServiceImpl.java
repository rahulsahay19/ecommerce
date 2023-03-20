package com.rahulsahay.ProductService.service;

import com.rahulsahay.ProductService.entity.Product;
import com.rahulsahay.ProductService.exception.ProductServiceException;
import com.rahulsahay.ProductService.model.ProductRequest;
import com.rahulsahay.ProductService.model.ProductResponse;
import com.rahulsahay.ProductService.repository.ProductRepository;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.beans.BeanUtils.*;

@Service
@Log4j2
public class ProductServiceImpl implements ProductService {

    @Autowired
    private ProductRepository productRepository;
    @Override
    public long addProduct(ProductRequest productRequest) {
        log.info("Adding Product...");
        //builder pattern is used to fill the props
        Product product = Product.builder()
                .productName(productRequest.getName())
                .quantity(productRequest.getQuantity())
                .price(productRequest.getPrice())
                .build();
        productRepository.save(product);
        log.info("Product created!!!");
        return product.getProductId();
    }

    @Override
    public ProductResponse getProductById(long productId) {
        log.info("Getting the product for Product Id: {}", productId);
        Product product = productRepository.findById(productId)
                .orElseThrow(()->new ProductServiceException("Product not found with the Id:" + productId, "PRODUCT_NOT_FOUND"));
        //now convert the product to productresponse either via beanUtils or builder pattern
        ProductResponse productResponse = new ProductResponse();
        //Now, this will only work when there is matching properties
        copyProperties(product, productResponse);
        return productResponse;
    }

    @Override
    public void reduceQuantity(long productId, long quantity) {
        log.info("Reduce Quantity {} for Id: {}", quantity, productId);
        Product product = productRepository.findById(productId)
                .orElseThrow(()-> new ProductServiceException("Product not found with the Id:" + productId, "PRODUCT_NOT_FOUND"));
        if(product.getQuantity() < quantity){
            throw new ProductServiceException("Product doesn't have sufficient quantity", "INSUFFICIENT_QUANTITY");
        }
        product.setQuantity(product.getQuantity() - quantity);
        productRepository.save(product);
        log.info("Product quantity updated successfully!!!");
    }

    @Override
    public List<ProductResponse> getAllProducts() {
        log.info("Getting all the Products!!!");
        List<Product> productList = productRepository.findAll();
        List<ProductResponse> products = productList
                .stream()
                .map(productEntity->{
                    ProductResponse product = new ProductResponse();
                    //Copying props from entity to response
                    BeanUtils.copyProperties(productEntity, product);
                    return product;
                }).collect(Collectors.toList());
        return products;
    }
}
