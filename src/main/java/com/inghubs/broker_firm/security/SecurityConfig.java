package com.inghubs.broker_firm.security;

import com.inghubs.broker_firm.enums.ROLE;
import com.inghubs.broker_firm.service.CustomUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    @Autowired
    private CustomUserDetailsService customUserDetailsService;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    private UserSecurity userSecurity;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(httpConf -> httpConf
                .requestMatchers("/api/admin/**").hasAuthority(ROLE.ADMIN.toString())
                .requestMatchers(
                    "/api/assets/searchByCustomer/{userId}",
                    "/api/assets/filter",
                    "/api/assets/{userId}/**",
                    "/api/customers/{userId}/**",
                    "/api/orders/filter",
                    "/api/orders/searchByCustomer/{userId}",
                    "/api/orders/{userId}/**").access(userSecurity)
                .requestMatchers("/api/customers/**","/api/orders/**","/api/assets/**")
                    .hasAnyAuthority(ROLE.ADMIN.toString(),ROLE.CUSTOMER.toString())
            )
            .httpBasic(Customizer.withDefaults())
            .csrf(AbstractHttpConfigurer::disable);
        return http.build();
    }

    @Bean
    public AuthenticationManager authManager(HttpSecurity http) throws Exception {
        AuthenticationManagerBuilder authenticationManagerBuilder =
            http.getSharedObject(AuthenticationManagerBuilder.class);
        authenticationManagerBuilder.userDetailsService(customUserDetailsService)
            .passwordEncoder(bCryptPasswordEncoder);
        return authenticationManagerBuilder.build();
    }
}
