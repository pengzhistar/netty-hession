package per.code.pz.server;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.Method;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.caucho.hessian.io.AbstractHessianInput;
import com.caucho.hessian.io.HessianFactory;
import com.caucho.hessian.io.HessianInputFactory;
import com.caucho.hessian.io.HessianOutput;
import com.caucho.hessian.io.SerializerFactory;
import com.caucho.services.server.AbstractSkeleton;

public class ServerSkeleton extends AbstractSkeleton {
	private Logger logger = LoggerFactory.getLogger(ServerSkeleton.class);
	private Object beanProxy;// 代理Bean
	/**
	 * 这个两个是有状态的 所以不能丢在Handler里面
	 */
	private HessianInputFactory _inputFactory = new HessianInputFactory();
	private HessianFactory _hessianFactory = new HessianFactory();

	public ServerSkeleton(Object beanProxy, Class<?> apiClass) {
		super(apiClass);
		this.beanProxy = beanProxy;
	}

	public ChannelBuffer invoke(byte [] bodyBuffer, SerializerFactory serializerFactory) throws IOException {
		AbstractHessianInput in = null;
		HessianOutput out = null;
		try{
			ByteArrayInputStream bais = new ByteArrayInputStream(bodyBuffer);// 输入
			HessianInputFactory.HeaderType header = _inputFactory.readHeader(bais);
			logger.debug("Current protocol:{}", header);
			in = _hessianFactory.createHessian2Input(bais);
			in.readCall();//
	
			if (serializerFactory != null) {
				in.setSerializerFactory(serializerFactory);
			}
			String methodName = in.readMethod();
			int argLength = in.readMethodArgLength();
			Method method = getMethod(methodName + "__" + argLength);
			if (method == null) {
				method = getMethod(methodName);
			}
	
			Class<?>[] args = method.getParameterTypes();
			if (argLength != args.length && argLength >= 0) {
				logger.error("NoSuchMethod", "method " + method + " argument length mismatch, received length=" + argLength, null);
			}
			Object[] values = new Object[args.length];
			for (int i = 0; i < args.length; i++) {
				values[i] = in.readObject(args[i]);
			}
			Object result = null;
			result = method.invoke(beanProxy, values);
			
			ByteArrayOutputStream bos = new ByteArrayOutputStream();  
		    out = new HessianOutput(bos);  
		    out.writeObject(result);  
		    out.flush();  
			
		    bodyBuffer = bos.toByteArray();// 执行结果
			ChannelBuffer channelBuffer = ChannelBuffers.buffer(bodyBuffer.length);
			channelBuffer.writeBytes(bodyBuffer);
			return channelBuffer;
		}catch (Exception e) {
			logger.error("Proxy Invok Failure !", e);
		}finally{
			if(in != null){
				in.close();
				out.close();
			}
		}
		return null;
	}
}
