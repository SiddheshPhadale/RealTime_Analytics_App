package com.example.analytics.common.events.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductViewEvent {

    private String userId;
    private String url;
    private Product product;
    private Instant timeStamp;
    private String sessionId;

}
