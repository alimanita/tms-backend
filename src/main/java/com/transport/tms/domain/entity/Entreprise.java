package com.transport.tms.domain.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
@Entity
@Table(name = "entreprise")
public class Entreprise   {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @CreatedDate
  @Column(name = "creationDate", nullable = false, updatable = false)
  private Instant creationDate;

  @LastModifiedDate
  @Column(name = "lastModifiedDate")
  private Instant lastModifiedDate;


  @Column(name = "nom")
  private String nom;

  @Column(name = "description")
  private String description;

  @Embedded
  private Adresse adresse;

  @Column(name = "codefiscal")
  private String codeFiscal;

  @Column(name = "photo")
  private String photo;

  @Column(name = "email")
  private String email;

  @Column(name = "numtel")
  private String numTel;

  @Column(name = "siteweb")
  private String steWeb;
  @Column(name = "matriculefiscal", unique = true, nullable = false)
  private String matriculeFiscal;
  @Column(name = "active")
  private Boolean active = false;

  @Column(name = "code_groupe", nullable = true)
  private String codeGroupe;
  @OneToMany(mappedBy = "entreprise", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<Utilisateur> utilisateurs = new ArrayList<>();



}
