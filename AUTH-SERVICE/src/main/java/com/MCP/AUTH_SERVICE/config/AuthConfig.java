package com.MCP.AUTH_SERVICE.config;

import com.MCP.AUTH_SERVICE.security.JwtAuthenticationEntryPoint;
import jakarta.servlet.http.HttpServletRequest;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
public class AuthConfig {

    @Autowired
    private JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration builder) throws Exception {
        return builder.getAuthenticationManager();
    }

    @Bean
    public ModelMapper modelMapper(){
        return new ModelMapper();
    }


    @Autowired
    private JwtAuthenticationEntryPoint point;

    public static final String[] PUBLIC_URLS = {
            "/api/v1/auth/**"
    };

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {

        httpSecurity.csrf(csrf -> csrf.disable())
//                .cors(httpSecurityCorsConfigurer -> httpSecurityCorsConfigurer
//                        .configurationSource(
//                                new CorsConfigurationSource() {
//                                    @Override
//                                    public CorsConfiguration getCorsConfiguration(HttpServletRequest request) {
//                                        CorsConfiguration configuration = new CorsConfiguration();
//
//                                        configuration.setAllowedOriginPatterns(List.of("*"));
//                                        configuration.setAllowedMethods(List.of("*"));
//                                        configuration.setAllowCredentials(true);
//                                        configuration.setAllowedHeaders(List.of("*"));
//                                        configuration.setMaxAge(4000L);
//
//                                        return configuration;
//                                    }
//                                }
//
//                        ))
                .authorizeHttpRequests(auth -> auth.requestMatchers(PUBLIC_URLS).permitAll()
                        .anyRequest().authenticated())
                .exceptionHandling(ex -> ex.authenticationEntryPoint(point));


        return httpSecurity.build();
    }

}
