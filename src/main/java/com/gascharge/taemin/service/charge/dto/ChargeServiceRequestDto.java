package com.gascharge.taemin.service.charge.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class ChargeServiceRequestDto {

    private String chargePlaceId;
    private String name;
    private Long totalCount;
    private Long currentCount;
    private String membership;

    @Builder
    public ChargeServiceRequestDto(String chargePlaceId, String name, Long totalCount, Long currentCount, String membership) {
        this.chargePlaceId = chargePlaceId;
        this.name = name;
        this.totalCount = totalCount;
        this.currentCount = currentCount;
        this.membership = membership;
    }
}