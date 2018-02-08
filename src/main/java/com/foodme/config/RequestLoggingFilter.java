package com.foodme.config;

import com.foodme.util.RequestThreadContext;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StreamUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingResponseWrapper;

import javax.servlet.FilterChain;
import javax.servlet.ReadListener;
import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.regex.Pattern;

import static org.apache.commons.lang3.StringUtils.substringBefore;
import static org.apache.commons.lang3.StringUtils.trim;

public class RequestLoggingFilter extends OncePerRequestFilter {

    private final Logger requestLog = LoggerFactory.getLogger("REQUEST");
    private final Logger responseLog = LoggerFactory.getLogger("RESPONSE");

    private final ImmutableSet<String> excludes;
    private final RequestAcceptor acceptor;
    private final ImmutableList<ReplaceSpec> replaces;
    private final boolean logBody;

    public interface RequestAcceptor {
        boolean accept(String method, String uri, String query, String contentType);
    }

    public static class Builder {
        private final ImmutableSet.Builder<String> excludes = ImmutableSet.builder();
        private final ImmutableList.Builder<ReplaceSpec> replaces = ImmutableList.builder();
        private RequestAcceptor acceptor = (method, uri, query, contentType) -> true;
        private boolean logBody;

        public Builder exclude(String... uris) {
            excludes.add(uris);
            return this;
        }

        public Builder accept(RequestAcceptor acceptor) {
            this.acceptor = acceptor;
            return this;
        }

        public Builder replace(String regex, String to) {
            replaces.add(new ReplaceSpec(Pattern.compile(regex), to));
            return this;
        }

        public Builder logBody(boolean logBody) {
            this.logBody = logBody;
            return this;
        }

        public RequestLoggingFilter build() {
            return new RequestLoggingFilter(excludes.build(), acceptor, replaces.build(), logBody);
        }
    }

    public static Builder builder() {
        return new Builder();
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        RequestThreadContext.initialize(request.getRemoteAddr());

        final String uri = request.getRequestURI();
        final String query = request.getQueryString();

        HttpServletRequest requestToUse = request;
        HttpServletResponse responseToUse = response;
        final String contentType = trim(substringBefore(request.getContentType(), ";"));


        final boolean form = "application/x-www-form-urlencoded".equals(contentType);
        final boolean excluded = excludes.contains(uri);
        final boolean accepted = acceptor.accept(request.getMethod(), request.getRequestURI(), request.getQueryString(), request.getContentType());

        if (!form && !excluded && accepted) {
            if (!(request instanceof ResettableStreamHttpServletRequest)) {
                final ResettableStreamHttpServletRequest wrapper = new ResettableStreamHttpServletRequest(request);
                final String payload = IOUtils.toString(wrapper.getInputStream(), wrapper.getCharacterEncoding());
                wrapper.resetInputStream();
                String message = request.getMethod() + " " + uri + (query==null? "" : "?" + query);
                if (logBody) {
                    final String body = doReplaces(payload);
                    if (body != null && !body.isEmpty())
                        message += " " + body;
                }
                requestLog.trace(message);
                requestToUse = wrapper;
            }
            if (!(response instanceof ContentCachingResponseWrapper)) {
                responseToUse = new ContentCachingResponseWrapper(response);
            }
        }
        try {
            filterChain.doFilter(requestToUse, responseToUse);
        } finally {
            if (responseToUse instanceof ContentCachingResponseWrapper) {
                final ContentCachingResponseWrapper wrapper = (ContentCachingResponseWrapper) responseToUse;
                String message = String.valueOf(wrapper.getStatus());
                if (logBody) {
                    final byte[] body = wrapper.getContentAsByteArray();
                    message += " " + doReplaces(new String(body, wrapper.getCharacterEncoding()));
                    StreamUtils.copy(body, wrapper.getResponse().getOutputStream());
                }
                responseLog.trace(message);
            }
            RequestThreadContext.clear();
        }
    }

    @Override
    protected boolean shouldNotFilterAsyncDispatch() {
        return true;
    }

    private String doReplaces(String body) {
        for (ReplaceSpec replace : replaces) {
            body = replace.what.matcher(body).replaceAll(replace.to);
        }
        return body;
    }

    private RequestLoggingFilter(ImmutableSet<String> excludes, RequestAcceptor acceptor, ImmutableList<ReplaceSpec> replaces, boolean logBody) {
        this.excludes = excludes;
        this.acceptor = acceptor;
        this.replaces = replaces;
        this.logBody = logBody;
    }

    private static class ReplaceSpec {
        public final Pattern what;
        public final String to;

        public ReplaceSpec(Pattern what, String to) {
            this.what = what;
            this.to = to;
        }
    }

    private static class ResettableStreamHttpServletRequest extends HttpServletRequestWrapper {

        private byte[] rawData;
        private HttpServletRequest request;
        private ResettableServletInputStream servletStream;

        public ResettableStreamHttpServletRequest(HttpServletRequest request) {
            super(request);
            this.request = request;
            this.servletStream = new ResettableServletInputStream();
        }


        public void resetInputStream() {
            servletStream.stream = new ByteArrayInputStream(rawData);
        }

        @Override
        public ServletInputStream getInputStream() throws IOException {
            if (rawData == null) {
                rawData = IOUtils.toByteArray(this.request.getReader());
                servletStream.stream = new ByteArrayInputStream(rawData);
            }
            return servletStream;
        }

        @Override
        public BufferedReader getReader() throws IOException {
            if (rawData == null) {
                rawData = IOUtils.toByteArray(this.request.getReader());
                servletStream.stream = new ByteArrayInputStream(rawData);
            }
            return new BufferedReader(new InputStreamReader(servletStream));
        }


        private class ResettableServletInputStream extends ServletInputStream {

            private InputStream stream;

            @Override
            public int read() throws IOException {
                return stream.read();
            }

            @Override
            public boolean isFinished() {
                return false;
            }

            @Override
            public boolean isReady() {
                return false;
            }

            @Override
            public void setReadListener(ReadListener readListener) {
                throw new IllegalStateException("Not implemented!");
            }
        }
    }
}
