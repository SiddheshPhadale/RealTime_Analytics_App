package com.example.analytics.common.events.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PurchaseEvent {

    private String userId;
    private String sessionId;
    private Instant timeStamp;
    private String orderId;
    private List<items> items;
    private Double totalAmount;
    private String currency;
    private String paymentMethod;



    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class items{
        private Product product;
        private int quantity;
    }

}
