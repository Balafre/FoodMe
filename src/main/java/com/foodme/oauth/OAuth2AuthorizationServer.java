package com.foodme.oauth;

import com.foodme.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.DefaultTokenServices;
import org.springframework.security.oauth2.provider.token.TokenEnhancer;
import org.springframework.security.oauth2.provider.token.TokenEnhancerChain;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;

/**
 * Oauth Authentication server managing JWT tokens.
 * To test token generation, the following Curl command can be used:
 * curl -X POST -H "Content-type: application/x-www-form-urlencoded; charset=utf-8" -H "Authorization: Basic Zm9vQ2xpZW50SWRQYXNzd29yZDpzZWNyZXQ=="
 * -d "grant_type=password" -d "username=john" -d "password=123" http://localhost:8080/oauth/token
 * where Zm9vQ2xpZW50SWRQYXNzd29yZDpzZWNyZXQ is the Base64-encoded pair client_id:client_secret
 */
@Configuration
@PropertySource("classpath:clients.properties")
@EnableAuthorizationServer
public class OAuth2AuthorizationServer extends AuthorizationServerConfigurerAdapter {

    @Autowired
    private Environment env;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private AccountService userDetailsService;


    @Override
    public void configure(final AuthorizationServerSecurityConfigurer oauthServer) throws Exception {
        oauthServer.tokenKeyAccess("permitAll()").checkTokenAccess("isAuthenticated()");
    }

    @Override
    public void configure(final ClientDetailsServiceConfigurer clients) throws Exception {// @formatter:off
        clients
               .inMemory()
               .withClient(env.getProperty("client.foodme.webapp.name"))
                    .secret(env.getProperty("client.foodme.webapp.secret"))
                    .authorizedGrantTypes("password","authorization_code", "refresh_token")
                    .scopes("read","write")
                    .accessTokenValiditySeconds(Integer.parseInt(env.getProperty("client.foodme.webapp.validity.accesstoken")))
                    .refreshTokenValiditySeconds(Integer.parseInt(env.getProperty("client.foodme.webapp.validity.refreshtoken")));
    } // @formatter:on

    @Override
    public void configure(final AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
        // @formatter:off
        final TokenEnhancerChain tokenEnhancerChain = new TokenEnhancerChain();
        tokenEnhancerChain.setTokenEnhancers(Arrays.asList(tokenEnhancer(), accessTokenConverter()));
        endpoints.tokenStore(tokenStore())
                .tokenEnhancer(tokenEnhancerChain)
                .authenticationManager(authenticationManager).userDetailsService(userDetailsService);
        // @formatter:on
    }

    @Bean
    public TokenStore tokenStore() {
        return new JwtTokenStore(accessTokenConverter());
    }

    @Bean
    public TokenEnhancer tokenEnhancer() {
        return new CustomTokenEnhancer();
    }

    @Bean
    public JwtAccessTokenConverter accessTokenConverter() {
        final JwtAccessTokenConverter converter = new CustomTokenEnhancer();
        converter.setSigningKey(env.getProperty("security.token.signing.key"));
//        final KeyStoreKeyFactory keyStoreKeyFactory = new KeyStoreKeyFactory(
//                new ClassPathResource(env.getProperty("security.keystore.name")),
//                env.getProperty("security.keystore.password").toCharArray());
//
//        converter.setKeyPair(keyStoreKeyFactory.getKeyPair(
//                env.getProperty("security.keystore.keypair.name")));
//
//        converter.setVerifierKey(keyStoreKeyFactory.getKeyPair(
//                env.getProperty("security.keystore.keypair.name")).getPublic().toString());

        return converter;
    }

    @Bean
    @Primary
    public DefaultTokenServices tokenServices() {
        final DefaultTokenServices defaultTokenServices = new DefaultTokenServices();
        defaultTokenServices.setTokenStore(tokenStore());
        defaultTokenServices.setSupportRefreshToken(true);
        return defaultTokenServices;
    }

}
