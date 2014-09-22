/**
 * RPCDecoder.java	  V1.0   2013-12-26 上午11:24:53
 *
 * Copyright pengzhistar@sina.com All rights reserved.
 *
 * Modification history(By    Time    Reason):
 * 
 * Description:
 */

package per.code.pz.rpc.transport;

import java.nio.charset.Charset;
import java.util.concurrent.atomic.AtomicLong;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.handler.codec.frame.FrameDecoder;
import org.jboss.netty.handler.codec.replay.VoidEnum;


public class Decoder extends FrameDecoder{
	/**
	 * 请求传输协议： 
	 * +--+--+--+--+--+...-+--+-...-+ 
	 * |a |b |c |d | e|  f  |g |  h  |
	 * +--+--+--+--+--+...-+--+-...-+ 
	 * a 标识位             1个字节
	 * b 协议类别        1个字节 
	 * c 标识d的长度 4个字节 
	 * d 请求key
	 * e beanName长度
	 * f BeanName体  x个字节 
	 * g 标识f的长度 4个字节 
	 * h 方法参数体    x个字节
	 */
	@Override
	protected Object decode(ChannelHandlerContext arg0, Channel channel,
			ChannelBuffer buffer) throws Exception {
		
		if (buffer.readableBytes() < 15) {
			return null;
		}
		byte async = buffer.readByte();//a 标识位             1个字节
		byte protal = buffer.readByte();//b 协议类别        1个字节 
		int keyLength = buffer.readInt();//c 标识d key 的长度 4个字节 
		String key = new String(buffer.readBytes(keyLength).array()); //d读取
		int beanNameLength= buffer.readInt();//e 标识beanName的长度 4个字节 
		String beanName = new String(buffer.readBytes(beanNameLength).array(),Charset.forName("ascii"));//f beanName获取
		int bodylength = buffer.readInt();//g 标识h的长度 4个字节 
		byte[] bodyBuffer = buffer.readBytes(bodylength).array();//g 方法参数体    x个字节
		
		Transport message = new Transport();
		message.setAsync(async == 0 ? false : true);
		message.setProtocol(Protocol.gen(protal));
		message.setKey(key);
		message.setBeanName(beanName);
		message.setBodyBuffer(bodyBuffer);

		return message;
	}
}
