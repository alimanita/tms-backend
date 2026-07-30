package com.transport.tms.mapper.fleet;


import com.transport.tms.domain.entity.fleet.NotificationFlotte;
import com.transport.tms.dto.fleet.response.NotificationFlotteResponse;
import org.springframework.stereotype.Component;

@Component
public class NotificationFlotteMapper {

    public NotificationFlotteResponse toResponse(NotificationFlotte notification) {
        return new NotificationFlotteResponse(
                notification.getId(),
                notification.getType(),
                notification.getSeverity(),
                notification.getEntityType(),
                notification.getEntityId(),
                notification.getEntityRef(),
                notification.getTitle(),
                notification.getMessage(),
                notification.getDueDate(),
                notification.getIsRead(),
                notification.getIsDismissed(),
                notification.getReadAt(),
                notification.getCreatedAt()
        );
    }
}