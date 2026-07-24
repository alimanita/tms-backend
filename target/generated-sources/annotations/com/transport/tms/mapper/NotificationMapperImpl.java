package com.transport.tms.mapper;

import com.transport.tms.domain.entity.Notification;
import com.transport.tms.dto.request.NotificationRequest;
import com.transport.tms.dto.response.NotificationResponse;
import java.time.Instant;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-07-24T10:28:44+0100",
    comments = "version: 1.6.3, compiler: javac, environment: Java 21.0.9 (Oracle Corporation)"
)
@Component
public class NotificationMapperImpl implements NotificationMapper {

    @Override
    public NotificationResponse toResponse(Notification entity) {
        if ( entity == null ) {
            return null;
        }

        Long id = null;
        String type = null;
        String severity = null;
        String title = null;
        String message = null;
        String entityType = null;
        Long entityId = null;
        boolean readFlag = false;
        String channel = null;
        Instant createdAt = null;

        id = entity.getId();
        type = entity.getType();
        severity = entity.getSeverity();
        title = entity.getTitle();
        message = entity.getMessage();
        entityType = entity.getEntityType();
        entityId = entity.getEntityId();
        readFlag = entity.isReadFlag();
        channel = entity.getChannel();
        createdAt = entity.getCreatedAt();

        NotificationResponse notificationResponse = new NotificationResponse( id, type, severity, title, message, entityType, entityId, readFlag, channel, createdAt );

        return notificationResponse;
    }

    @Override
    public Notification toEntity(NotificationRequest request) {
        if ( request == null ) {
            return null;
        }

        Notification.NotificationBuilder notification = Notification.builder();

        notification.type( request.type() );
        notification.severity( request.severity() );
        notification.title( request.title() );
        notification.message( request.message() );
        notification.entityType( request.entityType() );
        notification.entityId( request.entityId() );
        notification.channel( request.channel() );

        notification.readFlag( false );

        return notification.build();
    }
}
