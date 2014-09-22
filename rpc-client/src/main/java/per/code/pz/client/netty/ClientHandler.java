package per.code.pz.client.netty;

import java.util.concurrent.ConcurrentHashMap;

import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelStateEvent;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelUpstreamHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import per.code.pz.rpc.transport.Transport;

/**
 * 功能描述：客户端消息常规处理
 *
 * @author 彭志(pengzhistar@sina.com.cn)
 *
 * <p>修改历史：(修改人，修改时间，修改原因/内容)</p>
 */
public class ClientHandler extends SimpleChannelUpstreamHandler{
	
	private static final Logger logger = LoggerFactory.getLogger(ClientHandler.class);
	
	private ConcurrentHashMap<String, ResultHandler> callbackHandlerMap;
	
	public ClientHandler(ConcurrentHashMap<String, ResultHandler> callbackHandlerMap){
		this.callbackHandlerMap = callbackHandlerMap;
	}
	
	public void messageReceived(ChannelHandlerContext ctx, MessageEvent e){
		Transport transport = (Transport)e.getMessage();
		String keyString = new String(transport.getKey());
		ResultHandler handler = callbackHandlerMap.remove(keyString);
		if(handler!=null){
			handler.processor(transport.getBodyBuffer());
		}else{
			logger.error("Can not find the handle with the key {}", keyString);
		}
	}
	
	public void channelClosed(ChannelHandlerContext ctx, ChannelStateEvent e) throws Exception{
		super.channelClosed(ctx, e);
	}
	
	public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e) throws Exception{
		logger.error("Client Catch a Exception", e.getCause());
		e.getChannel().close();
	}

}
