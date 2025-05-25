package com.analytics.analytics_producer.controllers;

import com.analytics.analytics_producer.services.EventService;
import com.example.analytics.common.events.models.AddToCartEvent;
import com.example.analytics.common.events.models.ProductViewEvent;
import com.example.analytics.common.events.models.PurchaseEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/events")
public class EventController {

    @Autowired
    private EventService service;

    @PostMapping("/view")
    public ResponseEntity<?> viewEvent (@RequestBody ProductViewEvent viewEvent){
        return service.view(viewEvent);
    }

    @PostMapping("/add")
    public ResponseEntity<?> addEvent (@RequestBody AddToCartEvent addEvent){
        return service.add(addEvent);
    }

    @PostMapping("/purchase")
    public ResponseEntity<?> purchaseEvent(@RequestBody PurchaseEvent purchaseEvent){
        return service.purchase(purchaseEvent);
    }
}
