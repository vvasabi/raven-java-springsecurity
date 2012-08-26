package com.bradchen.raven.servlet;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Enumeration;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletRequest;

import org.json.simple.JSONObject;

import net.kencochrane.raven.spi.RequestProcessor;
import net.kencochrane.raven.spi.RavenMDC;

/**
 * A Raven plugin that adds HTTP request information to the log when available.
 *
 * @author vvasabi
 */
public class ServletRequestProcessor implements RequestProcessor {

	public static final String MDC_REQUEST
			= ServletRequestProcessor.class.getName() + ".httpServletRequest";

	private static final String HTTP_INTERFACE = "sentry.interfaces.Http";

	@Override
	@SuppressWarnings("unchecked")
	public void process(JSONObject json) {
		RavenMDC mdc = RavenMDC.getInstance();
		HttpServletRequest request = (HttpServletRequest)mdc.get(MDC_REQUEST);
		if (request == null) {
			// no request available; do nothing
			return;
		}

		json.put(HTTP_INTERFACE, buildHttpObject(request));
	}

	@SuppressWarnings("unchecked")
	private JSONObject buildHttpObject(HttpServletRequest request) {
		JSONObject http = new JSONObject();
		http.put("url", getUrl(request));
		http.put("method", request.getMethod());
		http.put("data", getData(request));
		http.put("query_string", request.getQueryString());
		http.put("headers", getHeaders(request));
		http.put("env", getEnvironmentVariables(request));
		return http;
	}

	private String getUrl(HttpServletRequest request) {
		try {
			return new URL(request.getScheme(), request.getServerName(),
				request.getServerPort(), request.getRequestURI()).toString();
		} catch (MalformedURLException exception) {
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	private JSONObject getData(HttpServletRequest request) {
		if ("GET".equals(request.getMethod())) {
			return null;
		}

		JSONObject data = new JSONObject();
		Map<String, String[]> params = request.getParameterMap();
		for (Entry<String, String[]> entry : params.entrySet()) {
			data.put(entry.getKey(), entry.getValue()[0]);
		}
		return data;
	}

	@SuppressWarnings("unchecked")
	private JSONObject getHeaders(HttpServletRequest request) {
		JSONObject headers = new JSONObject();
		Enumeration<String> headersEnum = request.getHeaderNames();
		while (headersEnum.hasMoreElements()) {
			String name = headersEnum.nextElement();
			headers.put(name, request.getHeader(name));
		}
		return headers;
	}

	@SuppressWarnings("unchecked")
	private JSONObject getEnvironmentVariables(HttpServletRequest request) {
		JSONObject env = new JSONObject();
		env.put("REMOTE_ADDR", request.getRemoteAddr());
		env.put("SERVER_NAME", request.getServerName());
		env.put("SERVER_PORT", request.getServerPort());
		env.put("SERVER_PROTOCOL", request.getProtocol());
		return env;
	}

}