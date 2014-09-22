/**
 * HelloPOJO.java	  V1.0   2013-12-26 下午12:13:07
 *
 * Copyright pengzhistar@sina.com All rights reserved.
 *
 * Modification history(By    Time    Reason):
 * 
 * Description:
 */

package com.test;

import java.io.Serializable;


public class HelloPOJO implements Serializable{
	
	String name;
	
	String say;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSay() {
		return say;
	}

	public void setSay(String say) {
		this.say = say;
	}
	
	public static void main(String[] args) {
	}
	
}
