package com.example.minipedidos;

import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.GetQueueUrlRequest;
import software.amazon.awssdk.services.sqs.model.SendMessageRequest;
import tools.jackson.databind.ObjectMapper;

@Service
public class PedidoSqsProducer {

    // Usamos el mismo nombre de la cola que configuraste en ElasticMQ
    private static final String QUEUE_NAME = "pedidos-queue"; 
    
    private final SqsClient sqsClient;
    private final ObjectMapper objectMapper;

    public PedidoSqsProducer(SqsClient sqsClient, ObjectMapper objectMapper) {
        this.sqsClient = sqsClient;
        this.objectMapper = objectMapper;
    }

    public void enviarPedido(Pedido pedido) {
        try {
            // 1. Obtener la URL dinámica de la cola
            String queueUrl = sqsClient.getQueueUrl(GetQueueUrlRequest.builder()
                    .queueName(QUEUE_NAME)
                    .build())
                    .queueUrl();

            // 2. Convertir el objeto Pedido a JSON
            String mensajeJson = objectMapper.writeValueAsString(pedido);

            // 3. Armar la petición de envío
            SendMessageRequest sendMsgRequest = SendMessageRequest.builder()
                    .queueUrl(queueUrl)
                    .messageBody(mensajeJson)
                    .build();

            // 4. Enviar a SQS
            sqsClient.sendMessage(sendMsgRequest);
            System.out.println("Pedido enviado a SQS correctamente: " + pedido.getId());

        } catch (Exception e) {
            System.err.println("Error enviando pedido a SQS: " + e.getMessage());
            throw new RuntimeException(e); // Lanza la excepción para que el controlador sepa que falló
        }
    }
}