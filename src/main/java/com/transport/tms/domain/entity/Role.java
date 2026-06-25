package com.transport.tms.domain.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity @Table(name = "role")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Role {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @Column(nullable = false, unique = true) private String code;
    @Column(nullable = false) private String label;
}
