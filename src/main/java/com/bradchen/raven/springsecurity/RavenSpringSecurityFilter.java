package com.bradchen.raven.springsecurity;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * Store Spring Security's SecurityContext in RavenMDC, allowing
 * {@link AbstractSpringSecurityJSONProcessor} to access it.
 *
 * @author vvasabi
 */
public class RavenSpringSecurityFilter implements Filter {

	private static final ThreadLocal<SecurityContext> THREAD_SECURITY_CONTEXT
			= new ThreadLocal<SecurityContext>();

	public static SecurityContext getSecurityContext() {
		return THREAD_SECURITY_CONTEXT.get();
	}

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		// NOOP
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws IOException, ServletException {
		THREAD_SECURITY_CONTEXT.set(SecurityContextHolder.getContext());
		try {
			chain.doFilter(request, response);
		} finally {
			THREAD_SECURITY_CONTEXT.remove();
		}
	}

	@Override
	public void destroy() {
		// NOOP
	}

}
