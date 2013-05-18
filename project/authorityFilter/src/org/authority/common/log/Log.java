package org.authority.common.log;

import org.apache.log4j.Logger;
/**
 *  @author  Json Zou
 *  @version 1.0.3, 2013-05-04
 */
public class Log {
	public  static  Logger log;
	public  static final String LOGPRE="-->#";
	private Log(String classmsg){
		 log=Logger.getLogger(classmsg);
	}
	public static Log in(String classmsg){
		 return new Log(classmsg);
	}
	public static Log in(Class clazz){
		return new Log(clazz.getName());
	}
    public static void debug(String message) {
    	log.debug(appendPre(message));
	}
	public static  void info(String message) {
		log.info(appendPre(message));
	}
	public  static void error(String message,Throwable t) {
		log.error(appendPre(message),t);
		
	}
	private static String appendPre(String msg){
		return LOGPRE+msg;
	}
}
