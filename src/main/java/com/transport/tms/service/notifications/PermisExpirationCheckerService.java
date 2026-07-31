package com.transport.tms.service.notifications;

import com.transport.tms.domain.entity.fleet.Chauffeur;
import com.transport.tms.domain.entity.fleet.NotificationFlotte;
import com.transport.tms.repository.UtilisateurRepository;
import com.transport.tms.repository.fleet.ChauffeurRepository;
import com.transport.tms.repository.fleet.NotificationFlotteRepository;
import com.transport.tms.service.fleet.fleetImpl.NotificationFlotteServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class PermisExpirationCheckerService {

    private final ChauffeurRepository chauffeurRepository;
    private final NotificationFlotteServiceImpl notificationService;
    private final UtilisateurRepository utilisateurRepository;
    private final NotificationFlotteRepository notificationRepository;
    //@Scheduled(cron = "0 0 7 * * *")

    @Scheduled(cron = "0 */2 * * * *")
    @Transactional
    public void verifierExpirationsPermis() {
        LocalDate today = LocalDate.now();
        LocalDate dansQuinzeJours = today.plusDays(15);

        List<Chauffeur> chauffeursConcernes = chauffeurRepository
                .findPermisExpirantAvant(today, dansQuinzeJours);

        if (chauffeursConcernes.isEmpty()) {
            log.info("Aucun permis n'expire dans les 15 prochains jours");
            return;
        }

        List<Long> adminIds = utilisateurRepository.findIdsByRoleIn(List.of("ADMIN", "SUPER_ADMIN"));
        int notificationsCreees = 0;

        for (Chauffeur chauffeur : chauffeursConcernes) {

            boolean dejaNotifie = !notificationRepository
                    .findByEntityTypeAndEntityIdAndIsDismissedFalse(
                            NotificationFlotte.TypeEntite.DRIVER,
                            chauffeur.getId()
                    ).isEmpty();

            if (dejaNotifie) {
                continue;
            }

            List<Long> destinataires = new ArrayList<>(adminIds);
            if (chauffeur.getUtilisateur() != null) {
                destinataires.add(Long.valueOf(chauffeur.getUtilisateur().getId()));
            }

            notificationService.creerSysteme(
                    NotificationFlotte.TypeNotification.LICENSE_EXPIRING,
                    NotificationFlotte.Severite.WARNING,
                    NotificationFlotte.TypeEntite.DRIVER,
                    chauffeur.getId(),
                    chauffeur.getCin(),
                    "Permis bientôt expiré",
                    String.format("Le permis de %s %s expire le %s",
                            chauffeur.getPrenom(), chauffeur.getNom(),
                            chauffeur.getDateExpirationPermis()),
                    chauffeur.getDateExpirationPermis(),
                    destinataires
            );
            notificationsCreees++;
        }

        log.info("{} notification(s) créée(s) sur {} chauffeur(s) concerné(s)",
                notificationsCreees, chauffeursConcernes.size());
    }
}

