package com.org.config;


import com.org.filter.JwtAuthFilter;
import com.org.service.CustomUserDetailsService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;

import org.springframework.context.annotation.Configuration;
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
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;


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

    //
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {


        http = http.csrf(csrf -> csrf.disable());

        http = http.sessionManagement(management -> management
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        http = http.exceptionHandling(handling -> handling
                .authenticationEntryPoint((request, response, exception) -> {
                    response.sendError(HttpServletResponse.SC_UNAUTHORIZED, exception.getMessage());
                }));


        http
                .authorizeHttpRequests((auth) -> auth
                        .requestMatchers(AUTH_WHITELIST)
                        .permitAll()

                        .requestMatchers(new AntPathRequestMatcher("/api/user/roles")).hasRole("admin")
                        .requestMatchers(new AntPathRequestMatcher("/api/location/add")).hasRole("admin")
//                        .requestMatchers(new AntPathRequestMatcher("/api/hotel/review/add")).hasRole("customer")
//                        .requestMatchers(new AntPathRequestMatcher("/api/hotel/add")).hasRole("admin")
                        .requestMatchers(new AntPathRequestMatcher("/api/facility/add")).hasRole("admin")
                        .requestMatchers(new AntPathRequestMatcher("/api/facility/hotel/add")).hasRole("admin")
                        .requestMatchers(new AntPathRequestMatcher("/api/book/hotel/update/status")).hasRole("admin")



                        .anyRequest().authenticated())
                .formLogin(withDefaults());

        http.addFilterBefore(authFilter, UsernamePasswordAuthenticationFilter.class);

        // .httpBasic(withDefaults());
        return http.build();
    }
    private static final AntPathRequestMatcher[] AUTH_WHITELIST = { new AntPathRequestMatcher("/api/v1/auth/**"),
            new AntPathRequestMatcher("/api/hotel/**"),
            new AntPathRequestMatcher("/v3/api-docs/**"),
            new AntPathRequestMatcher("/v3/api-docs.yaml"),
            new AntPathRequestMatcher("/swagger-ui/**"),
            new AntPathRequestMatcher("/swagger-ui.html"),
            new AntPathRequestMatcher("/api/user/login"),
            new AntPathRequestMatcher("/api/user/register"),

            new AntPathRequestMatcher("/api/location/fetch")};

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
