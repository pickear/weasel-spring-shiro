package com.weasel.security;

import java.io.IOException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.web.util.WebUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.ServletRequestUtils;

import com.weasel.core.helper.DemonPredict;
import com.weasel.helper.TimeHelper;
import com.weasel.security.domain.cache.LoginError;
import com.weasel.security.domain.user.User;
import com.weasel.security.domain.user.UserService;
import com.weasel.security.exception.InvalidCaptchaException;
import com.weasel.security.exception.LockAccountException;
import com.weasel.security.exception.UnAllowLoginException;
import com.weasel.security.exception.UserHasOnlineException;
import com.weasel.security.helper.HttpHelper;
import com.weasel.security.helper.ShiroAuthorizationHelper;
import com.weasel.security.helper.ShiroSecurityHelper;
import com.weasel.security.shiro.cache.repository.CurrentUserCacheService;
import com.weasel.security.shiro.cache.repository.LoginErrorCacheService;

/**
 * @author dylan
 * @email pickear@gmail.com
 * @time 2014年11月24日
 */
public class SecurityServer {

	private int lockErrorCount;
	private int lockUserTime;
	
	private final static Logger LOG = LoggerFactory.getLogger(SecurityServer.class);

	@Autowired
	private UserService userService;
	
	@Autowired
	private CurrentUserCacheService currentUserMemcacheService;

	@Autowired
	private LoginErrorCacheService loginErrorMemcacheService;

	/**
	 * 
	 * @param user
	 * @param request
	 * @param response
	 */
	public void login(User user, HttpServletRequest request, HttpServletResponse response) {
		user.encodePassword();
		baseLogin(user, request, response);
	}

	/**
	 * 自动登录
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	public Map<String, Object> autoLogin(HttpServletRequest request, HttpServletResponse response) {

		Map<String, Object> responseMsg = new HashMap<String, Object>();
		Subject currentUser = SecurityUtils.getSubject();
		if(currentUser.isRemembered()){
			String username = ShiroSecurityHelper.getCurrentUsername();
			LOG.info("用户【{}】自动登录----{}", username,TimeHelper.getCurrentTime());
			User user = userService.getByUsername(username);
			baseLogin(user, request, response);
			ShiroAuthorizationHelper.clearAuthorizationInfo(username); // 用户是自动登录，首先清一下用户权限缓存，让重新加载
			responseMsg.put("username", username);
		}
		return responseMsg;
	}

	/**
	 * 退出登录
	 * 
	 * @return
	 */
	public void logout() {
		Subject subject = SecurityUtils.getSubject();
		if (subject.isAuthenticated()) {
			String username = ShiroSecurityHelper.getCurrentUsername();
			subject.logout(); // session 会销毁，在SessionListener监听session销毁，清理权限缓存
			currentUserMemcacheService.remove(username);
			if (LOG.isDebugEnabled()) {
				LOG.debug("用户" + username + "退出登录");
			}
		}
	}
	
	/**用于跨域共享session，该方法应该在登录的应用开放。如登录页面在xxx.aa.com域名下，要和***.bb.com共享session
	 * 那么，在***.bb.com应该有个filter，用来重写向到xxx.aa.com域名下的token请求，并带上回调地址，在token方法中
	 * 会拿到xxx.aa.com下的sesison，以参数的形式再重定向回***.bb.com。在bb域名的filter中拿到该jsid，应该保存
	 * 到自己域名下的cookie里，以确保bb的cookie里jsid值和xxx.aa.com相同。filter的实现为casFilter。
	 * @param request
	 * @param response
	 * @return
	 */
	public void token(HttpServletRequest request, HttpServletResponse response) {
		try {
			String service = WebUtils.getCleanParam(request, "service");
			String rememberMe = HttpHelper.getCookieValue(request, "rememberMe");
			Session session = ShiroSecurityHelper.getSession();
			URL url = new URL(service);
			session.setAttribute(url.getHost(), true);
			WebUtils.issueRedirect(request, response, service+"?jsid="+session.getId()+"&rememberMe="+rememberMe);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 
	 * @param user
	 * @param request
	 * @param response
	 */
	public void baseLogin(User user, HttpServletRequest request, HttpServletResponse response) {
		
		DemonPredict.notNull(user, "用户密码不能为空");
		try {
			//如果用户已登录，先踢出
			ShiroSecurityHelper.kickOutUser(user.getUsername());
			Subject currentUser = SecurityUtils.getSubject();
			if (currentUser.isAuthenticated()) {
				currentUser.logout();
			}
			
			boolean rememberMe = ServletRequestUtils.getBooleanParameter(request, "rememberMe", false);
			UsernamePasswordToken token = new UsernamePasswordToken(user.getUsername(), user.getPassword(), rememberMe);
			currentUser.login(token); // 登录

			request.setAttribute("username", user.getUsername());
		} catch (Exception e) {
			request.setAttribute("error", translateException(e, user));
			throw new UnAllowLoginException(e);
		}finally{
			ShiroAuthorizationHelper.clearAuthorizationInfo(user.getUsername());
		}
	}
	

	/**
	 * 
	 * @param e
	 * @param user
	 * @return
	 */
	private String translateException(Exception e, User user) {
		if (e instanceof IncorrectCredentialsException || e instanceof UnknownAccountException) { // 密码不正确异常
			String username = user.getUsername();
			LoginError error = loginErrorMemcacheService.get(username);
			if (error.getErrorNumber() >= (lockErrorCount-1)) { // 从memcache取数据，如果错误登录指定次数，进行帐号锁定，锁定时间通过配置得到
				if (null != user) {
					user.setLockedTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(TimeHelper.getAfterMinuteTime(lockUserTime)));
					userService.lockUser(user);
					return "该账户登录出错已达上限，请"+lockUserTime+"小时后重试";
				}
			}
			error.increaseErrorNumber().setValidTime(TimeHelper.getAfterHourTime(lockUserTime));
			loginErrorMemcacheService.save(error);
			return "您输入的用户名或密码不正确，还有"+(lockErrorCount-error.getErrorNumber())+"次机会";
		}
		if (e instanceof LockAccountException) {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			SimpleDateFormat sdf2 = new SimpleDateFormat("HH:mm:ss");
			Date date = null;
			try {
				date = sdf.parse(user.getLockedTime());
			} catch (ParseException e1) {
				e1.printStackTrace();
			}
			String time = sdf2.format(date);
			return "您的帐号已被锁定,请在" + time + "后再登录";
		}
		if (e instanceof UserHasOnlineException) {
			return "帐号已处于登录状态";
		}
		if(e instanceof UnknownAccountException){
			return "您输入的用户或密码不正确";
		}
		if(e instanceof InvalidCaptchaException){
			return "您输入的验证码不正确";
		}
		if(e instanceof UnknownAccountException){
			return "该用户类型不能登录该站点";
		}
		e.printStackTrace();
		return "未知异常，请联系管理员";
	}

	public int getLockErrorCount() {
		return lockErrorCount;
	}
	public void setLockErrorCount(int lockErrorCount) {
		this.lockErrorCount = lockErrorCount;
	}

	public int getLockUserTime() {
		return lockUserTime;
	}

	public void setLockUserTime(int lockUserTime) {
		this.lockUserTime = lockUserTime;
	}
	
}
