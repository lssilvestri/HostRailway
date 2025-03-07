package com.flavioramses.huellitasbackend.controller;

import com.flavioramses.huellitasbackend.Exception.BadRequestException;
import com.flavioramses.huellitasbackend.Exception.ResourceNotFoundException;
import com.flavioramses.huellitasbackend.dto.UsuarioDTO;
import com.flavioramses.huellitasbackend.model.RolUsuario;
import com.flavioramses.huellitasbackend.model.Usuario;
import com.flavioramses.huellitasbackend.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = "/usuarios")
public class UsuarioController {

    @Autowired
    UsuarioService usuarioService;

    @GetMapping
    public ResponseEntity<List<UsuarioDTO>> getAllUsuarios() {
        return ResponseEntity.ok(UsuarioDTO.toUserDTOList(usuarioService.getAllUsuarios()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<UsuarioDTO> getUsuarioById(@PathVariable Long id) throws ResourceNotFoundException {
        return ResponseEntity.ok(UsuarioDTO.toUsuarioDTO(usuarioService.getUsuarioById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario con id " + id + " no encontrado"))));
    }

    @GetMapping("/rol/{id}")
    public ResponseEntity<String> getUsuarioRol(@PathVariable Long id) throws ResourceNotFoundException {
        Usuario usuario = usuarioService.getUsuarioById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario con id " + id + " no encontrado"));
        return ResponseEntity.ok(usuario.getRol().name());
    }

    @PostMapping
    public ResponseEntity<UsuarioDTO> saveUsuario(@RequestBody Usuario usuario) throws BadRequestException {
        Usuario usuarioGuardado = usuarioService.saveUsuario(usuario);
        return ResponseEntity.ok(UsuarioDTO.toUsuarioDTO(usuarioGuardado));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUsuarioById(@PathVariable Long id) {
        usuarioService.deleteUsuarioById(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/roles/{role}")
    public ResponseEntity<List<UsuarioDTO>> getUsuariosByRole(@PathVariable RolUsuario role) throws BadRequestException {
        if (role != RolUsuario.ADMIN && role != RolUsuario.USER) {
            throw new BadRequestException("Rol no válido");
        }
        return ResponseEntity.ok(UsuarioDTO.toUserDTOList(usuarioService.getUsersByRole(role)));
    }

    @PutMapping("/{usuarioId}/rol/{role}")
    public ResponseEntity<String> assignRole(
            @PathVariable Long usuarioId,
            @PathVariable String role,
            @AuthenticationPrincipal UserDetails adminUser) throws ResourceNotFoundException, BadRequestException {

        RolUsuario rolUsuario;
        try {
            rolUsuario = RolUsuario.valueOf(role.toUpperCase());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Rol no válido: " + role);
        }

        usuarioService.assignRole(usuarioId, rolUsuario, adminUser.getUsername());
        return ResponseEntity.ok("Rol actualizado correctamente.");
    }
}