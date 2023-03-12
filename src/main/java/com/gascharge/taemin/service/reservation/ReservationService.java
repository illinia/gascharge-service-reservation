package com.gascharge.taemin.service.reservation;

import com.gascharge.taemin.common.exception.jpa.NoEntityFoundException;
import com.gascharge.taemin.common.util.EntityDynamicUpdater;
import com.gascharge.taemin.domain.entity.charge.Charge;
import com.gascharge.taemin.domain.entity.reservation.Reservation;
import com.gascharge.taemin.domain.entity.reservation.search.ReservationSearchStatus;
import com.gascharge.taemin.domain.entity.user.User;
import com.gascharge.taemin.domain.enums.reservation.ReservationStatus;
import com.gascharge.taemin.domain.repository.charge.ChargeRepository;
import com.gascharge.taemin.domain.repository.reservation.ReservationRepository;
import com.gascharge.taemin.domain.repository.user.UserRepository;
import com.gascharge.taemin.redis.annotation.Cache;
import com.gascharge.taemin.redis.annotation.CachePut;
import com.gascharge.taemin.service.reservation.dto.ReservationServiceResponseDto;
import com.gascharge.taemin.service.util.reservation.ReservationUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.gascharge.taemin.service.util.reservation.ReservationUtil.getReservationServiceResponseDto;

@Service
@RequiredArgsConstructor
@Transactional
public class ReservationService {

    private final UserRepository userRepository;
    private final ChargeRepository chargeRepository;
    private final ReservationRepository reservationRepository;

    public ReservationServiceResponseDto reserve(Long id, String chargePlaceId, LocalDateTime time) {
        Optional<User> byUserId = userRepository.findById(id);

        if (byUserId.isEmpty()) {
            throw new NoEntityFoundException(User.class, id.toString());
        }

        Optional<Charge> byChargeId = chargeRepository.findByChargePlaceId(chargePlaceId);

        if (byChargeId.isEmpty()) {
            throw new NoEntityFoundException(Charge.class, chargePlaceId);
        }

        User user = byUserId.get();
        Charge charge = byChargeId.get();

        Reservation reservation = Reservation.builder()
                .user(user)
                .charge(charge)
                .reservationTime(time)
                .build();

        Reservation save = reservationRepository.save(reservation);

        return getReservationServiceResponseDto(save);
    }

    @CachePut(value = "reservation", key = "reservationValidationId")
    public ReservationServiceResponseDto updateStatus(String reservationValidationId, ReservationStatus status) {
        Optional<Reservation> byId = reservationRepository.findByReservationValidationId(reservationValidationId);

        if (byId.isEmpty()) {
            throw new NoEntityFoundException(Reservation.class, reservationValidationId);
        }

        Reservation reservation = byId.get();

        reservation.updateStatus(status);

        return getReservationServiceResponseDto(reservation);
    }

    @Transactional(readOnly = true)
    public boolean checkSameEmail(String email, String reservationValidationId) {
        Optional<Reservation> byId = reservationRepository.findByReservationValidationId(reservationValidationId);

        if (byId.isEmpty()) {
            throw new NoEntityFoundException(Reservation.class, reservationValidationId);
        }

        Reservation reservation = byId.get();

        return email.equals(reservation.getUser().getEmail());
    }

    @CachePut(value = "reservation", key = "reservationValidationId")
    public ReservationServiceResponseDto updateTime(String reservationValidationId, LocalDateTime time) {
        Optional<Reservation> byId = reservationRepository.findByReservationValidationId(reservationValidationId);

        if (byId.isEmpty()) {
            throw new NoEntityFoundException(Reservation.class, reservationValidationId);
        }

        Reservation reservation = byId.get();

        reservation.updateTime(time);

        return getReservationServiceResponseDto(reservation);
    }

    @Transactional(readOnly = true)
    @Cache(value = "reservation", key = "reservationValidationId")
    public ReservationServiceResponseDto findByReservationValidationId(String reservationValidationId) {
        Optional<Reservation> byId = reservationRepository.findByReservationValidationId(reservationValidationId);

        if (byId.isEmpty()) {
            throw new NoEntityFoundException(Reservation.class, reservationValidationId);
        }

        return getReservationServiceResponseDto(byId.get());
    }

    @Transactional(readOnly = true)
    public List<ReservationServiceResponseDto> findAll(String email, String chargePlaceId, Pageable pageable) {
        ReservationSearchStatus reservationSearchStatus = new ReservationSearchStatus();
        reservationSearchStatus.setEmail(email);
        reservationSearchStatus.setChargePlaceId(chargePlaceId);

        Page<Reservation> reservations = reservationRepository.findReservationWithSearchStatus(reservationSearchStatus, pageable);

        return reservations.getContent().stream().map(ReservationUtil::getReservationServiceResponseDto).collect(Collectors.toList());
    }

    @CachePut(value = "reservation", key = "reservationValidationId")
    public ReservationServiceResponseDto updateDynamicField(String reservationValidationId, Map<String, Object> attributesMap) {
        Optional<Reservation> byId = reservationRepository.findByReservationValidationId(reservationValidationId);

        if (byId.isEmpty()) {
            throw new NoEntityFoundException(Reservation.class, reservationValidationId);
        }

        Reservation reservation = byId.get();

        EntityDynamicUpdater.update(attributesMap, reservation);

        return getReservationServiceResponseDto(reservation);
    }
}
