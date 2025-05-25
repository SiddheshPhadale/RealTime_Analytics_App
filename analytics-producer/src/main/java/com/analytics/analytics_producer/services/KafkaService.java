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
    private KafkaTemplate<String, Object> template;

    public boolean sendAddToCartEvent(AddToCartEvent event){
        try {
            template.send(addToCart, event.getProduct().getProdId(), event);
            return true;
        }catch (Exception e){
            throw new RuntimeException(e.getMessage());
        }
    }

    public boolean sendProductViewEvent(ProductViewEvent event){
        try {
            template.send(viewProduct, event);
            return true;
        }catch (Exception e){
            throw new RuntimeException(e.getMessage());
        }
    }

    public boolean sendPurchaseEvent(PurchaseEvent event){
        try {
            template.send(purchase, event);
            return true;
        }catch (Exception e){
            throw new RuntimeException(e.getMessage());
        }
    }


}
