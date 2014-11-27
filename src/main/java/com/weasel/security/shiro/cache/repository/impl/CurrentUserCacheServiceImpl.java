package com.weasel.security.shiro.cache.repository.impl;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.weasel.core.helper.DemonPredict;
import com.weasel.security.domain.user.User;
import com.weasel.security.domain.user.UserService;
import com.weasel.security.shiro.cache.repository.CurrentUserCacheService;

/**
 * @author Dylan
 * @time 2013-12-2
 */
@Service
public class CurrentUserCacheServiceImpl implements CurrentUserCacheService{
	
	private final static Logger log = LoggerFactory.getLogger(CurrentUserCacheServiceImpl.class);
	
	private Cache<String, User> cache = CacheBuilder.newBuilder().maximumSize(100000).build();
	
	@Autowired
	private UserService userService;
	
	@Override
	public void save(User user) {
		DemonPredict.notNull(user, "user must not be null");
		DemonPredict.notEmpty(user.getUsername(), "username must not be empty");
		log.debug("save user " + user.getUsername() +" to cache");
		cache.put(user.getUsername(), user);
	}

	@Override
	public User get(final String username) {
		DemonPredict.notEmpty(username, "username must not be empty");
		try {
			return cache.get(username, new Callable<User>() {

				@Override
				public User call() throws Exception {
					User user = userService.getByUsername(username);
					DemonPredict.notNull(user, "unknow account 【"+username+"】");
					return user;
				}
				
			});
		} catch (ExecutionException e) {
			log.error(e.getMessage());
		}
		return null;
	}

	@Override
	public void remove(String username) {
		DemonPredict.notEmpty(username, "username must not be empty");
		log.debug("remove " + username +" from cache");
		cache.invalidate(username);
	}

}
