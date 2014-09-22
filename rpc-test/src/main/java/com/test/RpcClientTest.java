package com.test;


import per.code.pz.client.proxy.RemoteInterfaceProxyFactory;

public class RpcClientTest {



	public static void main(String[] args) {
		
		RemoteInterfaceProxyFactory factory = new RemoteInterfaceProxyFactory();
		for(int i = 0 ;i< 1;i++){
			HelloWorld helloWorld = factory.create(HelloWorld.class, "helloWorld");
			HelloPOJO po = new HelloPOJO();
			po.setName("彭志");
			po.setSay("你好啊！");
			po = helloWorld.say(po);
			System.out.println("收到的返回信息:"+po);
		}
		
//      HessianProxyFactory factory2 = new HessianProxyFactory();
//		try {
//			 Hello h = (Hello) factory2.create(Hello.class, "http://127.0.0.1:8080/hello");
//			 System.out.println(h.say(new Data()));
//		} catch (MalformedURLException e) {
//			e.printStackTrace();
//		}
	}
}
