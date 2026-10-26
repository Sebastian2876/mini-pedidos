package com.example.minipedidos.service;

import com.example.minipedidos.dto.QueueRequestDto;
import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.core.Queue;
import org.springframework.stereotype.Service;

@Service
public class RabbitAdminService {

    private final AmqpAdmin amqpAdmin;

    public RabbitAdminService(AmqpAdmin amqpAdmin) {
        this.amqpAdmin = amqpAdmin;
    }

    public void createQueue(QueueRequestDto dto) {
        Queue queue = new Queue(dto.getName(), dto.isDurable(), false, dto.isAutoDelete());
        amqpAdmin.declareQueue(queue);
    }

    public boolean deleteQueue(String queueName) {
        return amqpAdmin.deleteQueue(queueName);
    }
}