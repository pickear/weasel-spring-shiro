package com.weasel.security.shiro.filters;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.apache.shiro.subject.Subject;
import org.apache.shiro.web.filter.authz.AuthorizationFilter;

/**
 * /xx/** = perms["admin:delete","admin:update"] 有"admin:delete"或者"admin:update"这个权限的用户都可以访问
 * @author Dylan
 * @time 2013-8-26
 */
public class AnyPermissionsAuthorizationFilter extends AuthorizationFilter {

	@Override
	protected boolean isAccessAllowed(ServletRequest request, ServletResponse response, Object mappedValue) throws Exception {
		Subject subject = getSubject(request, response);
		String[] perms = (String[]) mappedValue;

		for (String perm : perms) {
			if (subject.isPermitted(perm)) {
				return true;
			}
		}
		
		return false;
	}

}
