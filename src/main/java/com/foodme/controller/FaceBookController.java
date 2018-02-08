package com.foodme.controller;

import com.foodme.enumeration.Authority;
import com.foodme.enumeration.SocialMediaService;
import com.foodme.model.Account;
import com.foodme.model.AccountAuthority;
import com.foodme.service.AccountService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.social.connect.Connection;
import org.springframework.social.connect.UserProfile;
import org.springframework.social.connect.UsersConnectionRepository;
import org.springframework.social.facebook.api.Facebook;
import org.springframework.social.facebook.connect.FacebookConnectionFactory;
import org.springframework.social.oauth2.AccessGrant;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Controller
public class FaceBookController {
    private final static String DUMMY_SSO_PASSWORD = "a4884744f260245fgf7ddc02ddee61";

    @Autowired
    private FacebookConnectionFactory facebookConnectionFactory;

    @Autowired
    private UsersConnectionRepository usersConnectionRepository;

//    private Facebook facebook;

//    @Autowired
//    private ConnectionRepository connectionRepository;

    @Autowired
    private AccountService accountService;


    @RequestMapping(value = "/auth/facebook", method = RequestMethod.POST)
    public String facebookLogin(@RequestParam String token,
                                @RequestParam String email,
                                @RequestParam Authority authority,
                                final RedirectAttributes redirectAttributes,
                                final HttpServletRequest request,
                                final HttpServletResponse response
                                ) {

        if (StringUtils.isBlank(email)) {
//            return new ResponseEntity<Void>(HttpStatus.BAD_REQUEST);
        }

//        FacebookConnectionFactory connectionFactory = new FacebookConnectionFactory(
//                "1030835126972059", "a48d69534f260245cee7ddc02dd8aa61");
//        connectionFactory.setScope("public_profile, email");

//        OAuth2Operations oauthOperations = connectionFactory.getOAuthOperations();
//        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
//        params.put("redirect_uri", Arrays.asList("http://localhost:8080"));
//        AccessGrant accessGrant = oauthOperations.exchangeCredentialsForAccess(
//                "merenkov1982@mail.ru", "", params);

        AccessGrant accessGrant = new AccessGrant(token);
        Connection<Facebook> connection = facebookConnectionFactory.createConnection(accessGrant);

//        ConnectionKey connectionKey = connection.getKey();
//        String userId = connectionKey.getProviderUserId();
        UserProfile userProfile = connection.fetchUserProfile();

        if (accountService.loadUserByUsernameSafely(email) == null) {
            Account account = new Account(email, DUMMY_SSO_PASSWORD, new AccountAuthority(authority));
            account.setFirstName(userProfile.getFirstName());
            account.setLastName(userProfile.getLastName());
            account.setSocialMediaService(SocialMediaService.FACEBOOK);

            accountService.save(account);
        }

//        redirectAttributes.addAttribute("username", email);
//        redirectAttributes.addAttribute("password", DUMMY_SSO_PASSWORD);
//        redirectAttributes.addAttribute("grant_type", "password");

        request.setAttribute("username", email);
        request.setAttribute("password", DUMMY_SSO_PASSWORD);
        request.setAttribute("grant_type", "password");
        RequestDispatcher rd = request.getRequestDispatcher("/dummy");

        try {
//            request.set
            response.setHeader("Content-type", "application/x-www-form-urlencoded; charset=utf-8");
            response.setHeader("Authorization", "Basic Zm9vZG1lV2ViOmJmZTBmZjcyLTY2YzctNDhlOC05NzE5LTNhMGNmOTg1MTAxNQ==");
//            response.sendRedirect("/dummy"); //("/oauth/token");
            rd.forward(request, response);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ServletException e) {
            e.printStackTrace();
        }

//        usersConnectionRepository.createConnectionRepository(email).addConnection(connection);

        return null;
    }

    @RequestMapping(value = "/dummy", method = RequestMethod.POST)
    public void dummyMethod(final HttpServletRequest request) {
        HttpServletRequest req = request;
    }
}
