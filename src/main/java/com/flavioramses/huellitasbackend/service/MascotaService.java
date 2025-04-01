package com.flavioramses.huellitasbackend.service;

import com.flavioramses.huellitasbackend.Exception.ResourceNotFoundException;
import com.flavioramses.huellitasbackend.Exception.UnauthorizedException;
import com.flavioramses.huellitasbackend.dto.MascotaDTO;
import com.flavioramses.huellitasbackend.model.Cliente;
import com.flavioramses.huellitasbackend.model.Mascota;
import com.flavioramses.huellitasbackend.repository.ClienteRepository;
import com.flavioramses.huellitasbackend.repository.MascotaRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MascotaService {
    @Autowired
    private final MascotaRepository mascotaRepository;
    @Autowired
    private final ClienteRepository clienteRepository;

    // Obtener todas las mascotas activas de un cliente
    public List<Mascota> getMascotasByClienteId(Long clienteId) {
        return mascotaRepository.findByClienteIdAndActivoTrue(clienteId);
    }

    // Obtener una mascota por ID y validar que pertenezca al cliente
    public Mascota getMascotaByIdAndCliente(Long id, Long clienteId) throws ResourceNotFoundException {
        return mascotaRepository.findByIdAndClienteId(id, clienteId)
                .orElseThrow(() -> new ResourceNotFoundException("Mascota no encontrada o no pertenece al cliente."));
    }

    // Guardar nueva mascota
    public Mascota saveMascota(MascotaDTO mascotaDTO) throws ResourceNotFoundException {
        Cliente cliente = clienteRepository.findById(mascotaDTO.getClienteId())
                .orElseThrow(() -> new ResourceNotFoundException("Cliente no encontrado."));

        Mascota mascota = new Mascota();
        mascota.setNombre(mascotaDTO.getNombre());
        mascota.setEspecie(mascotaDTO.getEspecie());
        mascota.setRaza(mascotaDTO.getRaza());
        mascota.setPeso(mascotaDTO.getPeso());
        mascota.setEdad(mascotaDTO.getEdad());
        mascota.setObservaciones(mascotaDTO.getObservaciones());
        mascota.setActivo(true);
        mascota.setCliente(cliente);

        return mascotaRepository.save(mascota);
    }

    // Actualizar una mascota (validando propietario)
    public Mascota updateMascota(Long id, Long clienteId, MascotaDTO mascotaDTO) throws ResourceNotFoundException {
        Mascota mascota = getMascotaByIdAndCliente(id, clienteId);
        mascota.setNombre(mascotaDTO.getNombre());
        mascota.setEspecie(mascotaDTO.getEspecie());
        mascota.setRaza(mascotaDTO.getRaza());
        mascota.setPeso(mascotaDTO.getPeso());
        mascota.setEdad(mascotaDTO.getEdad());
        mascota.setObservaciones(mascotaDTO.getObservaciones());
        return mascotaRepository.save(mascota);
    }

    // Eliminar una mascota validando propietario
    public void deleteMascotaById(Long id, Long clienteId) throws ResourceNotFoundException {
        Mascota mascota = getMascotaByIdAndCliente(id, clienteId);
        mascotaRepository.delete(mascota);
    }

    // Cambiar estado de una mascota (activar/desactivar)
    @Transactional
    public Mascota cambiarEstadoMascota(Long id, Long clienteId, boolean activo) throws ResourceNotFoundException {
        Mascota mascota = getMascotaByIdAndCliente(id, clienteId);
        mascota.setActivo(activo);
        return mascotaRepository.save(mascota);
    }

    public List<Mascota> getAllMascotas() {
        return mascotaRepository.findAll();
    }
}


