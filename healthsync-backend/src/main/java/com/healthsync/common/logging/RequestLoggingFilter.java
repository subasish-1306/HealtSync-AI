package com.healthsync.common.logging;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

/**
 * Filter that intercepts HTTP requests and responses to log URLs, statuses, performance, and payloads.
 */
@Component
public class RequestLoggingFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(RequestLoggingFilter.class);

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        if (isAsyncDispatch(request)) {
            filterChain.doFilter(request, response);
            return;
        }

        ContentCachingRequestWrapper wrappedRequest = new ContentCachingRequestWrapper(request);
        ContentCachingResponseWrapper wrappedResponse = new ContentCachingResponseWrapper(response);

        long startTime = System.currentTimeMillis();

        try {
            filterChain.doFilter(wrappedRequest, wrappedResponse);
        } finally {
            long duration = System.currentTimeMillis() - startTime;
            logRequestResponse(wrappedRequest, wrappedResponse, duration);
            wrappedResponse.copyBodyToResponse();
        }
    }

    private void logRequestResponse(ContentCachingRequestWrapper request, ContentCachingResponseWrapper response, long duration) {
        String uri = request.getRequestURI();
        String method = request.getMethod();
        int status = response.getStatus();
        String clientIp = request.getRemoteAddr();

        logger.info("HTTP {} {} | Status: {} | Time: {}ms | IP: {}", method, uri, status, duration, clientIp);

        if (logger.isDebugEnabled()) {
            String requestBody = getPayLoad(request.getContentAsByteArray(), request.getCharacterEncoding());
            String responseBody = getPayLoad(response.getContentAsByteArray(), response.getCharacterEncoding());

            if (!requestBody.isEmpty()) {
                logger.debug("Request Payload: {}", requestBody);
            }
            if (!responseBody.isEmpty()) {
                logger.debug("Response Payload: {}", responseBody);
            }
        }
    }

    private String getPayLoad(byte[] buf, String characterEncoding) {
        if (buf == null || buf.length == 0) {
            return "";
        }
        try {
            int length = Math.min(buf.length, 1024); // Limit log output to 1KB max
            return new String(buf, 0, length, characterEncoding);
        } catch (UnsupportedEncodingException e) {
            return "[Unsupported Encoding]";
        }
    }
}
