package com.analytics.analytics_api.controllers;

import com.analytics.analytics_api.dtos.TopProducts;
import com.analytics.analytics_api.services.AnalyticsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/analytics")
@Slf4j
public class AnalyticsController {

    @Autowired
    private AnalyticsService service;

    @GetMapping("/total-product-views")
    public ResponseEntity<Long> getTotalProductViews() {
        Long total = service.getTotalProductViews();
        return ResponseEntity.ok(total);
    }

    @GetMapping("/top-viewed-products")
    public ResponseEntity<List<TopProducts>> getTopViewedProducts(
            @RequestParam(defaultValue = "10") int limit) {
        List<TopProducts> topProducts = service.getTopViewedProducts(limit);
        return ResponseEntity.ok(topProducts);
    }

    @GetMapping("/total-adds-to-cart")
    public ResponseEntity<Long> getTotalAddsToCart() {
        Long total = service.getTotalAddToCarts();
        return ResponseEntity.ok(total);
    }

    @GetMapping("/average-add-to-cart-value")
    public ResponseEntity<Double> getAverageAddToCartValue() {
        Double average = service.averageAddToCartValue();
        return ResponseEntity.ok(average);
    }

    @GetMapping("/total-purchases")
    public ResponseEntity<Long> getTotalPurchases() {
        Long total = service.getTotalPurchases();
        return ResponseEntity.ok(total);
    }

    @GetMapping("/total-revenue")
    public ResponseEntity<Double> getTotalRevenue() {
        Double total = service.getTotalRevenue();
        return ResponseEntity.ok(total);
    }

    @GetMapping("/average-order-value")
    public ResponseEntity<Double> getAverageOrderValue() {
        Double average = service.getAverageOrderValue();
        return ResponseEntity.ok(average);
    }

    @GetMapping("/conversion/product-view-to-cart")
    public ResponseEntity<Double> getProductViewToCartConversionRate() {
        Double rate = service.getProductViewToCartConversionRate();
        return ResponseEntity.ok(rate);
    }

    @GetMapping("/conversion/cart-to-purchase")
    public ResponseEntity<Double> getCartToPurchaseConversionRate() {
        Double rate = service.getCartToPurchaseConversionRate();
        return ResponseEntity.ok(rate);
    }

    @GetMapping("/top-added-to-cart-products")
    public ResponseEntity<List<TopProducts>> getTopAddToCartProducts(@RequestParam(defaultValue = "10") int limit) {
        List<TopProducts> topProducts = service.getTopAddToCarts(limit);
        return ResponseEntity.ok(topProducts);
    }

    @GetMapping("/top-purchased-products")
    public ResponseEntity<List<TopProducts>> getTopPurchasedProducts(@RequestParam(defaultValue = "10") int limit) {
        List<TopProducts> topProducts = service.getTopPurchasedProducts(limit);
        return ResponseEntity.ok(topProducts);
    }
}
