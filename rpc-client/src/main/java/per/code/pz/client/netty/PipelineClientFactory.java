package per.code.pz.client.netty;

import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.channel.SimpleChannelUpstreamHandler;

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
	
	public PipelineClientFactory(SimpleChannelUpstreamHandler handler){
		this.handler = handler;
	}

	public ChannelPipeline getPipeline() throws Exception {
		ChannelPipeline pipeline = Channels.pipeline();
		
		pipeline.addLast("decoder", new Decoder());
		pipeline.addLast("encoder", new Encoder());
		pipeline.addLast("handler", handler);
		
		return pipeline;
	}

}
