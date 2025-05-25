package com.example.analytics.common.events.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Product {

    private String prodId;
    private String prodName;
    private Double price;
    private String category;
}
