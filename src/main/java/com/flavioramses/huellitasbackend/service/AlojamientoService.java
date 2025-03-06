package com.flavioramses.huellitasbackend.service;

import com.flavioramses.huellitasbackend.Exception.ResourceNotFoundException;
import com.flavioramses.huellitasbackend.dto.AlojamientoDashboardDTO;
import com.flavioramses.huellitasbackend.model.Alojamiento;
import com.flavioramses.huellitasbackend.model.Alojamiento;
import com.flavioramses.huellitasbackend.model.Categoria;
import com.flavioramses.huellitasbackend.repository.AlojamientoRepository;
import com.flavioramses.huellitasbackend.repository.CategoriaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class AlojamientoService {

    @Autowired
    public CategoriaRepository categoriaRepository;

    @Autowired
    public AlojamientoRepository alojamientoRepository;

    public Alojamiento saveAlojamiento(Alojamiento alojamiento) {
        Optional<Categoria> categoriaOptional = categoriaRepository.findById(alojamiento.getCategoria().getId());
        if (categoriaOptional.isPresent()) {
            alojamiento.setCategoria(categoriaOptional.get());
            return alojamientoRepository.save(alojamiento);
        }
        return null;
    }

    public List<Alojamiento> getAllAlojamientos() {
        return alojamientoRepository.findAll();
    }

    public List<AlojamientoDashboardDTO> getAllAlojamientosForDashboard() {
        List<Alojamiento> alojamientos = alojamientoRepository.findAll();
        return alojamientos.stream()
                .map(AlojamientoDashboardDTO::toAlojamientoDashboardDTO)
                .collect(Collectors.toList());
    }

    public Alojamiento updateAlojamiento(Long id, Alojamiento alojamientoNuevo) throws ResourceNotFoundException {
        Alojamiento alojamiento = alojamientoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Alojamiento no encontrado con ID: " + id));

        if (alojamientoNuevo == null) {
            throw new IllegalArgumentException("El alojamiento nuevo no puede ser nulo.");
        }
        alojamiento.setNombre(alojamientoNuevo.getNombre());
        alojamiento.setDescripcion(alojamientoNuevo.getDescripcion());
        alojamiento.setPrecio(alojamientoNuevo.getPrecio());

        if (alojamientoNuevo.getCategoria() != null && alojamientoNuevo.getCategoria().getId() != null) {
            Categoria categoria = categoriaRepository.findById(alojamientoNuevo.getCategoria().getId())
                    .orElseThrow(() -> new ResourceNotFoundException("Categoría no encontrada con ID: " + alojamientoNuevo.getCategoria().getId()));
            alojamiento.setCategoria(categoria);
        }

        return alojamientoRepository.save(alojamiento);
    }

    public Optional<Alojamiento> getAlojamientoById (Long id) {
        return alojamientoRepository.findById(id);
    }

    public void deleteAlojamientoById(Long id) {
        alojamientoRepository.deleteById(id);
    }
}
