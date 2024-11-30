package com.admin.login.config;

import com.admin.login.security.RestHeaderAuthFilter;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import static org.springframework.security.config.Customizer.withDefaults;


@Configuration
public class SecurityConfig  {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http,RestHeaderAuthFilter restHeaderAuthFilter) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.GET, "/api/test/**").permitAll()
                        .requestMatchers("/", "/webjars/**", "/login", "/resources/**").permitAll()
                        .anyRequest().authenticated()
                )
                .addFilterBefore(restHeaderAuthFilter, org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter.class)
                .formLogin(withDefaults())
                .httpBasic(withDefaults());
        return http.build();

    }

    @Bean
    public RestHeaderAuthFilter restHeaderAuthFilter(AuthenticationManager authenticationManager,
                                                     AuthenticationSuccessHandler authenticationSuccessHandler,
                                                     AuthenticationFailureHandler authenticationFailureHandler) {
        return new RestHeaderAuthFilter(authenticationManager, authenticationSuccessHandler, authenticationFailureHandler);
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public AuthenticationSuccessHandler authenticationSuccessHandler() {
        return (request, response, authentication) -> {
            response.setStatus(HttpServletResponse.SC_OK);
            response.getWriter().write("Authentication Successful");
        };
    }

    @Bean
    public AuthenticationFailureHandler authenticationFailureHandler() {
        return (request, response, exception) -> {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("Authentication Failed: " + exception.getMessage());
        };
    }

    @Bean
    protected UserDetailsService userDetailsService() {
        UserDetails admin = User
                                .withUsername("adana")
                                .password(passwordEncoder()
                                        .encode("password123"))
                                .roles("ADMIN")
                                .build();

        UserDetails user = User
                                .withUsername("user")
                                .password(passwordEncoder()
                                        .encode("user"))
                                .roles("USER").build();

        return new InMemoryUserDetailsManager(admin, user);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }









}
