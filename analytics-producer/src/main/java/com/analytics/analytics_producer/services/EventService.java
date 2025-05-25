package com.analytics.analytics_producer.services;

import com.example.analytics.common.events.models.AddToCartEvent;
import com.example.analytics.common.events.models.ProductViewEvent;
import com.example.analytics.common.events.models.PurchaseEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class EventService {

    @Autowired
    private KafkaService service;

    public ResponseEntity<?> view(ProductViewEvent event){
        event.setTimeStamp(Instant.now());
        boolean sent = service.sendProductViewEvent(event);
        if(sent){
            return new ResponseEntity<>(HttpStatus.OK);
        }
        else return new ResponseEntity<>(HttpStatus.BAD_GATEWAY);
    }

    public ResponseEntity<?> add(AddToCartEvent event){
        event.setTimeStamp(Instant.now());
        boolean sent = service.sendAddToCartEvent(event);
        if(sent){
            return new ResponseEntity<>(HttpStatus.OK);
        }
        else return new ResponseEntity<>(HttpStatus.BAD_GATEWAY);
    }

    public ResponseEntity<?> purchase(PurchaseEvent event){
        event.setTimeStamp(Instant.now());
        boolean sent = service.sendPurchaseEvent(event);
        if(sent){
            return new ResponseEntity<>(HttpStatus.OK);
        }
        else return new ResponseEntity<>(HttpStatus.BAD_GATEWAY);
    }
}
