package com.flavioramses.huellitasbackend.repository;

import com.flavioramses.huellitasbackend.model.Alojamiento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface AlojamientoRepository extends JpaRepository<Alojamiento, Long> {

    /**
     * Busca alojamientos disponibles por fechas y nombre.
     * Un alojamiento está disponible si no existe ninguna reserva confirmada o pendiente
     * que se superponga con el rango de fechas solicitado.
     * 
     * @param fechaInicio Fecha de inicio de la disponibilidad
     * @param fechaFin Fecha de fin de la disponibilidad
     * @param nombre Nombre o parte del nombre del alojamiento (búsqueda parcial)
     * @return Lista de alojamientos disponibles que coinciden con los criterios
     */
    @Query("""
    SELECT a FROM Alojamiento a
    WHERE a.activo = true 
    AND LOWER(a.nombre) LIKE LOWER(CONCAT('%', :nombre, '%')) 
    AND NOT EXISTS (
        SELECT 1 FROM Reserva r 
        WHERE r.alojamiento.id = a.id 
        AND r.fechaDesde <= :fechaFin AND r.fechaHasta >= :fechaInicio 
        AND r.estado IN ('PENDIENTE', 'CONFIRMADA')
    )
    ORDER BY a.nombre ASC
    """)
    List<Alojamiento> findDisponiblesByFechasAndNombre(
            @Param("fechaInicio") LocalDate fechaInicio,
            @Param("fechaFin") LocalDate fechaFin,
            @Param("nombre") String nombre);

    /**
     * Busca todos los alojamientos disponibles en un rango de fechas.
     * 
     * @param fechaInicio Fecha de inicio de la disponibilidad
     * @param fechaFin Fecha de fin de la disponibilidad
     * @return Lista de alojamientos disponibles
     */
    @Query("""
    SELECT a FROM Alojamiento a
    WHERE a.activo = true 
    AND NOT EXISTS (
        SELECT 1 FROM Reserva r 
        WHERE r.alojamiento.id = a.id 
        AND r.fechaDesde <= :fechaFin AND r.fechaHasta >= :fechaInicio 
        AND r.estado IN ('PENDIENTE', 'CONFIRMADA')
    )
    ORDER BY a.nombre ASC
    """)
    List<Alojamiento> findDisponiblesByFechas(
            @Param("fechaInicio") LocalDate fechaInicio,
            @Param("fechaFin") LocalDate fechaFin);
            
    /**
     * Verifica si un alojamiento está disponible en un rango de fechas específico.
     * 
     * @param alojamientoId ID del alojamiento a verificar
     * @param fechaInicio Fecha de inicio del rango
     * @param fechaFin Fecha de fin del rango
     * @return true si el alojamiento está disponible, false en caso contrario
     */
    @Query("""
    SELECT COUNT(r) = 0 FROM Reserva r 
    WHERE r.alojamiento.id = :alojamientoId
    AND r.fechaDesde <= :fechaFin 
    AND r.fechaHasta >= :fechaInicio 
    AND r.estado IN ('PENDIENTE', 'CONFIRMADA')
    """)
    boolean isDisponibleEnFechas(
            @Param("alojamientoId") Long alojamientoId,
            @Param("fechaInicio") LocalDate fechaInicio,
            @Param("fechaFin") LocalDate fechaFin);
}

