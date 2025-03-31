package com.flavioramses.huellitasbackend.repository;

import com.flavioramses.huellitasbackend.model.AlojamientoFavorito;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AlojamientoFavoritoRepository extends JpaRepository<AlojamientoFavorito, Long> {
    List<AlojamientoFavorito> findByClienteId(Long clienteId);

    boolean existsByClienteIdAndAlojamientoId(Long clienteId, Long alojamientoId);

    void deleteByClienteIdAndAlojamientoId(Long clienteId, Long alojamientoId);
}