package com.transport.tms.domain.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.Instant;

@Entity @Table(name = "customer")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Customer {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @Column(nullable = false) private String name;
    private String company;
    private String phone;
    private String email;
    private String address;
    private String city;
    private String country;
    private String nif;
    @Column(name = "tax_id") private String taxId;
    @Builder.Default
    private boolean active = true;
    @Builder.Default
    @Column(name = "created_at", updatable = false)
    private Instant createdAt = Instant.now();

}
