/**
 * 
 */
package org.authority.core.handler;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.FilterConfig;
import javax.servlet.ServletException;

import org.authority.common.exception.IllFilterConfigException;
import org.authority.common.log.Log;
import org.authority.common.util.CommonUtils;
import org.authority.core.checker.PermissionChecker;

import com.alibaba.fastjson.JSON;

/**
 * The class <code>AuthorityHandler</code> is  for caching, processing, and checking permission.  
 * @author  Json Zou
 * @version 1.0.3, 2013-05-04
 */
public class AuthorityHandler {
   private static FilterConfig filterConfig;
   private static PermissionChecker checker;
   private static  AuthorityHandler.AuthorityCache authorityCache= new AuthorityHandler.AuthorityCache();
   private static final String EXCLUDE="_exclude";
   private static final String CLASSPATH="classpath:";
   private static final String EXTENSION=".authority";//authority file name extension
   private static final String AUTHORITYFILE="authorityFile";//authoriity file
   private static final String PERMISSIONCHECKER="permissionChecker";//permission checker
	 
/*******************************read Authority from the path******************************************/  
/**
 * Read authority from the filter configuration.This method while runned when the AuthorityFilter is initing
 * @date 2013-5-4
 * @author JsonZou
 * @param filterConfig AuthorityFilter's FilterConfig in its init method.
 * @throws ServletException
 */
public static void readAuthority(FilterConfig _filterConfig) throws ServletException{
	 filterConfig=_filterConfig;
	 //get init param of authorityFile in the web.xml
	  String authorityFile=filterConfig.getInitParameter(AUTHORITYFILE);
	  if(CommonUtils.isNotEmpty(authorityFile)){
		   if(!authorityFile.endsWith(EXTENSION)){
			  throw new IllFilterConfigException("The init param of authorityFile's extension must be [.authority] .Please rename it like *.authority .");
		   }else{
			    try {
			    	  Log.in(AuthorityHandler.class);
				      Log.debug("read the authorityFile "+authorityFile);
			    	addAuthorityFromPath(CLASSPATH+authorityFile.trim());
				  } catch (IOException e) {
					e.printStackTrace();
					throw new IllFilterConfigException("Reade the authorityFile error!");
				  }
		   }
	  }
	   
   } 




/*******************************Instantiation the permissionChecker******************************************/ 
 
/**
 * Instantiation the permissionChecker from the filter configuration.This method while runned when the AuthorityFilter is initing
 * @date 2013-5-4
 * @author JsonZou
 * @param filterConfig AuthorityFilter's FilterConfig in its init method.
 * @throws ServletException
 */
public static PermissionChecker instantiationChecker(FilterConfig filterConfig) throws Exception{
	//get init param of permissionChecker in the web.xml
	  String permissionChecker=filterConfig.getInitParameter(PERMISSIONCHECKER); 
	  if(!CommonUtils.isNotEmpty(permissionChecker)){
		  throw new IllFilterConfigException("The init param of permissionChecker is undefined.Please config it in the web.xml.");
	  }else{
		 try {
			  Log.in(AuthorityHandler.class);
		      Log.debug("get the permissionChecker class "+permissionChecker);
			Class clazz=Class.forName(permissionChecker.trim());
			Object obj=clazz.newInstance();
			if(obj instanceof PermissionChecker){
				 checker=(PermissionChecker)obj;
				 return checker;
			}else{
			  throw new IllFilterConfigException("The init param of permissionChecker["+permissionChecker+"] must be extends the class PermissionChecker check it again.");	
			}
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			throw new IllFilterConfigException("Get the permissionChecker["+permissionChecker+"] that configing in the web.xml error!");
		} catch (InstantiationException e) {
			e.printStackTrace();
			throw new IllFilterConfigException("Instantiation the permissionChecker["+permissionChecker+"] that configing in the web.xml error!");
		} catch (IllegalAccessException e) {
			e.printStackTrace();
			throw new IllFilterConfigException("Instantiation the permissionChecker["+permissionChecker+"] that configing in the web.xml error!");
		}
	  }
}   





/*******************************check from the cached resources.******************************************/
 
/**
 * Check the url permission with many authority groups from cached authority. The groups type is string array.
 * @date 2013-5-4
 * @author JsonZou
 * @param groups   authority groups
 * @param url   That will be checked url
 */
public static Boolean check(String[] groups,String url){
	Collection<String> authority_exclude=new ArrayList<String>();
	Collection<String> authority_allow=new ArrayList<String>();
	  if(groups!=null&&url!=null){
		  for (String group : groups) {
			  if(authorityCache.get(group+EXCLUDE)!=null){
				  authority_exclude.addAll(authorityCache.get(group+EXCLUDE));
				}
			  if(authorityCache.get(group)!=null){
				  authority_allow.addAll(authorityCache.get(group));
			  }
		 }
		return  permissionCheck(url, authority_exclude, authority_allow);
	  }
	  return false;
  }
/**
 * Check the url permission with many authority groups from cached authority.. The groups type is string Collection.
 * @date 2013-5-4
 * @author JsonZou
 * @param groups   authority groups
 * @param url   That will be checked url
 */
public static Boolean check(Collection<String> groups,String url){
	Collection<String> authority_exclude=new ArrayList<String>();
	Collection<String> authority_allow=new ArrayList<String>();
	if(groups!=null&&url!=null){
		for (String group : groups) {
			if(authorityCache.get(group+EXCLUDE)!=null){
				authority_exclude.addAll(authorityCache.get(group+EXCLUDE));
			}
			if(authorityCache.get(group)!=null){
				authority_allow.addAll(authorityCache.get(group));
			}
		}
		return  permissionCheck(url, authority_exclude, authority_allow);
	}
	return false;
}
/**
 * Check the url permission with a single authority groups from cached authority. . The group type is string.
 * @date 2013-5-4
 * @author JsonZou
 * @param group   authority group
 * @param url   That will be checked url
 */
public static Boolean check(String group,String url){
	Collection<String> authority_exclude=new ArrayList<String>();
	Collection<String> authority_allow=new ArrayList<String>();
	if(group!=null&&url!=null){
	      if(authorityCache.get(group+EXCLUDE)!=null){
				authority_exclude.addAll(authorityCache.get(group+EXCLUDE));
			}
			if(authorityCache.get(group)!=null){
				authority_allow.addAll(authorityCache.get(group));
			}
			return  permissionCheck(url, authority_exclude, authority_allow);
	}
	return false;
}



/******************************check from the specified authority.******************************************/
/**
 * Check the url permission with the specified authority.
 * @date 2013-5-4
 * @author JsonZou
 * @param authority_exclude  not exclude authority
 * @param authority_allow   the allowable authority
 * @param url   That will be checked url
 */
public static Boolean check(String url,Collection<String> authority_exclude,Collection<String> authority_allow){
	
	return	permissionCheck(url,formatResources(authority_exclude), formatResources(authority_allow));
}
/**
 * Check the url permission with the specified allowable authority.
 * @date 2013-5-4
 * @author JsonZou
 * @param authority_allow   the allowable authority
 * @param url   That will be checked url
 */
public static Boolean check(String url,Collection<String> authority_allow){
	return	permissionCheck(url, null, formatResources(authority_allow));
}
/**
 * Check the url permission with the specified authority.
 * @date 2013-5-4
 * @author JsonZou
 * @param authority_exclude  not exclude authority
 * @param authority_allow   the allowable authority
 * @param url   That will be checked url
 */
public static Boolean check(String url,String[]  authority_exclude,String[]  authority_allow){
	return	permissionCheck(url, formatResources(authority_exclude), formatResources(authority_allow));
}
/**
 * Check the url permission with the specified allowable authority.
 * @date 2013-5-4
 * @author JsonZou
 * @param authority_allow   the allowable authority
 * @param url   That will be checked url
 */
public static Boolean check(String url,String[] authority_allow){
	return	permissionCheck(url, null, formatResources(authority_allow));
}





/*******************************add the cached resources******************************************/

/**
 * Add grouped authority from Map<String,Collection> to the cached authority.
 * <p> The same group will be merged.</p>
 * @date 2013-5-4
 * @author JsonZou
 * @param authenGroups   grouped authorities
 */
public static void addAuthorityFromMapColection(Map<String, ? extends Collection<String>> authenGroups) {
	if(authenGroups!=null){
		for (String  group : authenGroups.keySet()) {
			if(group!=null&&authenGroups.get(group)!=null){
				if(authorityCache.get(group)!=null){
					 Collection<String> resources=authorityCache.get(group);
					 resources.addAll(formatResources(authenGroups.get(group)));
					 authorityCache.put(group, resources);
				}else{
					authorityCache.put(group,formatResources(authenGroups.get(group)));
				}
			}
		}
	}
}

/**
 * Add  authorities from  Collection to the cached authority.
 * you must be specify a group to the authorities.
 * <p> The same group will be merged.</p>
 * @date 2013-5-4
 * @author JsonZou
 * @param authenGroups   grouped authorities
 */
public static void addAuthorityFromCollection( String group, Collection<String> resources) {
		 if(group!=null&&resources!=null){
			 if(authorityCache.get(group)!=null){
				 Collection<String> _resources=authorityCache.get(group);
				 _resources.addAll(formatResources(resources));
				 authorityCache.put(group, _resources);
			}else{
				authorityCache.put(group, formatResources(resources));
			}
		 }
}
/**
 * Add  authorities from standard JSON data to the cached authority.
 * <p>The standard JSON data like {"items":["itme1","item2","item3"]} or {'items':['itme1','item2','item3']}</p>
 * <p> The same group will be merged.</p>
 * @date 2013-5-4
 * @author JsonZou
 * @param jsonResources The data structure like {"items":["itme1","item2","item3"]} 
 * or {'items':['itme1','item2','item3']}
 */
public static void addAuthorityFromStandardJSON(String jsonResources) {
  Map<String,Collection<String>> _authen=formatResources(JSON.parseObject(jsonResources, Map.class));
	   if(_authen!=null){
		  addAuthority(_authen);
	   }
}

/**
 * Add  authorities from friendly JSON data to the cached authority.
 * <p>The friendly JSON data like {items:[itme1,item2,item3]}</p>
 * <p> The same group will be merged.</p>
 * @date 2013-5-4
 * @author JsonZou
 * @param jsonResources The data structure like  {items:[itme1,item2,item3]}
 */
public static void addAuthorityFromFriendlyJSON(String jsonResources) {
 Map<String,Collection<String>> _authen=JSON.parseObject(formatResources(jsonResources), Map.class);
	  if(_authen!=null){
			addAuthority(_authen);
		}
}
/**
 * Add  authorities from the path of file to the cached authority.
 * <p>The file's name must be *.authority</p>
 * <p> The same group will be merged.</p>
 * @date 2013-5-4
 * @author JsonZou
 * @param path The file's name must be *.authority;may be you can make path like classpath:com/aa.authority or like f:/com/aa.authority
 */
public static void addAuthorityFromPath(String path) throws IOException{
	if(path.trim().startsWith(CLASSPATH)){
		path=path.trim().replace(CLASSPATH,"");
		readAuthority(Thread.currentThread().getContextClassLoader().getResourceAsStream(path));
	}else{
		readAuthority(new File(path));
	}
}

/**
 * Add  authorities from file to the cached authority.
 * <p>The file's name must be *.authority</p>
 * <p> The same group will be merged.</p>
 * @date 2013-5-4
 * @author JsonZou
 * @param file
 */
public static void addAuthorityFromFile(File file) throws IOException{
	  readAuthority(file);
}
/**
 * Add  authorities from file to the cached authority.
 * <p>The file's name must be *.authority</p>
 * <p> The same group will be merged.</p>
 * @date 2013-5-4
 * @author JsonZou
 * @param stream
 */
public static void addAuthorityFromStream(InputStream in) throws IOException{
	readAuthority(in);
}

/*******************************remove the cached resources******************************************/
	
/**
 * Remove  authorities from the cached authorities by group.
 * @date 2013-5-4
 * @author JsonZou
 * @param group  one authority group 
 * @throws ServletException
 */
	public static void removeBygroup(String group) {
		if(CommonUtils.isNotEmpty(group)){
			authorityCache.remove(group);
		}
	}
	/**
	 * Remove  authorities from the cached authorities by groups.
	 * @date 2013-5-4
	 * @author JsonZou
	 * @param groups  many authority groups
	 */
	public static void removeBygroups(String[] groups) {
		for (String group : groups) {
			authorityCache.remove(group);
		}
	}
	/**
	 * Remove  authorities from the cached authorities by groups.
	 * @date 2013-5-4
	 * @author JsonZou
	 * @param groups many authority groups 
	 */
	public static void removeBygroups(Collection<String> groups) {
		for (String group : groups) {
			authorityCache.remove(group);
		}
	}
	/**
	 * Clear  the cached authorities.
	 * @date 2013-5-4
	 * @author JsonZou
	 */
	public static void clear() {
		authorityCache.clear();
	}
	
	
/*******************************refresh cached authority****************************************************/
	/**
	 * refresh  the cached authorities.
	 * @date 2013-5-4
	 * @author JsonZou
	 */
	public static void refresh() {
		  clear();
		  try {
		  readAuthority(filterConfig);
		  checker.init();
		} catch (ServletException e) {
			e.printStackTrace();
		}
	}
	
	
/*******************************get cached authority****************************************************/
	/**
	 * get  the cached authorities.
	 * @date 2013-5-4
	 * @author JsonZou
	 */
	public static Map<String, Collection<String>> getAuthorityAll() {
		 return authorityCache.all();
	 }
	/**
	 * Get  the cached authorities by group.
	 * @date 2013-5-4
	 * @author JsonZou
	 * @param group one authority group.
	 */
	public static Collection<String> getAuthorityByGroup(String group) {
		  if(!CommonUtils.isNotEmpty(group)){return null;}
		  return authorityCache.get(group);
	 }
	/**
	 * Get  the cached authorities by groups.
	 * @date 2013-5-4
	 * @author JsonZou
	 * @param groups many authority groups.
	 */
	public static Map<String,Collection<String>> getAuthorityByGroup(String[] groups) {
		if(groups==null){return null;}
		Map<String,Collection<String>> _authority=new HashMap<String,Collection<String>>();
		for (String group : groups) {
			_authority.put(group, authorityCache.get(group));
		}
		return _authority;
	}
	/**
	 * Get  the cached authorities by groups.
	 * @date 2013-5-4
	 * @author JsonZou
	 * @param groups many authority groups.
	 */
	public static Map<String,Collection<String>> getAuthorityByGroup(Collection<String> groups) {
		if(groups==null){return null;}
		Map<String,Collection<String>> _authority=new HashMap<String,Collection<String>>();
		for (String group : groups) {
			_authority.put(group, authorityCache.get(group));
		}
		return _authority;
	}






/*******************************private method of tool******************************************/
private static String formatResources(String resources){
	if(resources==null||"".equals(resources)){
		return "";
	}
	return resources.replaceAll("\\s*,\\s*"," , ")
     .replaceAll("\\s*\\[\\s*","\\[ ")
     .replaceAll("\\s*\\]\\s*,\\s*"," \\], ")
	 .replaceAll("\\s*:\\s*"," :")
	 .replaceAll("\\s*\\{\\s*","\\{ ")
     .replaceAll("\\s*\\]\\s*\\}\\s*"," \\]\\}")
	 .replaceAll(" +", "'")
	 .replaceAll("\\/", "\\\\\\\\/")
	 .replaceAll("\\.", "\\\\\\\\.")
	 .replaceAll("\\*", ".*")
	 .replaceAll("\\?", "\\\\\\\\?");
	
}
private static Collection<String> formatResources(Collection<String> resources){
	if(resources==null){
		return null;
	}
	List<String> _resources=new ArrayList<String>();
	for(String rs:resources){
		_resources.add(rs.replaceAll("\\/", "\\\\/")
		.replaceAll("\\.", "\\\\.")
		.replaceAll("\\*", ".*")
		.replaceAll("\\?", "\\\\?"));
	}
	return _resources;
}
private static String[] formatResources(String[] resources){
	if(resources==null){
		return null;
	}
	String[] _resources=new String[resources.length];
	int i=0;
	for(String rs:resources){
		_resources[i++]=rs.replaceAll("\\/", "\\\\/")
		.replaceAll("\\.", "\\\\.")
		.replaceAll("\\*", ".*")
		.replaceAll("\\?", "\\\\?");
	}
	return resources;
}
private static Map<String,Collection<String>> formatResources(Map<String,Collection<String>> grouupResources){
	if(grouupResources==null){
		return null;
	}
	for (String group : grouupResources.keySet()) {
		if(grouupResources.get(group)!=null){
			grouupResources.put(group,formatResources(grouupResources.get(group)));
		}
	}
	return grouupResources;
}
private static Boolean match(String url,String urlRegex){
		Pattern p = Pattern.compile(urlRegex);
	    Matcher m = p.matcher(url);
	    if(m.matches()){
	    	return true;
	    }
	    return false;
}
private static Boolean permissionCheck(String url,Collection<String> authority_exclude,Collection<String> authority_allow){
	if(url!=null){
		if(authority_exclude!=null){
			 
			for (String rs : authority_exclude) {
				if(match(url,rs)){return false;}
			}
		}
		if(authority_allow!=null){
			 
			for (String rs :authority_allow) {
				if(match(url,rs)){return true;}
			}
		}
	}
	return false;
}
private static Boolean permissionCheck(String url,String[] authority_exclude,String[] authority_allow){
	if(url!=null){
		if(authority_exclude!=null){
			 
			for (String rs : authority_exclude) {
				if(match(url,rs)){return false;}
			}
		}
		if(authority_allow!=null){
			 
			for (String rs :authority_allow) {
				if(match(url,rs)){return true;}
			}
		}
	}
	return false;
}
private static void addAuthority(Map<String, Collection<String>> authenGroups) {
	if(authenGroups!=null){
		for (String  group : authenGroups.keySet()) {
			if(group!=null&&authenGroups.get(group)!=null){
				if(authorityCache.get(group)!=null){
					 Collection<String> resources=authorityCache.get(group);
					 resources.addAll(authenGroups.get(group));
					 authorityCache.put(group, resources);
				}else{
					authorityCache.put(group,authenGroups.get(group));
				}
			}
		}
	}
}

private static void readAuthority(File file) throws IOException{
	   try {
		 if(file!=null&&!file.getName().endsWith(EXTENSION)){
			 throw new IOException("The init param of authorityFile's extension must be [.authority] .Please rename it like *.authority .");	 
		 }
		 FileInputStream fin = null;
		 fin = new FileInputStream(file);
		 readAuthority(fin);
	   } catch (FileNotFoundException e) {
			 e.printStackTrace();
			throw new FileNotFoundException("The authorityFile is not found int your system.The path is "+file.getPath());
		} catch (IOException e) {
			e.printStackTrace();
			throw new IOException("Reade the authorityFile errotr! The path is "+file.getPath());
		}
   }

private static void readAuthority(InputStream in) throws IOException{
		BufferedInputStream bin= new BufferedInputStream(in);
		byte[] resources_b = new byte[bin.available()];
		bin.read(resources_b);
		String jsonResources=new String(resources_b);
		addAuthorityFromFriendlyJSON(jsonResources);
}




/*************************************************authority cache****************************************************************/
  private  static final class AuthorityCache {
	 private final Lock lock = new ReentrantLock();
	 private final Map<String,Collection<String>> authority=new HashMap<String,Collection<String>>();
	public Collection<String> get(String group) {
		Collection<String> authoritis;
	 lock.lock();
	 try{
		 authoritis= this.authority.get(group);
	 }finally{
	  lock.unlock();
	 }
	 return authoritis;
	 }
	public void put(String group, Collection<String> authoritis) {
	 lock.lock();
	  try{
	    this.authority.put(group,authoritis);
	  }finally{
	   lock.unlock();
	 }
	 }
	public Collection<String> remove(String group) {
		Collection<String> authoritis;
		lock.lock();
		try{
			authoritis= this.authority.remove(group);
		}finally{
			lock.unlock();
		}
		return authoritis;
	}
	public void clear() {
		  lock.lock();
		try{
			this.authority.clear();
		}finally{
			lock.unlock();
		}
	  }
	 public  Map<String,Collection<String>> all() {
	      return this.authority;
	    }
	}
  
  
  
  
  
}
