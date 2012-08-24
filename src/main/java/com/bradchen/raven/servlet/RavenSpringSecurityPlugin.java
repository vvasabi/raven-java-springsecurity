package com.bradchen.raven.servlet;

import org.apache.log4j.MDC;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.userdetails.UserDetails;

import net.kencochrane.sentry.spi.RavenEvent;
import net.kencochrane.sentry.spi.RavenPlugin;

public class RavenSpringSecurityPlugin implements RavenPlugin {

	public static final String MDC_SECURITY_CONTEXT
		= RavenSpringSecurityPlugin.class.getName() + ".securityContext";

	private static final String USER_INTERFACE = "sentry.interfaces.User";

	@Override
	public void preProcessEvent(RavenEvent event) {
		SecurityContext context = (SecurityContext)MDC.get(MDC_SECURITY_CONTEXT);
		if ((context == null) || (context.getAuthentication() == null)) {
			// no security context available; do nothing
			return;
		}

		event.putData(USER_INTERFACE, buildUserObject(context));
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
	@SuppressWarnings("unchecked")
	public void postProcessRequestJSON(RavenEvent event, JSONObject json) {
		JSONObject user = (JSONObject)event.getData(USER_INTERFACE);
		if (user == null) {
			// no user object available; do nothing
			return;
		}

		json.put(USER_INTERFACE, user);
	}

}
