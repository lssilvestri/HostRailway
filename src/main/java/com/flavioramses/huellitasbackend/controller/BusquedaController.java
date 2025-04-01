package com.flavioramses.huellitasbackend.controller;

import com.flavioramses.huellitasbackend.dto.AlojamientoDTO;
import com.flavioramses.huellitasbackend.service.BusquedaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

/**
 * Controlador para gestionar búsquedas de alojamientos disponibles.
 * Este controlador proporciona endpoints para buscar alojamientos por 
 * disponibilidad en fechas y nombre.
 */
@RestController
@RequestMapping("/busqueda")
@CrossOrigin(origins = "*")
public class BusquedaController {

    private final BusquedaService busquedaService;

    @Autowired
    public BusquedaController(BusquedaService busquedaService) {
        this.busquedaService = busquedaService;
    }

    /**
     * Busca alojamientos disponibles en un rango de fechas.
     * Opcionalmente puede filtrar por nombre.
     * 
     * @param fechaInicio Fecha de inicio para la disponibilidad
     * @param fechaFin Fecha de fin para la disponibilidad
     * @param nombre Nombre o parte del nombre del alojamiento (opcional)
     * @return Lista de alojamientos disponibles
     */
    @GetMapping("/disponibles")
    public ResponseEntity<List<AlojamientoDTO>> buscarAlojamientosDisponibles(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFin,
            @RequestParam(required = false) String nombre) {
        
        // Validación de fechas
        if (fechaInicio.isAfter(fechaFin)) {
            return ResponseEntity.badRequest().build();
        }
        
        List<AlojamientoDTO> alojamientos = busquedaService.buscarAlojamientosDisponibles(
                fechaInicio, fechaFin, nombre);
        
        return ResponseEntity.ok(alojamientos);
    }
    
    /**
     * Verifica si un alojamiento específico está disponible en un rango de fechas.
     * 
     * @param alojamientoId ID del alojamiento a verificar
     * @param fechaInicio Fecha de inicio para verificar disponibilidad
     * @param fechaFin Fecha de fin para verificar disponibilidad
     * @return true si está disponible, false si no
     */
    @GetMapping("/disponibilidad/{alojamientoId}")
    public ResponseEntity<Boolean> verificarDisponibilidad(
            @PathVariable Long alojamientoId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFin) {
        
        // Validación de fechas
        if (fechaInicio.isAfter(fechaFin)) {
            return ResponseEntity.badRequest().build();
        }
        
        boolean disponible = busquedaService.verificarDisponibilidad(
                alojamientoId, fechaInicio, fechaFin);
        
        return ResponseEntity.ok(disponible);
    }
}