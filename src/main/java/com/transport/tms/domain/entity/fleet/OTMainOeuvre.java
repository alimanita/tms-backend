package com.transport.tms.domain.entity.fleet;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "maintenance_labor")
@Getter @Setter @NoArgsConstructor
public class OTMainOeuvre {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "maintenance_order_id", nullable = false)
    private OrdreTravail ordreTravail;

    @Column(name = "technician_name", nullable = false, length = 200)
    private String technicianName;

    @Column(name = "is_external")
    private Boolean isExternal = false;

    @Column(name = "hours_planned", precision = 8, scale = 2)
    private BigDecimal hoursPlanned;

    @Column(name = "hours_actual", precision = 8, scale = 2)
    private BigDecimal hoursActual;

    @Column(name = "hourly_rate", precision = 10, scale = 2)
    private BigDecimal hourlyRate;

    public BigDecimal getTotalCost() {
        BigDecimal hours = hoursActual != null ? hoursActual : hoursPlanned;
        if (hours == null || hourlyRate == null) return BigDecimal.ZERO;
        return hours.multiply(hourlyRate);
    }
}