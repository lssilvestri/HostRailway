package com.flavioramses.huellitasbackend.controller;

import com.flavioramses.huellitasbackend.Exception.BadRequestException;
import com.flavioramses.huellitasbackend.Exception.ResourceNotFoundException;
import com.flavioramses.huellitasbackend.Exception.UnauthorizedException;
import com.flavioramses.huellitasbackend.dto.MascotaDTO;
import com.flavioramses.huellitasbackend.model.Categoria;
import com.flavioramses.huellitasbackend.model.Cliente;
import com.flavioramses.huellitasbackend.model.Mascota;
import com.flavioramses.huellitasbackend.service.ClienteService;
import com.flavioramses.huellitasbackend.service.MascotaService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/mascotas")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class MascotaController {

    @Autowired
    private final MascotaService mascotaService;

    // Obtener todas las mascotas de un cliente
    @GetMapping("/cliente/{clienteId}")
    public ResponseEntity<List<MascotaDTO>> getMascotasByClienteId(@PathVariable Long clienteId) {
        List<Mascota> mascotas = mascotaService.getMascotasByClienteId(clienteId);
        return ResponseEntity.ok(MascotaDTO.toMascotaDTOList(mascotas));
    }
    @GetMapping
    public List<Mascota> getAllMascota() {
        return mascotaService.getAllMascotas();
    }

    // Obtener una mascota por ID (validando que pertenezca al usuario)
    @GetMapping("/{id}/cliente/{clienteId}")
    public ResponseEntity<MascotaDTO> getMascotaById(@PathVariable Long id, @PathVariable Long clienteId) throws ResourceNotFoundException {
        Mascota mascota = mascotaService.getMascotaByIdAndCliente(id, clienteId);
        return ResponseEntity.ok(MascotaDTO.toMascotaDTO(mascota));
    }

    // Registrar una nueva mascota
    @PostMapping
    public ResponseEntity<MascotaDTO> saveMascota(@RequestBody MascotaDTO mascotaDTO) throws ResourceNotFoundException {
        Mascota nuevaMascota = mascotaService.saveMascota(mascotaDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(MascotaDTO.toMascotaDTO(nuevaMascota));
    }

    // Actualizar una mascota (validando propietario)
    @PutMapping("/{id}/cliente/{clienteId}")
    public ResponseEntity<MascotaDTO> updateMascota(
            @PathVariable Long id,
            @PathVariable Long clienteId,
            @RequestBody MascotaDTO mascotaDTO) throws ResourceNotFoundException {
        Mascota mascotaActualizada = mascotaService.updateMascota(id, clienteId, mascotaDTO);
        return ResponseEntity.ok(MascotaDTO.toMascotaDTO(mascotaActualizada));
    }

    // Eliminar una mascota (validando propietario)
    @DeleteMapping("/{id}/cliente/{clienteId}")
    public ResponseEntity<Void> deleteMascotaById(@PathVariable Long id, @PathVariable Long clienteId) throws ResourceNotFoundException {
        mascotaService.deleteMascotaById(id, clienteId);
        return ResponseEntity.noContent().build();
    }

    // Activar/desactivar una mascota (en lugar de eliminarla)
    @PatchMapping("/{id}/cliente/{clienteId}/estado")
    public ResponseEntity<MascotaDTO> cambiarEstadoMascota(
            @PathVariable Long id,
            @PathVariable Long clienteId,
            @RequestParam boolean activo) throws ResourceNotFoundException {
        Mascota mascota = mascotaService.cambiarEstadoMascota(id, clienteId, activo);
        return ResponseEntity.ok(MascotaDTO.toMascotaDTO(mascota));
    }
}
