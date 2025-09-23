package com.org.config;


import com.org.filter.JwtAuthFilter;
import com.org.service.CustomUserDetailsService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.servlet.util.matcher.MvcRequestMatcher;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.servlet.handler.HandlerMappingIntrospector;


import static org.springframework.security.config.Customizer.withDefaults;


@EnableMethodSecurity
@EnableWebSecurity
@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    @Autowired
    private JwtAuthFilter authFilter;

    @Autowired
    CustomUserDetailsService customUserDetailService;

    @Bean
    public UserDetailsService userDetailsService() {
        return new CustomUserDetailsService();
    }

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http.cors(withDefaults())
                .csrf(csrf -> csrf.disable())
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .exceptionHandling(ex -> ex.authenticationEntryPoint(
                        (request, response, ex1) -> response.sendError(HttpServletResponse.SC_UNAUTHORIZED, ex1.getMessage())
                ))
                .authorizeHttpRequests(auth -> auth
                        // --- Public endpoints ---
                        .requestMatchers("/api/v1/auth/**").permitAll()
                        .requestMatchers("/api/hotel/**").permitAll()
                        .requestMatchers("/v3/api-docs/**").permitAll()
                        .requestMatchers("/v3/api-docs.yaml").permitAll()
                        .requestMatchers("/swagger-ui/**").permitAll()
                        .requestMatchers("/swagger-ui.html").permitAll()
                        .requestMatchers("/api/user/login").permitAll()
                        .requestMatchers("/api/user/register").permitAll()
                        .requestMatchers("/api/location/fetch").permitAll()
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()  // For CORS preflight

                        // --- Admin endpoints ---
                        .requestMatchers("/api/user/roles").hasRole("admin")
                        .requestMatchers("/api/location/add").hasRole("admin")
                        .requestMatchers("/api/facility/add").hasRole("admin")
                        .requestMatchers("/api/facility/hotel/add").hasRole("admin")
                        .requestMatchers("/api/book/hotel/update/status").hasRole("admin")

                        // --- Any other request ---
                        .anyRequest().authenticated()
                )
                .formLogin(withDefaults());

        http.addFilterBefore(authFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }


    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(customUserDetailService).passwordEncoder(bCryptPasswordEncoder());
    }
    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
        authenticationProvider.setUserDetailsService(userDetailsService());
        authenticationProvider.setPasswordEncoder(bCryptPasswordEncoder());
        return authenticationProvider;
    }

}
