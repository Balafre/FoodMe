package com.foodme.oauth;

import com.foodme.dto.AccountDto;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;

import java.util.*;

import static com.foodme.util.StreamUtils.streamOf;

public class CustomTokenEnhancer extends JwtAccessTokenConverter {
    @Override
    public OAuth2AccessToken enhance(OAuth2AccessToken accessToken, OAuth2Authentication authentication) {
        final AccountDto account = (AccountDto) authentication.getPrincipal();

        Map<String, Object> info = new LinkedHashMap<String, Object>(
                accessToken.getAdditionalInformation());

        List<String> authorities = new ArrayList<>(account.getAuthorities().size());
        streamOf(account.getAuthorities()).forEach(accAuthority -> {
            authorities.add(accAuthority.getAuthority());
        });

        info.put("accountId", account.getId());
        info.put("userName", account.getUsername());
        info.put("authorities", authorities);

        DefaultOAuth2AccessToken customAccessToken = new DefaultOAuth2AccessToken(accessToken);
        customAccessToken.setAdditionalInformation(info);
        return super.enhance(customAccessToken, authentication);
    }

}
