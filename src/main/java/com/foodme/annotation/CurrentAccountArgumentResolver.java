package com.foodme.annotation;

import com.foodme.repository.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.MethodParameter;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

@Component
public class CurrentAccountArgumentResolver implements HandlerMethodArgumentResolver {
    private AccountRepository accountRepository;

    @Autowired
    public CurrentAccountArgumentResolver(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasParameterAnnotation(CurrentAccount.class);
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
                                  NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {

        String username = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return accountRepository.findOneByUsername(username);
    }
}
