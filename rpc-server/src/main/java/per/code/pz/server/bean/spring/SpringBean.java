/**
 * SpringBean.java	  V1.0   2014年9月18日 下午2:19:14
 *
 * Copyright pengzhistar@sina.com All rights reserved.
 *
 * Modification history(By    Time    Reason):
 * 
 * Description:
 */

package per.code.pz.server.bean.spring;

import org.springframework.context.ApplicationContext;

import per.code.pz.server.bean.BeanFactory;

public class SpringBean {
//	public static ApplicationContext context = null; 
	public static void initBean(ApplicationContext context){
		String [] beanNames = context.getBeanDefinitionNames();
		if(beanNames != null)
		for (String name : beanNames) {
			BeanFactory.defineBean(name, context.getBean(name));
		}
	}
}
