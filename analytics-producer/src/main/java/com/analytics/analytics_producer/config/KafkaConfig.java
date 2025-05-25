package com.analytics.analytics_producer.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.KafkaAdmin;

import static com.analytics.analytics_producer.constans.topics.*;

@Configuration
public class KafkaConfig {

    @Bean
    public KafkaAdmin.NewTopics createTopics(){

        NewTopic productViewEvent = TopicBuilder
                .name(viewProduct)
                .build();

        NewTopic addToCartEvent = TopicBuilder
                .name(addToCart).build();

        NewTopic purchaseEvent = TopicBuilder
                .name(purchase).build();

        return new KafkaAdmin.NewTopics(productViewEvent, addToCartEvent, purchaseEvent);
    }
}
