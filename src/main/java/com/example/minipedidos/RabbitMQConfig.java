package com.example.minipedidos;

import org.springframework.amqp.core.*;
import org.springframework.amqp.support.converter.JacksonJsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    // Constantes de colas principales
    public static final String PEDIDOS_QUEUE = "pedidos.cola";
    public static final String NOTIFICACIONES_QUEUE = "notificaciones.cola";
    public static final String FACTURACION_QUEUE = "facturacion.cola";

    // Constantes de Dead Letter Queues (DLQ)
    public static final String DLQ_PEDIDOS = "pedidos.dlq";
    public static final String DLQ_NOTIFICACIONES = "notificaciones.dlq";
    public static final String DLQ_FACTURACION = "facturacion.dlq";

    // Exchanges (Direct y Topic) y DLX
    public static final String DIRECT_EXCHANGE_PEDIDOS = "pedidos.exchange";
    public static final String TOPIC_EXCHANGE_EVENTOS = "eventos.topic.exchange";
    public static final String DLX_PEDIDOS = "pedidos.dlx";

    // Routing Keys
    public static final String ROUTING_KEY_PEDIDO = "pedidos.nuevo";
    public static final String ROUTING_KEY_NOTIFICACION = "notificacion.*";
    public static final String ROUTING_KEY_FACTURA = "factura.#";
    public static final String ROUTING_KEY_ERROR = "pedidos.error";

    // Declaracion de colas principales
    @Bean
    public Queue pedidosQueue() {
        return QueueBuilder.durable(PEDIDOS_QUEUE).build();
    }

    @Bean
    public Queue notificacionesQueue() {
        return QueueBuilder.durable(NOTIFICACIONES_QUEUE).build();
    }

    @Bean
    public Queue facturacionQueue() {
        return QueueBuilder.durable(FACTURACION_QUEUE).build();
    }

    // Declaracion de Dead Letter Queues (DLQs)
    @Bean
    public Queue pedidosDeadLetterQueue() {
        return QueueBuilder.durable(DLQ_PEDIDOS).build();
    }

    @Bean
    public Queue notificacionesDeadLetterQueue() {
        return QueueBuilder.durable(DLQ_NOTIFICACIONES).build();
    }

    @Bean
    public Queue facturacionDeadLetterQueue() {
        return QueueBuilder.durable(DLQ_FACTURACION).build();
    }

    // Declaracion de Exchanges
    @Bean
    public DirectExchange pedidosDirectExchange() {
        return new DirectExchange(DIRECT_EXCHANGE_PEDIDOS);
    }

    @Bean
    public TopicExchange eventosTopicExchange() {
        return new TopicExchange(TOPIC_EXCHANGE_EVENTOS);
    }

    @Bean
    public DirectExchange pedidosDeadLetterExchange() {
        return new DirectExchange(DLX_PEDIDOS);
    }

    // Bindings (Vinculaciones)
    @Bean
    public Binding pedidosBinding() {
        return BindingBuilder.bind(pedidosQueue())
                .to(pedidosDirectExchange())
                .with(ROUTING_KEY_PEDIDO);
    }

    @Bean
    public Binding notificacionesBinding() {
        return BindingBuilder.bind(notificacionesQueue())
                .to(eventosTopicExchange())
                .with(ROUTING_KEY_NOTIFICACION);
    }

    @Bean
    public Binding facturacionBinding() {
        return BindingBuilder.bind(facturacionQueue())
                .to(eventosTopicExchange())
                .with(ROUTING_KEY_FACTURA);
    }

    // Bindings para las DLQ
    @Bean
    public Binding pedidosDeadLetterBinding() {
        return BindingBuilder.bind(pedidosDeadLetterQueue())
                .to(pedidosDeadLetterExchange())
                .with(ROUTING_KEY_ERROR);
    }

    @Bean
    public Binding notificacionesDeadLetterBinding() {
        return BindingBuilder.bind(notificacionesDeadLetterQueue())
                .to(pedidosDeadLetterExchange())
                .with("notificacion.error");
    }

    @Bean
    public Binding facturacionDeadLetterBinding() {
        return BindingBuilder.bind(facturacionDeadLetterQueue())
                .to(pedidosDeadLetterExchange())
                .with("factura.error");
    }

    // Convertidor de mensajes JSON
    @Bean
    public JacksonJsonMessageConverter jacksonMessageConverter() {
        return new JacksonJsonMessageConverter();
    }
}