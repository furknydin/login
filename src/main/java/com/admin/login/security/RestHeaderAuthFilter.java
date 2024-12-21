package com.admin.login.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;

import static com.admin.login.util.LoggingConstants.X_TRACE_ID;

public class RestHeaderAuthFilter extends OncePerRequestFilter {


    private final Logger logger = LoggerFactory.getLogger(RestHeaderAuthFilter.class);
    private final AuthenticationManager authenticationManager;
    private final AuthenticationSuccessHandler authenticationSuccessHandler;
    private final AuthenticationFailureHandler authenticationFailureHandler;

    public RestHeaderAuthFilter(AuthenticationManager authenticationManager,
                                AuthenticationSuccessHandler authenticationSuccessHandler,
                                AuthenticationFailureHandler authenticationFailureHandler) {
        this.authenticationManager = authenticationManager;
        this.authenticationSuccessHandler = authenticationSuccessHandler;
        this.authenticationFailureHandler = authenticationFailureHandler;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {


        String username = getUsername(request);
        String password = getPassword(request);

        RequestWrapper requestWrapper = new RequestWrapper(request);
        ResponseWrapper responseWrapper = new ResponseWrapper(response);

        generateRandomTraceId();
        setResponseHeaders(responseWrapper);

        if (username != null && password != null) {
            UsernamePasswordAuthenticationToken authenticationToken =
                    new UsernamePasswordAuthenticationToken(username, password);

            try {
                Authentication authentication = authenticationManager.authenticate(authenticationToken);
                request.setAttribute("authenticatedUser", authentication);
                logRequest(requestWrapper);
                filterChain.doFilter(requestWrapper, responseWrapper);
                logResponse(responseWrapper);
            } catch (AuthenticationException ex) {
                authenticationFailureHandler.onAuthenticationFailure(request, response, ex);
            }
        } else {
            filterChain.doFilter(requestWrapper, responseWrapper);
        }
    }

    private String getPassword(HttpServletRequest request) {
        return request.getHeader("Api-Secret");
    }

    private String getUsername(HttpServletRequest request) {
        return request.getHeader("Api-Key");
    }

    private void setResponseHeaders(HttpServletResponse response){
        response.addHeader(X_TRACE_ID, MDC.get(X_TRACE_ID));
    }

    private void logResponse(ResponseWrapper responseWrapper){
        String log = String.format("Response Status: %s, Response Headers: %s, Response Body: %s",
                responseWrapper.getStatus(),
                responseWrapper.getAllHeaders(),
                IOUtils.toString(responseWrapper.getBody(),responseWrapper.getCharacterEncoding()));


        logger.info(log);
    }


    private void logRequest(RequestWrapper requestWrapper) {
        String log = String.format("## %s ## Request Method: %s, RequestUrl: %s, RequestHeaders: %s, RequestBody: %s",
                requestWrapper.getServerName(),
                requestWrapper.getMethod(),
                requestWrapper.getRequestURL(),
                requestWrapper.getAllHeaders(),
                RequestWrapper.body);
        logger.info(log);
    }

    private String generateRandomTraceId(){
        String traceId = UUID.randomUUID().toString();
        MDC.put(X_TRACE_ID,traceId);
        return traceId;
    }
}
