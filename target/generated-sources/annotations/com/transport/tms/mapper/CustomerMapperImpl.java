package com.transport.tms.mapper;

import com.transport.tms.domain.entity.Customer;
import com.transport.tms.dto.request.CustomerRequest;
import com.transport.tms.dto.response.CustomerResponse;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-06-25T09:51:18+0100",
    comments = "version: 1.6.3, compiler: javac, environment: Java 21.0.9 (Oracle Corporation)"
)
@Component
public class CustomerMapperImpl implements CustomerMapper {

    @Override
    public CustomerResponse toResponse(Customer entity) {
        if ( entity == null ) {
            return null;
        }

        Long id = null;
        String name = null;
        String company = null;
        String phone = null;
        String email = null;
        String address = null;
        String city = null;
        String country = null;
        String nif = null;
        String taxId = null;
        boolean active = false;

        id = entity.getId();
        name = entity.getName();
        company = entity.getCompany();
        phone = entity.getPhone();
        email = entity.getEmail();
        address = entity.getAddress();
        city = entity.getCity();
        country = entity.getCountry();
        nif = entity.getNif();
        taxId = entity.getTaxId();
        active = entity.isActive();

        CustomerResponse customerResponse = new CustomerResponse( id, name, company, phone, email, address, city, country, nif, taxId, active );

        return customerResponse;
    }

    @Override
    public Customer toEntity(CustomerRequest request) {
        if ( request == null ) {
            return null;
        }

        Customer.CustomerBuilder customer = Customer.builder();

        customer.name( request.name() );
        customer.company( request.company() );
        customer.phone( request.phone() );
        customer.email( request.email() );
        customer.address( request.address() );
        customer.city( request.city() );
        customer.country( request.country() );
        customer.nif( request.nif() );
        customer.taxId( request.taxId() );

        customer.active( true );

        return customer.build();
    }

    @Override
    public void updateEntity(CustomerRequest request, Customer entity) {
        if ( request == null ) {
            return;
        }

        entity.setName( request.name() );
        entity.setCompany( request.company() );
        entity.setPhone( request.phone() );
        entity.setEmail( request.email() );
        entity.setAddress( request.address() );
        entity.setCity( request.city() );
        entity.setCountry( request.country() );
        entity.setNif( request.nif() );
        entity.setTaxId( request.taxId() );
    }
}
