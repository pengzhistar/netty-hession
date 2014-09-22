package per.code.pz.client.proxy;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import per.code.pz.client.netty.Client;
import per.code.pz.rpc.transport.Protocol;
import per.code.pz.rpc.transport.Transport;

import com.caucho.hessian.io.AbstractHessianOutput;
import com.caucho.hessian.io.Hessian2Output;
import com.caucho.hessian.io.HessianInput;
import com.caucho.hessian.io.HessianOutput;
import com.caucho.hessian.io.SerializerFactory;
import com.caucho.services.server.AbstractSkeleton;

/**
 * 功能描述：接口代理请求
 * 
 * @author 彭志(pengzhi@talkweb.com.cn)
 * 
 *         <p>
 *         修改历史：(修改人，修改时间，修改原因/内容)
 *         </p>
 */
public class RemoteInterfaceProxy implements InvocationHandler {
	private Logger logger = LoggerFactory.getLogger(getClass());
	private String host;
	private int port;
	private String beanName;
	private RemoteInterfaceProxyFactory _factory;

	public RemoteInterfaceProxy(String beanName,
			RemoteInterfaceProxyFactory syncNessianBeanFactory) {
		this.beanName = beanName;
		this._factory = syncNessianBeanFactory;
	}
	
	/**
	 * 方法代理执行
	 */
	@Override
	public Object invoke(Object proxy, Method method, Object[] args) {
		String methodName = method.getName();
		Class<?>[] params = method.getParameterTypes();
		//如果object的原生方法
		if (methodName.equals("equals") && params.length == 1
				&& params[0].equals(Object.class)) {
			Object value = args[0];
			if (value == null || !Proxy.isProxyClass(value.getClass())) {
				return Boolean.FALSE;
			}
			Object proxyHandler = Proxy.getInvocationHandler(value);
			if (!(proxyHandler instanceof RemoteInterfaceProxy)) {
				return Boolean.FALSE;
			}
			RemoteInterfaceProxy handler = (RemoteInterfaceProxy) proxyHandler;
			return equals(handler);
		} else if (methodName.equals("hashCode") && params.length == 0) {
			return hashCode();
		} else if (methodName.equals("getHessianType")) {
			return proxy.getClass().getInterfaces()[0].getName();
		} else if (methodName.equals("getHessianURL")) {
			return "tcp://" + host + ":" + port + "/" + beanName;
		} else if (methodName.equals("toString") && params.length == 0) {
			return toString();
		}
		//获取远程服务
		final Client client = Client.createClientProxy("localhost", 8080);
		try {
			//组建通讯协议
			Transport transport = new Transport();
			transport.setBeanName(beanName);

			String mangleName = mangleName(method);

			ByteArrayOutputStream baos = new ByteArrayOutputStream(1024);
			AbstractHessianOutput out = new Hessian2Output(baos);
			out.setSerializerFactory(new SerializerFactory());
			out.call(mangleName, args);
			out.flush();

			transport.setBodyBuffer(baos.toByteArray());
			transport.setAsync(true);
			transport.setBeanName(beanName);
			transport.setKey(UUID.randomUUID().toString());
			transport.setProtocol(Protocol.tcp);
			transport.setBodyBuffer(baos.toByteArray());
			byte[] response = client.blockingGet(transport, 10000l);
			ByteArrayInputStream bis = new ByteArrayInputStream(response);
			HessianInput in = new HessianInput(bis);
			return in.readObject();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	/**
	 * 功能描述：如果开启方法重载，那么方法名进行hession计算
	 *
	 * @author  彭志(pengzhi@talkweb.com.cn)
	 * <p>创建日期 ：2014年9月20日 下午2:39:53</p>
	 *
	 * @param method
	 * @return
	 *
	 * <p>修改历史 ：(修改人，修改时间，修改原因/内容)</p>
	 */
	private String mangleName(Method method) {
		Class<?>[] param = method.getParameterTypes();
		if (param == null || param.length == 0) {
			return method.getName();
		} else {
			return AbstractSkeleton.mangleName(method, false);
		}
	}

}
