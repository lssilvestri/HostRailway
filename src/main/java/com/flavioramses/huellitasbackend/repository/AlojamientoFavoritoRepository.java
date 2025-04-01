package com.flavioramses.huellitasbackend.repository;

import com.flavioramses.huellitasbackend.model.AlojamientoFavorito;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repositorio para manejar operaciones de base de datos relacionadas con AlojamientoFavorito.
 */
@Repository
public interface AlojamientoFavoritoRepository extends JpaRepository<AlojamientoFavorito, Long> {
    
    /**
     * Busca todos los alojamientos favoritos de un cliente.
     * 
     * @param clienteId ID del cliente
     * @return Lista de alojamientos favoritos
     */
    @Query("SELECT af FROM AlojamientoFavorito af JOIN FETCH af.alojamiento a LEFT JOIN FETCH a.imagenes WHERE af.cliente.id = :clienteId")
    List<AlojamientoFavorito> findByClienteIdWithAlojamiento(@Param("clienteId") Long clienteId);
    
    /**
     * Verifica si un alojamiento ya está marcado como favorito por un cliente.
     * 
     * @param clienteId ID del cliente
     * @param alojamientoId ID del alojamiento
     * @return true si ya está marcado como favorito, false en caso contrario
     */
    boolean existsByClienteIdAndAlojamientoId(Long clienteId, Long alojamientoId);
    
    /**
     * Encuentra un alojamiento favorito específico por cliente y alojamiento.
     * 
     * @param clienteId ID del cliente
     * @param alojamientoId ID del alojamiento
     * @return El alojamiento favorito o vacío si no existe
     */
    Optional<AlojamientoFavorito> findByClienteIdAndAlojamientoId(Long clienteId, Long alojamientoId);

    /**
     * Elimina un alojamiento favorito por cliente y alojamiento.
     * 
     * @param clienteId ID del cliente
     * @param alojamientoId ID del alojamiento
     */
    @Modifying
    @Query("DELETE FROM AlojamientoFavorito af WHERE af.cliente.id = :clienteId AND af.alojamiento.id = :alojamientoId")
    void deleteByClienteIdAndAlojamientoId(@Param("clienteId") Long clienteId, @Param("alojamientoId") Long alojamientoId);
    
    /**
     * Verifica si un alojamiento es favorito para un cliente.
     * 
     * @param clienteId ID del cliente
     * @param alojamientoId ID del alojamiento
     * @return true si es favorito, false en caso contrario
     */
    @Query("SELECT CASE WHEN COUNT(af) > 0 THEN true ELSE false END FROM AlojamientoFavorito af WHERE af.cliente.id = :clienteId AND af.alojamiento.id = :alojamientoId")
    boolean esFavorito(@Param("clienteId") Long clienteId, @Param("alojamientoId") Long alojamientoId);
}