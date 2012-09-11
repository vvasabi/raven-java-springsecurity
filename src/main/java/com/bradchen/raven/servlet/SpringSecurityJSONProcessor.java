package com.bradchen.raven.servlet;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.userdetails.UserDetails;

import net.kencochrane.raven.spi.RavenMDC;
import net.kencochrane.raven.spi.JSONProcessor;

/**
 * Add user information to logs when logs are created on HTTP request threads.
 *
 * @author vvasabi
 */
public class SpringSecurityJSONProcessor implements JSONProcessor {

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

		// check authentication
		Authentication auth = context.getAuthentication();
		if (!auth.isAuthenticated()) {
			user.put("is_authenticated", false);
			return user;
		}

		// user is authenticated; populate user object
		user.put("is_authenticated", true);

		// get username from UserDetails
		if (!(auth.getPrincipal() instanceof UserDetails)) {
			// principal class unsupported; stop here
			return user;
		}
		UserDetails userDetails = (UserDetails)auth.getPrincipal();
		user.put("username", userDetails.getUsername());

		// detect anonymous user
		if (auth instanceof AnonymousAuthenticationToken) {
			user.put("username", "anonymous");
		}

		// list authorities
		JSONArray authorities = new JSONArray();
		for (GrantedAuthority authority: auth.getAuthorities()) {
			authorities.add(authority.getAuthority());
		}
		user.put("authorities", authorities);

		return user;
	}

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
