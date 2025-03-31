package com.flavioramses.huellitasbackend.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "alojamientos_favoritos", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"cliente_id", "alojamiento_id"})
})
public class AlojamientoFavorito {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "cliente_id", nullable = false)
    private Cliente cliente;

    @ManyToOne
    @JoinColumn(name = "alojamiento_id", nullable = false)
    private Alojamiento alojamiento;

    @Column(name = "fecha_marcado")
    private LocalDateTime fechaMarcado;
}