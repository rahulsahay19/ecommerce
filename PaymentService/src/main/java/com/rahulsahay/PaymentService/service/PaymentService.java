package com.rahulsahay.PaymentService.service;

import com.rahulsahay.PaymentService.model.PaymentRequest;

public interface PaymentService {
    long doPayment(PaymentRequest paymentRequest);
}
