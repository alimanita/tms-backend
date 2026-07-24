package com.transport.tms.domain.entity;

import com.transport.tms.domain.enums.MissionExpenseType;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.Instant;

@Entity @Table(name = "mission_expense")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class MissionExpense {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "mission_id", nullable = false) private TransportMission mission;
    @Enumerated(EnumType.STRING) @Column(name = "expense_type", nullable = false) private MissionExpenseType expenseType;
    @Column(nullable = false) private BigDecimal amount;
    @Builder.Default private String currency = "EUR";
    @Column(name = "expense_date", nullable = false) private Instant expenseDate;
    private String description;
    @Builder.Default private boolean reimbursable = true;
    @Builder.Default @Column(name = "created_at", updatable = false) private Instant createdAt = Instant.now();
}
