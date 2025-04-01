package com.flavioramses.huellitasbackend.service;

import com.flavioramses.huellitasbackend.Exception.ResourceNotFoundException;
import com.flavioramses.huellitasbackend.dto.AlojamientoDTO;
import com.flavioramses.huellitasbackend.dto.ReservaDTO;
import com.flavioramses.huellitasbackend.model.Alojamiento;
import com.flavioramses.huellitasbackend.model.AlojamientoFavorito;
import com.flavioramses.huellitasbackend.model.Cliente;
import com.flavioramses.huellitasbackend.model.ImagenAlojamiento;
import com.flavioramses.huellitasbackend.repository.AlojamientoFavoritoRepository;
import com.flavioramses.huellitasbackend.repository.AlojamientoRepository;
import com.flavioramses.huellitasbackend.repository.ClienteRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Servicio para gestionar los alojamientos favoritos de los clientes.
 * Proporciona funcionalidades para agregar, eliminar y consultar favoritos.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AlojamientoFavoritoService {
    
    private final AlojamientoFavoritoRepository favoritoRepository;
    private final ClienteRepository clienteRepository;
    private final AlojamientoRepository alojamientoRepository;

    /**
     * Obtiene todos los alojamientos favoritos de un cliente.
     * 
     * @param clienteId ID del cliente
     * @return Lista de DTOs de alojamientos favoritos
     */
    public List<AlojamientoDTO> obtenerFavoritosPorCliente(Long clienteId) {
        if (!clienteRepository.existsById(clienteId)) {
            log.warn("Cliente con ID {} no encontrado al obtener favoritos", clienteId);
            return List.of();
        }

        return favoritoRepository.findByClienteIdWithAlojamiento(clienteId).stream()
                .map(favorito -> mapToDTO(favorito.getAlojamiento(), true))
                .collect(Collectors.toList());
    }

    /**
     * Agrega un alojamiento a los favoritos de un cliente.
     * No hace nada si el alojamiento ya es favorito.
     * 
     * @param clienteId ID del cliente
     * @param alojamientoId ID del alojamiento
     * @return true si se agregó como favorito, false si ya lo era
     * @throws ResourceNotFoundException si el cliente o alojamiento no existen
     */
    @Transactional
    public boolean agregarFavorito(Long clienteId, Long alojamientoId) throws ResourceNotFoundException {
        // Verificar si ya es favorito
        if (favoritoRepository.existsByClienteIdAndAlojamientoId(clienteId, alojamientoId)) {
            log.info("El alojamiento {} ya es favorito del cliente {}", alojamientoId, clienteId);
            return false;
        }

        // Obtener cliente y alojamiento
        Cliente cliente = clienteRepository.findById(clienteId)
                .orElseThrow(() -> {
                    log.error("Cliente con ID {} no encontrado al agregar favorito", clienteId);
                    return new ResourceNotFoundException("Cliente no encontrado con ID: " + clienteId);
                });

        Alojamiento alojamiento = alojamientoRepository.findById(alojamientoId)
                .orElseThrow(() -> {
                    log.error("Alojamiento con ID {} no encontrado al agregar favorito", alojamientoId);
                    return new ResourceNotFoundException("Alojamiento no encontrado con ID: " + alojamientoId);
                });

        // Crear y guardar favorito
        AlojamientoFavorito favorito = AlojamientoFavorito.crear(cliente, alojamiento);
        favoritoRepository.save(favorito);
        
        log.info("Alojamiento {} agregado como favorito para el cliente {}", alojamientoId, clienteId);
        return true;
    }

    /**
     * Elimina un alojamiento de los favoritos de un cliente.
     * No hace nada si el alojamiento no era favorito.
     * 
     * @param clienteId ID del cliente
     * @param alojamientoId ID del alojamiento
     * @return true si se eliminó de favoritos, false si no era favorito
     */
    @Transactional
    public boolean eliminarFavorito(Long clienteId, Long alojamientoId) {
        // Verificar que exista como favorito
        if (!favoritoRepository.existsByClienteIdAndAlojamientoId(clienteId, alojamientoId)) {
            log.info("El alojamiento {} no es favorito del cliente {}", alojamientoId, clienteId);
            return false;
        }

        // Eliminar favorito
        favoritoRepository.deleteByClienteIdAndAlojamientoId(clienteId, alojamientoId);
        
        log.info("Alojamiento {} eliminado de favoritos para el cliente {}", alojamientoId, clienteId);
        return true;
    }

    /**
     * Verifica si un alojamiento es favorito de un cliente.
     * 
     * @param clienteId ID del cliente
     * @param alojamientoId ID del alojamiento
     * @return true si es favorito, false en caso contrario
     */
    public boolean esFavorito(Long clienteId, Long alojamientoId) {
        return favoritoRepository.esFavorito(clienteId, alojamientoId);
    }

    /**
     * Alterna el estado de favorito de un alojamiento para un cliente.
     * Si es favorito, lo elimina; si no es favorito, lo agrega.
     * 
     * @param clienteId ID del cliente
     * @param alojamientoId ID del alojamiento
     * @return true si ahora es favorito, false si ahora no lo es
     * @throws ResourceNotFoundException si el cliente o alojamiento no existen
     */
    @Transactional
    public boolean alternarFavorito(Long clienteId, Long alojamientoId) throws ResourceNotFoundException {
        boolean esFav = esFavorito(clienteId, alojamientoId);
        
        if (esFav) {
            eliminarFavorito(clienteId, alojamientoId);
            return false;
        } else {
            agregarFavorito(clienteId, alojamientoId);
            return true;
        }
    }

    /**
     * Convierte un alojamiento a su DTO, opcionalmente marcándolo como favorito.
     * 
     * @param alojamiento Entidad Alojamiento
     * @param esFavorito Si el alojamiento es favorito o no
     * @return DTO del alojamiento
     */
    private AlojamientoDTO mapToDTO(Alojamiento alojamiento, boolean esFavorito) {
        AlojamientoDTO dto = new AlojamientoDTO();
        dto.setId(alojamiento.getId());
        dto.setNombre(alojamiento.getNombre());
        dto.setDescripcion(alojamiento.getDescripcion());
        dto.setPrecio(alojamiento.getPrecio());
        
        if (alojamiento.getCategoria() != null) {
            dto.setCategoriaId(alojamiento.getCategoria().getId());
        }

        // Mapear las imágenes
        List<String> imagenesUrl = alojamiento.getImagenes().stream()
                .map(ImagenAlojamiento::getUrlImagen)
                .collect(Collectors.toList());
        dto.setImagenesUrl(imagenesUrl);
        
        // Marcar como favorito
        dto.setEsFavorito(esFavorito);

        // Mapear las reservas
        List<ReservaDTO> reservasDTO = alojamiento.getReservas().stream()
                .map(ReservaDTO::fromEntity)
                .collect(Collectors.toList());
        dto.setReservas(reservasDTO);

        return dto;
    }
}
