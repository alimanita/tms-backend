package com.transport.tms.service;

import com.transport.tms.domain.entity.MissionExpense;
import com.transport.tms.domain.entity.TransportMission;
import com.transport.tms.domain.enums.MissionStatus;
import com.transport.tms.dto.request.MissionExpenseRequest;
import com.transport.tms.dto.response.MissionExpenseResponse;
import com.transport.tms.exception.BusinessException;
import com.transport.tms.exception.ResourceNotFoundException;
import com.transport.tms.mapper.FleetExtensionMapper;
import com.transport.tms.repository.MissionExpenseRepository;
import com.transport.tms.repository.TransportMissionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MissionExpenseService {

    private final MissionExpenseRepository missionExpenseRepository;
    private final TransportMissionRepository transportMissionRepository;
    private final FleetExtensionMapper fleetExtensionMapper;

    @Transactional(readOnly = true)
    public List<MissionExpenseResponse> listByMission(Long missionId) {
        findMission(missionId);
        return missionExpenseRepository.findAllByMission_IdOrderByExpenseDateDesc(missionId).stream()
                .map(fleetExtensionMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public BigDecimal totalByMission(Long missionId) {
        return missionExpenseRepository.sumAmountByMissionId(missionId);
    }

    @Transactional
    public MissionExpenseResponse add(Long missionId, MissionExpenseRequest request) {
        TransportMission mission = findMission(missionId);
        ensureExpensesEditable(mission);

        MissionExpense expense = fleetExtensionMapper.toEntity(request);
        expense.setMission(mission);
        expense.setCurrency(request.currency() != null ? request.currency() : "EUR");
        expense.setReimbursable(request.reimbursable() == null || request.reimbursable());
        expense.setExpenseDate(request.expenseDate());
        return fleetExtensionMapper.toResponse(missionExpenseRepository.save(expense));
    }

    @Transactional
    public void remove(Long missionId, Long expenseId) {
        TransportMission mission = findMission(missionId);
        ensureExpensesEditable(mission);

        MissionExpense expense = missionExpenseRepository.findById(expenseId)
                .filter(e -> e.getMission().getId().equals(missionId))
                .orElseThrow(() -> new ResourceNotFoundException("MissionExpense", expenseId));
        missionExpenseRepository.delete(expense);
    }

    private TransportMission findMission(Long missionId) {
        return transportMissionRepository.findWithDetailsById(missionId)
                .orElseThrow(() -> new ResourceNotFoundException("TransportMission", missionId));
    }

    private void ensureExpensesEditable(TransportMission mission) {
        if (mission.getStatus() == MissionStatus.DELIVERED || mission.getStatus() == MissionStatus.CANCELLED) {
            throw new BusinessException("MISSION_CLOSED", "Les depenses ne peuvent plus etre modifiees pour cette mission");
        }
    }
}
