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

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import per.code.pz.server.bean.BeanFactory;

public class SpringBean implements ApplicationContextAware {
//	public static ApplicationContext context = null; 

	@Override
	public void setApplicationContext(ApplicationContext context)
			throws BeansException {
		String [] beanNames = context.getBeanDefinitionNames();
		if(beanNames != null)
		for (String name : beanNames) {
			BeanFactory.defineBean(name, context.getBean(name));
		}
	}
}
