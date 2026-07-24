package com.transport.tms.service;

import com.transport.tms.domain.entity.Driver;
import com.transport.tms.dto.request.ChauffeurRequest;
import com.transport.tms.dto.response.ChauffeurResponse;
import com.transport.tms.dto.response.PageResponse;
import com.transport.tms.exception.ResourceNotFoundException;
import com.transport.tms.mapper.ChauffeurMapper;
import com.transport.tms.repository.DriverRepository;
import com.transport.tms.util.PageMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ChauffeurService {

    private final DriverRepository driverRepository;
    private final ChauffeurMapper chauffeurMapper;

    @Transactional(readOnly = true)
    public PageResponse<ChauffeurResponse> list(int page, int size) {
        return PageMapper.map(
                driverRepository.findByActiveTrue(PageRequest.of(page, size, Sort.by("lastName"))),
                chauffeurMapper::toResponse
        );
    }

    @Transactional(readOnly = true)
    public ChauffeurResponse getById(Long id) {
        return chauffeurMapper.toResponse(findActive(id));
    }

    @Transactional(readOnly = true)
    public List<ChauffeurResponse> getDisponibles() {
        return driverRepository.findByStatutAndActiveTrue("DISPONIBLE")
                .stream()
                .map(chauffeurMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public ChauffeurResponse create(ChauffeurRequest request) {
        Driver driver = chauffeurMapper.toEntity(request);
        return chauffeurMapper.toResponse(driverRepository.save(driver));
    }

    @Transactional
    public ChauffeurResponse update(Long id, ChauffeurRequest request) {
        Driver driver = findActive(id);
        chauffeurMapper.updateEntity(request, driver);
        return chauffeurMapper.toResponse(driverRepository.save(driver));
    }

    @Transactional
    public ChauffeurResponse toggleActif(Long id) {
        Driver driver = driverRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Chauffeur", id));
        driver.setActive(!driver.isActive());
        return chauffeurMapper.toResponse(driverRepository.save(driver));
    }

    @Transactional
    public void delete(Long id) {
        Driver driver = findActive(id);
        driver.setActive(false);
        driverRepository.save(driver);
    }

    private Driver findActive(Long id) {
        return driverRepository.findByIdAndActiveTrue(id)
                .orElseThrow(() -> new ResourceNotFoundException("Chauffeur", id));
    }
}
