package com.flavioramses.huellitasbackend.service;

import com.flavioramses.huellitasbackend.Exception.BadRequestException;
import com.flavioramses.huellitasbackend.Exception.ResourceNotFoundException;
import com.flavioramses.huellitasbackend.Exception.UnauthorizedException;
import com.flavioramses.huellitasbackend.dto.ReservaDTO;
import com.flavioramses.huellitasbackend.dto.ReservaNuevaDTO;
import com.flavioramses.huellitasbackend.model.*;
import com.flavioramses.huellitasbackend.repository.AlojamientoRepository;
import com.flavioramses.huellitasbackend.repository.ClienteRepository;
import com.flavioramses.huellitasbackend.repository.MascotaRepository;
import com.flavioramses.huellitasbackend.repository.ReservaRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReservaService {
    private final ReservaRepository reservaRepository;
    private final ClienteRepository clienteRepository;
    private final AlojamientoRepository alojamientoRepository;
    private final MascotaRepository mascotaRepository;

    public List<ReservaDTO> getAllReservas() {
        return reservaRepository.findAll().stream().map(ReservaDTO::fromEntity).collect(Collectors.toList());
    }

    public ReservaDTO getReservaById(Long id) throws ResourceNotFoundException {
        Reserva reserva = reservaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Reserva no encontrada"));
        return ReservaDTO.fromEntity(reserva);
    }

    @Transactional
    public ReservaDTO saveReserva(ReservaNuevaDTO reservaDTO) throws ResourceNotFoundException, BadRequestException {
        Cliente cliente = clienteRepository.findById(reservaDTO.getClienteId())
                .orElseThrow(() -> new ResourceNotFoundException("Cliente no encontrado"));

        Mascota mascota = mascotaRepository.findById(reservaDTO.getMascotaId())
                .orElseThrow(() -> new ResourceNotFoundException("Mascota no encontrada"));

        Alojamiento alojamiento = alojamientoRepository.findById(reservaDTO.getAlojamientoId())
                .orElseThrow(() -> new ResourceNotFoundException("Alojamiento no encontrado"));

        boolean estaReservado = reservaRepository.existsByAlojamientoAndFechaEntre(
                alojamiento.getId(), reservaDTO.getFechaDesde(), reservaDTO.getFechaHasta());

        if (estaReservado) {
            throw new BadRequestException("El alojamiento ya está reservado en esas fechas.");
        }

        Reserva reserva = new Reserva(null, mascota, alojamiento, cliente, reservaDTO.getFechaDesde(),
                reservaDTO.getFechaHasta(), EstadoReserva.PENDIENTE, LocalDateTime.now());

        return ReservaDTO.fromEntity(reservaRepository.save(reserva));
    }

    @Transactional
    public ReservaDTO updateReserva(Long id, ReservaNuevaDTO reservaDTO) throws ResourceNotFoundException {
        Reserva reserva = reservaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Reserva no encontrada"));

        reserva.setFechaDesde(reservaDTO.getFechaDesde());
        reserva.setFechaHasta(reservaDTO.getFechaHasta());

        return ReservaDTO.fromEntity(reservaRepository.save(reserva));
    }

    @Transactional
    public void deleteReserva(Long id) throws ResourceNotFoundException {
        if (!reservaRepository.existsById(id)) {
            throw new ResourceNotFoundException("Reserva no encontrada");
        }
        reservaRepository.deleteById(id);
    }

    @Transactional
    public ReservaDTO cancelarReserva(Long reservaId, Long clienteId) throws ResourceNotFoundException, UnauthorizedException, BadRequestException {
        Reserva reserva = reservaRepository.findById(reservaId)
                .orElseThrow(() -> new ResourceNotFoundException("Reserva no encontrada"));

        if (!reserva.getCliente().getId().equals(clienteId)) {
            throw new UnauthorizedException("No tienes permiso para cancelar esta reserva.");
        }

        if (reserva.getFechaDesde().isBefore(LocalDate.now())) {
            throw new BadRequestException("No se pueden cancelar reservas ya iniciadas o finalizadas.");
        }

        reservaRepository.delete(reserva);
        return ReservaDTO.fromEntity(reserva);
    }
}
