package com.flavioramses.huellitasbackend.dto;

import com.flavioramses.huellitasbackend.model.Alojamiento;
import lombok.Data;

@Data
public class AlojamientoDashboardDTO {
    private Long id;
    private String nombre;
    private String descripcion;
    private Double precio;
    private String categoriaNombre;
    private String imagenUrl;

    public AlojamientoDashboardDTO(Alojamiento alojamiento) {
        this.id = alojamiento.getId();
        this.nombre = alojamiento.getNombre();
        this.descripcion = alojamiento.getDescripcion();
        this.precio = alojamiento.getPrecio();
        this.categoriaNombre = alojamiento.getCategoria().getNombre();
        this.imagenUrl = alojamiento.getImagenUrl();
    }

    public static AlojamientoDashboardDTO toAlojamientoDashboardDTO(Alojamiento alojamiento) {
        return new AlojamientoDashboardDTO(alojamiento);
    }
}