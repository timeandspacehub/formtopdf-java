package com.timeandspacehub.formtopdf.config;

import com.timeandspacehub.formtopdf.services.CustomUserDetailsService;

import static org.springframework.security.config.Customizer.withDefaults;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

import com.timeandspacehub.formtopdf.services.CustomUserDetailsService;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {
	
    private final CustomUserDetailsService customUserDetailsService;
    
	public SecurityConfig(CustomUserDetailsService customUserDetailsService) {
        this.customUserDetailsService = customUserDetailsService;
    }

	  // Needed for @PreAuthorize + Bcrypt authentication
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
    
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http
                .authorizeHttpRequests(auth -> auth
                         // Allows OPTIONS requests (preflight) to go through without auth
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        .requestMatchers("/api/health").permitAll()
                        .anyRequest().authenticated())
                .httpBasic(withDefaults()) // popup login
                .csrf(csrf -> csrf.disable());

        return http.build();
    }
    
    //For Password Encoding
    @Bean
    public org.springframework.security.crypto.password.PasswordEncoder passwordEncoder() {
        return new org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder();
    }


//    @Bean
//    public UserDetailsService users() {
//        UserDetails user = User.withDefaultPasswordEncoder()
//                .username("adnsnin")
//                .password("pwd*1234")
//                .roles("USER")
//                .build();
//
//        return new InMemoryUserDetailsManager(user);
//    }

}
