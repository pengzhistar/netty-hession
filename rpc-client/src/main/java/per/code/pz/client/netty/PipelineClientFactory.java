package per.code.pz.client.netty;

import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.channel.SimpleChannelUpstreamHandler;
import org.jboss.netty.handler.timeout.ReadTimeoutHandler;
import org.jboss.netty.util.HashedWheelTimer;

import per.code.pz.rpc.transport.Decoder;
import per.code.pz.rpc.transport.Encoder;



/**
 * 功能描述：连接工厂实现
 *
 * @author  彭志(pengzhi@talkweb.com.cn)
 *
 * <p>修改历史：(修改人，修改时间，修改原因/内容)</p>
 */
public class PipelineClientFactory implements ChannelPipelineFactory{
	
	private SimpleChannelUpstreamHandler handler;
	
	private int connectTimeout = 0;
	
	public PipelineClientFactory(SimpleChannelUpstreamHandler handler){
		this.handler = handler;
	}
	
	public PipelineClientFactory(SimpleChannelUpstreamHandler handler,int connectTimeout){
		this.handler = handler;
		this.connectTimeout = connectTimeout;
	}

	public ChannelPipeline getPipeline() throws Exception {
		ChannelPipeline pipeline = Channels.pipeline();
		
		pipeline.addLast("decoder", new Decoder());
		pipeline.addLast("encoder", new Encoder());
		pipeline.addLast("handler", handler);
		if(connectTimeout > 0){
			 pipeline.addLast("timeout", new ReadTimeoutHandler(new HashedWheelTimer(), connectTimeout/1000)); 
		}
		
		return pipeline;
	}

}
