package com.transport.tms.mapper;

import com.transport.tms.domain.entity.Driver;
import com.transport.tms.dto.request.DriverRequest;
import com.transport.tms.dto.response.DriverResponse;
import java.math.BigDecimal;
import java.time.LocalDate;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-07-24T10:28:44+0100",
    comments = "version: 1.6.3, compiler: javac, environment: Java 21.0.9 (Oracle Corporation)"
)
@Component
public class DriverMapperImpl implements DriverMapper {

    @Override
    public DriverResponse toResponse(Driver entity) {
        if ( entity == null ) {
            return null;
        }

        Long id = null;
        String firstName = null;
        String lastName = null;
        String cin = null;
        String phone = null;
        String address = null;
        LocalDate hireDate = null;
        BigDecimal salary = null;
        String licenseNumber = null;
        String licenseCategory = null;
        LocalDate licenseExpiry = null;
        boolean active = false;

        id = entity.getId();
        firstName = entity.getFirstName();
        lastName = entity.getLastName();
        cin = entity.getCin();
        phone = entity.getPhone();
        address = entity.getAddress();
        hireDate = entity.getHireDate();
        salary = entity.getSalary();
        licenseNumber = entity.getLicenseNumber();
        licenseCategory = entity.getLicenseCategory();
        licenseExpiry = entity.getLicenseExpiry();
        active = entity.isActive();

        String fullName = entity.getFullName();

        DriverResponse driverResponse = new DriverResponse( id, firstName, lastName, fullName, cin, phone, address, hireDate, salary, licenseNumber, licenseCategory, licenseExpiry, active );

        return driverResponse;
    }

    @Override
    public Driver toEntity(DriverRequest request) {
        if ( request == null ) {
            return null;
        }

        Driver.DriverBuilder driver = Driver.builder();

        driver.firstName( request.firstName() );
        driver.lastName( request.lastName() );
        driver.cin( request.cin() );
        driver.phone( request.phone() );
        driver.address( request.address() );
        driver.hireDate( request.hireDate() );
        driver.salary( request.salary() );
        driver.licenseNumber( request.licenseNumber() );
        driver.licenseCategory( request.licenseCategory() );
        driver.licenseExpiry( request.licenseExpiry() );

        driver.active( true );

        return driver.build();
    }

    @Override
    public void updateEntity(DriverRequest request, Driver entity) {
        if ( request == null ) {
            return;
        }

        entity.setFirstName( request.firstName() );
        entity.setLastName( request.lastName() );
        entity.setCin( request.cin() );
        entity.setPhone( request.phone() );
        entity.setAddress( request.address() );
        entity.setHireDate( request.hireDate() );
        entity.setSalary( request.salary() );
        entity.setLicenseNumber( request.licenseNumber() );
        entity.setLicenseCategory( request.licenseCategory() );
        entity.setLicenseExpiry( request.licenseExpiry() );
    }
}
