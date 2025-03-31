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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AlojamientoFavoritoService {

    private final AlojamientoFavoritoRepository favoritoRepository;
    private final ClienteRepository clienteRepository;
    private final AlojamientoRepository alojamientoRepository;

    @Autowired
    public AlojamientoFavoritoService(AlojamientoFavoritoRepository favoritoRepository,
                                      ClienteRepository clienteRepository,
                                      AlojamientoRepository alojamientoRepository) {
        this.favoritoRepository = favoritoRepository;
        this.clienteRepository = clienteRepository;
        this.alojamientoRepository = alojamientoRepository;
    }

    @Transactional
    public void agregarFavorito(Long clienteId, Long alojamientoId) throws ResourceNotFoundException {
        if (favoritoRepository.existsByClienteIdAndAlojamientoId(clienteId, alojamientoId)) {
            return; // Ya está marcado como favorito
        }

        Cliente cliente = clienteRepository.findById(clienteId)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente no encontrado con ID: " + clienteId));

        Alojamiento alojamiento = alojamientoRepository.findById(alojamientoId)
                .orElseThrow(() -> new ResourceNotFoundException("Alojamiento no encontrado con ID: " + alojamientoId));

        AlojamientoFavorito favorito = new AlojamientoFavorito();
        favorito.setCliente(cliente);
        favorito.setAlojamiento(alojamiento);
        favorito.setFechaMarcado(LocalDateTime.now());

        favoritoRepository.save(favorito);
    }

    @Transactional
    public void eliminarFavorito(Long clienteId, Long alojamientoId) {
        favoritoRepository.deleteByClienteIdAndAlojamientoId(clienteId, alojamientoId);
    }

    public List<AlojamientoDTO> obtenerFavoritosPorCliente(Long clienteId) {
        return favoritoRepository.findByClienteId(clienteId).stream()
                .map(favorito -> mapToDTO(favorito.getAlojamiento()))
                .collect(Collectors.toList());
    }

    private AlojamientoDTO mapToDTO(Alojamiento alojamiento) {
        AlojamientoDTO dto = new AlojamientoDTO();
        dto.setId(alojamiento.getId());
        dto.setNombre(alojamiento.getNombre());
        dto.setDescripcion(alojamiento.getDescripcion());
        dto.setPrecio(alojamiento.getPrecio());
        if (alojamiento.getCategoria() != null) {
            dto.setCategoriaId(alojamiento.getCategoria().getId());
        } else {
            dto.setCategoriaId(null); // O algún valor por defecto
        }

        // Mapear las imágenes
        List<String> imagenesUrl = alojamiento.getImagenes().stream()
                .map(ImagenAlojamiento::getUrlImagen)
                .collect(Collectors.toList());
        dto.setImagenesUrl(imagenesUrl);

        // Mapear las reservas
        List<ReservaDTO> reservasDTO = alojamiento.getReservas().stream()
                .map(ReservaDTO::fromEntity)
                .collect(Collectors.toList());
        dto.setReservas(reservasDTO);

        return dto;
    }
}
