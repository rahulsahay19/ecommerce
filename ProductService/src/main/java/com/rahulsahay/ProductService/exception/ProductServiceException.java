package com.rahulsahay.ProductService.exception;

import lombok.Data;

@Data

public class ProductServiceException extends RuntimeException {
    private String errorCode;
    public ProductServiceException(String message, String errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

}
