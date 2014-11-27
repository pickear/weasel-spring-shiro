package com.weasel.security.shiro.cache.repository.impl;

import java.util.Set;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.weasel.security.shiro.cache.repository.CacheRepository;

@SuppressWarnings("unchecked")
public class LocalCacheOperations implements CacheRepository {
	
	private Cache<String, Object> cache = CacheBuilder.newBuilder().maximumSize(1000000).build();

	@Override
	public <T> T get(String key) {
		
		return (T) cache.getIfPresent(key);
	}

	@Override
	public <T> void save(String key, T entity) {
		cache.put(key, entity);
	}

	@Override
	public void remove(String key) {
		cache.invalidate(key);
	}

	@Override
	public Set<String> keys() {
		return cache.asMap().keySet();
	}

}
