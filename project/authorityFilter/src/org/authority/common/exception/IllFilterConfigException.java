/*
 * <p>IllFilterConfigException.java</p>
 * 2011-7-25 : Version 1.0 
 */
package org.authority.common.exception;

import javax.servlet.ServletException;
/**
 *  @author  Json Zou
 *  @version 1.0.3, 2013-05-04
 */
public class IllFilterConfigException  extends ServletException {
	
	private static final long serialVersionUID = -973164380779422375L;

	public IllFilterConfigException() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param message
	 */
	public IllFilterConfigException(String message) {
		super(message);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param cause
	 */
	public IllFilterConfigException(Throwable cause) {
		super(cause);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param message
	 * @param cause
	 */
	public IllFilterConfigException(String message, Throwable cause) {
		super(message, cause);
		// TODO Auto-generated constructor stub
	}

}
