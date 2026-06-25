package com.transport.tms.service;

import com.transport.tms.domain.entity.*;
import com.transport.tms.dto.request.CustomerOrderRequest;
import com.transport.tms.dto.response.CustomerOrderResponse;
import com.transport.tms.dto.response.PageResponse;
import com.transport.tms.exception.BusinessException;
import com.transport.tms.exception.ResourceNotFoundException;
import com.transport.tms.mapper.CustomerOrderMapper;
import com.transport.tms.repository.CustomerOrderRepository;
import com.transport.tms.repository.CustomerRepository;
import com.transport.tms.util.PageMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
@RequiredArgsConstructor
public class CustomerOrderService {

    private final CustomerOrderRepository customerOrderRepository;
    private final CustomerRepository customerRepository;
    private final CustomerOrderMapper customerOrderMapper;

    @Transactional(readOnly = true)
    public PageResponse<CustomerOrderResponse> list(int page, int size) {
        return PageMapper.map(
                customerOrderRepository.findAllByOrderByOrderDateDesc(PageRequest.of(page, size)),
                customerOrderMapper::toResponse
        );
    }

    @Transactional(readOnly = true)
    public CustomerOrderResponse getById(Long id) {
        return customerOrderMapper.toResponse(findWithDetails(id));
    }

    @Transactional
    public CustomerOrderResponse create(CustomerOrderRequest request) {
        if (customerOrderRepository.existsByReference(request.reference())) {
            throw new BusinessException("DUPLICATE_REFERENCE", "Reference commande deja existante");
        }
        CustomerOrder order = buildOrder(new CustomerOrder(), request);
        return customerOrderMapper.toResponse(customerOrderRepository.save(order));
    }

    @Transactional
    public CustomerOrderResponse update(Long id, CustomerOrderRequest request) {
        if (customerOrderRepository.existsByReferenceAndIdNot(request.reference(), id)) {
            throw new BusinessException("DUPLICATE_REFERENCE", "Reference commande deja existante");
        }
        CustomerOrder order = findWithDetails(id);
        order.getLines().clear();
        buildOrder(order, request);
        return customerOrderMapper.toResponse(customerOrderRepository.save(order));
    }

    @Transactional
    public void delete(Long id) {
        customerOrderRepository.delete(findWithDetails(id));
    }

    private CustomerOrder buildOrder(CustomerOrder order, CustomerOrderRequest request) {
        Customer customer = customerRepository.findById(request.customerId())
                .filter(Customer::isActive)
                .orElseThrow(() -> new ResourceNotFoundException("Customer", request.customerId()));

        order.setReference(request.reference());
        order.setOrderDate(request.orderDate());
        order.setCustomer(customer);
        order.setStatus(request.status());
        order.setNotes(request.notes());

        request.lines().forEach(lineReq -> {
            BigDecimal total = lineReq.quantity().multiply(lineReq.salePrice()).setScale(2, RoundingMode.HALF_UP);
            order.getLines().add(CustomerOrderLine.builder()
                    .customerOrder(order)
                    .productRef(lineReq.productRef())
                    .designation(lineReq.designation())
                    .quantity(lineReq.quantity())
                    .salePrice(lineReq.salePrice())
                    .totalPrice(total)
                    .build());
        });

        BigDecimal totalAmount = order.getLines().stream()
                .map(CustomerOrderLine::getTotalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        order.setTotalAmount(totalAmount);
        return order;
    }

    private CustomerOrder findWithDetails(Long id) {
        return customerOrderRepository.findWithDetailsById(id)
                .orElseThrow(() -> new ResourceNotFoundException("CustomerOrder", id));
    }
}
