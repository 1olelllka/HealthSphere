package com._olelllka.HealthSphere_Backend.configuration;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class WebSecurityConfig {

    final AuthenticationProvider provider;
    final JwtAuthFilter jwtAuthFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.
                csrf(AbstractHttpConfigurer::disable)
//                csrf(csrf -> csrf.csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse()))
                .authorizeHttpRequests((authorize) -> {
                    authorize
                            .requestMatchers(HttpMethod.POST, "/api/v1/login", "/api/v1/register/patient")
                            .permitAll()
                            .requestMatchers(HttpMethod.POST, "/api/v1/register/doctor").hasRole("ADMIN")
                            .requestMatchers(HttpMethod.GET, "/api/v1/patient/me").hasRole("PATIENT")
                            .requestMatchers(HttpMethod.PATCH, "/api/v1/patient/*").hasRole("PATIENT")
                            .requestMatchers(HttpMethod.DELETE, "/api/v1/patient/*").hasRole("PATIENT")
                            .requestMatchers(HttpMethod.POST, "/api/v1/patient/medical-records", "/api/v1/prescriptions", "/api/v1/prescriptions/**").hasRole("DOCTOR")
                            .requestMatchers(HttpMethod.DELETE, "/api/v1/doctors/me", "/api/v1/patient/medical-records/*", "/api/v1/prescriptions/**").hasRole("DOCTOR")
                            .requestMatchers(HttpMethod.PATCH, "/api/v1/doctors/me", "/api/v1/patient/medical-records/*", "/api/v1/prescriptions/**").hasRole("DOCTOR")
                            .requestMatchers(HttpMethod.GET, "/api/v1/doctors/me").hasRole("DOCTOR")
                            .requestMatchers(HttpMethod.GET, "/api/v1/get-jwt", "/actuator/health/**", "/api/v1/doctors", "/api/v1/doctors/**").permitAll()
                            .anyRequest()
                            .authenticated();
                })
                .sessionManagement((session) -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authenticationProvider(provider)
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
                .logout((logout) -> {
                    logout
                            .logoutUrl("/api/v1/logout")
                            .deleteCookies("JSESSIONID", "accessToken")
                            .invalidateHttpSession(true)
                            .clearAuthentication(true)
                            .logoutSuccessHandler(((request, response, authentication) -> {
                                response.setStatus(HttpServletResponse.SC_OK);
                            }));
                });

        return http.build();
    }
}
