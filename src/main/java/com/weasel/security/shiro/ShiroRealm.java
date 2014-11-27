package com.weasel.security.shiro;

import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.shiro.authc.AccountException;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.DisabledAccountException;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.weasel.security.domain.user.Role;
import com.weasel.security.domain.user.User;
import com.weasel.security.domain.user.UserService;
import com.weasel.security.exception.LockAccountException;
import com.weasel.security.helper.ShiroSecurityHelper;

/**
 * @author Dylan
 * @time 2013-8-2
 */
public class ShiroRealm extends AuthorizingRealm{

	private final static Logger LOG = LoggerFactory.getLogger(ShiroRealm.class);
	
	public final static String REALM_NAME = "ShiroCasRealm";
	
	@Autowired
	private UserService userService;
	
	public ShiroRealm() {
		setName(REALM_NAME); // This name must match the name in the User
								// class's getPrincipals() method
	//	setCredentialsMatcher(new Sha256CredentialsMatcher());
	}
	
	/**
	 * 认证
	 */
	@Override
	protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authcToken) throws AuthenticationException {
		
		UsernamePasswordToken token = (UsernamePasswordToken) authcToken;
		String username = token.getUsername();
		if(LOG.isTraceEnabled()){
			LOG.trace("开始认证 "+ username);
		}
		try {
			if(StringUtils.isBlank(username)){
				throw new AccountException("can not handle this login");
			}
			User user = userService.getByUsername(username);
			checkUser(user, username);
			ShiroSecurityHelper.setUser(user); // 把user放到cache中
			return new SimpleAuthenticationInfo(user.getUsername(), user.getPassword(), getName());
		} catch (Exception e) {
			throw translateAuthenticationException(e);
		}
	}
	
	/**
	 * 授权
	 */
	@Override
	protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
		
		String username = (String)getAvailablePrincipal(principals);
		
		if(LOG.isTraceEnabled()){
			LOG.trace("开始授权 "+ username);
		}
		
		User user = userService.getByUsername(username);
		
		SimpleAuthorizationInfo info = new SimpleAuthorizationInfo();
		Set<String> rolesAsString = user.getRolesAsString();
		info.addRoles(rolesAsString);
		if(user.hasAuths()){
			info.addStringPermissions(user.getAuthAsString());
		}
		for(Role role : user.getRoles()){
			info.addStringPermissions(role.getAuthsAsString());
		}
		return info;
	}

	/**
	 * 异常转换
	 * @param e
	 * @return
	 */
	private AuthenticationException translateAuthenticationException(Exception e) {
		if (e instanceof AuthenticationException) {
			return (AuthenticationException) e;
		}
		if(e instanceof DisabledAccountException){
			return (DisabledAccountException)e;
		}
		if(e instanceof UnknownAccountException){
			return (UnknownAccountException)e;
		}
		return new AuthenticationException(e);
	}
	/**
	 * 检查用户
	 * @param user
	 * @param username
	 */
	private void checkUser(User user,String username){
		if(null == user){
			throw new UnknownAccountException(username + " can not find "+username+" from system");
		}
		if(user.isLocked()){
			throw new LockAccountException("the account is locked now");
		}
	}
	
}
