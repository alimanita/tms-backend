package com.transport.tms.mapper;

import com.transport.tms.domain.entity.Notification;
import com.transport.tms.dto.request.NotificationRequest;
import com.transport.tms.dto.response.NotificationResponse;
import org.mapstruct.*;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface NotificationMapper {
    NotificationResponse toResponse(Notification entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "readFlag", constant = "false")
    @Mapping(target = "createdAt", ignore = true)
    Notification toEntity(NotificationRequest request);
}
