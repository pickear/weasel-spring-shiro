package com.weasel.security.helper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import com.weasel.core.helper.DemonPredict;

/**
 * 
 * @author Dylan
 * @time 2013-7-25
 */
public class SpringBeanHolder implements ApplicationContextAware {

	private static ApplicationContext context;
	private final static Logger log = LoggerFactory.getLogger(SpringBeanHolder.class);

	@Override
	public void setApplicationContext(ApplicationContext _context)
			throws BeansException {
		DemonPredict.notNull(_context);
		context = _context;
	}

	public static ApplicationContext getContext() {
		if (null == context)
			throw new RuntimeException(
					"please register the SpringBeanHolder bean to spring...");
		return context;
	}

	/**
	 * 
	 * @param clazz
	 * @return
	 */
	public static <T> T getBean(Class<T> clazz) {
		try {
			return getContext().getBean(clazz);
		} catch (BeansException e) {
			log.warn("can not found bean " + clazz.getName());
		}
		return null;
	}

	/**
	 * 
	 * @param beanName
	 * @return
	 */
	public static Object getBean(String beanName) {
		try {
			return getContext().getBean(beanName);
		} catch (BeansException e) {
			log.warn("can not found bean " + beanName);
		}
		return null;
	}

}
