package per.code.pz.server.netty;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelFutureListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import per.code.pz.rpc.transport.Transport;

/**
 * 功能描述：服务端回复消息
 *
 * @author 彭志(pengzhistar@sina.com.cn)
 *
 * <p>修改历史：(修改人，修改时间，修改原因/内容)</p>
 */
public class ServerSender {
	
	private static final Logger logger = LoggerFactory.getLogger(ServerSender.class);
	
	private Channel channel;
	private Transport transport; 
	public ServerSender(Channel channel,Transport transport) {
		this.channel = channel;
		this.transport = transport;
	}
	
	/**
	 * 向客户端返回消息
	 * @param ret
	 */
	public void send(ChannelBuffer bodyBuffer){
		transport.setBodyBuffer(bodyBuffer.array());
		channel.write(transport).addListener(new ChannelFutureListener() {
			public void operationComplete(ChannelFuture future) throws Exception {
				if(!future.isSuccess()){
					logger.error("Send a message not success.");
				}else{
					logger.info("Send a message success.");
				}
			}
		});
	}

	public Channel getChannel() {
		return channel;
	}

	public void setChannel(Channel channel) {
		this.channel = channel;
	}
}
