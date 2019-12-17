package com.pr.sepp.utils;

import com.pr.sepp.common.constants.CommonParameter;
import com.pr.sepp.common.constants.Env;
import com.google.common.collect.Sets;
import com.google.common.net.HttpHeaders;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.HashSet;

import static com.google.common.net.HttpHeaders.*;
@Component
@WebFilter(urlPatterns = "/*", filterName = "domainAccessFilter")
public class AccessFilter implements Filter {

	private static final String ALLOW_CREDENTIALS = "true";
	private static final String MAX_AGE = "3600";
	private static final String ALLOW_METHODS = "POST, GET, OPTIONS, DELETE";
	private static final String ALLOW_HEADERS = "x-requested-with,content-type";
	private static final String LOGIN_VALID_ERROR_MESSAGE = "{\"error\":\"token过期，请重新登录\"}";
	private static final String CONTENT_TYPE = "application/json;charset=UTF-8";

	public void destroy() {

	}

	public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
			throws IOException, ServletException {
		HttpServletRequest httpServletRequest = (HttpServletRequest) servletRequest;
		HttpServletResponse httpServletResponse = (HttpServletResponse) servletResponse;
		buildResponseHeaders(httpServletRequest, httpServletResponse);
		if (Env.LOCAL == Env.getCurrentEnv() || true) {
			filterChain.doFilter(httpServletRequest,httpServletResponse);
			return;
		}
		//登陆校验
		boolean passed = loginValid(httpServletRequest, httpServletResponse);
		if (passed) {
			filterChain.doFilter(httpServletRequest, httpServletResponse);
		}
	}

	private void buildResponseHeaders(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) {
		httpServletResponse.setHeader(ACCESS_CONTROL_ALLOW_ORIGIN, httpServletRequest.getHeader("origin"));
		httpServletResponse.setHeader(ACCESS_CONTROL_ALLOW_CREDENTIALS, ALLOW_CREDENTIALS);
		httpServletResponse.setHeader(ACCESS_CONTROL_MAX_AGE, MAX_AGE);
		httpServletResponse.setHeader(ACCESS_CONTROL_ALLOW_METHODS, ALLOW_METHODS);
		httpServletResponse.setHeader(ACCESS_CONTROL_ALLOW_HEADERS, ALLOW_HEADERS);
	}

	private boolean loginValid(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws IOException {
		HttpSession session = httpServletRequest.getSession();
		String uid = httpServletRequest.getParameter(CommonParameter.USER_ID);
		String token = (String) session.getAttribute(uid);
		String url=httpServletRequest.getRequestURI();
		HashSet<String> canAccessUrls = Sets.newHashSet("/sepp/user/list_domain", "/", "/sepp/", "/sepp/user/list_domain",
				"/sepp/user/ldap_auth","/sepp/myHandler");
		if (canAccessUrls.contains(url) || url.contains("/user")) {
			return true;
		}
		if(StringUtils.isBlank(token))
		{
			httpServletResponse.setHeader(HttpHeaders.CONTENT_TYPE, CONTENT_TYPE);
			String errorMessage = LOGIN_VALID_ERROR_MESSAGE;
			httpServletResponse.getWriter().write(errorMessage);
			return false;
		}
		return true;
	}

	public void init(FilterConfig arg0) {

	}
}
