package com.az.gretapyta.questionnaires.security;

import com.az.gretapyta.qcore.controller.APIController;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import static com.az.gretapyta.qcore.controller.APIController.RESTRICTED_ENTITY;

@Log4j2
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

  @Autowired
  private JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;

  @Autowired
  private JwtRequestFilter jwtRequestFilter;

  @Autowired
  UserIdentityAssignFilter userIdentityAssignFilter;

  @Autowired
  private UserDetailsService userDetailsService;

  @Autowired
  private PasswordEncoder passwordEncoder;

  @Bean
  public AuthenticationManager authenticationManager( HttpSecurity http,
                                                      UserDetailsService userDetailsService,
                                                      PasswordEncoder passwordEncoder) throws Exception {
    AuthenticationManagerBuilder authenticationManagerBuilder =
        http.getSharedObject(AuthenticationManagerBuilder.class);
    authenticationManagerBuilder.userDetailsService(userDetailsService)
        .passwordEncoder(passwordEncoder);
    return authenticationManagerBuilder.build();
  }

  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    // Configure AuthenticationManagerBuilder
    AuthenticationManagerBuilder authenticationManagerBuilder = http.getSharedObject(AuthenticationManagerBuilder.class);
    authenticationManagerBuilder.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder);
    // Get AuthenticationManager
    AuthenticationManager authenticationManager = authenticationManagerBuilder.build();

    http.csrf(AbstractHttpConfigurer::disable)
        .authorizeHttpRequests(auth -> auth

            // Allow only Administrators to DELETE anything (can be changed in the future)
            //.requestMatchers()
            .requestMatchers(HttpMethod.DELETE,APIController.DRAWERS_URL + "/**")
                .hasRole(UserRoles.ADMIN.getCode())

            // Allow only Administrators to POST in Drawers (Categories)
            .requestMatchers(HttpMethod.POST,APIController.DRAWERS_URL + "/**")
                .hasRole(UserRoles.ADMIN.getCode())

            // Allow only Administrators to PUT in Drawers (Categories)
            .requestMatchers(HttpMethod.PUT,APIController.DRAWERS_URL + "/**")
                .hasRole(UserRoles.ADMIN.getCode())

            // Allow only Administrators to PATCH in Drawers (Categories)
            .requestMatchers(HttpMethod.PATCH,APIController.DRAWERS_URL + "/**")
                .hasRole(UserRoles.ADMIN.getCode())

            // Allow only Administrators and Demo Administrators to GET from Users:
            .requestMatchers(HttpMethod.GET, APIController.USERS_URL + RESTRICTED_ENTITY + "/**")
                .hasAnyRole(UserRoles.ADMIN.getCode(), UserRoles.ADMIN_DEMO.getCode())

            // Allow creating the User:
            .requestMatchers(HttpMethod.POST, APIController.USERS_URL + "")
                .permitAll()

            // Allow updating the User (PUT):
            .requestMatchers(HttpMethod.PUT, APIController.USERS_URL + "")
                .permitAll()

             // Allow updating the User (PATCH):
            .requestMatchers(HttpMethod.PATCH, APIController.USERS_URL + "")
                .permitAll()

                // Allow updating the Questionnaire:
            .requestMatchers(HttpMethod.PATCH, APIController.QUESTIONNAIRES_URL)
                .permitAll()
                .requestMatchers("/**")
                .permitAll()
        )
        .authenticationManager(authenticationManager)
        .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .httpBasic(Customizer.withDefaults())
        .addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class)
        .addFilterAfter(userIdentityAssignFilter, UsernamePasswordAuthenticationFilter.class); // AbstractAuthenticationProcessingFilter.class);

    http.exceptionHandling((exception)-> exception.authenticationEntryPoint(jwtAuthenticationEntryPoint));
    return http.build();
  }
}