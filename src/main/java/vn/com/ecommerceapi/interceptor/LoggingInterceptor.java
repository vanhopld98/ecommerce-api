package vn.com.ecommerceapi.interceptor;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import org.slf4j.Logger;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import vn.com.ecommerceapi.logging.LoggingFactory;
import vn.com.ecommerceapi.utils.JsonUtils;
import vn.com.ecommerceapi.utils.MaskingUtils;

import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Component
public class LoggingInterceptor extends OncePerRequestFilter {

    private static final Logger LOGGER = LoggingFactory.getLogger(LoggingInterceptor.class);

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        HttpRequestWrapper httpRequestWrapper = new HttpRequestWrapper(request);
        logRequest(httpRequestWrapper);

        HttpResponseWrapper httpResponseWrapper = new HttpResponseWrapper(response);
        filterChain.doFilter(httpRequestWrapper, httpResponseWrapper);
        logResponse(httpRequestWrapper, httpResponseWrapper);
    }

    private void logRequest(HttpRequestWrapper request) {
        StringBuilder sb = new StringBuilder().append("[REQUEST]");

        appendPathAndMethod(request, sb);
        appendHeaders(request, sb);
        appendBody(request, sb);

        String requestString = sb.toString();
        LOGGER.info(requestString);
    }

    private static void appendPathAndMethod(HttpServletRequest request, StringBuilder sb) {
        sb.append("[Path: ").append(getFullURL(request)).append("]");
        sb.append("[Method: ").append(request.getMethod()).append("]");
    }

    private static void appendHeaders(HttpServletRequest request, StringBuilder sb) {
        Enumeration<String> headerNames = request.getHeaderNames();
        Map<String, String> headers = new HashMap<>();
        while (headerNames.hasMoreElements()) {
            String headerName = headerNames.nextElement();
            headers.put(headerName, request.getHeader(headerName));
        }
        Object headersMasking = MaskingUtils.maskingSensitiveData(headers);
        sb.append("[Headers: ").append(JsonUtils.toJson(headersMasking)).append("]");
    }

    private static void appendBody(HttpRequestWrapper request, StringBuilder sb) {
        byte[] bodyByte = request.getBodyBytes();
        if (bodyByte != null && bodyByte.length > 0) {
            Object body = JsonUtils.toObject(bodyByte, Object.class);
            Object bodyMasking = MaskingUtils.maskingSensitiveData(body);
            if (Objects.nonNull(body)) {
                sb.append("[Request Body: ").append(JsonUtils.toJson(bodyMasking)).append("]");
            }
        }
    }

    private static void appendResponseBody(HttpResponseWrapper responseWrapper, StringBuilder sb) {
        byte[] bodyByte = responseWrapper.getCopy();
        if (bodyByte != null && bodyByte.length > 0) {
            Object body = JsonUtils.toObject(bodyByte, Object.class);
            Object bodyMasking = MaskingUtils.maskingSensitiveData(body);
            if (Objects.nonNull(body)) {
                sb.append("[Response Body: ").append(JsonUtils.toJson(bodyMasking)).append("]");
            }
        }
    }

    private void logResponse(HttpRequestWrapper request, HttpResponseWrapper response) {
        StringBuilder sb = new StringBuilder().append("[RESPONSE]");

        sb.append("[Path: ").append(getFullURL(request)).append("]");
        sb.append("[Method: ").append(request.getMethod()).append("]");
        sb.append("[Status: ").append(response.getStatus()).append("]");

        appendResponseBody(response, sb);

        String responseString = sb.toString();
        LOGGER.info(responseString);
    }

    private static String getFullURL(HttpServletRequest request) {
        StringBuilder requestURL = new StringBuilder(request.getRequestURL().toString());
        String queryString = request.getQueryString();
        if (queryString == null) {
            return requestURL.toString();
        }
        return requestURL.append('?').append(queryString).toString();

    }

}
