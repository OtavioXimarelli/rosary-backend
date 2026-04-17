package dev.ximarelli.rosary.backend.config;

import org.jspecify.annotations.Nullable;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

public class CurrentUserArgumentResolver implements HandlerMethodArgumentResolver {

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.getParameterAnnotation(CurrentUser.class) != null &&
        parameter.getParameterType().equals(String.class);
    }



    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
                                  NativeWebRequest webRequest, WebDataBinderFactory binderFactory) {

        String authHeader = webRequest.getHeader("Authorization");
        //SECURITY TODO: ADD A REAL JWT PARSING AND VALIDATION HERE

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7).trim();
            if (!token.isBlank()) {
                return token;
            }
        }

        // Fallback for development phase
        String fallbackId = webRequest.getHeader("X-User-Id");
        return (fallbackId != null && !fallbackId.isBlank()) ? fallbackId : "demo-user";

    }
}
