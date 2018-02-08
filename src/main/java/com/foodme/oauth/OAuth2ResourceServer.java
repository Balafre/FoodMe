package com.foodme.oauth;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.ResourceServerTokenServices;


@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true)
@EnableResourceServer
public class OAuth2ResourceServer extends ResourceServerConfigurerAdapter {
    @Value("${app.resource.id}")
    private String resourceId;

    @Autowired
    private ResourceServerTokenServices tokenServices;

    @Override
    public void configure(ResourceServerSecurityConfigurer resources) {
        resources
                .resourceId(resourceId)
                .tokenServices(tokenServices)
                .stateless(true);
    }

    @Override
    public void configure(HttpSecurity http) throws Exception {
            http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.NEVER)
                .and()
            .formLogin().permitAll()
                .and()
            .authorizeRequests()
                .antMatchers("/account", "/auth/facebook").permitAll()
                .anyRequest().authenticated();
    }
}
