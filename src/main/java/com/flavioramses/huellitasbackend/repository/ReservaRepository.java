package com.flavioramses.huellitasbackend.repository;

import com.flavioramses.huellitasbackend.model.Alojamiento;
import com.flavioramses.huellitasbackend.model.EstadoReserva;
import com.flavioramses.huellitasbackend.model.Reserva;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;

@Repository
public interface ReservaRepository extends JpaRepository<Reserva, Long> {
    // Reservaciones por rangos de fechas
    @Query("SELECT r FROM Reserva r WHERE r.fechaDesde >= :fechaInicio AND r.fechaHasta <= :fechaFin AND r.estado != 'CANCELADA'")
    List<Reserva> findByFechaDesdeBetween(@Param("fechaInicio") LocalDate fechaInicio, @Param("fechaFin") LocalDate fechaFin);

    // Reservas disponibles en el rango de fechas especificado
    @Query("""
    SELECT a FROM Alojamiento a 
    WHERE a.id NOT IN (
        SELECT r.alojamiento.id FROM Reserva r 
        WHERE (r.fechaDesde <= :fechaFin AND r.fechaHasta >= :fechaInicio)
        AND r.estado IN :estados
    )
    """)
    List<Alojamiento> findAlojamientosDisponibles(
            @Param("fechaInicio") LocalDate fechaInicio,
            @Param("fechaFin") LocalDate fechaFin
    );

    @Query("SELECT r.alojamiento.id FROM Reserva r WHERE " +
            "(r.fechaInicio <= :fechaFin AND r.fechaFin >= :fechaInicio)")
    List<Long> findAlojamientosReservadosEntre(@Param("fechaInicio") LocalDate fechaInicio,
                                               @Param("fechaFin") LocalDate fechaFin);


    // Contar reservas solapadas por alojamiento
    @Query("""
    SELECT COUNT(r) FROM Reserva r
    WHERE r.alojamiento.id = :alojamientoId
    AND ((r.fechaDesde <= :fechaFin AND r.fechaHasta >= :fechaInicio))
    AND r.estado IN :estados
    """)
    long countReservasSolapadasPorAlojamiento(
            @Param("alojamientoId") Long alojamientoId,
            @Param("fechaInicio") LocalDate fechaInicio,
            @Param("fechaFin") LocalDate fechaFin,
            @Param("estados") List<EstadoReserva> estados
    );

    // Obtener reservas por cliente ordenadas por fecha de creación descendente
    List<Reserva> findByClienteIdOrderByFechaCreacionDesc(Long clienteId);

    @Query("SELECT COUNT(r) > 0 FROM Reserva r WHERE " +
            "r.alojamiento.id = :alojamientoId " +
            "AND r.fechaDesde <= :fechaFin " +   // Usa fechaDesde y compara con :fechaFin
            "AND r.fechaHasta >= :fechaInicio") // Usa fechaHasta y compara con :fechaInicio
    boolean existsByAlojamientoAndFechaEntre(@Param("alojamientoId") Long alojamientoId,
                                             @Param("fechaInicio") LocalDate fechaInicio,
                                             @Param("fechaFin") LocalDate fechaFin);

}
