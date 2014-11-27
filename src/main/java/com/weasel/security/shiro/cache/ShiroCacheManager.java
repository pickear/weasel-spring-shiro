package com.weasel.security.shiro.cache;

import org.apache.shiro.cache.AbstractCacheManager;
import org.apache.shiro.cache.Cache;
import org.apache.shiro.cache.CacheException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Dylan
 * @time 2014年1月6日
 */
@SuppressWarnings("rawtypes")
public class ShiroCacheManager extends AbstractCacheManager {

	private static final Logger log = LoggerFactory.getLogger(ShiroCacheManager.class);

	@Override
	protected Cache createCache(String cacheName) throws CacheException {

		if (log.isTraceEnabled()) {
			log.trace("create a cache name : " + cacheName);
		}

		return new ShiroCache<Object, Object>(cacheName);
	}

}
