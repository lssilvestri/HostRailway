package com.flavioramses.huellitasbackend.controller;

import com.flavioramses.huellitasbackend.Exception.BadRequestException;
import com.flavioramses.huellitasbackend.dto.AlojamientoDTO;
import com.flavioramses.huellitasbackend.dto.DisponibilidadBusquedaDTO;
import com.flavioramses.huellitasbackend.service.BusquedaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/busqueda")
public class BusquedaController {
    @Autowired
    private final BusquedaService busquedaService;

    @Autowired
    public BusquedaController(BusquedaService busquedaService) {
        this.busquedaService = busquedaService;
    }

    @PostMapping("/disponibilidad")
    public ResponseEntity<List<AlojamientoDTO>> buscarDisponibilidad(@RequestBody DisponibilidadBusquedaDTO busquedaDTO) throws BadRequestException {
        if (busquedaDTO.getFechaInicio().isAfter(busquedaDTO.getFechaFin())) {
            throw new BadRequestException("La fecha de inicio no puede ser posterior a la fecha de fin.");
        }

        List<AlojamientoDTO> resultados = busquedaService.buscarAlojamientosDisponibles(
                busquedaDTO.getFechaInicio(), busquedaDTO.getFechaFin(), busquedaDTO.getNombreAlojamiento()
        );

        return ResponseEntity.ok(resultados);
    }

}