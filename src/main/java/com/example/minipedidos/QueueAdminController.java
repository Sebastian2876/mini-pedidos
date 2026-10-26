package com.example.minipedidos;
import com.example.minipedidos.dto.QueueRequestDto;
import com.example.minipedidos.service.RabbitAdminService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/queues")
public class QueueAdminController {

    private final RabbitAdminService rabbitAdminService;

    public QueueAdminController(RabbitAdminService rabbitAdminService) {
        this.rabbitAdminService = rabbitAdminService;
    }

    @PostMapping
    public ResponseEntity<?> createQueue(@Valid @RequestBody QueueRequestDto request) {
        rabbitAdminService.createQueue(request);
        Map<String, String> response = new HashMap<>();
        response.put("message", "Cola creada exitosamente");
        response.put("queue", request.getName());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @DeleteMapping("/{name}")
    public ResponseEntity<?> deleteQueue(@PathVariable("name") String name) {
        boolean deleted = rabbitAdminService.deleteQueue(name);
        Map<String, String> response = new HashMap<>();
        if (deleted) {
            response.put("message", "Cola eliminada exitosamente");
            response.put("queue", name);
            return ResponseEntity.ok(response);
        }
        response.put("error", "No se pudo encontrar o eliminar la cola");
        response.put("queue", name);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }
}