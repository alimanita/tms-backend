package com.transport.tms.domain.entity.fleet;

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
@Table(name = "peage")
@EntityListeners(AuditingEntityListener.class)
@Getter @Setter @NoArgsConstructor
public class Peage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false, length = 50)
    private String reference;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vehicle_id", nullable = false)
    private Vehicule vehicule;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "driver_id")
    private Chauffeur chauffeur;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mission_id")
    private Mission mission;

    @Column(name = "date_passage", nullable = false)
    private LocalDateTime datePassage = LocalDateTime.now();

    @Column(name = "amount_ht", precision = 12, scale = 3)
    private BigDecimal amountHT;

    @Column(name = "tva_rate", precision = 5, scale = 2)
    private BigDecimal tvaRate;

    @Column(name = "tva_amount", precision = 12, scale = 3)
    private BigDecimal tvaAmount;

    @Column(name = "amount_ttc", nullable = false, precision = 12, scale = 3)
    private BigDecimal amountTTC;

    @Column(name = "gare_entree", length = 150)
    private String gareEntree;

    @Column(name = "gare_sortie", length = 150)
    private String gareSortie;

    @Column(name = "receipt_number", length = 100)
    private String receiptNumber;

    @Column(name = "societe_autoroute", length = 100)
    private String societeAutoroute;

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
