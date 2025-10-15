package org.example.client_processing.config;

import org.example.client_processing.security.BlockedClientFilter;
import org.example.client_processing.security.JwtAuthenticationFilter;
import org.example.starter.security.ServiceJwtFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final BlockedClientFilter blockedClientFilter;
    private final ServiceJwtFilter serviceJwtFilter;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter, 
                         BlockedClientFilter blockedClientFilter,
                         ServiceJwtFilter serviceJwtFilter) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
        this.blockedClientFilter = blockedClientFilter;
        this.serviceJwtFilter = serviceJwtFilter;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(authz -> authz
                    .requestMatchers("/auth/**").permitAll()
                    .requestMatchers("/clients/register").permitAll()
                    .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()
                    .requestMatchers("/actuator/**").permitAll()
                    .requestMatchers("/products/**").authenticated()
                    .requestMatchers("/admin/**").authenticated()
                    .anyRequest().authenticated()
            )
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
            .addFilterAfter(serviceJwtFilter, JwtAuthenticationFilter.class)
            .addFilterAfter(blockedClientFilter, ServiceJwtFilter.class);

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
