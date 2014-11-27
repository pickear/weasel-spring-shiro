package com.weasel.security.shiro.filters;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.apache.shiro.web.servlet.AdviceFilter;
import org.apache.shiro.web.util.WebUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.weasel.security.helper.HttpHelper;
import com.weasel.security.helper.ShiroSecurityHelper;


/**用于跨域共享session,那个应用需要与登录应用共享session，就开放此filter，并且应该是拦截所有请求，至少是拦截所有爱保护的资源
 * @author Dylan
 * @mail pickear@gmail.com
 * @time 2014年4月14日
 */
public class CasFilter extends AdviceFilter {
	
	private final static Logger log = LoggerFactory.getLogger(CasFilter.class);
	
	private final String CAS_SERVER_URL_NAME = "casServerURL";
	
	private String casServerURL;
	private String domain;

	@Override
	protected boolean preHandle(ServletRequest request, ServletResponse response)throws Exception {
		
		boolean hasSyn = (null == ShiroSecurityHelper.getSession().getAttribute(request.getServerName()) ? false : (Boolean) ShiroSecurityHelper.getSession().getAttribute(request.getServerName()));
		if(ShiroSecurityHelper.hasAuthenticated() || hasSyn){
			return true;
		}
		String jsid = WebUtils.getCleanParam(request, "jsid");
		HttpServletRequest httpRequest = WebUtils.toHttp(request);
		String url = httpRequest.getRequestURL().toString();
		url = StringUtils.remove(url, httpRequest.getContextPath());
		if(StringUtils.isNotBlank(jsid)){
			HttpHelper.setCookie(WebUtils.toHttp(httpRequest),WebUtils.toHttp(response), "jsid", jsid,domain,"/");
			HttpHelper.setCookie(WebUtils.toHttp(httpRequest),WebUtils.toHttp(response), "rememberMe", WebUtils.getCleanParam(request, "rememberMe"),domain,"/");
			WebUtils.issueRedirect(request, response, url);
			log.info("redirect : " + url);
			return false;
		}
		String uri = casServerURL + "?service=" + url;
		WebUtils.issueRedirect(request, response, uri);
		log.info("redirect : " + uri);
		return false;
	}
	
	@Override
	protected void onFilterConfigSet() throws Exception {
		setCasServerURL(getInitParam(CAS_SERVER_URL_NAME));
	}

	@Override
	protected void postHandle(ServletRequest request, ServletResponse response)throws Exception {
		super.postHandle(request, response);
	}

	public void setCasServerURL(String casServerURL) {
		this.casServerURL = casServerURL;
	}

	public void setDomain(String domain) {
		this.domain = domain;
	}
	
}
