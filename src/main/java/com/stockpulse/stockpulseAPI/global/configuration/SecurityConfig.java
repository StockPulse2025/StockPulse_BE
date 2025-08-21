package com.stockpulse.stockpulseAPI.global.configuration;

import com.stockpulse.stockpulseAPI.global.security.filter.JwtRequestFilter;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private static final String ALLOW_AUTH_URL = "/api/auth/**";
    private static final String ALLOW_OPEN_API = "/api/open-api/**";

    private static final String[] SECURITY_ALLOW_ARRAY = { //인증 없이 접근 가능한 엔드 포인트
            ALLOW_AUTH_URL,
            ALLOW_OPEN_API,
            "/error",
            "/swagger-ui/**",
            "/swagger-resources/**",
            "/v3/api-docs/**",
            "/webjars/**",
            "/swagger-ui.html",
            "/login",
            "/refresh",
            "/actuator/prometheus",
            "/ws/**",
            "/topic/**",
            "/api/post/**",
            "/api/recommend/**",
            "/ws-stock/**",
            "/ws-stock",
            "/favicon.ico",
            "/api/v1/news/pipeline/**"
    };

    private final JwtRequestFilter jwtRequestFilter;

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable);
        http.cors(cors -> cors.configurationSource(corsConfigurationSource()));
        http.formLogin(AbstractHttpConfigurer::disable);
        http.httpBasic(AbstractHttpConfigurer::disable);
        http.headers(headers -> headers.frameOptions(HeadersConfigurer.FrameOptionsConfig::sameOrigin));
        http.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
        configureAuthorization(http);
        configureExceptionHandling(http);
        http.addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOriginPatterns(List.of("*"));
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    private void configureAuthorization(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(auth -> auth
                .requestMatchers(SECURITY_ALLOW_ARRAY).permitAll()
                .anyRequest().hasAnyAuthority("ROLE_USER")
        );
    }

    private void configureExceptionHandling(HttpSecurity http) throws Exception {
        http.exceptionHandling(exceptions -> exceptions
                .accessDeniedHandler(accessDeniedHandler())
                .authenticationEntryPoint((request, response, authException) -> {
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    response.setContentType("application/json");
                    response.setCharacterEncoding("UTF-8");
                    response.getWriter().write("{ \"error\": \"Unauthorized\", \"message\": \"인증이 필요합니다.\" }");
                })
        );
    }

    @Bean
    public AccessDeniedHandler accessDeniedHandler() {
        return (request, response, accessDeniedException) -> {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            response.getWriter().write("{ \"error\": \"Access Denied\", \"message\": \"권한이 없습니다.\", \"path\": \""
                    + request.getRequestURI() + "\" }");
        };
    }

}