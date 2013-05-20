authorityFilter
===============

The authority filter for JAVA.Based JAVA's Filter.Check the url permission is allowed or not.
基于java 过滤器（Filter）实现对权限控制的框架。依赖jar包：log4j.jar,fastjson.jar


软件由三部分组成： 

权限过滤器AuthorityFilter # 负责过滤url并执行权限检查器中的权限验证方法（check）.配置在web.xml中 

权限检查器PermissionChecker # 由用户基于业务扩展，但必须继承类：PermissionChecker,实现check方法。
用户继承的Checker类必须配置在Authority的init-param中，param-name 为用户扩展的permissionChecker.            
权限数据处理器AuthorityHandler # 权限数据是有结构的。相当于一个hash.即权限组的概念。
{
group1:[/webModel1.do*,/webModel2/*.do*,/webModel3.do?method=hello*]
          
} 
                                                 
注：'*'代表任意字符。
                                                                    
      权限数据的主要来源有两种：
    1.DB，从DB查出数据组装出以上hash结构。即：Map<String,Collection<String>>.
                                                                      
    2.从权限数据文件获得。
                                                                       
       此文件可以配置在AuthorityFilter的init-param中，param-nam为：authortyFile(文件扩展名必须是.authorty).
                                                                               
       如果不从web.xml配置，也可以用权限数据处理器中的方法去加载文件。
       
      权限数据文件中权限的格式是一种我称之为友好型JSON（friendly json）[不需要双引号、单引号之类的字符]的形式
      ={ group1:[/webModel1.do*,/webModel2/*.do*,/webModel3.domethod=hello*] , 
      group2:[/webModel1.do*,/webModel2.do?mechod=add*,/webModel3.do*]  
                                                                                            
     } 
  
    两种数据来源也可以混合使用
    权限数据处理提供了很多中<验证权限的方法>，<管理权限数据的方法>，<加载权限数据的方法>。 
                                                                                                                    
                                                                                                                                                                       欢迎使用！！ 
                                                                                                                                                                       
                                                                                                                                                                                                                            《谢谢》 
                                                                                                                                                                                                                            
                                                                                                                                                                                                                              
                                                                                                                                                                                                                              
