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

/**
 * Store Spring Security's SecurityContext in RavenMDC, allowing
 * {@link SpringSecurityJSONProcessor} to access it.
 *
 * @author vvasabi
 */
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
		mdc.put(SpringSecurityJSONProcessor.MDC_SECURITY_CONTEXT, context);
		try {
			chain.doFilter(request, response);
		} finally {
			mdc.remove(SpringSecurityJSONProcessor.MDC_SECURITY_CONTEXT);
		}
	}

	@Override
	public void destroy() {
		// NOOP
	}

}
