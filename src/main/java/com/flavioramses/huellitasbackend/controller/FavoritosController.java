package com.flavioramses.huellitasbackend.controller;

import com.flavioramses.huellitasbackend.Exception.ResourceNotFoundException;
import com.flavioramses.huellitasbackend.dto.AlojamientoDTO;
import com.flavioramses.huellitasbackend.service.AlojamientoFavoritoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/favoritos")
public class FavoritosController {

    private final AlojamientoFavoritoService favoritoService;

    @Autowired
    public FavoritosController(AlojamientoFavoritoService favoritoService) {
        this.favoritoService = favoritoService;
    }

    @GetMapping("/cliente/{clienteId}")
    public ResponseEntity<List<AlojamientoDTO>> obtenerFavoritos(@PathVariable Long clienteId) {
        return ResponseEntity.ok(favoritoService.obtenerFavoritosPorCliente(clienteId));
    }

    @PostMapping("/cliente/{clienteId}/alojamiento/{alojamientoId}")
    public ResponseEntity<Void> agregarFavorito(
            @PathVariable Long clienteId,
            @PathVariable Long alojamientoId) throws ResourceNotFoundException {
        favoritoService.agregarFavorito(clienteId, alojamientoId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/cliente/{clienteId}/alojamiento/{alojamientoId}")
    public ResponseEntity<Void> eliminarFavorito(
            @PathVariable Long clienteId,
            @PathVariable Long alojamientoId) {
        favoritoService.eliminarFavorito(clienteId, alojamientoId);
        return ResponseEntity.ok().build();
    }
}
