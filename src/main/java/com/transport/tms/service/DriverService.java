package com.transport.tms.service;

import com.transport.tms.domain.entity.Driver;
import com.transport.tms.dto.request.DriverRequest;
import com.transport.tms.dto.response.DriverResponse;
import com.transport.tms.dto.response.PageResponse;
import com.transport.tms.exception.ResourceNotFoundException;
import com.transport.tms.mapper.DriverMapper;
import com.transport.tms.repository.DriverRepository;
import com.transport.tms.util.PageMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class DriverService {

    private final DriverRepository driverRepository;
    private final DriverMapper driverMapper;

    @Transactional(readOnly = true)
    public PageResponse<DriverResponse> list(int page, int size) {
        return PageMapper.map(
                driverRepository.findByActiveTrue(PageRequest.of(page, size, Sort.by("lastName"))),
                driverMapper::toResponse
        );
    }

    @Transactional(readOnly = true)
    public DriverResponse getById(Long id) {
        return driverMapper.toResponse(findActive(id));
    }

    @Transactional
    public DriverResponse create(DriverRequest request) {
        return driverMapper.toResponse(driverRepository.save(driverMapper.toEntity(request)));
    }

    @Transactional
    public DriverResponse update(Long id, DriverRequest request) {
        Driver driver = findActive(id);
        driverMapper.updateEntity(request, driver);
        return driverMapper.toResponse(driverRepository.save(driver));
    }

    @Transactional
    public void delete(Long id) {
        Driver driver = findActive(id);
        driver.setActive(false);
        driverRepository.save(driver);
    }

    private Driver findActive(Long id) {
        return driverRepository.findById(id)
                .filter(Driver::isActive)
                .orElseThrow(() -> new ResourceNotFoundException("Driver", id));
    }
}
