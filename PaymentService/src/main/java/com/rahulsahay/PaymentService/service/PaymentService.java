package com.rahulsahay.PaymentService.service;

import com.rahulsahay.PaymentService.model.PaymentRequest;
import com.rahulsahay.PaymentService.model.PaymentResponse;

public interface PaymentService {
    long doPayment(PaymentRequest paymentRequest);

    PaymentResponse getPaymentDetailsByOrderId(String orderId);
}
