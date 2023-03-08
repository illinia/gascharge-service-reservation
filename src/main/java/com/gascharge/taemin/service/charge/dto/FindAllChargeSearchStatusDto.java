package com.gascharge.taemin.service.charge.dto;

import com.gascharge.taemin.domain.enums.charge.ChargePlaceMembership;
import com.gascharge.taemin.domain.repository.charge.dto.ChargeSearchStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Pageable;


@NoArgsConstructor
@AllArgsConstructor
@Getter
public class FindAllChargeSearchStatusDto {
    private String name;
    private ChargePlaceMembership chargePlaceMembership;
    private Pageable pageable;
}
