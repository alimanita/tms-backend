/*
package com.transport.tms.service;

import com.transport.tms.dto.request.SparePartRequest;
import com.transport.tms.dto.response.PageResponse;
import com.transport.tms.dto.response.SparePartResponse;
import com.transport.tms.exception.BusinessException;
import com.transport.tms.exception.ResourceNotFoundException;
import com.transport.tms.mapper.SparePartMapper;
import com.transport.tms.util.PageMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SparePartService {

    private final SparePartRepository sparePartRepository;
    private final SparePartMapper sparePartMapper;

    @Transactional(readOnly = true)
    public PageResponse<SparePartResponse> list(int page, int size) {
        return PageMapper.map(
                sparePartRepository.findByActiveTrue(PageRequest.of(page, size, Sort.by("reference"))),
                sparePartMapper::toResponse
        );
    }

    @Transactional(readOnly = true)
    public SparePartResponse getById(Long id) {
        return sparePartMapper.toResponse(findActive(id));
    }

    @Transactional
    public SparePartResponse create(SparePartRequest request) {
        if (sparePartRepository.existsByReferenceIgnoreCase(request.reference())) {
            throw new BusinessException("DUPLICATE_REFERENCE", "Reference piece deja existante");
        }
        return sparePartMapper.toResponse(sparePartRepository.save(sparePartMapper.toEntity(request)));
    }

    @Transactional
    public SparePartResponse update(Long id, SparePartRequest request) {
        if (sparePartRepository.existsByReferenceIgnoreCaseAndIdNot(request.reference(), id)) {
            throw new BusinessException("DUPLICATE_REFERENCE", "Reference piece deja existante");
        }
        SparePart part = findActive(id);
        sparePartMapper.updateEntity(request, part);
        return sparePartMapper.toResponse(sparePartRepository.save(part));
    }

    @Transactional
    public void delete(Long id) {
        SparePart part = findActive(id);
        part.setActive(false);
        sparePartRepository.save(part);
    }

    private SparePart findActive(Long id) {
        return sparePartRepository.findById(id)
                .filter(SparePart::isActive)
                .orElseThrow(() -> new ResourceNotFoundException("SparePart", id));
    }
}
*/
