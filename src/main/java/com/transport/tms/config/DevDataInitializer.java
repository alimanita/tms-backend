package com.transport.tms.config;

import com.transport.tms.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@RequiredArgsConstructor
public class DevDataInitializer {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Bean
    CommandLineRunner initAdminPassword() {
        return args -> {
            userRepository.findByUsernameAndActiveTrue("admin").ifPresent(user -> {
                user.setPassword(passwordEncoder.encode("admin123"));
                userRepository.save(user);
            });
            userRepository.findByUsername("ali.bensalah").ifPresent(user -> {
                user.setPassword(passwordEncoder.encode("driver123"));
                userRepository.save(user);
            });
        };
    }
}
