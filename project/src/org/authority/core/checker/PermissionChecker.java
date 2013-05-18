/**
 * 
 */
package org.authority.core.checker;


import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.authority.common.log.Log;

/**
 *  This class is the  based Permission Checker who must be extended by user.
 *  Do check permission is its work all.
 *  @author  Json Zou
 *  @version 1.0.3, 2013-05-04
 */
public abstract class PermissionChecker{ 
	 
 
 
	/**
	 * Do check permission.
	 * @date 2013-5-4
	 * @author JsonZou
	 * @param response  HttpServletRequest
	 * @param request   HttpServletResponse
	 * @throws IOException
	 * @throws ServletException
	 */
	public abstract Boolean check(HttpServletRequest  request,HttpServletResponse response)throws IOException, ServletException;
	 
	/**
	 * It will be called when the AuthorityFilter init...
	 * @date 2013-5-4
	 * @author JsonZou
	 */
	public void init(){
		Log.in(this.getClass());
	    Log.debug("init Perssionchecker...");
	};
	 
	/**
	 * It will be called when the AuthorityFilter destroy...
	 * @date 2013-5-4
	 * @author JsonZou
	 */
	public void destroy(){
		Log.in(this.getClass());
	    Log.debug("destroy Perssionchecker...");
	};
	 
	/**
	 * Get the request's servlet path
	 * @date 2013-5-4
	 * @author JsonZou
	 * @param request   HttpServletResponse
	 */
	public String getUrl(HttpServletRequest request){
		    return request.getServletPath();
	};
	
	/**
	 * Forward to the  path
	 * @date 2013-5-4
	 * @author JsonZou
	 * @param path will be forwarded
	 * @param response  HttpServletRequest
	 * @param request   HttpServletResponse
	 * @throws IOException
	 * @throws ServletException
	 */
	public void forward(String path,HttpServletRequest request,HttpServletResponse response)throws IOException, ServletException{
			Log.in(this.getClass());
		    Log.debug("forward to "+path);
			request.getRequestDispatcher(path).forward(request, response);
	};
	/**
	 * redirect to the  path
	 * @date 2013-5-4
	 * @author JsonZou
	 * @param path will be redirected
	 * @param response  HttpServletRequest
	 * @param request   HttpServletResponse
	 * @throws IOException
	 * @throws ServletException
	 */
	public void redirect(String path,HttpServletRequest request,HttpServletResponse response)throws IOException, ServletException{
		    Log.in(this.getClass());
		    Log.debug("redirect to "+path);
		    response.sendRedirect(path); 
	};
  
}
