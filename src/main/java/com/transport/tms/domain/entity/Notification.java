package com.transport.tms.domain.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.Instant;

@Entity @Table(name = "notification")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Notification {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    private String type;
    private String severity;
    private String title;
    private String message;
    @Column(name = "entity_type") private String entityType;
    @Column(name = "entity_id") private Long entityId;
    @Column(name = "read_flag") private boolean readFlag;
    private String channel;
    @Builder.Default
    @Column(name = "created_at")
    private Instant createdAt = Instant.now();
}
