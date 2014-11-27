package com.weasel.security.shiro.cache.repository.impl;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.weasel.core.helper.DemonPredict;
import com.weasel.security.domain.cache.LoginError;
import com.weasel.security.shiro.cache.repository.LoginErrorCacheService;

/**
 * @author Dylan
 * @time 2013-8-14
 */
@Service
public class LoginErrorCacheServiceImpl implements LoginErrorCacheService {
	
	private final static Logger log = LoggerFactory.getLogger(LoginErrorCacheServiceImpl.class);

	private Cache<String, LoginError> cache = CacheBuilder.newBuilder().maximumSize(1000).build();

	@Override
	public void save(LoginError loginError) {
		DemonPredict.notNull(loginError, "entity can not be null");
		DemonPredict.notEmpty(loginError.getUsername(), "LoginError'username must not be empty");
		cache.put(loginError.getUsername(), loginError);
	}

	@Override
	public LoginError get(final String username) {
		try {
			return cache.get(username, new Callable<LoginError>() {

				@Override
				public LoginError call() throws Exception {
					LoginError error = LoginError.newLoginError();
					error.setErrorNumber(0);
					error.setUsername(username);
					return error;
				}
				
			});
		} catch (ExecutionException e) {
			log.error(e.getMessage());
		}
		return null;
	}

	@Override
	public void remove(String username) {
		cache.invalidate(username);
	}

}
