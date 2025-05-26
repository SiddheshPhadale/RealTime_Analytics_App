package com.analytics.analytics_producer.services;

import com.example.analytics.common.events.models.AddToCartEvent;
import com.example.analytics.common.events.models.ProductViewEvent;
import com.example.analytics.common.events.models.PurchaseEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.time.Instant;

import static com.analytics.analytics_producer.constans.topics.*;

@Service
@Slf4j
public class KafkaService {

    @Autowired
    private KafkaTemplate<String, String> template;
    @Autowired
    private ObjectMapper objectMapper;

    public boolean sendAddToCartEvent(AddToCartEvent event){
        try {
            String jsonEvent = objectMapper.writeValueAsString(event);
            template.send(addToCart, event.getProduct().getProdId(), jsonEvent);
            return true;
        }catch (Exception e){
            log.error("Error sending AddToCartEvent: {}", e.getMessage(), e);
            return false;
        }
    }

    public boolean sendProductViewEvent(ProductViewEvent event){
        try {
            String jsonEvent = objectMapper.writeValueAsString(event);
            template.send(viewProduct, jsonEvent);
            return true;
        }catch (Exception e){
            log.error("Error sending ProductViewEvent: {}", e.getMessage(), e);
            return false;
        }
    }

    public boolean sendPurchaseEvent(PurchaseEvent event){
        try {
            String jsonEvent = objectMapper.writeValueAsString(event);
            template.send(purchase, jsonEvent);
            return true;
        }catch (Exception e){
            log.error("Error sending PurchaseEvent: {}", e.getMessage(), e);
            return false;
        }
    }


}
