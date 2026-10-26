package com.example.minipedidos;

import org.springframework.amqp.core.*;
import org.springframework.amqp.support.converter.JacksonJsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {
    
    public static final String PEDIDOS_QUEUE = "pedidos.cola";
    public static final String EXCHANGE_PEDIDOS = "pedidos.exchange";
    public static final String ROUTING_KEY = "pedidos.nuevo";
    
    // Constantes para el manejo de errores (Fase 13)
    public static final String DLX_PEDIDOS = "pedidos.dlx";
    public static final String DLQ_PEDIDOS = "pedidos.dlq";
    public static final String ROUTING_KEY_ERROR = "pedidos.error";

    @Bean
    public Queue pedidosQueue() {
        return QueueBuilder.durable(PEDIDOS_QUEUE).build();
    }

    @Bean
    public DirectExchange pedidosExchange() {
        return new DirectExchange(EXCHANGE_PEDIDOS);
    }

    @Bean
    public Binding pedidosBinding() {
        return BindingBuilder.bind(pedidosQueue())
                .to(pedidosExchange())
                .with(ROUTING_KEY);
    }

    // Creación del Dead Letter Exchange
    @Bean
    public DirectExchange pedidosDeadLetterExchange() {
        return new DirectExchange(DLX_PEDIDOS);
    }

    // Creación de la Dead Letter Queue
    @Bean
    public Queue pedidosDeadLetterQueue() {
        return QueueBuilder.durable(DLQ_PEDIDOS).build();
    }

    // Vinculación de la DLQ con el DLX
    @Bean
    public Binding pedidosDeadLetterBinding() {
        return BindingBuilder.bind(pedidosDeadLetterQueue())
                .to(pedidosDeadLetterExchange())
                .with(ROUTING_KEY_ERROR);
    }

    @Bean
    public JacksonJsonMessageConverter jacksonMessageConverter() {
        return new JacksonJsonMessageConverter();
    }
}