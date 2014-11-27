package com.weasel.security.handlers;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;

/**
 * 处理异常类
 * @author Dylan
 * @time 2013-8-16
 */
public class SpringExceptionHandler implements HandlerExceptionResolver {

	public static Logger LOG = LoggerFactory.getLogger(SpringExceptionHandler.class);
	
	@Override
	public ModelAndView resolveException(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
		if ((request.getHeader("accept").indexOf("application/json") > -1 || (request  
                .getHeader("X-Requested-With")!= null && request  
                .getHeader("X-Requested-With").indexOf("XMLHttpRequest") > -1))) {    //如果请求是一个异步请求，就用PrintWriter把异常信息写回去
			
			try {
				PrintWriter writer = response.getWriter();
				writer.write(ex.getMessage());  
				writer.flush();
				return null;
			} catch (IOException e) {
				LOG.error(ex.getMessage());
			}  
			
		}
		Map<String,Object> retMap = new HashMap<String,Object>();
		LOG.error(ex.getMessage());
		retMap.put("exception", ex.getMessage());
		return new ModelAndView("exception",retMap);
	}

}
