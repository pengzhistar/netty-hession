package per.code.pz.server.netty;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.caucho.hessian.io.SerializerFactory;

import per.code.pz.rpc.transport.Protocol;
import per.code.pz.rpc.transport.Transport;
import per.code.pz.server.ServerSkeleton;
import per.code.pz.server.bean.BeanFactory;




/**
 * 功能描述：通讯业务处理
 *
 * @author  彭志(pengzhi@talkweb.com.cn)
 *
 * <p>修改历史：(修改人，修改时间，修改原因/内容)</p>
 */
public class ServerHandler extends AbstractServerHandler{
	
	Logger logger = LoggerFactory.getLogger(this.getClass());

	@Override
	protected void processor(Channel channel, Transport transport) {
		
		try{
			Object bean = BeanFactory.getBean(transport.getBeanName());
			ServerSkeleton _serviceProxy = new ServerSkeleton(bean,bean.getClass());
			ChannelBuffer channelBuffer = _serviceProxy.invoke(transport.getBodyBuffer(),new SerializerFactory());
			ServerSender sender = new ServerSender(channel,transport);
			sender.send(channelBuffer);
		}catch (Exception e) {
			e.printStackTrace();
			throw new IllegalAccessError(e.getMessage());
		}		
	}
}
