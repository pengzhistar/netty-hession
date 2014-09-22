/**
 * HelloWorld.java	  V1.0   2013-12-26 下午12:11:40
 *
 * Copyright pengzhistar@sina.com All rights reserved.
 *
 * Modification history(By    Time    Reason):
 * 
 * Description:
 */

package com.test;

public interface HelloWorld {
	public String sayHello(String name);
	
	public String sayHello(HelloPOJO po);
	
	public HelloPOJO say(HelloPOJO po);
}
