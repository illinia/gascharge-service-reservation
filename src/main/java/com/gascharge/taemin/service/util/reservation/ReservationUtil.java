package com.gascharge.taemin.service.util.reservation;

import com.gascharge.taemin.domain.entity.reservation.Reservation;
import com.gascharge.taemin.service.reservation.dto.ReservationServiceResponseDto;

public class ReservationUtil {
    public static ReservationServiceResponseDto getReservationServiceResponseDto(Reservation reservation) {
        return ReservationServiceResponseDto.builder()
                .id(reservation.getId())
                .reservationValidationId(reservation.getReservationValidationId())
                .userEmail(reservation.getUser().getEmail())
                .chargePlaceId(reservation.getCharge().getChargePlaceId())
                .reserveTime(reservation.getReservationTime())
                .status(reservation.getStatus())
                .build();
    }
}
