package com.bradchen.raven.servlet;

import javax.servlet.ServletRequestEvent;
import javax.servlet.ServletRequestListener;

import net.kencochrane.raven.spi.RavenMDC;

/**
 * Store HttpServletRequest object in a ThreadLocal for static access.
 *
 * @author vvasabi
 */
public class RavenServletRequestListener implements ServletRequestListener {

	@Override
	public void requestInitialized(ServletRequestEvent sre) {
		RavenMDC mdc = RavenMDC.getInstance();
		mdc.put(ServletRequestProcessor.MDC_REQUEST, sre.getServletRequest());
	}

	@Override
	public void requestDestroyed(ServletRequestEvent sre) {
		RavenMDC mdc = RavenMDC.getInstance();
		mdc.remove(ServletRequestProcessor.MDC_REQUEST);
	}

}
