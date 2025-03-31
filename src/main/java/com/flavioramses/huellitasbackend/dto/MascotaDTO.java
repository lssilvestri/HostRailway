package com.flavioramses.huellitasbackend.dto;

import com.flavioramses.huellitasbackend.model.Mascota;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MascotaDTO {
    private Long id;
    private String nombre;
    private Long clienteId;
    private String nombreCliente;
    private String especie;
    private String raza;
    private Double peso;
    private Integer edad;
    private String observaciones;
    private Boolean activo;

    // Método estático para convertir una entidad Mascota a MascotaDTO
    public static MascotaDTO toMascotaDTO(Mascota mascota) {
        if (mascota == null) {
            return null;
        }

        MascotaDTO dto = new MascotaDTO();
        dto.setId(mascota.getId());
        dto.setNombre(mascota.getNombre());

        // Asegurar que el cliente no sea nulo antes de obtener su ID y nombre
        if (mascota.getCliente() != null) {
            dto.setClienteId(mascota.getCliente().getId());
            dto.setNombreCliente(mascota.getCliente().getUsuario().getNombre());
        }

        dto.setEspecie(mascota.getEspecie());
        dto.setRaza(mascota.getRaza());
        dto.setPeso(mascota.getPeso());
        dto.setEdad(mascota.getEdad());
        dto.setObservaciones(mascota.getObservaciones());
        dto.setActivo(mascota.isActivo());

        return dto;
    }

    // Método estático para convertir una lista de entidades Mascota a una lista de MascotaDTO
    public static List<MascotaDTO> toMascotaDTOList(List<Mascota> mascotas) {
        if (mascotas == null) {
            return new ArrayList<>();
        }

        return mascotas.stream()
                .map(MascotaDTO::toMascotaDTO)
                .collect(Collectors.toList());
    }
}