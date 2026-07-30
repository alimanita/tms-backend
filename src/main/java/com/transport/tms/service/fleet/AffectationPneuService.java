package com.transport.tms.service.fleet;


import com.transport.tms.domain.entity.fleet.AffectationPneu;
import com.transport.tms.dto.fleet.request.AffectationPneuRequest;
import com.transport.tms.dto.fleet.request.PneuRequest;
import com.transport.tms.dto.fleet.response.AffectationPneuResponse;
import com.transport.tms.dto.fleet.response.PneuResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.List;

public interface AffectationPneuService {

    Page<AffectationPneuResponse> findAllAffectations(Pageable pageable);

    AffectationPneuResponse affecter(AffectationPneuRequest request);

    AffectationPneuResponse demonter(Long id, BigDecimal unmountMileage, AffectationPneu.RaisonDemontage raison);

    List<AffectationPneuResponse> findByVehicule(Long vehiculeId);


    Page<PneuResponse> findAll(Pageable pageable);

    PneuResponse findById(Long id);

    PneuResponse create(PneuRequest request);

    PneuResponse update(Long id, PneuRequest request);

    List<PneuResponse> findEnStock();
}