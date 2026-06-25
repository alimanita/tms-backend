package com.transport.tms.service;

import com.transport.tms.domain.entity.FinancialEntry;
import com.transport.tms.dto.request.FinancialEntryRequest;
import com.transport.tms.dto.response.FinancialEntryResponse;
import com.transport.tms.dto.response.PageResponse;
import com.transport.tms.exception.ResourceNotFoundException;
import com.transport.tms.mapper.FinancialEntryMapper;
import com.transport.tms.repository.FinancialEntryRepository;
import com.transport.tms.util.PageMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class FinancialEntryService {

    private final FinancialEntryRepository financialEntryRepository;
    private final FinancialEntryMapper financialEntryMapper;

    @Transactional(readOnly = true)
    public PageResponse<FinancialEntryResponse> list(int page, int size) {
        return PageMapper.map(
                financialEntryRepository.findAllByOrderByEntryDateDesc(PageRequest.of(page, size)),
                financialEntryMapper::toResponse
        );
    }

    @Transactional(readOnly = true)
    public FinancialEntryResponse getById(Long id) {
        return financialEntryMapper.toResponse(find(id));
    }

    @Transactional
    public FinancialEntryResponse create(FinancialEntryRequest request) {
        return financialEntryMapper.toResponse(financialEntryRepository.save(financialEntryMapper.toEntity(request)));
    }

    @Transactional
    public FinancialEntryResponse update(Long id, FinancialEntryRequest request) {
        FinancialEntry entry = find(id);
        financialEntryMapper.updateEntity(request, entry);
        return financialEntryMapper.toResponse(financialEntryRepository.save(entry));
    }

    @Transactional
    public void delete(Long id) {
        financialEntryRepository.delete(find(id));
    }

    private FinancialEntry find(Long id) {
        return financialEntryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("FinancialEntry", id));
    }
}
