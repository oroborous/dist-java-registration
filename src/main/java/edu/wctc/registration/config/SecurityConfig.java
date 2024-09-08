package edu.wctc.registration.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    private final String[] PERMITTED_REQUESTS = {"/", "/v/**", "/c/**", "/r/**", "/h2-console/**"};

    @Bean
    protected SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
                .csrf((csrfConfig -> csrfConfig.disable()))
                .authorizeHttpRequests(authz ->
                        authz
                                .requestMatchers(PERMITTED_REQUESTS)
                                .permitAll()
                                .anyRequest()
                                .authenticated())
                .formLogin(login ->
                        login
                                .loginPage("/v/login")
                                .defaultSuccessUrl("/"))
                .logout(logoutConfig ->
                        logoutConfig
                                .logoutUrl("/v/logout")
                                .deleteCookies("JSESSIONID"))
                .build();
    }

//
//    public void configure(WebSecurity webSecurity) throws Exception {
//        webSecurity.ignoring().antMatchers();
//    }


    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
