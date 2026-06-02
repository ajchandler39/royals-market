package com.royalsmarket.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        // Static assets + dev tools
                        .requestMatchers("/css/**", "/js/**", "/images/**", "/webjars/**",
                                "/favicon.ico", "/h2-console/**").permitAll()
                        .requestMatchers("/actuator/health").permitAll()
                        // Authenticated areas — declared BEFORE the public /listings/* matcher
                        // so they take precedence (first match wins).
                        .requestMatchers("/listings/new", "/listings/mine").authenticated()
                        .requestMatchers("/listings/*/edit", "/listings/*/bids", "/listings/*/buy-now",
                                "/listings/*/offers", "/listings/*/sold", "/listings/*/cancel",
                                "/listings/*/contact").authenticated()
                        .requestMatchers("/offers/**", "/profile/**", "/messages/**").authenticated()
                        // Public pages (method-agnostic; the only writes under these paths are the
                        // authenticated action endpoints matched above, plus POST /register here).
                        .requestMatchers("/", "/browse", "/login", "/register",
                                "/listings/*", "/u/*").permitAll()
                        .anyRequest().authenticated())
                .formLogin(form -> form
                        .loginPage("/login")
                        .defaultSuccessUrl("/", false)
                        .permitAll())
                .logout(logout -> logout
                        .logoutSuccessUrl("/")
                        .permitAll())
                // H2 console serves frames and posts without the app's CSRF token.
                .csrf(csrf -> csrf.ignoringRequestMatchers("/h2-console/**"))
                .headers(headers -> headers.frameOptions(frame -> frame.sameOrigin()));
        return http.build();
    }
}
