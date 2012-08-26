package com.bradchen.raven.servlet;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import net.kencochrane.raven.spi.RavenMDC;

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
		RavenMDC mdc = RavenMDC.getInstance();
		mdc.put(SpringSecurityRequestProcessor.MDC_SECURITY_CONTEXT, context);
		chain.doFilter(request, response);
		mdc.remove(SpringSecurityRequestProcessor.MDC_SECURITY_CONTEXT);
	}

	@Override
	public void destroy() {
		// NOOP
	}

}
