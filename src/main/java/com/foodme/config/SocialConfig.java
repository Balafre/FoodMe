package com.foodme.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.security.crypto.encrypt.Encryptors;
import org.springframework.social.UserIdSource;
import org.springframework.social.config.annotation.ConnectionFactoryConfigurer;
import org.springframework.social.config.annotation.EnableSocial;
import org.springframework.social.config.annotation.SocialConfigurerAdapter;
import org.springframework.social.connect.ConnectionFactoryLocator;
import org.springframework.social.connect.UsersConnectionRepository;
import org.springframework.social.connect.jdbc.JdbcUsersConnectionRepository;
import org.springframework.social.facebook.connect.FacebookConnectionFactory;
import org.springframework.social.security.AuthenticationNameUserIdSource;

import javax.sql.DataSource;

@Configuration
@EnableSocial
public class SocialConfig extends SocialConfigurerAdapter {
    @Autowired
    private Environment env;

    @Autowired
    private DataSource dataSource;

    @Bean
    public FacebookConnectionFactory facebookConnectionFactory() {
        FacebookConnectionFactory facebookConnectionFactory =
                new FacebookConnectionFactory(
                        env.getProperty("spring.social.facebook.app-id"),
                        env.getProperty("spring.social.facebook.app-secret"));
        facebookConnectionFactory.setScope("public_profile, email");
        return facebookConnectionFactory;
    }

//    @Override
//    public void addConnectionFactories(ConnectionFactoryConfigurer cfConfig, Environment env) {
//        cfConfig.addConnectionFactory(facebookConnectionFactory());
//    }

    @Override
    public UserIdSource getUserIdSource() {
        return new AuthenticationNameUserIdSource();
    }

    @Override
    public UsersConnectionRepository getUsersConnectionRepository(ConnectionFactoryLocator connectionFactoryLocator) {
        return new JdbcUsersConnectionRepository(dataSource, connectionFactoryLocator, Encryptors.noOpText());
    }
}