package com.weasel.security.shiro.cache.repository;

import com.weasel.security.domain.user.User;

/**
 * @author Dylan
 * @time 2013-12-2
 */
public interface CurrentUserCacheService {

	/**
	 * @param user
	 */
	void save(User user);
	
	
	/**
	 * @param username
	 * @return
	 */
	User get(String username);
	
	/**
	 * 
	 */
	void remove(String username);
}
