package com.weasel.security.helper;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Map;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;

import com.weasel.helper.JsonHelper;
import com.weasel.security.domain.user.User;

/**
 * @author Dylan
 * @time 2013-8-15
 */
public final class HttpHelper {

	public final static String USER_COOKIE = "cookie_user";

	@Deprecated
	public final static String SJSSIONID = "SJSSIONID";

	@Deprecated
	public final static String USER_COOKIE_NAME = "user_name";

	public final static int COOKIE_TIME = 2592000; // 秒，相当于30天
	/**
	 * @param response
	 * @param key
	 * @param value
	 * @param seconds  保存的时间（秒），如果不想设置，请把seconds设置为<=0
	 */
	public static void addCookie(HttpServletResponse response, String key, String value, int seconds) {

		Cookie cookie = new Cookie(key, value);
		if (seconds > 0) {
			cookie.setMaxAge(seconds);
		}
		addCookie(response, cookie);
	}
	
	public static void setCookie(HttpServletRequest request,HttpServletResponse response,String key,String value,String domain,String path){
		Cookie cookie = getCookie(request, key);
		if(null == cookie){
			cookie = new Cookie(key, value);
		}
		cookie.setDomain(domain);
		cookie.setPath(path);
		cookie.setValue(value);
		addCookie(response, cookie);
	}

	/**
	 * @param response
	 * @param cookie
	 */
	public static void addCookie(HttpServletResponse response, Cookie cookie) {

		response.addCookie(cookie);
	}

	/**
	 * 
	 * @param request
	 * @param response
	 * @param key
	 */
	public static void removeCookie(HttpServletRequest request, HttpServletResponse response, String key) {
		Cookie[] cookies = request.getCookies();
		if (null != cookies) {
			for (Cookie cookie : cookies) {
				if (StringUtils.equals(key, cookie.getName())) {
					cookie.setMaxAge(0);
					response.addCookie(cookie);
				}
			}
		}
	}

	/**
	 * @param request
	 * @param key
	 * @return
	 */
	public static String getCookieValue(HttpServletRequest request, String key) {
		Cookie cookie = getCookie(request, key);
		return null != cookie ? cookie.getValue() : "";
	}
	
	public static Cookie getCookie(HttpServletRequest request,String key){
		Cookie[] cookies = request.getCookies();
		if (null != cookies) {
			for (Cookie cookie : cookies) {
				if (StringUtils.equals(key, cookie.getName())) {
					return cookie;
				}
			}
		}
		return null;
	}

	/**
	 * 
	 * @param map
	 * @param response
	 */
	public static void addCookies(HttpServletResponse response, Map<String, String> map, int seconds) {
		for (String key : map.keySet()) {
			addCookie(response, key, map.get(key), seconds);
		}
	}

	/**
	 * @param response
	 * @param user
	 * @param seconds
	 */
	public static void addUserTOCookie(HttpServletResponse response, User user, int seconds) {
		try {
			user.setPassword("");
			String userJson = URLEncoder.encode(JsonHelper.toJsonString(user), "utf-8");
			addCookie(response, USER_COOKIE, userJson, seconds);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}

	/**
	 * @param request
	 * @return
	 */
	public static User getUserFromCookie(HttpServletRequest request) {
		Cookie[] cookies = request.getCookies();
		if (null != cookies) {
			for (Cookie cookie : cookies) {
				if (StringUtils.equals(USER_COOKIE, cookie.getName())) {
					try {
						return JsonHelper.fromJsonString(URLDecoder.decode(cookie.getValue(), "utf-8"), User.class);
					} catch (UnsupportedEncodingException e) {
						e.printStackTrace();
					}
				}
			}
		}
		return null;
	}

	/**
	 * get current sessionid
	 * 
	 * @param request
	 * @return
	 */
	public static String getCurrentSessionId(HttpServletRequest request) {
		return getCurrentSession(request).getId();
	}

	/**
	 * @param request
	 * @return
	 */
	public static HttpSession getCurrentSession(HttpServletRequest request) {
		return request.getSession();
	}

	/**
	 * @param request
	 * @return
	 */
	public static String getClientIP(HttpServletRequest request) {
		String ip = request.getHeader("x-forwarded-for");
		if (StringUtils.isEmpty(ip) || StringUtils.equalsIgnoreCase("unknown", ip)) {
			ip = request.getHeader("Proxy-Client-IP");
		}
		if (StringUtils.isEmpty(ip) || StringUtils.equalsIgnoreCase("unknown", ip)) {
			ip = request.getHeader("WL-Proxy-Client-IP");
		}
		if (StringUtils.isEmpty(ip) || StringUtils.equalsIgnoreCase("unknown", ip)) {
			ip = request.getRemoteAddr();
		}
		return ip;
	}

	/**
	 * 获得访问的url
	 * 
	 * @return
	 */
	public static String getRequestUrl(HttpServletRequest request) {
		return request.getRequestURL().toString();
	}
}
