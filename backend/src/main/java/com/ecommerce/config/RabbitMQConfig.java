package com.ecommerce.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * RabbitMQ 消息队列配置
 * ⭐⭐ 面试考点：消息队列应用场景（订单异步处理、削峰填谷、解耦）
 */
@Configuration
public class RabbitMQConfig {

    /** 订单交换机 */
    public static final String ORDER_EXCHANGE = "ecommerce.order.exchange";
    /** 订单队列 */
    public static final String ORDER_QUEUE = "ecommerce.order.queue";
    /** 订单路由键 */
    public static final String ORDER_ROUTING_KEY = "order.create";

    /**
     * 订单交换机（Direct 类型）
     * Direct Exchange：根据路由键精确匹配队列
     */
    @Bean
    public DirectExchange orderExchange() {
        return new DirectExchange(ORDER_EXCHANGE, true, false);
    }

    /**
     * 订单队列
     */
    @Bean
    public Queue orderQueue() {
        return QueueBuilder.durable(ORDER_QUEUE).build();
    }

    /**
     * 绑定队列到交换机
     */
    @Bean
    public Binding orderBinding() {
        return BindingBuilder.bind(orderQueue())
                .to(orderExchange())
                .with(ORDER_ROUTING_KEY);
    }

    /**
     * 消息转换器 — 使用 JSON 格式序列化消息
     */
    @Bean
    public Jackson2JsonMessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    /**
     * RabbitTemplate — 用于发送消息
     */
    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(messageConverter());
        return template;
    }
}
