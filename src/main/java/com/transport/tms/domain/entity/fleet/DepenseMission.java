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
@Table(name = "mission_expense")
@EntityListeners(AuditingEntityListener.class)
@Getter @Setter @NoArgsConstructor
public class DepenseMission {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mission_id", nullable = false)
    private Mission mission;

    @Column(name = "expense_type", nullable = false, length = 50)
    @Enumerated(EnumType.STRING)
    private TypeDepense expenseType;

    @Column(name = "montant", nullable = false, precision = 15, scale = 2)
    private BigDecimal montant;

    @Column(length = 3)
    private String currency = "TND";

    @Column(name = "expense_date", nullable = false)
    private LocalDateTime expenseDate;

    @Column(length = 300)
    private String description;

    @Column(name = "receipt_path", length = 500)
    private String receiptPath;

    @Column(name = "is_reimbursable")
    private Boolean isReimbursable = true;

    @CreatedBy
    @Column(name = "created_by", updatable = false, nullable = false)
    private Long createdBy;

    @CreatedDate
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    public enum TypeDepense { FUEL, TOLL, MEAL, LODGING, REPAIR, OTHER }
}