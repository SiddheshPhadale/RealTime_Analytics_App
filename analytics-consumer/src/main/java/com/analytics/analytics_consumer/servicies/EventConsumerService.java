package com.analytics.analytics_consumer.servicies;

import com.example.analytics.common.events.models.AddToCartEvent;
import com.example.analytics.common.events.models.Product;
import com.example.analytics.common.events.models.ProductViewEvent;
import com.example.analytics.common.events.models.PurchaseEvent;
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

    @KafkaListener(topics = viewProduct, groupId = groupId)
    public void consumeProductView(ProductViewEvent event){
        log.info("Received ProductViewEvent: {}", event);
        template.opsForValue().increment("total_product_views");
        template.opsForSet().add("Product : " + event.getProduct().getProdId() + " : Unique viewers", event.getUserId());
        template.opsForZSet().incrementScore("top_viewed_products", event.getProduct().getProdId(), 1);

        String prodHashKey = "product_details : " + event.getProduct().getProdId();

        if (!template.hasKey(prodHashKey)){
            template.opsForHash().put("product_details : " + event.getProduct().getProdId(), "name", event.getProduct().getProdName());
            template.opsForHash().put("product_details : " + event.getProduct().getProdId(), "category", event.getProduct().getCategory());
        }
    }

    @KafkaListener(topics = addToCart, groupId = groupId)
    public void consumeAddToCart(AddToCartEvent event){
        log.info("Received AddToCartEvent: {}", event);
        template.opsForValue().increment("total_add_to_cart");
        template.opsForZSet().incrementScore("top_add_to_carts", event.getProduct().getProdId(), 1);
        template.opsForList().rightPush("add_to_cart_values", event.getProduct().getPrice());
    }

    @KafkaListener(topics = purchase, groupId = groupId)
    public void consumePurchase(PurchaseEvent event){
        log.info("Recieved PurchaseEvent : {}", event);
        template.opsForValue().increment("total_purchases");
        template.opsForValue().increment("total_revenue", event.getTotalAmount());
        template.opsForSet().add("unique_buyers", event.getUserId());
        event.getItems().forEach(items ->{
            template.opsForZSet().incrementScore("top_purchased_products", items.getProduct().getProdId(), items.getQuantity());

            String prodHashKey = "product_details : " + items.getProduct().getProdId();
            if (!template.hasKey(prodHashKey)){
                template.opsForHash().put("product_details : " + items.getProduct().getProdId(), "name", items.getProduct().getProdName());
                template.opsForHash().put("product_details : " + items.getProduct().getProdId(), "category", items.getProduct().getCategory());
            }
        });

        template.opsForList().rightPush("purchase_order_values", event.getTotalAmount());
    }
}
