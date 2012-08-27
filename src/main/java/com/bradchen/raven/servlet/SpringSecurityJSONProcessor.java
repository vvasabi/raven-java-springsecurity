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

	public static final String MDC_SECURITY_CONTEXT
			= SpringSecurityJSONProcessor.class.getName() + ".securityContext";

	private static final String USER_INTERFACE = "sentry.interfaces.User";

	@Override
	@SuppressWarnings("unchecked")
	public void process(JSONObject json) {
		RavenMDC mdc = RavenMDC.getInstance();
		SecurityContext context = (SecurityContext)mdc.get(MDC_SECURITY_CONTEXT);
		if ((context == null) || (context.getAuthentication() == null)) {
			// no security context available; do nothing
			return;
		}

		json.put(USER_INTERFACE, buildUserObject(context));
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

}
