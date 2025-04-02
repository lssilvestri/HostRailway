package com.flavioramses.huellitasbackend.filter;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;
import java.util.Enumeration;
import java.util.stream.Collectors;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
@Slf4j
public class RequestLoggingFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        try {
            // Registrar detalles de la solicitud
            String path = request.getRequestURI();
            String method = request.getMethod();
            String queryString = request.getQueryString() != null ? "?" + request.getQueryString() : "";
            
            // Obtener todas las cabeceras
            Enumeration<String> headerNames = request.getHeaderNames();
            StringBuilder headers = new StringBuilder();
            while (headerNames.hasMoreElements()) {
                String headerName = headerNames.nextElement();
                String headerValue = request.getHeader(headerName);
                // No registrar valores sensibles como tokens o contraseñas
                if (headerName.equalsIgnoreCase("Authorization")) {
                    headerValue = "CENSORED";
                }
                headers.append(headerName).append(": ").append(headerValue).append(", ");
            }
            
            log.info("Recibida solicitud: {} {} {} - Headers: {}", method, path, queryString, headers);
        } catch (Exception e) {
            log.error("Error al registrar la solicitud: {}", e.getMessage());
        }
        
        // Registrar el tiempo de respuesta
        long startTime = System.currentTimeMillis();
        try {
            filterChain.doFilter(request, response);
        } finally {
            long duration = System.currentTimeMillis() - startTime;
            int status = response.getStatus();
            log.info("Solicitud completada: {} {} - Estado: {} - Tiempo: {}ms", 
                    request.getMethod(), request.getRequestURI(), status, duration);
        }
    }
} 