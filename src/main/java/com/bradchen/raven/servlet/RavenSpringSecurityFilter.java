package com.bradchen.raven.servlet;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.apache.log4j.MDC;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

public class RavenSpringSecurityFilter implements Filter {

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		// NOOP
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws IOException, ServletException {
		SecurityContext context = SecurityContextHolder.getContext();
		MDC.put(RavenSpringSecurityPlugin.MDC_SECURITY_CONTEXT, context);
		chain.doFilter(request, response);
		MDC.remove(RavenSpringSecurityPlugin.MDC_SECURITY_CONTEXT);
	}

	@Override
	public void destroy() {
		// NOOP
	}

}
