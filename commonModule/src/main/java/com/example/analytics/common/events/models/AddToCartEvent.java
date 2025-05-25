package com.example.analytics.common.events.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class AddToCartEvent {

    private String userId;
    private Product product;
    private String sessionId;
    private Instant timeStamp;
    private int quantity;
    private Double totalAmount;
}
