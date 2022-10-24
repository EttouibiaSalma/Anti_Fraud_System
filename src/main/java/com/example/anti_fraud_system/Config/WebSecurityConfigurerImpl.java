package com.example.anti_fraud_system.Config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@EnableWebSecurity
public class WebSecurityConfigurerImpl extends WebSecurityConfigurerAdapter {

    @Autowired
    UserDetailsService userDetailsService;

    public WebSecurityConfigurerImpl(UserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService)
                .passwordEncoder(passwordEncoder());
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .authorizeRequests()
                .mvcMatchers("/api/auth/user", "/actuator/shutdown").permitAll()
                .mvcMatchers(HttpMethod.POST, "/api/antifraud/transaction").hasRole("MERCHANT")
                .mvcMatchers(HttpMethod.GET, "/api/auth/list").hasAnyRole("SUPPORT", "ADMINISTRATOR")// manage access
                .mvcMatchers(HttpMethod.DELETE, "/api/auth/user/**").hasRole("ADMINISTRATOR")
                .mvcMatchers(HttpMethod.PUT, "/api/auth/access").hasRole("ADMINISTRATOR")
                //.mvcMatchers(HttpMethod.PUT, "/api/auth/role").hasRole("ADMINISTRATOR")
                .mvcMatchers("/api/antifraud/**").hasRole("SUPPORT")
                .and()
                .httpBasic()
                .authenticationEntryPoint(new RestAuthenticationEntryPoint())// Handles auth error
                .and()
                .sessionManagement()// no session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS);
        http
                .csrf().disable().headers().frameOptions().disable(); // for Postman, the H2 console
    }

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

}
