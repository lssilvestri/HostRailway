package com.flavioramses.huellitasbackend.service;

import com.flavioramses.huellitasbackend.Exception.ResourceNotFoundException;
import com.flavioramses.huellitasbackend.Exception.UnauthorizedException;
import com.flavioramses.huellitasbackend.model.Cliente;
import com.flavioramses.huellitasbackend.model.Mascota;
import com.flavioramses.huellitasbackend.repository.ClienteRepository;
import com.flavioramses.huellitasbackend.repository.MascotaRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import java.util.List;
import java.util.Optional;

@Service
public class MascotaService {
    @Autowired
    private final MascotaRepository mascotaRepository;

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    public MascotaService(MascotaRepository mascotaRepository) {
        this.mascotaRepository = mascotaRepository;
    }

    public List<Mascota> getAllMascotas() {
        return mascotaRepository.findAll();
    }

    public Optional<Mascota> getMascotaById(Long id) {
        return mascotaRepository.findById(id);
    }

    public Mascota updateMascota(Long id, Mascota mascotaNueva) throws UnauthorizedException, ResourceNotFoundException {
        Mascota mascota = mascotaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Mascota no encontrada con ID: " + id));

        // Verificar que el cliente tenga permiso para modificar esta mascota
        if (!mascota.getCliente().getId().equals(mascotaNueva.getCliente().getId())) {
            throw new UnauthorizedException("No tienes permiso para modificar esta mascota");
        }

        // Actualizar todos los campos de la mascota
        mascota.setNombre(mascotaNueva.getNombre());
        mascota.setEspecie(mascotaNueva.getEspecie());
        mascota.setRaza(mascotaNueva.getRaza());
        mascota.setPeso(mascotaNueva.getPeso());
        mascota.setEdad(mascotaNueva.getEdad());
        mascota.setObservaciones(mascotaNueva.getObservaciones());

        // No actualizamos el cliente ni el estado activo aquí, ya que son campos especiales
        // que tienen sus propios métodos específicos

        return mascotaRepository.save(mascota);
    }

    public Mascota saveMascota(Mascota mascota) throws ResourceNotFoundException {
        // Verificar si el cliente existe antes de asignarlo
        Cliente cliente = clienteRepository.findById(mascota.getCliente().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Cliente no encontrado con ID: " + mascota.getCliente().getId()));

        mascota.setCliente(cliente); // Asignar el cliente gestionado por Hibernate
        return mascotaRepository.save(mascota);
    }

    public void deleteMascotaById(Long id) {
        mascotaRepository.deleteById(id);
    }

    public List<Mascota> getMascotasByClienteId(Long clienteId) {
        return mascotaRepository.findByClienteIdAndActivoTrue(clienteId);
    }

    @Transactional
    public Mascota desactivarMascota(Long mascotaId, Long clienteId) throws ResourceNotFoundException, UnauthorizedException {
        Mascota mascota = mascotaRepository.findById(mascotaId)
                .orElseThrow(() -> new ResourceNotFoundException("Mascota no encontrada con ID: " + mascotaId));

        // Validar que la mascota pertenece al cliente
        if (!mascota.getCliente().getId().equals(clienteId)) {
            throw new UnauthorizedException("No tienes permiso para modificar esta mascota");
        }

        mascota.setActivo(false);
        return mascotaRepository.save(mascota);
    }

    @Transactional
    public Mascota activarMascota(Long mascotaId, Long clienteId) throws ResourceNotFoundException, UnauthorizedException {
        Mascota mascota = mascotaRepository.findById(mascotaId)
                .orElseThrow(() -> new ResourceNotFoundException("Mascota no encontrada con ID: " + mascotaId));

        // Validar que la mascota pertenece al cliente
        if (!mascota.getCliente().getId().equals(clienteId)) {
            throw new UnauthorizedException("No tienes permiso para modificar esta mascota");
        }

        mascota.setActivo(true);
        return mascotaRepository.save(mascota);
    }

    public List<Mascota> buscarMascotasPorNombre(String nombre) {
        return mascotaRepository.findByNombreContainingIgnoreCaseAndActivoTrue(nombre);
    }

    public List<Mascota> buscarMascotasPorEspecie(String especie) {
        return mascotaRepository.findByEspecieIgnoreCaseAndActivoTrue(especie);
    }

    public List<Mascota> buscarMascotasPorRaza(String raza) {
        return mascotaRepository.findByRazaIgnoreCaseAndActivoTrue(raza);
    }
}

