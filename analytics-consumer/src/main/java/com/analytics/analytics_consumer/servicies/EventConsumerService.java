package com.analytics.analytics_consumer.servicies;

import com.example.analytics.common.events.models.AddToCartEvent;
import com.example.analytics.common.events.models.Product;
import com.example.analytics.common.events.models.ProductViewEvent;
import com.example.analytics.common.events.models.PurchaseEvent;
import com.fasterxml.jackson.databind.ObjectMapper; // Add this import
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import static com.analytics.analytics_consumer.constants.Constants.*;

@Service
@Slf4j
public class EventConsumerService {
    @Autowired
    private RedisTemplate<String, Object> template;

    @Autowired
    private ObjectMapper objectMapper;

    @KafkaListener(topics = viewProduct, groupId = groupId)
    public void consumeProductView(String message){
        try {
            ProductViewEvent event = objectMapper.readValue(message, ProductViewEvent.class);
            log.info("Received ProductViewEvent: {}", event);
            template.opsForValue().increment("total_product_views");
            template.opsForSet().add("Product : " + event.getProduct().getProdId() + " : Unique viewers", event.getUserId());
            template.opsForZSet().incrementScore("top_viewed_products", event.getProduct().getProdId(), 1);

            // --- STRICT FUNNEL TRACKING ---
            String funnelKey = event.getUserId() + ":" + event.getProduct().getProdId();
            Long isNewView = template.opsForSet().add("funnel:views", funnelKey);
            if (isNewView != null && isNewView > 0) {
                template.opsForValue().increment("funnel_unique_views");
            }

            String prodHashKey = "product_details : " + event.getProduct().getProdId();
            if (!template.hasKey(prodHashKey)){
                template.opsForHash().put(prodHashKey, "name", event.getProduct().getProdName());
                template.opsForHash().put(prodHashKey, "category", event.getProduct().getCategory());
            }
        } catch (Exception e) {
            log.error("Error deserializing ProductViewEvent: {}", message, e);
        }
    }

    @KafkaListener(topics = addToCart, groupId = groupId)
    public void consumeAddToCart(String message){
        try {
            AddToCartEvent event = objectMapper.readValue(message, AddToCartEvent.class);
            log.info("Received AddToCartEvent: {}", event);
            template.opsForValue().increment("total_add_to_cart");
            template.opsForZSet().incrementScore("top_add_to_carts", event.getProduct().getProdId(), 1);
            template.opsForList().rightPush("add_to_cart_values", event.getProduct().getPrice());

            // --- STRICT FUNNEL TRACKING ---
            String funnelKey = event.getUserId() + ":" + event.getProduct().getProdId();
            // Only count if this specific user viewed this specific product first
            Boolean hasViewed = template.opsForSet().isMember("funnel:views", funnelKey);
            if (Boolean.TRUE.equals(hasViewed)) {
                Long isNewCart = template.opsForSet().add("funnel:carts", funnelKey);
                if (isNewCart != null && isNewCart > 0) {
                    template.opsForValue().increment("funnel_unique_carts");
                }
            }
        } catch (Exception e) {
            log.error("Error deserializing AddToCartEvent: {}", message, e);
        }
    }

    @KafkaListener(topics = purchase, groupId = groupId)
    public void consumePurchase(String message){
        try {
            PurchaseEvent event = objectMapper.readValue(message, PurchaseEvent.class);
            log.info("Received PurchaseEvent : {}", event);
            template.opsForValue().increment("total_purchases");
            template.opsForValue().increment("total_revenue", event.getTotalAmount());
            template.opsForSet().add("unique_buyers", event.getUserId());

            event.getItems().forEach(items -> {
                if (items.getProduct() == null) {
                    log.warn("Skipping purchase item with null product for orderId={} quantity={}", event.getOrderId(), items.getQuantity());
                    return;
                }

                template.opsForZSet().incrementScore("top_purchased_products", items.getProduct().getProdId(), items.getQuantity());

                // --- STRICT FUNNEL TRACKING ---
                String funnelKey = event.getUserId() + ":" + items.getProduct().getProdId();
                // Only count if this specific user added this specific product to cart first
                Boolean hasCarted = template.opsForSet().isMember("funnel:carts", funnelKey);
                if (Boolean.TRUE.equals(hasCarted)) {
                    Long isNewPurchase = template.opsForSet().add("funnel:purchases", funnelKey);
                    if (isNewPurchase != null && isNewPurchase > 0) {
                        template.opsForValue().increment("funnel_unique_purchases");
                    }
                }

                String prodHashKey = "product_details : " + items.getProduct().getProdId();
                if (!template.hasKey(prodHashKey)){
                    template.opsForHash().put(prodHashKey, "name", items.getProduct().getProdName());
                    template.opsForHash().put(prodHashKey, "category", items.getProduct().getCategory());
                }
            });
            template.opsForList().rightPush("purchase_values", event.getTotalAmount());
        } catch (Exception e) {
            log.error("Error deserializing PurchaseEvent: {} | raw message: {}", e.getMessage(), message, e);
        }
    }
}