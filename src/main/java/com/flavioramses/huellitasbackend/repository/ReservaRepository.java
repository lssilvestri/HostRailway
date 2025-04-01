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

    // Obtener reservas activas dentro de un rango de fechas
    @Query("""
    SELECT r FROM Reserva r 
    WHERE r.fechaDesde <= :fechaFin AND r.fechaHasta >= :fechaInicio 
    AND r.estado IN ('PENDIENTE', 'CONFIRMADA')
    """)
    List<Reserva> findReservasActivasEnRango(
            @Param("fechaInicio") LocalDate fechaInicio,
            @Param("fechaFin") LocalDate fechaFin);

    // Obtener reservas por cliente ordenadas por fecha de creación descendente
    List<Reserva> findByClienteIdOrderByFechaCreacionDesc(Long clienteId);

    // Verificar si un alojamiento está reservado en un rango de fechas
    @Query("""
    SELECT COUNT(r) > 0 FROM Reserva r 
    WHERE r.alojamiento.id = :alojamientoId
    AND r.fechaDesde <= :fechaFin 
    AND r.fechaHasta >= :fechaInicio 
    AND r.estado IN ('PENDIENTE', 'CONFIRMADA')
    """)
    boolean existsByAlojamientoAndFechaEntre(
            @Param("alojamientoId") Long alojamientoId,
            @Param("fechaInicio") LocalDate fechaInicio,
            @Param("fechaFin") LocalDate fechaFin);

    // Obtener reservas por estado
    List<Reserva> findByEstado(EstadoReserva estado);
    
    // Obtener reservas por alojamiento
    List<Reserva> findByAlojamientoId(Long alojamientoId);
    
    // Obtener reservas por mascota
    List<Reserva> findByMascotaId(Long mascotaId);
}


