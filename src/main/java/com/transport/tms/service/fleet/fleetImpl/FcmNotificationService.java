package com.transport.tms.service.fleet.fleetImpl;

import com.google.firebase.messaging.*;
import com.transport.tms.domain.entity.fleet.FcmToken;
import com.transport.tms.repository.fleet.FcmTokenRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class FcmNotificationService {

    private final FcmTokenRepository fcmTokenRepository;

    public void envoyerNotificationUtilisateur(Long userId, String titre, String message, String data) {
        List<FcmToken> tokens = fcmTokenRepository.findByUserId(userId);
        for (FcmToken fcmToken : tokens) {
            envoyer(fcmToken.getToken(), titre, message, data);
        }
    }

    public void envoyerNotificationTousUtilisateurs(List<Long> userIds, String titre, String message, String data) {
        for (Long userId : userIds) {
            envoyerNotificationUtilisateur(userId, titre, message, data);
        }
    }

    private void envoyer(String token, String titre, String message, String data) {
        try {
            Message fcmMessage = Message.builder()
                    .setToken(token)
                    .setNotification(Notification.builder()
                            .setTitle(titre)
                            .setBody(message)
                            .build())
                    .putData("data", data != null ? data : "")
                    .setWebpushConfig(WebpushConfig.builder()
                            .setNotification(WebpushNotification.builder()
                                    .setTitle(titre)
                                    .setBody(message)
                                    .setIcon("/assets/icons/notification-icon.png")
                                    .build())
                            .build())
                    .build();

            String response = FirebaseMessaging.getInstance().send(fcmMessage);
            log.info("Notification FCM envoyée : {}", response);

        } catch (FirebaseMessagingException e) {
            if (e.getMessagingErrorCode() == MessagingErrorCode.UNREGISTERED
                    || e.getMessagingErrorCode() == MessagingErrorCode.INVALID_ARGUMENT) {
                log.warn("Token FCM invalide/expiré, suppression : {}", token);
                fcmTokenRepository.deleteByToken(token);
            } else {
                log.error("Erreur envoi notification FCM", e);
            }
        }
    }
}