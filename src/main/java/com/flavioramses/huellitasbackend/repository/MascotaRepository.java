package com.flavioramses.huellitasbackend.repository;

import com.flavioramses.huellitasbackend.model.Mascota;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MascotaRepository extends JpaRepository<Mascota, Long> {
    // Buscar mascotas activas por cliente
    List<Mascota> findByClienteIdAndActivoTrue(Long clienteId);
    Optional<Mascota> findByIdAndClienteId(Long id, Long clienteId);
}