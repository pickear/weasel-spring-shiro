package com.weasel.security.shiro.cache.repository;

import java.util.Set;

/**
 * @author Dylan
 * @time 2014年3月14日
 */
public interface CacheRepository {

	/**
	 * 从cache中得到的是一个json格式的字符串，转化为想要的实体
	 */
	<T> T get(String key) ;

	/**
	 * 为了不同类型的转换，保存的时候以json格式保存到cache,默认12小时后自动从cache删除
	 * @param key
	 * @param entity
	 */
	<T> void save(String key,T entity) ;
	
	/**
	 * 根据标识，从memcache中删除
	 */
	void remove(String key);

	/**
	 * 获得cache所有key
	 * @return
	 */
	Set<String> keys();

}
