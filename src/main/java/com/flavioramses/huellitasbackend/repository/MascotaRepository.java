package com.flavioramses.huellitasbackend.repository;

import com.flavioramses.huellitasbackend.model.Mascota;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MascotaRepository extends JpaRepository<Mascota, Long> {
    // Buscar mascotas activas por cliente
    List<Mascota> findByClienteIdAndActivoTrue(Long clienteId);

    // Buscar mascotas por nombre (ignorando mayúsculas/minúsculas) que estén activas
    List<Mascota> findByNombreContainingIgnoreCaseAndActivoTrue(String nombre);

    // Buscar mascotas por especie que estén activas
    List<Mascota> findByEspecieIgnoreCaseAndActivoTrue(String especie);

    // Buscar mascotas por raza que estén activas
    List<Mascota> findByRazaIgnoreCaseAndActivoTrue(String raza);

    // Contar mascotas activas por cliente
    Long countByClienteIdAndActivoTrue(Long clienteId);

    // Buscar todas las mascotas activas
    List<Mascota> findByActivoTrue();

    // Buscar mascotas por cliente independientemente de su estado
    List<Mascota> findByClienteId(Long clienteId);
}