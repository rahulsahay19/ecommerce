package com.rahulsahay.CloudGateway.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class FallbackController {
    @GetMapping("/orderServiceFallBack")
    public String orderServiceFallBack(){
        return "Order service is down!!! Order some pizzas and enjoy!!!";
    }

    @GetMapping("/paymentServiceFallBack")
    public String paymentServiceFallback() {
        return "Payment Service is down!";
    }

    @GetMapping("/productServiceFallBack")
    public String productServiceFallback() {
        return "Product Service is down!";
    }
}
