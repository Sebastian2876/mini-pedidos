package com.example.minipedidos.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public class QueueRequestDto {

    @NotBlank(message = "El nombre de la cola no puede estar vacío")
    @Pattern(regexp = "^[a-zA-Z0-9.-_]+$", message = "El nombre de la cola contiene caracteres no válidos")
    private String name;

    private boolean durable = true;
    private boolean autoDelete = false;

    public QueueRequestDto() {}

    public QueueRequestDto(String name, boolean durable, boolean autoDelete) {
        this.name = name;
        this.durable = durable;
        this.autoDelete = autoDelete;
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public boolean isDurable() { return durable; }
    public void setDurable(boolean durable) { this.durable = durable; }

    public boolean isAutoDelete() { return autoDelete; }
    public void setAutoDelete(boolean autoDelete) { this.autoDelete = autoDelete; }
}