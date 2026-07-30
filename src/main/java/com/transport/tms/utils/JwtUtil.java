package com.transport.tms.utils;

import com.transport.tms.security.UserPrincipal;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class JwtUtil {

  // Clé secrète pour signer les tokens (à mettre dans application.properties)
  @Value("${jwt.secret:mySecretKeyForJWTTokenGenerationThatIsVeryLongAndSecure123456789}")
  private String SECRET_KEY;

  // Durée de validité du token (24 heures par défaut)
  @Value("${jwt.expiration:86400000}")
  private long JWT_TOKEN_VALIDITY;

  // Durée de validité du refresh token (7 jours par défaut)
  @Value("${jwt.refresh-expiration:604800000}")
  private long JWT_REFRESH_TOKEN_VALIDITY;

  /**
   * Extraire l'username (email) du token
   */
  public String extractUsername(String token) {
    return extractClaim(token, Claims::getSubject);
  }

  /**
   * Extraire la date d'expiration du token
   */
  public Date extractExpiration(String token) {
    return extractClaim(token, Claims::getExpiration);
  }

  /**
   * Extraire un claim spécifique du token
   */
  public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
    final Claims claims = extractAllClaims(token);
    return claimsResolver.apply(claims);
  }

  /**
   * Extraire tous les claims du token
   */
  private Claims extractAllClaims(String token) {
    return Jwts.parser()
            .verifyWith(getSigningKey())
            .build()
            .parseSignedClaims(token)
            .getPayload();
  }

  /**
   * Vérifier si le token est expiré
   */
  private Boolean isTokenExpired(String token) {
    return extractExpiration(token).before(new Date());
  }

  /**
   * Générer un token JWT
   */
  public String generateToken(UserDetails userDetails) {
    Map<String, Object> claims = new HashMap<>();

    List<String> roles = userDetails.getAuthorities().stream()
            .map(GrantedAuthority::getAuthority)
            .collect(Collectors.toList());
    claims.put("roles", roles);
    claims.put("authorities", roles);

    if (userDetails instanceof UserPrincipal userPrincipal) {
      claims.put("userId", userPrincipal.getId());
    }
    return createToken(claims, userDetails.getUsername(), JWT_TOKEN_VALIDITY);
  }


  /**
   * Générer un refresh token
   */
  public String generateRefreshToken(UserDetails userDetails) {
    Map<String, Object> claims = new HashMap<>();
    claims.put("type", "refresh");
    return createToken(claims, userDetails.getUsername(), JWT_REFRESH_TOKEN_VALIDITY);
  }

  /**
   * Créer un token avec les claims, subject et durée de validité
   */
  private String createToken(Map<String, Object> claims, String subject, long validity) {
    return Jwts.builder()
            .claims(claims)
            .subject(subject)
            .issuedAt(new Date(System.currentTimeMillis()))
            .expiration(new Date(System.currentTimeMillis() + validity))
            .signWith(getSigningKey())
            .compact();
  }

  /**
   * Valider le token
   */
  public Boolean validateToken(String token, UserDetails userDetails) {
    final String username = extractUsername(token);
    return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
  }

  public Integer extractUserId(String token) {
    return extractClaim(token, claims -> claims.get("userId", Integer.class));
  }

  public Integer extractEntrepriseId(String token) {
    return extractClaim(token, claims -> claims.get("entrepriseId", Integer.class));
  }

  /**
   * Obtenir la clé de signature
   */
  private SecretKey getSigningKey() {
    byte[] keyBytes = SECRET_KEY.getBytes();
    return Keys.hmacShaKeyFor(keyBytes);
  }
}

