package com.example.minipedidos;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.CreateQueueRequest;

import java.net.URI;

@Configuration
public class SqsConfig {

    @Bean
    public SqsClient sqsClient() {
        return SqsClient.builder()
                .endpointOverride(URI.create("http://localhost:9324"))
                .region(Region.US_EAST_1)
                .credentialsProvider(
                    StaticCredentialsProvider.create(
                        AwsBasicCredentials.create("x", "x") // Credenciales ficticias para ElasticMQ local
                    )
                )
                .build();
    }

    @Bean
    public CommandLineRunner createSqsQueue(SqsClient sqsClient) {
        return args -> {
            try {
                sqsClient.createQueue(CreateQueueRequest.builder()
                        .queueName("pedidos-queue") // Asegúrate de usar el mismo nombre que en PedidoSqsConsumer
                        .build());
                System.out.println(">>> Cola SQS creada exitosamente en ElasticMQ");
            } catch (Exception e) {
                System.err.println(">>> Error o la cola SQS ya existe: " + e.getMessage());
            }
        };
    }
}