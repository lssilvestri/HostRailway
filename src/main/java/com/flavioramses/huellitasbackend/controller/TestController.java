package com.flavioramses.huellitasbackend.controller;

import com.flavioramses.huellitasbackend.Exception.EmailAlreadyExistsException;
import com.flavioramses.huellitasbackend.Exception.ResourceNotFoundException;
import com.flavioramses.huellitasbackend.dto.PerfilUsuarioDTO;
import com.flavioramses.huellitasbackend.dto.UsuarioRegistroDTO;
import com.flavioramses.huellitasbackend.model.Cliente;
import com.flavioramses.huellitasbackend.model.RolUsuario;
import com.flavioramses.huellitasbackend.model.Usuario;
import com.flavioramses.huellitasbackend.repository.ClienteRepository;
import com.flavioramses.huellitasbackend.repository.UsuarioRepository;
import com.flavioramses.huellitasbackend.service.PerfilService;
import com.flavioramses.huellitasbackend.service.UsuarioService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * Controlador para realizar pruebas básicas de conectividad.
 * Estos endpoints no requieren autenticación.
 */
@RestController
@RequestMapping("/test")
@RequiredArgsConstructor
@Slf4j
public class TestController {

    private final UsuarioService usuarioService;
    private final UsuarioRepository usuarioRepository;
    private final ClienteRepository clienteRepository;
    private final PasswordEncoder passwordEncoder;
    private final PerfilService perfilService;

    @GetMapping("/status")
    public ResponseEntity<Map<String, Object>> getStatus() {
        log.info("Recibida solicitud de estado del servidor");
        Map<String, Object> response = new HashMap<>();
        response.put("status", "UP");
        response.put("mensaje", "El servidor está funcionando correctamente");
        response.put("timestamp", System.currentTimeMillis());
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/cors")
    public ResponseEntity<Map<String, Object>> testCors() {
        log.info("Recibida solicitud de prueba CORS");
        Map<String, Object> response = new HashMap<>();
        response.put("cors", "OK");
        response.put("mensaje", "Las cabeceras CORS están configuradas correctamente");
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/crear-usuario")
    public ResponseEntity<?> crearUsuarioPrueba(@RequestBody UsuarioRegistroDTO dto) {
        try {
            log.info("Creando usuario de prueba: {}", dto.getEmail());
            
            // Comprobar si el usuario ya existe
            if (usuarioRepository.existsByEmail(dto.getEmail())) {
                Usuario usuarioExistente = usuarioRepository.findByEmail(dto.getEmail()).get();
                Cliente clienteExistente = clienteRepository.findByUsuarioId(usuarioExistente.getId())
                        .orElse(null);
                
                Map<String, Object> response = new HashMap<>();
                response.put("mensaje", "El usuario ya existe");
                response.put("usuario", Map.of(
                    "id", usuarioExistente.getId(),
                    "email", usuarioExistente.getEmail(),
                    "nombre", usuarioExistente.getNombre(),
                    "rol", usuarioExistente.getRol()
                ));
                
                if (clienteExistente != null) {
                    response.put("cliente", Map.of(
                        "id", clienteExistente.getId(),
                        "numeroTelefono", clienteExistente.getNumeroTelefono()
                    ));
                }
                
                return ResponseEntity.ok(response);
            }
            
            // Crear nuevo usuario
            Usuario usuario = new Usuario();
            usuario.setNombre(dto.getNombre());
            usuario.setApellido(dto.getApellido());
            usuario.setEmail(dto.getEmail());
            usuario.setContrasena(passwordEncoder.encode(dto.getContrasena()));
            usuario.setRol(RolUsuario.USER);
            
            Usuario usuarioGuardado = usuarioRepository.save(usuario);
            
            // Crear cliente asociado
            Cliente cliente = new Cliente();
            cliente.setUsuario(usuarioGuardado);
            cliente.setNumeroTelefono("123456789"); // Valor por defecto
            
            Cliente clienteGuardado = clienteRepository.save(cliente);
            
            // Crear respuesta
            Map<String, Object> response = new HashMap<>();
            response.put("mensaje", "Usuario y cliente creados correctamente");
            response.put("usuario", Map.of(
                "id", usuarioGuardado.getId(),
                "email", usuarioGuardado.getEmail(),
                "nombre", usuarioGuardado.getNombre(),
                "rol", usuarioGuardado.getRol()
            ));
            response.put("cliente", Map.of(
                "id", clienteGuardado.getId(),
                "numeroTelefono", clienteGuardado.getNumeroTelefono()
            ));
            
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            log.error("Error al crear usuario de prueba: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error al crear usuario: " + e.getMessage()));
        }
    }
    
    @PutMapping("/actualizar-perfil/{id}")
    public ResponseEntity<?> actualizarPerfilPrueba(@PathVariable Long id, @RequestBody PerfilUsuarioDTO perfilDTO) {
        try {
            log.info("Actualizando perfil de usuario con ID {} directamente: {}", id, perfilDTO);
            PerfilUsuarioDTO perfilActualizado = perfilService.actualizarPerfil(id, perfilDTO);
            return ResponseEntity.ok(perfilActualizado);
        } catch (ResourceNotFoundException e) {
            log.error("No se encontró el usuario/cliente: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            log.error("Error al actualizar perfil: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error al actualizar perfil: " + e.getMessage()));
        }
    }

    @PutMapping("/actualizar-perfil-email")
    public ResponseEntity<?> actualizarPerfilPorEmail(@RequestBody Map<String, Object> datos) {
        try {
            log.info("Actualizando perfil mediante email: {}", datos);
            
            // Extraer email y datos del perfil
            String email = (String) datos.get("email");
            if (email == null || email.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "El email es requerido"));
            }
            
            // Buscar usuario por email
            Usuario usuario = usuarioRepository.findByEmail(email)
                    .orElseThrow(() -> new ResourceNotFoundException("Usuario con email " + email + " no encontrado"));
            
            // Crear objeto PerfilUsuarioDTO con los datos recibidos
            PerfilUsuarioDTO perfilDTO = new PerfilUsuarioDTO();
            perfilDTO.setNombre((String) datos.get("nombre"));
            perfilDTO.setApellido((String) datos.get("apellido"));
            perfilDTO.setEmail(email);
            perfilDTO.setNumeroTelefono((String) datos.get("numeroTelefono"));
            
            // Actualizar perfil
            PerfilUsuarioDTO perfilActualizado = perfilService.actualizarPerfil(usuario.getId(), perfilDTO);
            return ResponseEntity.ok(perfilActualizado);
        } catch (ResourceNotFoundException e) {
            log.error("No se encontró el usuario: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            log.error("Error al actualizar perfil por email: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error al actualizar perfil: " + e.getMessage()));
        }
    }
} 