package com.gascharge.taemin.service.charge;

import com.gascharge.taemin.annotation.Cache;
import com.gascharge.taemin.annotation.CacheDelete;
import com.gascharge.taemin.annotation.CachePut;
import com.gascharge.taemin.common.exception.jpa.NoEntityFoundException;
import com.gascharge.taemin.common.util.EntityDynamicUpdater;
import com.gascharge.taemin.domain.entity.charge.Charge;
import com.gascharge.taemin.domain.repository.charge.ChargeRepository;
import com.gascharge.taemin.domain.repository.charge.dto.ChargeSearchStatus;
import com.gascharge.taemin.redis.RedisJson;
import com.gascharge.taemin.service.charge.dto.ChargeServiceRequestDto;
import com.gascharge.taemin.service.charge.dto.ChargeServiceResponseDto;
import com.gascharge.taemin.service.charge.dto.FindAllChargeSearchStatusDto;
import com.gascharge.taemin.util.charge.ChargeUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.gascharge.taemin.util.charge.ChargeUtil.getChargeSearchStatus;
import static com.gascharge.taemin.util.charge.ChargeUtil.getChargeServiceReturnDto;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class ChargeService {

    private final ChargeRepository chargeRepository;
    private final RedisJson redisJson;

    public void updateCount(String chargePlaceId, Long totalCount, Long currentCount) {
        Optional<Charge> byChargePlaceId = chargeRepository.findByChargePlaceId(chargePlaceId);

        if (byChargePlaceId.isEmpty()) {
            throw new NoEntityFoundException();
        }

        Charge charge = byChargePlaceId.get();

        charge.updateCounts(totalCount, currentCount);
    }

    @Transactional(readOnly = true)
    @Cache(value = "charge", key = "chargePlaceId")
    public ChargeServiceResponseDto findByChargePlaceId(String chargePlaceId) {
        Optional<Charge> byChargePlaceId = chargeRepository.findByChargePlaceId(chargePlaceId);

        if (byChargePlaceId.isEmpty()) {
            throw new NoEntityFoundException(Charge.class, chargePlaceId);
        }

        Charge charge = byChargePlaceId.get();

        return getChargeServiceReturnDto(charge);
    }

    public ChargeServiceResponseDto saveCharge(ChargeServiceRequestDto dto) {
        Optional<Charge> byId = chargeRepository.findByChargePlaceId(dto.getChargePlaceId());

        if (byId.isPresent()) throw new DuplicateKeyException("ChargePlaceId is duplicated. Duplicated chargePlaceId is " + dto.getChargePlaceId());

        Charge charge = chargeRepository.save(ChargeUtil.getCharge(dto));

        return getChargeServiceReturnDto(charge);
    }

    @Transactional(readOnly = true)
    public List<ChargeServiceResponseDto> findAll(FindAllChargeSearchStatusDto dto) {
        ChargeSearchStatus chargeSearchStatus = getChargeSearchStatus(dto);

        Page<Charge> result = chargeRepository.findChargeWithSearchStatus(chargeSearchStatus, dto.getPageable());
        log.info("findAll : {}", result);
        return result.getContent().stream()
                .map(ChargeUtil::getChargeServiceReturnDto).toList();
    }

    @CachePut(value = "charge", key = "chargePlaceId")
    public ChargeServiceResponseDto updateDynamicField(String chargePlaceId, Map<String, Object> attributesMap) {
        Optional<Charge> byChargePlaceId = chargeRepository.findByChargePlaceId(chargePlaceId);

        if (byChargePlaceId.isEmpty()) {
            throw new NoEntityFoundException(Charge.class, chargePlaceId);
        }

        Charge charge = byChargePlaceId.get();

        EntityDynamicUpdater.update(attributesMap, charge);

        return getChargeServiceReturnDto(charge);
    }

    @CacheDelete(value = "charge", key = "chargePlaceId")
    public String deleteCharge(String chargePlaceId) {
        Optional<Charge> byChargePlaceId = chargeRepository.findByChargePlaceId(chargePlaceId);

        if (byChargePlaceId.isEmpty()) {
            throw new NoEntityFoundException(Charge.class, chargePlaceId);
        }

        Charge charge = byChargePlaceId.get();

        // TODO Charge 의 Reservation Cascade All 때문에 예약이 조회되고 있으면 같이 삭제되는 듯 함. 후에 수정 필요
        chargeRepository.delete(charge);

        return "Delete " + chargePlaceId + " Success";
    }
}
