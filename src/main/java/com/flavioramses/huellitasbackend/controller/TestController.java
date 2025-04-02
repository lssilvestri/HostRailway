package com.flavioramses.huellitasbackend.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * Controlador para realizar pruebas básicas de conectividad.
 * Estos endpoints no requieren autenticación.
 */
@RestController
@RequestMapping("/test")
@RequiredArgsConstructor
@Slf4j
public class TestController {

    @GetMapping("/status")
    public ResponseEntity<Map<String, Object>> getStatus() {
        log.info("Recibida solicitud de estado del servidor");
        Map<String, Object> response = new HashMap<>();
        response.put("status", "UP");
        response.put("mensaje", "El servidor está funcionando correctamente");
        response.put("timestamp", System.currentTimeMillis());
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/cors")
    public ResponseEntity<Map<String, Object>> testCors() {
        log.info("Recibida solicitud de prueba CORS");
        Map<String, Object> response = new HashMap<>();
        response.put("cors", "OK");
        response.put("mensaje", "Las cabeceras CORS están configuradas correctamente");
        return ResponseEntity.ok(response);
    }
} 