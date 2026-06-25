package com.transport.tms.service;

import com.transport.tms.domain.entity.Notification;
import com.transport.tms.dto.request.NotificationRequest;
import com.transport.tms.dto.response.NotificationResponse;
import com.transport.tms.dto.response.PageResponse;
import com.transport.tms.exception.ResourceNotFoundException;
import com.transport.tms.mapper.NotificationMapper;
import com.transport.tms.repository.NotificationRepository;
import com.transport.tms.util.PageMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final NotificationMapper notificationMapper;

    @Transactional(readOnly = true)
    public PageResponse<NotificationResponse> list(int page, int size) {
        return PageMapper.map(
                notificationRepository.findAllByOrderByCreatedAtDesc(PageRequest.of(page, size)),
                notificationMapper::toResponse
        );
    }

    @Transactional(readOnly = true)
    public NotificationResponse getById(Long id) {
        return notificationMapper.toResponse(find(id));
    }

    @Transactional
    public NotificationResponse create(NotificationRequest request) {
        Notification notification = notificationMapper.toEntity(request);
        if (notification.getSeverity() == null || notification.getSeverity().isBlank()) {
            notification.setSeverity("INFO");
        }
        if (notification.getChannel() == null || notification.getChannel().isBlank()) {
            notification.setChannel("IN_APP");
        }
        return notificationMapper.toResponse(notificationRepository.save(notification));
    }

    @Transactional
    public NotificationResponse markAsRead(Long id) {
        Notification notification = find(id);
        notification.setReadFlag(true);
        return notificationMapper.toResponse(notificationRepository.save(notification));
    }

    @Transactional
    public void delete(Long id) {
        notificationRepository.delete(find(id));
    }

    private Notification find(Long id) {
        return notificationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Notification", id));
    }
}
