package com.transport.tms.service.Impl;

import com.transport.tms.domain.entity.Roles;
import com.transport.tms.dto.RolesDto;
import com.transport.tms.exception.ErrorCodes;
import com.transport.tms.exception.InvalidEntityException;
import com.transport.tms.exception.InvalidOperationException;
import com.transport.tms.repository.RolesRepository;
import com.transport.tms.service.RolesService;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class RolesServiceImpl implements RolesService {

    private final RolesRepository rolesRepository;

    @Autowired
    public RolesServiceImpl(RolesRepository rolesRepository) {
        this.rolesRepository = rolesRepository;
    }

    @Override
    public RolesDto save(RolesDto dto) {
        if (dto == null || !StringUtils.hasLength(dto.getRoleName())) {
            log.error("RolesDto invalide : {}", dto);
            throw new InvalidEntityException(
                    "Le nom du rôle est obligatoire",
                    ErrorCodes.ROLE_NOT_VALID,
                    Collections.singletonList("Veuillez renseigner le nom du rôle")
            );
        }

        // ✅ Normaliser : toujours stocker en majuscules avec préfixe ROLE_
        String roleName = dto.getRoleName().trim().toUpperCase();
        dto.setRoleName(roleName);
        dto.setRoleName(roleName);

        // ✅ Vérifier l'unicité
        String finalRoleName = roleName;
        rolesRepository.findByRoleName(roleName).ifPresent(existing -> {
            throw new InvalidEntityException(
                    "Un rôle avec ce nom existe déjà : " + finalRoleName,
                    ErrorCodes.ROLES_ALREADY_EXISTS,
                    Collections.singletonList("Le nom du rôle doit être unique")
            );
        });

        Roles saved = rolesRepository.save(RolesDto.toEntity(dto));
        log.info("✅ Rôle créé : {}", saved.getRoleName());
        return RolesDto.fromEntity(saved);
    }

    @Override
    public RolesDto findById(Long id) {
        if (id == null) {
            log.error("Roles ID is null");
            return null;
        }
        return rolesRepository.findById(id)
                .map(RolesDto::fromEntity)
                .orElseThrow(() -> new EntityNotFoundException(
                ));
    }

    @Override
    public List<RolesDto> findAll() {
        return rolesRepository.findAll().stream()
                .map(RolesDto::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    public void delete(Long id) {
        if (id == null) {
            log.error("Roles ID is null");
            return;
        }
        Roles role = rolesRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                ));

        // ✅ Vérifier que le rôle n'est pas utilisé par des utilisateurs
        if (role.getUtilisateurs() != null && !role.getUtilisateurs().isEmpty()) {
            throw new InvalidOperationException(
                    "Impossible de supprimer ce rôle : il est assigné à "
                            + role.getUtilisateurs().size() + " utilisateur(s)",
                    ErrorCodes.ROLES_ALREADY_IN_USE
            );
        }

        rolesRepository.deleteById(id);
        log.info("✅ Rôle supprimé : {}", role.getRoleName());
    }
}