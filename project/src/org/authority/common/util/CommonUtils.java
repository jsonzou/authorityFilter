/**
 * <p>CommonUtils</p>
 */
package org.authority.common.util;

import java.util.Collection;
/**
 *  @author  Json Zou
 *  @version 1.0.3, 2013-05-04
 */
public final class CommonUtils {
 
	public static boolean isNotEmpty(String str)
	{
		boolean rtnValue = true;
		if (str == null || str.equals("") || str.equals("null") || str.trim().length() == 0)
		{
			rtnValue = false;
		}
		return rtnValue;
	}
	
	public static <T> boolean isNotEmptyCollection(Collection<T> coll){
		boolean flag=true;
		if(coll==null){
			flag=false;
		}else{
			if(coll.isEmpty()){
				flag=false;
			}
		}
		return flag;
	}
 
}
