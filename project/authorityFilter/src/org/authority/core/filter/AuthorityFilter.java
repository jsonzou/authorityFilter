package org.authority.core.filter;

/**
 * 
 */


import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.authority.common.exception.IllFilterConfigException;
import org.authority.common.log.Log;
import org.authority.core.checker.PermissionChecker;
import org.authority.core.handler.AuthorityHandler;


/**
 * The Authority Filter.intercept the url to check its permission.
 *  @author  Json Zou
 *  @version 1.0.3, 2013-05-04
 */
public  class AuthorityFilter implements Filter { 
	private PermissionChecker checker;
	/**
	 * init AuthorityFilter
	 * @date 2013-5-4
	 * @author JsonZou
	 * @param filterConfig FilterConfig
	 * @throws ServletException
	 */
	public void init(FilterConfig filterConfig) throws ServletException {
		  Log.in(this.getClass());
	      Log.debug("init AuthorityFilter start...");
		  AuthorityHandler.readAuthority(filterConfig);
		  try {
			checker=AuthorityHandler.instantiationChecker(filterConfig);
		  } catch (Exception e) {
			 throw new IllFilterConfigException("instantiation permissionChecker which config int the AuthorityFilter init-param  error.");
		   }
		  if(checker!=null){
	         checker.init();
		   }else{
			   throw new IllFilterConfigException("instantiation permissionChecker which config int the AuthorityFilter init-param  error.");
		   }
		  Log.info("the cached authoritys = "+ AuthorityHandler.getAuthorityAll());
		  Log.debug("init AuthorityFilter end...");
	}
	/**
	 * doFilter AuthorityFilter
	 * @date 2013-5-4
	 * @author JsonZou
	 * @param request ServletRequest
	 * @param response ServletResponse
	 * @param filterChain FilterChain
	 * @throws ServletException
	 * @throws IOException
	 */
	public void doFilter(ServletRequest request,
			ServletResponse response, FilterChain filterChain)
			throws IOException, ServletException {
		    Log.in(this.getClass());
		    Log.debug("doFilter AuthorityFilter begin...");
		    if(checker.check((HttpServletRequest)request,(HttpServletResponse)response)){
			   filterChain.doFilter(request, response);
		    }
		    Log.debug("doFilter AuthorityFilter end...");
	  }
	/**
	 * destroy AuthorityFilter
	 * @date 2013-5-4
	 * @author JsonZou
	 */
	public void destroy() {
		checker.destroy();
		AuthorityHandler.clear();
		    Log.in(this.getClass());
		    Log.debug("destroy AuthenFilter...");
	}
	
}
