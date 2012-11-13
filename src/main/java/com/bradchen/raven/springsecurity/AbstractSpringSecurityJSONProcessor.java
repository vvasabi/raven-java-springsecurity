package com.bradchen.raven.springsecurity;

import java.util.Map;
import java.util.Map.Entry;

import org.json.simple.JSONObject;
import org.springframework.security.core.context.SecurityContext;

import net.kencochrane.raven.spi.RavenMDC;
import net.kencochrane.raven.spi.JSONProcessor;

/**
 * Add user information to logs when logs are created on HTTP request threads.
 *
 * @author vvasabi
 */
public abstract class AbstractSpringSecurityJSONProcessor
		implements JSONProcessor {

	private static final String USER_INTERFACE = "sentry.interfaces.User";

	@Override
	public void prepareDiagnosticContext() {
		SecurityContext context = RavenSpringSecurityFilter.getSecurityContext();
		if ((context == null) || (context.getAuthentication() == null)) {
			// no security context available; do nothing
			return;
		}

		RavenMDC.getInstance().put(USER_INTERFACE, buildUserObject(context));
	}

	@SuppressWarnings("unchecked")
	private JSONObject buildUserObject(SecurityContext context) {
		JSONObject user = new JSONObject();
		user.put("is_authenticated", isAuthenticated(context));

		String id = getId(context);
		if (id != null) {
			user.put("id", id);
		}

		String username = getUsername(context);
		if (username != null) {
			user.put("username", username);
		}

		String email = getEmail(context);
		if (email != null) {
			user.put("email", email);
		}

		Map<String, Object> additionalData = getAdditionalData(context);
		if (additionalData != null) {
			for (Entry<String, Object> entry : additionalData.entrySet()) {
				user.put(entry.getKey(), entry.getValue());
			}
		}
		return user;
	}

	protected abstract boolean isAuthenticated(SecurityContext context);

	protected abstract String getId(SecurityContext context);

	protected abstract String getUsername(SecurityContext context);

	protected abstract String getEmail(SecurityContext context);

	protected abstract Map<String, Object> getAdditionalData(SecurityContext
		context);

	@Override
	public void clearDiagnosticContext() {
		RavenMDC.getInstance().remove(USER_INTERFACE);
	}

	@Override
	@SuppressWarnings("unchecked")
	public void process(JSONObject json, Throwable exception) {
		JSONObject user = (JSONObject)RavenMDC.getInstance().get(USER_INTERFACE);
		if (user != null) {
			json.put(USER_INTERFACE, user);
		}
	}

}
