package com.transport.tms.service;

import com.transport.tms.domain.entity.Customer;
import com.transport.tms.dto.request.CustomerRequest;
import com.transport.tms.dto.response.CustomerResponse;
import com.transport.tms.dto.response.PageResponse;
import com.transport.tms.exception.ResourceNotFoundException;
import com.transport.tms.mapper.CustomerMapper;
import com.transport.tms.repository.CustomerRepository;
import com.transport.tms.util.PageMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CustomerService {

    private final CustomerRepository customerRepository;
    private final CustomerMapper customerMapper;

    @Transactional(readOnly = true)
    public PageResponse<CustomerResponse> list(int page, int size) {
        return PageMapper.map(
                customerRepository.findByActiveTrue(PageRequest.of(page, size, Sort.by("name"))),
                customerMapper::toResponse
        );
    }

    @Transactional(readOnly = true)
    public CustomerResponse getById(Long id) {
        return customerMapper.toResponse(findActive(id));
    }

    @Transactional
    public CustomerResponse create(CustomerRequest request) {
        return customerMapper.toResponse(customerRepository.save(customerMapper.toEntity(request)));
    }

    @Transactional
    public CustomerResponse update(Long id, CustomerRequest request) {
        Customer customer = findActive(id);
        customerMapper.updateEntity(request, customer);
        return customerMapper.toResponse(customerRepository.save(customer));
    }

    @Transactional
    public void delete(Long id) {
        Customer customer = findActive(id);
        customer.setActive(false);
        customerRepository.save(customer);
    }

    private Customer findActive(Long id) {
        return customerRepository.findById(id)
                .filter(Customer::isActive)
                .orElseThrow(() -> new ResourceNotFoundException("Customer", id));
    }
}
