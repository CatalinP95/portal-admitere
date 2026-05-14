package com.campus.dormitory.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("!test")
public class RabbitConfig {

    public static final String EXCHANGE = "dormitory.events";

    public static final String RK_REQUEST_SUBMITTED = "blockrequest.submitted";
    public static final String RK_REQUEST_APPROVED = "blockrequest.approved";
    public static final String RK_REQUEST_REJECTED = "blockrequest.rejected";
    public static final String RK_RECEIPT_PAID = "receipt.paid";

    public static final String Q_REQUEST_SUBMITTED = "dormitory.blockrequest.submitted.q";
    public static final String Q_REQUEST_APPROVED = "dormitory.blockrequest.approved.q";
    public static final String Q_REQUEST_REJECTED = "dormitory.blockrequest.rejected.q";
    public static final String Q_RECEIPT_PAID = "dormitory.receipt.paid.q";

    @Bean
    public TopicExchange dormitoryExchange() {
        return new TopicExchange(EXCHANGE, true, false);
    }

    @Bean
    public Queue blockRequestSubmittedQueue() { return QueueBuilder.durable(Q_REQUEST_SUBMITTED).build(); }

    @Bean
    public Queue blockRequestApprovedQueue() { return QueueBuilder.durable(Q_REQUEST_APPROVED).build(); }

    @Bean
    public Queue blockRequestRejectedQueue() { return QueueBuilder.durable(Q_REQUEST_REJECTED).build(); }

    @Bean
    public Queue receiptPaidQueue() { return QueueBuilder.durable(Q_RECEIPT_PAID).build(); }

    @Bean
    public Binding bindSubmitted() {
        return BindingBuilder.bind(blockRequestSubmittedQueue()).to(dormitoryExchange()).with(RK_REQUEST_SUBMITTED);
    }

    @Bean
    public Binding bindApproved() {
        return BindingBuilder.bind(blockRequestApprovedQueue()).to(dormitoryExchange()).with(RK_REQUEST_APPROVED);
    }

    @Bean
    public Binding bindRejected() {
        return BindingBuilder.bind(blockRequestRejectedQueue()).to(dormitoryExchange()).with(RK_REQUEST_REJECTED);
    }

    @Bean
    public Binding bindReceiptPaid() {
        return BindingBuilder.bind(receiptPaidQueue()).to(dormitoryExchange()).with(RK_RECEIPT_PAID);
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule());
        return new Jackson2JsonMessageConverter(mapper);
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory, MessageConverter converter) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(converter);
        template.setExchange(EXCHANGE);
        return template;
    }
}
