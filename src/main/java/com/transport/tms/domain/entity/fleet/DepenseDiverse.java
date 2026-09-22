package com.transport.tms.domain.entity.fleet;

import com.transport.tms.domain.enums.CategorieDepense;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "depense_diverse", indexes = {
    @Index(name = "idx_depense_diverse_date", columnList = "date_depense DESC"),
    @Index(name = "idx_depense_diverse_driver_id", columnList = "driver_id"),
    @Index(name = "idx_depense_diverse_vehicle_id", columnList = "vehicle_id"),
    @Index(name = "idx_depense_diverse_categorie", columnList = "categorie")
})
@EntityListeners(AuditingEntityListener.class)
@Getter @Setter @NoArgsConstructor
public class DepenseDiverse {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false, length = 50)
    private String reference;

    /** Chauffeur obligatoire : la dépense est liée au chauffeur, pas à la mission */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "driver_id", nullable = false)
    private Chauffeur chauffeur;

    /** Véhicule optionnel */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vehicle_id")
    private Vehicule vehicule;

    @Column(name = "date_depense", nullable = false)
    private LocalDateTime dateDepense = LocalDateTime.now();

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private CategorieDepense categorie = CategorieDepense.AUTRE;

    @Column(length = 255)
    private String description;

    @Column(name = "amount_ttc", nullable = false, precision = 12, scale = 3)
    private BigDecimal amountTTC;

    @Column(name = "receipt_number", length = 100)
    private String receiptNumber;

    @Column(columnDefinition = "TEXT")
    private String notes;

    @Column(name = "proof_file_path", length = 255)
    private String proofFilePath;

    @CreatedBy
    @Column(name = "created_by", updatable = false, nullable = false)
    private Long createdBy;

    @CreatedDate
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
}
