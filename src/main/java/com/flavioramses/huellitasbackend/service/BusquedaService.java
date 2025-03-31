package com.flavioramses.huellitasbackend.service;

import com.flavioramses.huellitasbackend.dto.AlojamientoDTO;
import com.flavioramses.huellitasbackend.dto.ReservaDTO;
import com.flavioramses.huellitasbackend.model.Alojamiento;
import com.flavioramses.huellitasbackend.model.ImagenAlojamiento;
import com.flavioramses.huellitasbackend.repository.AlojamientoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class BusquedaService {
    @Autowired
    private AlojamientoRepository alojamientoRepository;

    public List<AlojamientoDTO> buscarAlojamientosDisponibles(LocalDate fechaInicio, LocalDate fechaFin, String nombre) {
        if (fechaInicio.isAfter(fechaFin)) {
            throw new IllegalArgumentException("La fecha de inicio debe ser anterior a la fecha fin");
        }

        List<Alojamiento> alojamientos = (nombre != null && !nombre.trim().isEmpty()) ?
                alojamientoRepository.findDisponiblesByFechasAndNombre(fechaInicio, fechaFin, nombre.trim()) :
                alojamientoRepository.findDisponiblesByFechas(fechaInicio, fechaFin);

        return alojamientos.stream().map(this::mapToDTO).collect(Collectors.toList());
    }

    private AlojamientoDTO mapToDTO(Alojamiento alojamiento) {
        return new AlojamientoDTO(
                alojamiento.getId(),
                alojamiento.getNombre(),
                alojamiento.getDescripcion(),
                alojamiento.getPrecio(),
                alojamiento.getCategoria().getId(),
                alojamiento.getImagenes().stream().map(ImagenAlojamiento::getUrlImagen).collect(Collectors.toList()),
                new ArrayList<>()
        );
    }
}

