package com.transport.tms.domain.entity.fleet;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "maintenance_spare_part")
@Getter @Setter @NoArgsConstructor
public class OTPieceRechange {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "maintenance_order_id", nullable = false)
    private OrdreTravail ordreTravail;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "spare_part_id", nullable = false)
    private PieceRechange pieceRechange;

    @Column(name = "quantity_planned", nullable = false, precision = 10, scale = 2)
    private BigDecimal quantityPlanned;

    @Column(name = "quantity_used", precision = 10, scale = 2)
    private BigDecimal quantityUsed;

    @Column(name = "unit_cost", nullable = false, precision = 15, scale = 2)
    private BigDecimal unitCost;

    public BigDecimal getTotalCost() {
        BigDecimal qty = quantityUsed != null ? quantityUsed : quantityPlanned;
        return qty.multiply(unitCost);
    }
}