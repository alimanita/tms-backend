package com.transport.tms.mapper;

import com.transport.tms.domain.entity.FinancialEntry;
import com.transport.tms.dto.request.FinancialEntryRequest;
import com.transport.tms.dto.response.FinancialEntryResponse;
import java.math.BigDecimal;
import java.time.LocalDate;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-06-25T09:51:18+0100",
    comments = "version: 1.6.3, compiler: javac, environment: Java 21.0.9 (Oracle Corporation)"
)
@Component
public class FinancialEntryMapperImpl implements FinancialEntryMapper {

    @Override
    public FinancialEntryResponse toResponse(FinancialEntry entity) {
        if ( entity == null ) {
            return null;
        }

        Long id = null;
        LocalDate entryDate = null;
        String entryType = null;
        String category = null;
        BigDecimal amount = null;
        String description = null;

        id = entity.getId();
        entryDate = entity.getEntryDate();
        entryType = entity.getEntryType();
        category = entity.getCategory();
        amount = entity.getAmount();
        description = entity.getDescription();

        FinancialEntryResponse financialEntryResponse = new FinancialEntryResponse( id, entryDate, entryType, category, amount, description );

        return financialEntryResponse;
    }

    @Override
    public FinancialEntry toEntity(FinancialEntryRequest request) {
        if ( request == null ) {
            return null;
        }

        FinancialEntry.FinancialEntryBuilder financialEntry = FinancialEntry.builder();

        financialEntry.entryDate( request.entryDate() );
        financialEntry.entryType( request.entryType() );
        financialEntry.category( request.category() );
        financialEntry.amount( request.amount() );
        financialEntry.description( request.description() );

        return financialEntry.build();
    }

    @Override
    public void updateEntity(FinancialEntryRequest request, FinancialEntry entity) {
        if ( request == null ) {
            return;
        }

        entity.setEntryDate( request.entryDate() );
        entity.setEntryType( request.entryType() );
        entity.setCategory( request.category() );
        entity.setAmount( request.amount() );
        entity.setDescription( request.description() );
    }
}
