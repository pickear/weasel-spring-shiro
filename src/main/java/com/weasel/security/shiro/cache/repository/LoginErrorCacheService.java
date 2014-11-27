package com.weasel.security.shiro.cache.repository;

import com.weasel.security.domain.cache.LoginError;



/**保存用户错误登录次数到memcache
 * @author Dylan
 * @time 2013-8-14
 */
public interface LoginErrorCacheService {

	/**
	 * 
	 * @param username  用户名
	 * @param number  登录错误次数
	 */
	void save(LoginError loginError);
	
	/**
	 * 得到用户错误登录次数
	 * @param username 用户名
	 * @return
	 */
	LoginError get(String username);
	
	/**
	 * 删除用户登录错误信息
	 * @param username
	 */
	void remove(String username);
}
