package com.analytics.analytics_api.services;

import com.analytics.analytics_api.dtos.TopProducts;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@Slf4j
public class AnalyticsService {

    @Autowired
    RedisTemplate<String, Object> template;

    public Long getTotalProductViews(){
        try {
            Long value = (Long) template.opsForValue().get("total_product_views");
            if(value != null ) return value;
            else return 0L;
        }catch (Exception e){
            throw new RuntimeException(e.getMessage());
        }
    }



    public List<TopProducts> getTopViewedProducts(int limit){
        Set<ZSetOperations.TypedTuple<Object>> topViewedProducts = template.opsForZSet().reverseRangeWithScores("top_viewed_products", 0, limit - 1);

        if(topViewedProducts == null || topViewedProducts.isEmpty()) return new ArrayList<>();

        else {
            List<TopProducts> result = new ArrayList<>();

            for (ZSetOperations.TypedTuple<Object> tuple : topViewedProducts){

                String prodId = (String) tuple.getValue();
                Long count = tuple.getScore() != null ? tuple.getScore().longValue() : 0L;

                Map<Object, Object> prodDetailsHash = template.opsForHash().entries("product_details : " + prodId);

                TopProducts productDto = new TopProducts();
                productDto.setProductId(prodId);
                productDto.setCount(count);

                if(prodDetailsHash != null || !prodDetailsHash.isEmpty()){
                    productDto.setProductName(Objects.toString(prodDetailsHash.get("name"), "Unknown"));
                    productDto.setCategory(Objects.toString(prodDetailsHash.get("category"), "Unknown"));
                }else {
                    productDto.setProductName("Unknown Product");
                    productDto.setCategory("Unknown Category");
                    log.warn("Product details not found in Redis for productId: {}", prodId);
                }
                result.add(productDto);
            }
            return result;
        }
    }


    public List<TopProducts> getTopAddToCarts(int limit){
        Set<ZSetOperations.TypedTuple<Object>> topAddToCarts = template.opsForZSet().reverseRangeWithScores("top_add_to_carts", 0, limit-1);

        if(topAddToCarts == null || topAddToCarts.isEmpty()) return new ArrayList<>();

        List<TopProducts> result = new ArrayList<>();

        for(ZSetOperations.TypedTuple<Object> tuple : topAddToCarts){

            String productId = (String) tuple.getValue();
            Long count = tuple.getScore().longValue();

            Map<Object, Object> prodDetailHash = template.opsForHash().entries("product_details : " + productId);

            TopProducts productDto = new TopProducts();
            productDto.setProductId(productId);
            productDto.setCount(count);

            if(prodDetailHash != null || !prodDetailHash.isEmpty()){
                productDto.setProductName(Objects.toString(prodDetailHash.get("name"), "Unknown"));
                productDto.setCategory(Objects.toString(prodDetailHash.get("category"), "Unknown"));
            }else {
                productDto.setProductName("Unknown Product");
                productDto.setCategory("Unknown Category");
                log.warn("Product details not found in Redis for productId: {}", productId);
            }
            result.add(productDto);
        }
        return result;
    }


    public List<TopProducts> getTopPurchasedProducts(int limit) {
        Set<ZSetOperations.TypedTuple<Object>> topProducts = template.opsForZSet()
                .reverseRangeWithScores("top_purchased_products", 0, limit - 1);

        if (topProducts == null || topProducts.isEmpty()) {
            return new ArrayList<>();
        }

        List<TopProducts> result = new ArrayList<>();
        for (ZSetOperations.TypedTuple<Object> tuple : topProducts) {
            String productId = Objects.toString(tuple.getValue(), "unknown_id");
            Long count = tuple.getScore() != null ? tuple.getScore().longValue() : 0L;

            Map<Object, Object> productDetailsHash = template.opsForHash().entries("product_details : " + productId);

            TopProducts productDTO = new TopProducts();
            productDTO.setProductId(productId);
            productDTO.setCount(count);

            if (productDetailsHash != null && !productDetailsHash.isEmpty()) {
                productDTO.setProductName(Objects.toString(productDetailsHash.get("name"), "Unknown Product"));
                productDTO.setCategory(Objects.toString(productDetailsHash.get("category"), "Unknown Category"));
            } else {
                productDTO.setProductName("Unknown Product");
                productDTO.setCategory("Unknown Category");
                log.warn("Product details not found in Redis for productId: {}", productId);
            }
            result.add(productDTO);
        }
        return result;
    }


    public Long getTotalAddToCarts(){
        try {
            Long value = (Long) template.opsForValue().get("total_add_to_cart");
            if(value != null ) return value;
            else return 0L;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    public Double averageAddToCartValue(){
        List<Object> values = template.opsForList().range("add_to_cart_values", 0, -1);

        if (values == null) {
            return 0.0;
        }
        double sum = values.stream().filter(Objects::nonNull)
                    .mapToDouble(
                            v -> {
                                try {
                                    return Double.parseDouble(v.toString());
                                }catch (NumberFormatException e){
                                    log.warn("Skipping non-numeric add_to_cart_value: {}", v);
                                    return 0.0;
                                }
                            }).sum();

            return sum / values.size();

    }

    public Long getTotalPurchases(){
        Long value = (Long) template.opsForValue().get("total_purchases");
        if(value != null ) return value;
        else return 0L;
    }

    public Double getTotalRevenue(){
        try {
            Double value = (Double) template.opsForValue().get("total_revenue");
            if(value != null) return value;
            else return 0.0D;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public Double getAverageOrderValue() {
        List<Object> values = template.opsForList().range("purchase_order_values", 0, -1);
        if (values == null || values.isEmpty()) {
            return 0.0;
        }
        double sum = values.stream()
                .filter(Objects::nonNull)
                .mapToDouble(v -> {
                    try {
                        return Double.parseDouble(v.toString());
                    } catch (NumberFormatException e) {
                        log.warn("Skipping non-numeric purchase_order_value: {}", v);
                        return 0.0;
                    }
                })
                .sum();
        return sum / values.size();
    }

    public Double getProductViewToCartConversionRate() {
        Long totalViews = getTotalProductViews();
        Long totalAddsToCart = getTotalAddToCarts();

        if (totalViews == 0L) {
            return 0.0;
        }
        return (double) totalAddsToCart / totalViews * 100.0;
    }

    public Double getCartToPurchaseConversionRate() {
        Long totalAddsToCart = getTotalAddToCarts();
        Long totalPurchases = getTotalPurchases();

        if (totalAddsToCart == 0L) {
            return 0.0;
        }
        return (double) totalPurchases / totalAddsToCart * 100.0;
    }

}
