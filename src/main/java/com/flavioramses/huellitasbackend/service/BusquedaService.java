package com.flavioramses.huellitasbackend.service;

import com.flavioramses.huellitasbackend.dto.AlojamientoDTO;
import com.flavioramses.huellitasbackend.dto.ReservaDTO;
import com.flavioramses.huellitasbackend.model.Alojamiento;
import com.flavioramses.huellitasbackend.model.ImagenAlojamiento;
import com.flavioramses.huellitasbackend.repository.AlojamientoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class BusquedaService {
    @Autowired
    private final AlojamientoRepository alojamientoRepository;

    @Autowired
    public BusquedaService(AlojamientoRepository alojamientoRepository) {
        this.alojamientoRepository = alojamientoRepository;
    }

    public List<AlojamientoDTO> buscarAlojamientosDisponibles(LocalDate fechaInicio, LocalDate fechaFin, String nombre) {
        if (fechaInicio.isAfter(fechaFin)) {
            throw new IllegalArgumentException("La fecha de inicio debe ser anterior a la fecha fin");
        }

        if (nombre != null && !nombre.trim().isEmpty()) {
            return alojamientoRepository.findDisponiblesByFechasAndNombre(fechaInicio, fechaFin, nombre.trim())
                    .stream()
                    .map(this::mapToDTO)
                    .collect(Collectors.toList());
        } else {
            return alojamientoRepository.findDisponiblesByFechas(fechaInicio, fechaFin)
                    .stream()
                    .map(this::mapToDTO)
                    .collect(Collectors.toList());
        }
    }

    private AlojamientoDTO mapToDTO(Alojamiento alojamiento) {
        AlojamientoDTO dto = new AlojamientoDTO();
        dto.setId(alojamiento.getId());
        dto.setNombre(alojamiento.getNombre());
        dto.setDescripcion(alojamiento.getDescripcion());
        dto.setPrecio(alojamiento.getPrecio());
        if (alojamiento.getCategoria() != null) {
            dto.setCategoriaId(alojamiento.getCategoria().getId());
        } else {
            dto.setCategoriaId(null);
        }

        List<String> imagenesUrl = alojamiento.getImagenes().stream()
                .map(ImagenAlojamiento::getUrlImagen)
                .collect(Collectors.toList());
        dto.setImagenesUrl(imagenesUrl);

        List<ReservaDTO> reservasDTO = alojamiento.getReservas().stream()
                .map(ReservaDTO::fromEntity)
                .collect(Collectors.toList());
        dto.setReservas(reservasDTO);

        return dto;
    }
}
