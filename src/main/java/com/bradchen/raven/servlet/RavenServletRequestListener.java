package com.bradchen.raven.servlet;

import javax.servlet.ServletRequestEvent;
import javax.servlet.ServletRequestListener;

import org.apache.log4j.MDC;

/**
 * Store HttpServletRequest object in a ThreadLocal for static access.
 *
 * @author vvasabi
 */
public class RavenServletRequestListener implements ServletRequestListener {

	@Override
	public void requestInitialized(ServletRequestEvent sre) {
		MDC.put(RavenServletPlugin.MDC_REQUEST, sre.getServletRequest());
	}

	@Override
	public void requestDestroyed(ServletRequestEvent sre) {
		MDC.remove(RavenServletPlugin.MDC_REQUEST);
	}

}
