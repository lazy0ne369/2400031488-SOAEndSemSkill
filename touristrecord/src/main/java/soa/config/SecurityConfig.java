package soa.config;

import java.util.List;

import javax.crypto.SecretKey;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;

import soa.service.JWTService;

@Configuration
public class SecurityConfig {

    private final JWTService jwtService;

    public SecurityConfig(JWTService jwtService) {
        this.jwtService = jwtService;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
            .csrf(csrf -> csrf.disable())

            .sessionManagement(session ->
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )

            .authorizeHttpRequests(auth -> auth

                // LOGIN and REGISTER are PUBLIC
                .requestMatchers("/api/auth/**").permitAll()

                .requestMatchers(HttpMethod.GET, "/api/tourist-records", "/api/tourist-records/**")
                .hasAnyRole("ADMIN", "USER")
                .requestMatchers(HttpMethod.POST, "/api/tourist-records")
                .hasAnyRole("ADMIN", "USER")
                .requestMatchers(HttpMethod.PUT, "/api/tourist-records/**")
                .hasAnyRole("ADMIN", "USER")
                .requestMatchers(HttpMethod.DELETE, "/api/tourist-records/**").hasRole("ADMIN")

                // Users management - ADMIN only
                .requestMatchers("/api/users", "/api/users/**")
                .hasRole("ADMIN")

                // Everything else requires authentication
                .anyRequest().authenticated()
            )

            .oauth2ResourceServer(oauth2 ->
                oauth2.jwt(jwt ->
                    jwt.jwtAuthenticationConverter(jwtAuthenticationConverter())
                )
            );

        return http.build();
    }

    @Bean
    public JwtDecoder jwtDecoder() {

        SecretKey key = jwtService.getKey();

        return NimbusJwtDecoder
                .withSecretKey(key)
                .macAlgorithm(MacAlgorithm.HS384)
                .build();
    }

    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter() {

        JwtAuthenticationConverter converter =
                new JwtAuthenticationConverter();

        converter.setJwtGrantedAuthoritiesConverter(jwt -> {

            String role = jwt.getClaimAsString("role");

            if (role == null || role.isEmpty()) {
                return List.of();
            }

            String roleName = role.startsWith("ROLE_") ? role : "ROLE_" + role;

            return List.of(new SimpleGrantedAuthority(roleName));
        });

        return converter;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
