package com.transport.tms.mapper;

import com.transport.tms.domain.entity.Customer;
import com.transport.tms.domain.entity.CustomerOrder;
import com.transport.tms.domain.entity.CustomerOrderLine;
import com.transport.tms.domain.enums.CustomerOrderStatus;
import com.transport.tms.dto.response.CustomerOrderResponse;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-07-24T10:28:44+0100",
    comments = "version: 1.6.3, compiler: javac, environment: Java 21.0.9 (Oracle Corporation)"
)
@Component
public class CustomerOrderMapperImpl implements CustomerOrderMapper {

    @Override
    public CustomerOrderResponse toResponse(CustomerOrder entity) {
        if ( entity == null ) {
            return null;
        }

        Long customerId = null;
        String customerName = null;
        Long id = null;
        String reference = null;
        LocalDate orderDate = null;
        CustomerOrderStatus status = null;
        BigDecimal totalAmount = null;
        String notes = null;
        List<CustomerOrderResponse.LineResponse> lines = null;

        customerId = entityCustomerId( entity );
        customerName = entityCustomerName( entity );
        id = entity.getId();
        reference = entity.getReference();
        orderDate = entity.getOrderDate();
        status = entity.getStatus();
        totalAmount = entity.getTotalAmount();
        notes = entity.getNotes();
        lines = customerOrderLineListToLineResponseList( entity.getLines() );

        CustomerOrderResponse customerOrderResponse = new CustomerOrderResponse( id, reference, orderDate, customerId, customerName, status, totalAmount, notes, lines );

        return customerOrderResponse;
    }

    @Override
    public CustomerOrderResponse.LineResponse toLineResponse(CustomerOrderLine line) {
        if ( line == null ) {
            return null;
        }

        Long id = null;
        String productRef = null;
        String designation = null;
        BigDecimal quantity = null;
        BigDecimal salePrice = null;
        BigDecimal totalPrice = null;

        id = line.getId();
        productRef = line.getProductRef();
        designation = line.getDesignation();
        quantity = line.getQuantity();
        salePrice = line.getSalePrice();
        totalPrice = line.getTotalPrice();

        CustomerOrderResponse.LineResponse lineResponse = new CustomerOrderResponse.LineResponse( id, productRef, designation, quantity, salePrice, totalPrice );

        return lineResponse;
    }

    private Long entityCustomerId(CustomerOrder customerOrder) {
        Customer customer = customerOrder.getCustomer();
        if ( customer == null ) {
            return null;
        }
        return customer.getId();
    }

    private String entityCustomerName(CustomerOrder customerOrder) {
        Customer customer = customerOrder.getCustomer();
        if ( customer == null ) {
            return null;
        }
        return customer.getName();
    }

    protected List<CustomerOrderResponse.LineResponse> customerOrderLineListToLineResponseList(List<CustomerOrderLine> list) {
        if ( list == null ) {
            return null;
        }

        List<CustomerOrderResponse.LineResponse> list1 = new ArrayList<CustomerOrderResponse.LineResponse>( list.size() );
        for ( CustomerOrderLine customerOrderLine : list ) {
            list1.add( toLineResponse( customerOrderLine ) );
        }

        return list1;
    }
}
