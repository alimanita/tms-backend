package com.transport.tms.service.fleet;



import com.transport.tms.domain.entity.fleet.OrdreTravail;
import com.transport.tms.dto.fleet.rapport.*;

import java.time.LocalDate;
import java.util.List;

public interface RapportEntretiensService {

    /** Coûts d'entretien agrégés par mois (main d'œuvre + pièces) */
    List<MaintenanceMensuelleDto> getRapportMensuel(
            OrdreTravail.TypeEntite entityType,
            LocalDate debut,
            LocalDate fin);

    /** Coûts d'entretien agrégés par année */
    List<MaintenanceAnnuelleDto> getRapportAnnuel(
            OrdreTravail.TypeEntite entityType,
            int anDebut,
            int anFin);

    /** Liste détaillée des OT terminés sur la période */
    List<MaintenanceDetailDto> getRapportDetail(
            OrdreTravail.TypeEntite entityType,
            LocalDate debut,
            LocalDate fin);

    /** Synthèse globale (totaux maintenance + carburant) */
    SyntheseEntretiensDto getSynthese(
            OrdreTravail.TypeEntite entityType,
            LocalDate debut,
            LocalDate fin);

    /** Coût carburant agrégé par mois */
    List<CarburantMensuelDto> getCarburantMensuel(
            Long vehiculeId,
            LocalDate debut,
            LocalDate fin);

    /** Coût carburant agrégé par année */
    List<CarburantAnnuelDto> getCarburantAnnuel(
            Long vehiculeId,
            int anDebut,
            int anFin);
}
