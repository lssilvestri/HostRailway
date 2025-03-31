package com.flavioramses.huellitasbackend.repository;

import com.flavioramses.huellitasbackend.model.Alojamiento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface AlojamientoRepository extends JpaRepository<Alojamiento, Long> {

    @Query("SELECT a FROM Alojamiento a WHERE LOWER(a.nombre) LIKE LOWER(CONCAT('%', :nombre, '%')) AND a.activo = true")
    List<Alojamiento> findByNombreContainingIgnoreCaseAndActivo(String nombre);

    @Query("""
    SELECT a FROM Alojamiento a 
    WHERE a.activo = true AND NOT EXISTS (
        SELECT 1 FROM Reserva r 
        WHERE r.alojamiento.id = a.id 
        AND ((r.fechaDesde <= :fechaFin AND r.fechaHasta >= :fechaInicio) 
        AND r.estado IN ('PENDIENTE', 'CONFIRMADA'))
    )
    """)
    List<Alojamiento> findDisponiblesByFechas(LocalDate fechaInicio, LocalDate fechaFin);



    @Query("""
    SELECT a FROM Alojamiento a 
    WHERE a.activo = true 
    AND LOWER(a.nombre) LIKE LOWER(CONCAT('%', :nombre, '%')) 
    AND NOT EXISTS (
        SELECT 1 FROM Reserva r 
        WHERE r.alojamiento.id = a.id 
        AND ((r.fechaDesde <= :fechaFin AND r.fechaHasta >= :fechaInicio) 
        AND r.estado IN ('PENDIENTE', 'CONFIRMADA'))
    )
    """)
    List<Alojamiento> findDisponiblesByFechasAndNombre(LocalDate fechaInicio, LocalDate fechaFin, String nombre);

}
