package per.code.pz.client.proxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;

import com.caucho.hessian.io.HessianRemoteObject;

/**
 * 功能描述：接口代理工厂
 *
 * @author  彭志(pengzhistar@sina.com.cn)
 *
 * <p>修改历史：(修改人，修改时间，修改原因/内容)</p>
 */
public class RemoteInterfaceProxyFactory {
	
	private ClassLoader _loader;
	
	/**
	 * 功能描述：创建代理类
	 *
	 * @author  彭志(pengzhistar@sina.com.cn)
	 * <p>创建日期 ：2014年9月20日 下午2:38:21</p>
	 *
	 * @param api
	 * @param beanName
	 * @param loader
	 * @return
	 *
	 * <p>修改历史 ：(修改人，修改时间，修改原因/内容)</p>
	 */
	public <T> T create(Class<T> api, String beanName) {
		if (api == null)
			throw new NullPointerException("api must not be null for HessianProxyFactory.create()");
		InvocationHandler invokeHandler = new RemoteInterfaceProxy(beanName,this);
		Object  obj = Proxy.newProxyInstance(_loader, new Class[] { api, HessianRemoteObject.class }, invokeHandler);
		return api.cast(obj);
	}
	
	public RemoteInterfaceProxyFactory()
	  {
	    this._loader = Thread.currentThread().getContextClassLoader();
	  }

}
