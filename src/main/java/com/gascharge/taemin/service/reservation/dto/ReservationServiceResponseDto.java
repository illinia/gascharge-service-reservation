package com.gascharge.taemin.service.reservation.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.gascharge.taemin.domain.enums.reservation.ReservationStatus;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@ToString
public class ReservationServiceResponseDto {
    private Long id;
    private String reservationValidationId;
    private String userEmail;
    private String chargePlaceId;
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    private LocalDateTime reserveTime;
    private ReservationStatus status;

    @Builder
    public ReservationServiceResponseDto(Long id, String reservationValidationId, String userEmail, String chargePlaceId, LocalDateTime reserveTime, ReservationStatus status) {
        this.id = id;
        this.reservationValidationId = reservationValidationId;
        this.userEmail = userEmail;
        this.chargePlaceId = chargePlaceId;
        this.reserveTime = reserveTime;
        this.status = status;
    }
}
