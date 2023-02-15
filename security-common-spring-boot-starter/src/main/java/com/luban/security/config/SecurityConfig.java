package com.luban.security.config;

import com.luban.security.base.JwtAuthenticationEntryPoint;
import com.luban.security.base.JwtAuthenticationProvider;
import com.luban.security.base.JwtAuthenticationTokenFilter;
import com.luban.security.base.extension.DummyUserContextAware;
import com.luban.security.base.extension.UserContextAware;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.header.writers.frameoptions.XFrameOptionsHeaderWriter;
import org.springframework.web.cors.CorsUtils;

/**
 * @author HP
 */
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@ConditionalOnMissingBean
@EnableGlobalMethodSecurity(securedEnabled = true, prePostEnabled = true)
public class SecurityConfig {

    private final JwtAuthenticationEntryPoint unauthorizedEntryPoint;
    private final JwtAuthenticationProvider jwtAuthenticationProvider;
    private final SecurityCommonProperties commonProperties;

    @ConditionalOnMissingBean
    @Bean
    public UserContextAware dummyUserContext() {
        return new DummyUserContextAware();
    }

    @ConditionalOnMissingBean
    @Bean
    public AuthenticationManagerBuilder authenticationManagerBuilder(AuthenticationManagerBuilder auth) throws Exception {
        auth.authenticationProvider(jwtAuthenticationProvider);
        return auth;
    }

    @ConditionalOnMissingBean
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        final AuthenticationManager authenticationManager = authenticationConfiguration.getAuthenticationManager();
        if (authenticationManager instanceof ProviderManager) {
            final ProviderManager manager = (ProviderManager) authenticationManager;
            manager.setEraseCredentialsAfterAuthentication(false);
        }
        return authenticationManager;
    }

    @ConditionalOnMissingBean
    @Bean
    public JwtAuthenticationTokenFilter authenticationTokenFilter() {
        return new JwtAuthenticationTokenFilter();
    }

    @ConditionalOnMissingBean
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.requiresChannel()
                .anyRequest().requiresInsecure()
                .and()
                .csrf().disable()//csrf取消
                .cors().disable()
                .exceptionHandling().authenticationEntryPoint(unauthorizedEntryPoint)
                .and()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()//不再存储session
                .headers().frameOptions().disable()
                .and()
                .headers().addHeaderWriter(
                        new XFrameOptionsHeaderWriter(XFrameOptionsHeaderWriter.XFrameOptionsMode.SAMEORIGIN))
                .and()
                .authorizeRequests()
                .requestMatchers(CorsUtils::isPreFlightRequest).permitAll()
                .antMatchers(HttpMethod.OPTIONS).permitAll()
                .antMatchers(HttpMethod.GET,
                        "/",
                        "/*.html",
                        "/favicon.ico",
                        "/**/*.html", "/**/*.css",
                        "/**/*.js").permitAll()
                .antMatchers("/swagger-ui.html").permitAll()
                .antMatchers("/swagger-resources/**").permitAll()
                .antMatchers("/images/**").permitAll()
                .antMatchers("/webjars/**").permitAll()
                .antMatchers("/v2/api-docs").permitAll()
                .antMatchers("/configuration/ui").permitAll()
                .antMatchers("/configuration/security").permitAll()
                .antMatchers("/auth/**").permitAll()
                .antMatchers("/public/**").permitAll()
                .antMatchers(commonProperties.getUnAuthUrls().toArray(new String[commonProperties.getUnAuthUrls().size()])).permitAll()
                .antMatchers("/admin/**").hasRole("ADMIN")
                .anyRequest().authenticated();
        http.addFilterBefore(authenticationTokenFilter(), UsernamePasswordAuthenticationFilter.class);
        http.headers().cacheControl();
        return http.build();
    }

    @ConditionalOnMissingBean
    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return web -> web.ignoring().antMatchers("/v2/api-docs",
                        "/configuration/ui",
                        "/swagger-resources",
                        "/configuration/security",
                        "/swagger-ui.html",
                        "/webjars/**"
                )
                .antMatchers(HttpMethod.GET,
                        "/",
                        "/*.html",
                        "/favicon.ico",
                        "/**/*.html", "/**/*.css",
                        "/**/*.js");
    }
}
