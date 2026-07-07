package com.oxaira.airq.shared.interfaces.rest;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.Map;

/**
 * Endpoint de Health Check ligero para evitar Cold Starts en la nube.
 * Este punto de control es público y no requiere autenticación JWT.
 */
@RestController
@RequestMapping("/api/v1/health")
public class HealthCheckController {

    @GetMapping
    public ResponseEntity<Map<String, String>> checkHealth() {
        return ResponseEntity.ok(Map.of("status", "ok", "message", "AirQ Backend is alive"));
    }
}
