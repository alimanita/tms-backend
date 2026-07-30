package com.transport.tms.service.fleet.fleetImpl;

import com.transport.tms.domain.entity.fleet.NotificationFlotte;
import com.transport.tms.dto.fleet.response.NotificationFlotteResponse;
import com.transport.tms.mapper.fleet.NotificationFlotteMapper;
import com.transport.tms.repository.fleet.NotificationFlotteRepository;
import com.transport.tms.service.fleet.NotificationFlotteService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@Slf4j
@Transactional
@RequiredArgsConstructor
public class NotificationFlotteServiceImpl implements NotificationFlotteService {

    private final NotificationFlotteRepository notificationRepository;
    private final NotificationFlotteMapper mapper;
    private final FcmNotificationService fcmNotificationService;
    @Override
    @Transactional(readOnly = true)
    public Page<NotificationFlotteResponse> findAll(Pageable pageable) {
        return notificationRepository
                .findByIsDismissedFalseOrderByCreatedAtDesc(pageable)
                .map(mapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public List<NotificationFlotteResponse> findNonLues() {
        return notificationRepository
                .findByIsReadFalseAndIsDismissedFalseOrderByCreatedAtDesc()
                .stream().map(mapper::toResponse).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<NotificationFlotteResponse> findCritiques() {
        return notificationRepository
                .findBySeverityAndIsReadFalseAndIsDismissedFalse(NotificationFlotte.Severite.CRITICAL)
                .stream().map(mapper::toResponse).toList();
    }

    @Override
    public NotificationFlotteResponse marquerLue(Long id) {
        NotificationFlotte notif = findEntityById(id);
        notif.marquerLue(null); // TODO: passer userId depuis SecurityContext
        return mapper.toResponse(notificationRepository.save(notif));
    }

    @Override
    public void marquerToutesLues() {
        int count = notificationRepository.marquerToutesLues();
        log.info("{} notifications marquées comme lues", count);
    }

    @Override
    public void ignorer(Long id) {
        NotificationFlotte notif = findEntityById(id);
        notif.setIsDismissed(true);
        notificationRepository.save(notif);
    }

    @Override
    @Transactional(readOnly = true)
    public long countNonLues() {
        return notificationRepository.countByIsReadFalseAndIsDismissedFalse();
    }

    private NotificationFlotte findEntityById(Long id) {
        return notificationRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Notification introuvable avec l'ID = " + id));
    }
    public NotificationFlotte creerSysteme(
            NotificationFlotte.TypeNotification type,
            NotificationFlotte.Severite severity,
            NotificationFlotte.TypeEntite entityType,
            Long entityId,
            String entityRef,
            String title,
            String message,
            LocalDate dueDate,
            List<Long> destinataireUserIds // ajouté : qui doit recevoir le push
    ) {
        NotificationFlotte notification = new NotificationFlotte();
        notification.setType(type);
        notification.setSeverity(severity);
        notification.setEntityType(entityType);
        notification.setEntityId(entityId);
        notification.setEntityRef(entityRef);
        notification.setTitle(title);
        notification.setMessage(message);
        notification.setDueDate(dueDate);

        NotificationFlotte saved = notificationRepository.save(notification);

        if (destinataireUserIds != null && !destinataireUserIds.isEmpty()) {
            fcmNotificationService.envoyerNotificationTousUtilisateurs(
                    destinataireUserIds,
                    title,
                    message,
                    "{\"notificationId\":\"" + saved.getId() + "\",\"entityType\":\"" + entityType + "\"}"
            );
            saved.setFcmSent(true);
            notificationRepository.save(saved);
        }

        return saved;
    }
}