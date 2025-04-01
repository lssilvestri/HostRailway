package com.flavioramses.huellitasbackend.controller;

import com.flavioramses.huellitasbackend.Exception.BadRequestException;
import com.flavioramses.huellitasbackend.Exception.ResourceNotFoundException;
import com.flavioramses.huellitasbackend.Exception.UnauthorizedException;
import com.flavioramses.huellitasbackend.dto.ReservaDTO;
import com.flavioramses.huellitasbackend.dto.ReservaNuevaDTO;
import com.flavioramses.huellitasbackend.service.ReservaService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/reservas")
@RequiredArgsConstructor
public class ReservaController {
    @Autowired
    private final ReservaService reservaService;

    @GetMapping
    public ResponseEntity<List<ReservaDTO>> getAllReservas() {
        return ResponseEntity.ok(reservaService.getAllReservas());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ReservaDTO> getReservaById(@PathVariable Long id) throws ResourceNotFoundException {
        return ResponseEntity.ok(reservaService.getReservaById(id));
    }

    @PostMapping
    public ResponseEntity<ReservaDTO> saveReserva(@RequestBody ReservaNuevaDTO reservaDTO) throws BadRequestException, ResourceNotFoundException {
        return ResponseEntity.status(HttpStatus.CREATED).body(reservaService.saveReserva(reservaDTO));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ReservaDTO> updateReserva(@PathVariable Long id, @RequestBody ReservaNuevaDTO reservaDTO) throws ResourceNotFoundException {
        return ResponseEntity.ok(reservaService.updateReserva(id, reservaDTO));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteReserva(@PathVariable Long id) throws ResourceNotFoundException {
        reservaService.deleteReserva(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{reservaId}/cancelar")
    public ResponseEntity<ReservaDTO> cancelarReserva(@PathVariable Long reservaId, @RequestParam Long clienteId) throws UnauthorizedException, BadRequestException, ResourceNotFoundException {
        return ResponseEntity.ok(reservaService.cancelarReserva(reservaId, clienteId));
    }
}