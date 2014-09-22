/**
 * HelloWorldImpl.java	  V1.0   2013-12-26 下午12:16:12
 *
 * Copyright pengzhistar@sina.com All rights reserved.
 *
 * Modification history(By    Time    Reason):
 * 
 * Description:
 */

package com.test;

public class HelloWorldImpl implements HelloWorld {

	@Override
	public String sayHello(String name) {
		System.out.println("Hello World,"+name+"!");
		return "Hello World,"+name+"!";
	}

	@Override
	public String sayHello(HelloPOJO po) {
		System.out.println(po.getSay()+","+po.getName()+"!");
		return po.getSay()+","+po.getName()+"!";
	}
	
	@Override
	public HelloPOJO say(HelloPOJO po) {
		System.out.println(po.toString());
		return po;
	}

}
