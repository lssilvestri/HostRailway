package com.flavioramses.huellitasbackend.controller;

import com.flavioramses.huellitasbackend.Exception.BadRequestException;
import com.flavioramses.huellitasbackend.Exception.ResourceNotFoundException;
import com.flavioramses.huellitasbackend.Exception.UnauthorizedException;
import com.flavioramses.huellitasbackend.dto.MascotaDTO;
import com.flavioramses.huellitasbackend.dto.MascotaReservaDTO;
import com.flavioramses.huellitasbackend.model.Cliente;
import com.flavioramses.huellitasbackend.model.Mascota;
import com.flavioramses.huellitasbackend.service.ClienteService;
import com.flavioramses.huellitasbackend.service.MascotaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/mascotas")
@CrossOrigin(origins = "*")
public class MascotaController {

    private final MascotaService mascotaService;
    private final ClienteService clienteService;

    @Autowired
    public MascotaController(MascotaService mascotaService, ClienteService clienteService) {
        this.mascotaService = mascotaService;
        this.clienteService = clienteService;
    }

    @GetMapping
    public ResponseEntity<List<MascotaDTO>> getAllMascotas() {
        List<Mascota> mascotas = mascotaService.getAllMascotas();
        return ResponseEntity.ok(MascotaDTO.toMascotaDTOList(mascotas));
    }

    @GetMapping("/{id}")
    public ResponseEntity<MascotaDTO> getMascotaById(@PathVariable("id") Long id) throws ResourceNotFoundException {
        Optional<Mascota> mascotaBuscada = mascotaService.getMascotaById(id);
        if(mascotaBuscada.isPresent()){
            return ResponseEntity.ok(MascotaDTO.toMascotaDTO(mascotaBuscada.get()));
        }else{
            throw new ResourceNotFoundException("Mascota no encontrada con ID: " + id);
        }
    }

    @GetMapping("/cliente/{clienteId}")
    public ResponseEntity<List<MascotaDTO>> getMascotasByClienteId(@PathVariable Long clienteId) throws ResourceNotFoundException {
        // Verificar si el cliente existe
        if (!clienteService.getClienteById(clienteId).isPresent()) {
            throw new ResourceNotFoundException("Cliente no encontrado con ID: " + clienteId);
        }

        List<Mascota> mascotas = mascotaService.getMascotasByClienteId(clienteId);
        return ResponseEntity.ok(MascotaDTO.toMascotaDTOList(mascotas));
    }

    @GetMapping("/buscar/nombre/{nombre}")
    public ResponseEntity<List<MascotaDTO>> buscarMascotasPorNombre(@PathVariable String nombre) {
        List<Mascota> mascotas = mascotaService.buscarMascotasPorNombre(nombre);
        return ResponseEntity.ok(MascotaDTO.toMascotaDTOList(mascotas));
    }

    @GetMapping("/buscar/especie/{especie}")
    public ResponseEntity<List<MascotaDTO>> buscarMascotasPorEspecie(@PathVariable String especie) {
        List<Mascota> mascotas = mascotaService.buscarMascotasPorEspecie(especie);
        return ResponseEntity.ok(MascotaDTO.toMascotaDTOList(mascotas));
    }

    @GetMapping("/buscar/raza/{raza}")
    public ResponseEntity<List<MascotaDTO>> buscarMascotasPorRaza(@PathVariable String raza) {
        List<Mascota> mascotas = mascotaService.buscarMascotasPorRaza(raza);
        return ResponseEntity.ok(MascotaDTO.toMascotaDTOList(mascotas));
    }

    @PutMapping("/{id}")
    public ResponseEntity<MascotaDTO> updateMascota(@PathVariable Long id, @RequestBody Mascota mascota) throws ResourceNotFoundException, UnauthorizedException, BadRequestException {
        try {
            Mascota mascotaActualizada = mascotaService.updateMascota(id, mascota);
            return ResponseEntity.ok(MascotaDTO.toMascotaDTO(mascotaActualizada));
        } catch (ResourceNotFoundException | UnauthorizedException e) {
            throw e;
        } catch (Exception e) {
            throw new BadRequestException("Ocurrió un error al actualizar la mascota: " + e.getMessage());
        }
    }

    @PostMapping
    public ResponseEntity<MascotaDTO> saveMascota(@RequestBody MascotaReservaDTO mascotaRequest) throws BadRequestException, ResourceNotFoundException {
        // Validar que el nombre y el clienteId no sean nulos o vacíos
        if (mascotaRequest.getNombre() == null || mascotaRequest.getNombre().trim().isEmpty()) {
            throw new BadRequestException("El nombre de la mascota no puede estar vacío.");
        }
        if (mascotaRequest.getClienteId() == null) {
            throw new BadRequestException("El ID del cliente no puede estar vacío.");
        }

        try {
            // Obtén el cliente desde la base de datos usando el clienteId
            Cliente cliente = clienteService.getClienteById(mascotaRequest.getClienteId())
                    .orElseThrow(() -> new ResourceNotFoundException("Cliente no encontrado con ID: " + mascotaRequest.getClienteId()));

            // Crea la mascota y asocia el cliente
            Mascota mascota = new Mascota();
            mascota.setNombre(mascotaRequest.getNombre());
            mascota.setEspecie(mascotaRequest.getEspecie());
            mascota.setRaza(mascotaRequest.getRaza());
            mascota.setPeso(mascotaRequest.getPeso());
            mascota.setEdad(mascotaRequest.getEdad());
            mascota.setObservaciones(mascotaRequest.getObservaciones());
            mascota.setCliente(cliente);
            mascota.setActivo(true);

            // Guarda la mascota
            Mascota mascotaGuardada = mascotaService.saveMascota(mascota);
            return ResponseEntity.status(HttpStatus.CREATED).body(MascotaDTO.toMascotaDTO(mascotaGuardada));

        } catch (ResourceNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new BadRequestException("Hubo un error al registrar la mascota: " + e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMascotaById(@PathVariable Long id) throws BadRequestException {
        try {
            mascotaService.deleteMascotaById(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            throw new BadRequestException("Error al eliminar la mascota: " + e.getMessage());
        }
    }

    @PatchMapping("/{mascotaId}/desactivar/cliente/{clienteId}")
    public ResponseEntity<MascotaDTO> desactivarMascota(
            @PathVariable Long mascotaId,
            @PathVariable Long clienteId) throws ResourceNotFoundException, UnauthorizedException {

        Mascota mascotaDesactivada = mascotaService.desactivarMascota(mascotaId, clienteId);
        return ResponseEntity.ok(MascotaDTO.toMascotaDTO(mascotaDesactivada));
    }

    @PatchMapping("/{mascotaId}/activar/cliente/{clienteId}")
    public ResponseEntity<MascotaDTO> activarMascota(
            @PathVariable Long mascotaId,
            @PathVariable Long clienteId) throws ResourceNotFoundException, UnauthorizedException {

        Mascota mascotaActivada = mascotaService.activarMascota(mascotaId, clienteId);
        return ResponseEntity.ok(MascotaDTO.toMascotaDTO(mascotaActivada));
    }
}