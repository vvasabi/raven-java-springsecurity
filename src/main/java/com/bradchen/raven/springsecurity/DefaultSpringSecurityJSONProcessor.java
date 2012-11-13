package com.bradchen.raven.springsecurity;

import java.util.HashMap;
import java.util.Map;

import org.json.simple.JSONArray;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * Default implementation of AbstractSpringSecurityJSONProcessor. This
 * implementation does not do much and logs only username, but at least you have
 * something to work with.
 *
 * @author vvasabi
 */
public class DefaultSpringSecurityJSONProcessor
		extends AbstractSpringSecurityJSONProcessor {

	@Override
	protected boolean isAuthenticated(SecurityContext context) {
		Authentication auth = context.getAuthentication();
		return auth.isAuthenticated();
	}

	@Override
	protected String getId(SecurityContext context) {
		// UserDetails does not provide ID
		return null;
	}

	@Override
	protected String getUsername(SecurityContext context) {
		Authentication auth = context.getAuthentication();
		if (!auth.isAuthenticated()
				|| !(auth.getPrincipal() instanceof UserDetails)) {
			return null;
		}

		// detect anonymous user
		if (auth instanceof AnonymousAuthenticationToken) {
			return "anonymous";
		}

		UserDetails userDetails = (UserDetails)auth.getPrincipal();
		return userDetails.getUsername();
	}

	@Override
	protected String getEmail(SecurityContext context) {
		// UserDetails does not provide email
		return null;
	}

	@Override
	@SuppressWarnings("unchecked")
	protected Map<String, Object> getAdditionalData(SecurityContext context) {
		Authentication auth = context.getAuthentication();
		if (!auth.isAuthenticated()) {
			return null;
		}

		JSONArray authorities = new JSONArray();
		for (GrantedAuthority authority: auth.getAuthorities()) {
			authorities.add(authority.getAuthority());
		}

		Map<String, Object> additionalData = new HashMap<String, Object>();
		additionalData.put("authorities", authorities);
		return additionalData;
	}

}
