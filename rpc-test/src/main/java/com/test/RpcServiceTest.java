/**
 * ServerStart.java	  V1.0   2014年9月18日 上午10:52:07
 *
 * Copyright pengzhistar@sina.com All rights reserved.
 *
 * Modification history(By    Time    Reason):
 * 
 * Description:
 */

package com.test;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import per.code.pz.server.bean.spring.SpringBean;

public class RpcServiceTest {
	public static void main(String[] args) {
		//远程服务指向
		ApplicationContext context = new ClassPathXmlApplicationContext("classpath:spring/springcontext-rpc.xml","classpath:springcontext-service.xml");
		SpringBean.initBean(context);
	}
}
