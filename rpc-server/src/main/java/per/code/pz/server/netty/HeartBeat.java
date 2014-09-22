package per.code.pz.server.netty;

import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.handler.timeout.IdleState;
import org.jboss.netty.handler.timeout.IdleStateAwareChannelHandler;
import org.jboss.netty.handler.timeout.IdleStateEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 功能描述：心跳检测，如果连续3次，还没有任何操作，则做断线处理
 *
 * @author 彭志(pengzhistar@sina.com.cn)
 *
 * <p>修改历史：(修改人，修改时间，修改原因/内容)</p>
 */
public class HeartBeat extends IdleStateAwareChannelHandler{
	
	private static final Logger logger = LoggerFactory.getLogger(HeartBeat.class);
	
	int i = 0;
	
	public void channelIdle(ChannelHandlerContext ctx, IdleStateEvent e) throws Exception{
		
		super.channelIdle(ctx, e);
		
		if(e.getState()==IdleState.ALL_IDLE){
			i++;
		}
			
		if(i==3){
			e.getChannel().close();
			logger.warn("Long time no operation, close this channel.");
		}
	}

}
