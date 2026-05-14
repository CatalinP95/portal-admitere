package com.campus.dormitory.messaging;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class EventPublisher {

    private final ObjectProvider<RabbitTemplate> rabbitTemplate;

    public EventPublisher(ObjectProvider<RabbitTemplate> rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void publish(String routingKey, DormitoryEvent event) {
        RabbitTemplate template = rabbitTemplate.getIfAvailable();
        if (template == null) {
            log.debug("RabbitTemplate unavailable (test profile?), skipping publish for {}", routingKey);
            return;
        }
        try {
            template.convertAndSend(routingKey, event);
            log.info("Published event {} -> {}", event.getType(), routingKey);
        } catch (AmqpException e) {
            log.warn("Failed to publish event {} to {}: {}", event.getType(), routingKey, e.getMessage());
        }
    }
}
