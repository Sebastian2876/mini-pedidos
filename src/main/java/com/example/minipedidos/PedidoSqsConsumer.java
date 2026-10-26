package com.example.minipedidos;

import java.util.List;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.DeleteMessageRequest;
import software.amazon.awssdk.services.sqs.model.GetQueueUrlRequest;
import software.amazon.awssdk.services.sqs.model.Message;
import software.amazon.awssdk.services.sqs.model.ReceiveMessageRequest;
import tools.jackson.databind.ObjectMapper;

@Service
public class PedidoSqsConsumer {

    // Usamos el nombre exacto de la cola en lugar de la URL estática
    private static final String QUEUE_NAME = "pedidos-queue"; 

    private final SqsClient sqsClient;
    private final ObjectMapper objectMapper;

    public PedidoSqsConsumer(
            SqsClient sqsClient,
            ObjectMapper objectMapper) {
        this.sqsClient = sqsClient;
        this.objectMapper = objectMapper;
    }

    @Scheduled(fixedDelay = 3000)
    public void recibirPedidos() {
        try {
            // 1. Obtener la URL dinámica de la cola desde ElasticMQ
            String queueUrl = sqsClient.getQueueUrl(GetQueueUrlRequest.builder()
                    .queueName(QUEUE_NAME)
                    .build())
                    .queueUrl();

            // 2. Configurar la petición con la URL correcta
            ReceiveMessageRequest request =
                    ReceiveMessageRequest.builder()
                            .queueUrl(queueUrl)
                            .maxNumberOfMessages(10)
                            .waitTimeSeconds(1)
                            .build();

            List<Message> mensajes = sqsClient.receiveMessage(request).messages();

            for (Message mensaje : mensajes) {
                try {
                    Pedido pedido = objectMapper.readValue(
                            mensaje.body(),
                            Pedido.class
                    );

                    System.out.println("==============================");
                    System.out.println("CONSUMIDOR SQS - Pedido recibido");
                    System.out.println("ID: " + pedido.getId());
                    System.out.println("Producto: " + pedido.getProducto());
                    System.out.println("Cantidad: " + pedido.getCantidad());
                    System.out.println("Cliente: " + pedido.getCliente());
                    System.out.println("Pedido SQS procesado correctamente");
                    System.out.println("==============================");

                    DeleteMessageRequest deleteRequest =
                            DeleteMessageRequest.builder()
                                    .queueUrl(queueUrl)
                                    .receiptHandle(mensaje.receiptHandle())
                                    .build();

                    sqsClient.deleteMessage(deleteRequest);

                    System.out.println("Mensaje eliminado de SQS: " + mensaje.messageId());

                } catch (Exception e) {
                    System.err.println("Error procesando el cuerpo del mensaje SQS: " + e.getMessage());
                }
            }
        } catch (Exception e) {
            // Este catch evita que la tarea programada crashee si la cola aún no ha terminado de crearse
            System.err.println("Esperando a que la cola SQS esté disponible... " + e.getMessage());
        }
    }
}