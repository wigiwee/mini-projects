package com.ZorvynFinanceApp.backend.configurations.security;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.ZorvynFinanceApp.backend.exceptions.ErrorResponse;
import com.ZorvynFinanceApp.backend.models.RoleType;

import jakarta.servlet.http.HttpServletResponse;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import tools.jackson.databind.ObjectMapper;

@Configuration
@EnableWebSecurity
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class SecurityConfiguration {


    CustomAuthenticationEntryPoint customAuthenticationEntryPoint;

    JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
	AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
		return configuration.getAuthenticationManager();
	}

    @Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {

        httpSecurity
            .cors(cors ->{
                CorsConfiguration corsConfig = new CorsConfiguration();

                corsConfig.setAllowedOrigins(List.of("http://localhost:3000"));
                UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
                source.registerCorsConfiguration("/**", corsConfig);
                cors.configurationSource(source);
            })

            .csrf(AbstractHttpConfigurer::disable)

            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth ->{

                auth
                    .requestMatchers("/api/v1/auth/**").permitAll()
                    .requestMatchers( "/v3/api-docs/**","/v3/api-docs" , "/swagger-ui/**", "/swagger-resources/**").permitAll();

                auth
                    .requestMatchers(HttpMethod.GET, "/api/v1/records/**", "/api/v1/records").hasAnyRole( RoleType.VIEWER.name(),  RoleType.ANALYST.name() ,  RoleType.ADMIN.name())
                    .requestMatchers(HttpMethod.GET, "/api/v1/dashboard").hasAnyRole( RoleType.ANALYST.name(),  RoleType.ADMIN.name())

                    .requestMatchers(HttpMethod.POST, "/api/v1/records", "/api/v1/records/**").hasRole( RoleType.ADMIN.name())
                    .requestMatchers(HttpMethod.PUT, "/api/v1/records/**").hasRole( RoleType.ADMIN.name())
                    .requestMatchers(HttpMethod.DELETE, "/api/v1/records/**").hasRole( RoleType.ADMIN.name());
            })
            
            .httpBasic(customizer -> customizer.authenticationEntryPoint(customAuthenticationEntryPoint))
            
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
            
            .exceptionHandling(customizer -> customizer
                .authenticationEntryPoint(customAuthenticationEntryPoint)
                .accessDeniedHandler((request, response, accessDeniedException )->{
                    response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                    response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                    ErrorResponse<String> errorResponse = ErrorResponse.<String>builder()
							.message("Access Denied! " + accessDeniedException.getMessage())
							.status(HttpStatus.FORBIDDEN)
							.payload("User does not have permission to access this resource")
							.success(false)
							.build();
                    String jsonString = new ObjectMapper().writeValueAsString(errorResponse);
                    response.getWriter().write(jsonString);
                })
            );
            
        return httpSecurity.build();
    }

}