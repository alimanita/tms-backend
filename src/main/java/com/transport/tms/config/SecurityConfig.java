package com.transport.tms.config;

import com.transport.tms.security.JwtAuthFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthFilter jwtAuthFilter;
    private final UserDetailsService userDetailsService;

    @Value("${tms.cors.allowed-origins}")
    private String allowedOrigins;

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(csrf -> csrf.disable())
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/api/v1/auth/**",
                                "/actuator/health",
                                "/swagger-ui/**",
                                "/swagger-ui.html",
                                "/api/v1/entreprises/register",
                                "/api/v1/statistics/role-dashboard",
                                "/api-docs/**",
                                "/v3/api-docs/**"
                        ).permitAll()
                        .requestMatchers("/api/v1/utilisateurs/**", "/api/v1/roles/**")
                        .hasAnyRole("SUPER_ADMIN", "ADMIN")
                        .requestMatchers("/api/v1/financial-entries/**", "/api/v1/finance/**")
                        .hasAnyRole("SUPER_ADMIN", "MANAGER", "ACCOUNTANT")
                        .requestMatchers("/api/v1/missions/my/**", "/api/v1/fuel-records/my/**")
                        .hasAnyRole("CHAUFFEUR", "DRIVER", "SUPER_ADMIN", "MANAGER")
                        .requestMatchers("/api/v1/fleet/chauffeurs/me")
                        .hasAnyRole("CHAUFFEUR", "DRIVER", "SUPER_ADMIN", "MANAGER")
                        .requestMatchers("/api/v1/dashboard", "/api/v1/accountant/**")
                        .hasAnyRole("SUPER_ADMIN", "MANAGER", "ACCOUNTANT","COMPTABLE", "CHAUFFEUR")
                        .requestMatchers("/api/v1/fleet/missions/**")
                        .hasAnyRole("SUPER_ADMIN", "MANAGER", "CHAUFFEUR")
                        .requestMatchers("/api/v1/fleet/vehicules/**")
                        .hasAnyRole("SUPER_ADMIN", "MANAGER", "CHAUFFEUR")
                        .requestMatchers("/api/v1/fleet/pleins-carburant/**")
                        .hasAnyRole("SUPER_ADMIN", "MANAGER", "CHAUFFEUR", "COMPTABLE")
                        .requestMatchers("/api/v1/fleet/rapports/**")
                        .hasAnyRole("SUPER_ADMIN")
                        .requestMatchers(
                                "/api/v1/fleet/documents/**",
                                "/api/v1/fleet/machines/**",
                                "/api/v1/fleet/chauffeurs/**",
                                "/api/v1/fleet/changements-huile/**",
                                "/api/v1/fleet/ordres-travail/**",
                                "api/v1/fleet/pieces-rechange/**",
                                "/api/v1/fleet/notifications/**",
                                "/api/v1/fleet/pneus/**"
                        )
                        .hasAnyRole("SUPER_ADMIN", "MANAGER", "CHAUFFEUR", "COMPTABLE")
                        .requestMatchers("/api/v1/**")
                        .hasAnyRole("SUPER_ADMIN", "MANAGER", "ACCOUNTANT", "COMPTABLE")
                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

/*
    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(Arrays.asList(allowedOrigins.split(",")));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
*/


    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOriginPatterns(List.of("*"));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    AuthenticationManager authenticationManager() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        return new ProviderManager(provider);
    }
}
