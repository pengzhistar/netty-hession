package per.code.pz.server.netty;

import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.handler.timeout.IdleStateHandler;
import org.jboss.netty.util.HashedWheelTimer;

import per.code.pz.rpc.transport.Decoder;
import per.code.pz.rpc.transport.Encoder;


/**
 * 功能描述：连接通道工厂配置
 *
 * @author  彭志(pengzhi@talkweb.com.cn)
 *
 * <p>修改历史：(修改人，修改时间，修改原因/内容)</p>
 */
public class PipelineServerFactory implements ChannelPipelineFactory{
	
	
	public static final int readerIdleTimeSeconds = 30; //单位为秒
	public static final int writerIdleTimeSeconds = 30; //单位为秒
	public static final int allIdleTimeSeconds = 300; //单位为妙
	
	private AbstractServerHandler serverHandler;
	private static final HashedWheelTimer WHEEL_TIMER = new HashedWheelTimer();
	
	public PipelineServerFactory(AbstractServerHandler serverHandler){
		this.serverHandler = serverHandler;
	}

	public ChannelPipeline getPipeline() throws Exception {
		
		ChannelPipeline pipeline = Channels.pipeline();
		
		pipeline.addLast("decoder", new Decoder());
		pipeline.addLast("encoder", new Encoder());
		pipeline.addLast("timeout", new IdleStateHandler(WHEEL_TIMER, readerIdleTimeSeconds, writerIdleTimeSeconds, allIdleTimeSeconds));
		pipeline.addLast("heartbeat", new HeartBeat());
		pipeline.addLast("handler", this.serverHandler);
		
		return pipeline;
	}

}
