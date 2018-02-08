package com.foodme.config;

import com.foodme.annotation.CurrentAccountArgumentResolver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import java.util.List;

@Configuration
@EnableWebMvc
class WebMvcConfig extends WebMvcConfigurerAdapter {
    private static final String RESOURCES_LOCATION = "/resources/";
    private static final String RESOURCES_HANDLER = RESOURCES_LOCATION + "**";

    @Autowired
    private CurrentAccountArgumentResolver currentAccountArgumentResolver;

//    @Override
//    public RequestMappingHandlerMapping requestMappingHandlerMapping() {
//        RequestMappingHandlerMapping requestMappingHandlerMapping = super.requestMappingHandlerMapping();
//        requestMappingHandlerMapping.setUseSuffixPatternMatch(false);
//        requestMappingHandlerMapping.setUseTrailingSlashMatch(false);
//        return requestMappingHandlerMapping;
//    }
//
//    @Bean(name = "messageSource")
//    public MessageSource messageSource() {
//        ReloadableResourceBundleMessageSource messageSource = new ReloadableResourceBundleMessageSource();
//        messageSource.setBasename(MESSAGE_SOURCE);
//        messageSource.setCacheSeconds(5);
//        return messageSource;
//    }
//
//    @Override
//    public Validator getValidator() {
//        LocalValidatorFactoryBean validator = new LocalValidatorFactoryBean();
//        validator.setValidationMessageSource(messageSource());
//        return validator;
//    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler(RESOURCES_HANDLER).addResourceLocations(RESOURCES_LOCATION);

        registry.addResourceHandler("swagger-ui.html")
                .addResourceLocations("classpath:/META-INF/resources/");

        registry.addResourceHandler("/webjars/**")
                .addResourceLocations("classpath:/META-INF/resources/webjars/");
    }

//    @Override
//    public void configureDefaultServletHandling(DefaultServletHandlerConfigurer configurer) {
//        configurer.enable();
//    }

    /**
     * Handles favicon.ico requests assuring no <code>404 Not Found</code> error is returned.
     */
    @Controller
    static class FaviconController {
        @RequestMapping("favicon.ico")
        String favicon() {
            return "forward:/resources/images/favicon.ico";
        }
    }

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
        argumentResolvers.add(currentAccountArgumentResolver);
        super.addArgumentResolvers(argumentResolvers);
    }

    @Bean
    /**
     * This filter trace requests and response params.
     *
     * Logger names is `REQUEST` and `RESPONSE`.
     * Logger can be configured by builder. Example:
     *
     * RequestLoggingFilter.builder()
     *      .accept((method, uri, query, contentType) -> uri.startsWith("/v1/") || uri.startsWith("/admin/"))
     *      .replace(
     *            "\"password\"\\s*:\\s*\".*?\"",
     *            "\"password\":\"[HIDDEN]\""
     *      )
     *      .logBody(true)
     *      .build();
     */
    public RequestLoggingFilter loggingFilter(@Value("${log-request-response-body:false}") boolean logBody) {
        return RequestLoggingFilter.builder().logBody(logBody).build();
    }
}
