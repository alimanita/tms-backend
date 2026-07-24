package com.transport.tms.mapper;

import com.transport.tms.domain.entity.Driver;
import com.transport.tms.dto.request.ChauffeurRequest;
import com.transport.tms.dto.response.ChauffeurResponse;
import org.springframework.stereotype.Component;

@Component
public class ChauffeurMapper {

    public ChauffeurResponse toResponse(Driver driver) {
        return toResponse(driver, null, null);
    }

    public ChauffeurResponse toResponse(Driver driver, Long idUtilisateur, String utilisateurEmail) {
        return new ChauffeurResponse(
                driver.getId(),
                driver.getLastName(),       // nom
                driver.getFirstName(),      // prenom
                driver.getCin(),
                driver.getPhone(),          // telephone
                driver.getEmail(),
                driver.getAddress(),        // adresse
                driver.getHireDate() != null ? driver.getHireDate().toString() : null,
                driver.getStatut() != null ? driver.getStatut() : "DISPONIBLE",
                driver.getLicenseNumber(),   // numeroPermis
                driver.getLicenseCategory(), // categoriesPermis
                null,                        // dateDelivrancePermis (not in entity)
                driver.getLicenseExpiry() != null ? driver.getLicenseExpiry().toString() : null,
                null,                        // dateExpirationVisiteMedicale
                null,                        // totalKilometres
                null,                        // nombreIncidents
                null,                        // notes
                driver.isActive(),           // actif
                driver.getCreatedAt() != null ? driver.getCreatedAt().toString() : null,
                null,                        // updatedAt
                idUtilisateur,
                utilisateurEmail
        );
    }

    public Driver toEntity(ChauffeurRequest request) {
        return Driver.builder()
                .lastName(request.nom())
                .firstName(request.prenom())
                .phone(request.telephone())
                .email(request.email())
                .licenseNumber(request.numeroPermis())
                .licenseExpiry(request.dateExpirationPermis())
                .statut(request.statut() != null ? request.statut() : "DISPONIBLE")
                .active(request.actif() != null ? request.actif() : true)
                .build();
    }

    public void updateEntity(ChauffeurRequest request, Driver driver) {
        driver.setLastName(request.nom());
        driver.setFirstName(request.prenom());
        driver.setPhone(request.telephone());
        driver.setEmail(request.email());
        driver.setLicenseNumber(request.numeroPermis());
        driver.setLicenseExpiry(request.dateExpirationPermis());
        if (request.statut() != null) {
            driver.setStatut(request.statut());
        }
        if (request.actif() != null) {
            driver.setActive(request.actif());
        }
    }
}
