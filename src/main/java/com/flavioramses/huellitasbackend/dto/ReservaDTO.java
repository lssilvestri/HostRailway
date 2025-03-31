package com.flavioramses.huellitasbackend.dto;

import com.flavioramses.huellitasbackend.model.*;
import lombok.*;

import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ReservaDTO {
    private Long id;
    private String fechaDesde;
    private String fechaHasta;
    private String mascotaNombre;
    private Long mascotaId;
    private String alojamientoNombre;
    private Long alojamientoId;
    private Double alojamientoPrecio;
    private String clienteNombre;
    private String clienteApellido;
    private String clienteEmail;
    private EstadoReserva estado;
    private String fechaCreacion;

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public static ReservaDTO fromEntity(Reserva reserva) {
        ReservaDTO dto = new ReservaDTO();
        dto.setId(reserva.getId());
        dto.setFechaDesde(Optional.ofNullable(reserva.getFechaDesde())
                .map(f -> f.format(FORMATTER)).orElse("Fecha no disponible"));
        dto.setFechaHasta(Optional.ofNullable(reserva.getFechaHasta())
                .map(f -> f.format(FORMATTER)).orElse("Fecha no disponible"));
        dto.setMascotaNombre(Optional.ofNullable(reserva.getMascota())
                .map(Mascota::getNombre).orElse("Desconocida"));
        dto.setMascotaId(Optional.ofNullable(reserva.getMascota())
                .map(Mascota::getId).orElse(null));
        dto.setAlojamientoId(Optional.ofNullable(reserva.getAlojamiento())
                .map(Alojamiento::getId).orElse(null));
        dto.setAlojamientoNombre(Optional.ofNullable(reserva.getAlojamiento())
                .map(Alojamiento::getNombre).orElse("No disponible"));
        dto.setAlojamientoPrecio(Optional.ofNullable(reserva.getAlojamiento())
                .map(Alojamiento::getPrecio).orElse(0.0));
        dto.setClienteNombre(Optional.ofNullable(reserva.getCliente())
                .map(c -> c.getUsuario().getNombre()).orElse("No disponible"));
        dto.setClienteApellido(Optional.ofNullable(reserva.getCliente())
                .map(c -> c.getUsuario().getApellido()).orElse("No disponible"));
        dto.setClienteEmail(Optional.ofNullable(reserva.getCliente())
                .map(c -> c.getUsuario().getEmail()).orElse("No disponible"));
        dto.setEstado(reserva.getEstado());
        dto.setFechaCreacion(Optional.ofNullable(reserva.getFechaCreacion())
                .map(f -> f.format(FORMATTER)).orElse("Fecha no disponible"));
        return dto;
    }

    public static List<ReservaDTO> toReservaDTOList(List<Reserva> reservas) {
        return reservas != null ? reservas.stream().map(ReservaDTO::fromEntity).collect(Collectors.toList()) : Collections.emptyList();
    }
}
