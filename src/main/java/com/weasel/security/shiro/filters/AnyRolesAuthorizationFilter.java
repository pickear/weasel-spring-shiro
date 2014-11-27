package com.weasel.security.shiro.filters;

import java.util.Set;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.apache.shiro.subject.Subject;
import org.apache.shiro.util.CollectionUtils;
import org.apache.shiro.web.filter.authz.AuthorizationFilter;

/**
 * /xx/** = roles[zhan,li] 有zhan或者li这个角色的用户都可以访问
 * 
 * @author Dylan
 * @time 2013-8-23
 */
public class AnyRolesAuthorizationFilter extends AuthorizationFilter {

	
	@Override
	protected boolean isAccessAllowed(ServletRequest request, ServletResponse response, Object mappedValue) throws Exception {

		Subject subject = getSubject(request, response);
		String[] rolesArray = (String[]) mappedValue;

		if (rolesArray == null || rolesArray.length == 0) {
			// no roles specified, so nothing to check - allow access.
			return true;
		}

		Set<String> roles = CollectionUtils.asSet(rolesArray);
		for (String role : roles) {
			if (subject.hasRole(role)) {
				return true;
			}
		}
		return false;
	}

}
