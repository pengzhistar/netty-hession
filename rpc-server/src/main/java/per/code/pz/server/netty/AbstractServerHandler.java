package per.code.pz.server.netty;

import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelStateEvent;
import org.jboss.netty.channel.ChildChannelStateEvent;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelUpstreamHandler;
import org.jboss.netty.channel.group.ChannelGroup;
import org.jboss.netty.channel.group.DefaultChannelGroup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import per.code.pz.rpc.transport.Transport;



/**
 * 功能描述：通讯常规处理类
 *
 * @author  彭志(pengzhi@talkweb.com.cn)
 *
 * <p>修改历史：(修改人，修改时间，修改原因/内容)</p>
 */
public abstract class AbstractServerHandler extends SimpleChannelUpstreamHandler{
	
	private static final Logger logger = LoggerFactory.getLogger(AbstractServerHandler.class);
	
	public static final ChannelGroup allChannels = new DefaultChannelGroup("Child-Channel-Group");
	
	public AbstractServerHandler(){
		super();
	}
	
	protected abstract void processor(Channel channel, Transport transport);
	
	@Override
	public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) throws Exception{
		processor(ctx.getChannel(), (Transport)(e.getMessage()));
	}
	
	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e) throws Exception{
		logger.error("Catch a exception, now close this channel.", e.getCause());
		e.getChannel().close();
		e.getCause().printStackTrace();
	}
	
	@Override
	public void channelConnected(ChannelHandlerContext ctx, ChannelStateEvent e) throws Exception{
		logger.info("Child Channel Connected, channelId is {}", e.getChannel().getId());
		super.channelConnected(ctx, e);
	}
	
	@Override
	public void channelDisconnected(ChannelHandlerContext ctx, ChannelStateEvent e) throws Exception{
		logger.info("Child Channel Disconnected, channelId is {}", e.getChannel().getId());
//		super.channelDisconnected(ctx, e);
	}
	
	
	@Override
	public void childChannelOpen(ChannelHandlerContext ctx, ChildChannelStateEvent e) throws Exception{
		logger.info("Child Channel Open, channelId is {}", e.getChannel().getId());
		super.childChannelOpen(ctx, e);
		try{
			allChannels.add(e.getChannel());
		}catch (Exception e1) {
			logger.error("Add channel to allChannels.", e1);
		}
		
	}
	
	@Override
	public void childChannelClosed(ChannelHandlerContext ctx, ChildChannelStateEvent e) throws Exception{
		logger.info("Child Channel Closed, channelId is {}", e.getChannel().getId());
//		super.childChannelClosed(ctx, e);
		Channel channel= e.getChannel();
		if(allChannels.contains(channel)){
			try{
				allChannels.remove(channel);
			}catch (Exception e1) {
				logger.error("Remove channel from allChannels.", e1);
			}
		}
	}
	
	/**
	 * 关闭所有的客户端连接
	 */
	public void close(){
		logger.info("Close all children channels!");
		try{
			allChannels.close().awaitUninterruptibly();
		}catch(Exception e){
			logger.error("Close all children channels Exception.", e);
		}
	}

}
