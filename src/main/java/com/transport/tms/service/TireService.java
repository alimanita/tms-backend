package com.transport.tms.service;

import com.transport.tms.domain.entity.Tire;
import com.transport.tms.dto.request.TireRequest;
import com.transport.tms.dto.response.PageResponse;
import com.transport.tms.dto.response.TireResponse;
import com.transport.tms.exception.BusinessException;
import com.transport.tms.exception.ResourceNotFoundException;
import com.transport.tms.mapper.TireMapper;
import com.transport.tms.repository.TireRepository;
import com.transport.tms.util.PageMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TireService {

    private final TireRepository tireRepository;
    private final TireMapper tireMapper;

    @Transactional(readOnly = true)
    public PageResponse<TireResponse> list(int page, int size) {
        return PageMapper.map(
                tireRepository.findByActiveTrue(PageRequest.of(page, size, Sort.by("serialNumber"))),
                tireMapper::toResponse
        );
    }

    @Transactional(readOnly = true)
    public TireResponse getById(Long id) {
        return tireMapper.toResponse(findActive(id));
    }

    @Transactional
    public TireResponse create(TireRequest request) {
        if (tireRepository.existsBySerialNumberIgnoreCase(request.serialNumber())) {
            throw new BusinessException("DUPLICATE_SERIAL", "Numero de serie deja utilise");
        }
        Tire saved = tireRepository.save(tireMapper.toEntity(request));
        return tireMapper.toResponse(saved);
    }

    @Transactional
    public TireResponse update(Long id, TireRequest request) {
        Tire tire = findActive(id);
        tireMapper.updateEntity(request, tire);
        return tireMapper.toResponse(tireRepository.save(tire));
    }

    @Transactional
    public void delete(Long id) {
        Tire tire = findActive(id);
        tire.setActive(false);
        tireRepository.save(tire);
    }

    public Tire findActive(Long id) {
        return tireRepository.findById(id)
                .filter(Tire::isActive)
                .orElseThrow(() -> new ResourceNotFoundException("Tire", id));
    }
}
