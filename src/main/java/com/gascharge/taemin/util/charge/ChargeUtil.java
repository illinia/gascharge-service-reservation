package com.gascharge.taemin.util.charge;

import com.gascharge.taemin.domain.entity.charge.Charge;
import com.gascharge.taemin.domain.repository.charge.dto.ChargeSearchStatus;
import com.gascharge.taemin.service.charge.dto.ChargeServiceRequestDto;
import com.gascharge.taemin.service.charge.dto.ChargeServiceResponseDto;
import com.gascharge.taemin.service.charge.dto.FindAllChargeSearchStatusDto;

import static com.gascharge.taemin.domain.enums.charge.ChargePlaceMembership.getChargePlaceMembership;

public class ChargeUtil {
    public static Charge getCharge(ChargeServiceRequestDto dto) {
        return Charge.builder()
                .chargePlaceId(dto.getChargePlaceId())
                .name(dto.getName())
                .totalCount(dto.getTotalCount())
                .currentCount(dto.getCurrentCount())
                .membership(getChargePlaceMembership(dto.getMembership()))
                .build();
    }

    public static ChargeServiceResponseDto getChargeServiceReturnDto(Charge charge) {
        return ChargeServiceResponseDto.builder()
                .chargePlaceId(charge.getChargePlaceId())
                .name(charge.getName())
                .totalCount(charge.getTotalCount())
                .currentCount(charge.getCurrentCount())
                .membership(charge.getMembership().getMembershipString())
                .build();
    }

    public static ChargeSearchStatus getChargeSearchStatus(FindAllChargeSearchStatusDto dto) {
        ChargeSearchStatus chargeSearchStatus = new ChargeSearchStatus();
        chargeSearchStatus.setName(dto.getName());
        chargeSearchStatus.setChargePlaceMembership(dto.getChargePlaceMembership());
        return chargeSearchStatus;
    }
}
