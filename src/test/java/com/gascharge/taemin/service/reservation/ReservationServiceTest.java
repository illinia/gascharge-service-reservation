package com.gascharge.taemin.service.reservation;

import com.gascharge.taemin.common.exception.jpa.NoEntityFoundException;
import com.gascharge.taemin.domain.entity.charge.Charge;
import com.gascharge.taemin.domain.entity.charge.ChargeTestData;
import com.gascharge.taemin.domain.entity.reservation.Reservation;
import com.gascharge.taemin.domain.entity.reservation.search.ReservationSearchStatus;
import com.gascharge.taemin.domain.entity.user.User;
import com.gascharge.taemin.domain.entity.user.UserTestData;
import com.gascharge.taemin.domain.enums.reservation.ReservationStatus;
import com.gascharge.taemin.domain.repository.charge.ChargeRepository;
import com.gascharge.taemin.domain.repository.reservation.ReservationRepository;
import com.gascharge.taemin.domain.repository.user.UserRepository;
import com.gascharge.taemin.service.reservation.dto.ReservationServiceResponseDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.time.LocalDateTime;
import java.util.*;

import static com.gascharge.taemin.service.util.reservation.ReservationUtil.getReservationServiceResponseDto;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReservationServiceTest {

    @InjectMocks
    ReservationService reservationService;
    @Mock
    ReservationRepository reservationRepository;
    @Mock
    UserRepository userRepository;
    @Mock
    ChargeRepository chargeRepository;

    private User user;
    private User admin;
    private Charge charge;
    private LocalDateTime localDateTime;
    private Reservation reservation;

    @BeforeEach
    void set() {
        this.user = UserTestData.getCloneUser();
        this.admin = UserTestData.getCloneAdmin();
        this.charge = ChargeTestData.getTestCharge();
        this.localDateTime = LocalDateTime.now().plusHours(1);
        this.reservation = Reservation.builder()
                .reservationValidationId(UUID.randomUUID().toString())
                .user(this.user)
                .charge(this.charge)
                .reservationTime(this.localDateTime)
                .build();
    }

    @Test
    void reserve() {
        // given
        Long id = 0L;
        String chargePlaceId = this.charge.getChargePlaceId();
        LocalDateTime localDateTime = this.localDateTime;

        // when
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        // then
        assertThatThrownBy(() -> reservationService.reserve(id, chargePlaceId, localDateTime))
                .isInstanceOf(NoEntityFoundException.class);

        // when
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(this.user));
        when(chargeRepository.findByChargePlaceId(anyString())).thenReturn(Optional.empty());

        // then
        assertThatThrownBy(() -> reservationService.reserve(id, chargePlaceId, localDateTime))
                .isInstanceOf(NoEntityFoundException.class);

        // when
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(this.user));
        when(chargeRepository.findByChargePlaceId(anyString())).thenReturn(Optional.of(this.charge));
        when(reservationRepository.save(any(Reservation.class))).thenReturn(this.reservation);

        // then
        assertThat(reservationService.reserve(id, chargePlaceId, localDateTime).toString()).isEqualTo(getReservationServiceResponseDto(this.reservation).toString());
    }

    @Test
    void updateStatus() {
        // given
        String reservationValidationId = this.reservation.getReservationValidationId();
        ReservationStatus status = ReservationStatus.NO_SHOW;

        // when
        when(reservationRepository.findByReservationValidationId(anyString())).thenReturn(Optional.empty());

        // then
        assertThatThrownBy(() -> reservationService.updateStatus(reservationValidationId, status))
                .isInstanceOf(NoEntityFoundException.class);

        // when
        when(reservationRepository.findByReservationValidationId(anyString())).thenReturn(Optional.of(this.reservation));

        // then
        assertThat(reservationService.updateStatus(reservationValidationId, status).getStatus()).isEqualTo(status);
    }

    @Test
    void checkSameEmail() {
        // given
        String email = this.user.getEmail();
        String adminEmail = this.admin.getEmail();
        String reservationValidationId = this.reservation.getReservationValidationId();

        // when
        when(reservationRepository.findByReservationValidationId(anyString())).thenReturn(Optional.empty());

        // then
        assertThatThrownBy(() -> reservationService.checkSameEmail(email, reservationValidationId))
                .isInstanceOf(NoEntityFoundException.class);

        // when
        when(reservationRepository.findByReservationValidationId(anyString())).thenReturn(Optional.of(this.reservation));

        // then
        assertThat(reservationService.checkSameEmail(email, reservationValidationId)).isTrue();
        assertThat(reservationService.checkSameEmail(adminEmail, reservationValidationId)).isFalse();
    }

    @Test
    void updateTime() {
        // given
        String reservationValidationId = this.reservation.getReservationValidationId();
        LocalDateTime localDateTime = LocalDateTime.now().plusHours(1);

        // when
        when(reservationRepository.findByReservationValidationId(anyString())).thenReturn(Optional.empty());

        // then
        assertThatThrownBy(() -> reservationService.updateTime(reservationValidationId, localDateTime))
                .isInstanceOf(NoEntityFoundException.class);

        // when
        when(reservationRepository.findByReservationValidationId(anyString())).thenReturn(Optional.of(this.reservation));
        ReservationServiceResponseDto result = reservationService.updateTime(reservationValidationId, localDateTime);

        // then
        assertThat(result.getReservationValidationId()).isEqualTo(reservationValidationId);
        assertThat(result.getReserveTime()).isEqualTo(localDateTime);
    }

    @Test
    void findByReservationId() {
        // given
        String reservationValidationId = this.reservation.getReservationValidationId();

        // when
        when(reservationRepository.findByReservationValidationId(anyString())).thenReturn(Optional.empty());

        // then
        assertThatThrownBy(() -> reservationService.findByReservationValidationId(reservationValidationId))
                .isInstanceOf(NoEntityFoundException.class);

        // when
        when(reservationRepository.findByReservationValidationId(anyString())).thenReturn(Optional.of(this.reservation));
        ReservationServiceResponseDto result = reservationService.findByReservationValidationId(reservationValidationId);

        // then
        assertThat(result.getReservationValidationId()).isEqualTo(reservationValidationId);
        assertThat(result.getReserveTime()).isEqualTo(this.localDateTime);
    }

    @Test
    void findAll() {
        // given
        Reservation reservation1 = Reservation.builder()
                .reservationValidationId(UUID.randomUUID().toString())
                .user(this.admin)
                .charge(this.charge)
                .reservationTime(LocalDateTime.now().plusDays(1))
                .build();

        List<Reservation> reservationList = new ArrayList<>();
        reservationList.add(this.reservation);
        reservationList.add(reservation1);

        int page = 0;
        int size = 10;
        Sort sort = Sort.by("time").descending();
        PageRequest pageRequest = PageRequest.of(page, size, sort);

        Page<Reservation> pageImpl = new PageImpl<>(reservationList, pageRequest, reservationList.size());

        // when
        when(reservationRepository.findReservationWithSearchStatus(any(ReservationSearchStatus.class), any(Pageable.class))).thenReturn(pageImpl);
        List<ReservationServiceResponseDto> result = reservationService.findAll(null, null, pageRequest);

        // then
//        assertThat(result.getPageable().getPageNumber()).isEqualTo(page);
//        assertThat(result.getPageable().getPageSize()).isEqualTo(size);
//        assertThat(result.getPageable().getSort()).isEqualTo(sort);
        assertThat(result).hasSize(2);
        assertThat(result.get(0).toString()).isEqualTo(getReservationServiceResponseDto(this.reservation).toString());
        assertThat(result.get(1).toString()).isEqualTo(getReservationServiceResponseDto(reservation1).toString());
    }

    @Test
    void updateDynamicField() {
        // given
        String reservationValidationId = this.reservation.getReservationValidationId();

        Map<String, Object> objectMap = new HashMap<>();
        ReservationStatus status = ReservationStatus.NO_SHOW;
        LocalDateTime localDateTime = LocalDateTime.now().plusDays(1);
        objectMap.put("status", status);
        objectMap.put("reservationTime", localDateTime);

        // when
        when(reservationRepository.findByReservationValidationId(anyString())).thenReturn(Optional.empty());

        // then
        assertThatThrownBy(() -> reservationService.updateDynamicField(reservationValidationId, null))
                .isInstanceOf(NoEntityFoundException.class);

        // when
        when(reservationRepository.findByReservationValidationId(anyString())).thenReturn(Optional.of(this.reservation));
        ReservationServiceResponseDto result = reservationService.updateDynamicField(reservationValidationId, objectMap);

        // then
        assertThat(result.getReservationValidationId()).isEqualTo(reservationValidationId);
        assertThat(result.getStatus()).isEqualTo(status);
        assertThat(result.getReserveTime()).isEqualTo(localDateTime);
    }






}