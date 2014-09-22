/**
 * Encoder.java	  V1.0   2014年9月17日 下午4:13:10
 *
 * Copyright pengzhistar@sina.com All rights reserved.
 *
 * Modification history(By    Time    Reason):
 * 
 * Description:
 */

package per.code.pz.rpc.transport;

import java.nio.charset.Charset;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.handler.codec.oneone.OneToOneEncoder;

public class Encoder extends OneToOneEncoder{

	/**
	 * 请求传输协议： 
	 * +--+--+--+--+--+...-+--+-...-+ 
	 * |a |b |c |d | e|  f  |g |  h  |
	 * +--+--+--+--+--+...-+--+-...-+ 
	 * a 标识位             1个字节
	 * b 协议类别        1个字节 
	 * c 标识d的长度 4个字节 
	 * d 请求key
	 * e beanName长度  4个字节
	 * f BeanName体  x个字节 
	 * g 标识f的长度 4个字节 
	 * h 方法参数体    x个字节
	 */
	@Override
	protected Object encode(ChannelHandlerContext arg0, Channel channel,
			Object obj) throws Exception {
		if(obj == null){
			return null;
		}
		Transport transport = (Transport) obj;
		//固定长度位是14 abceg
		int bufferLength = 14;
		//d key长度
		if(transport.getKey() == null){
			throw new IllegalArgumentException("transport key is null");
		}
		byte[] keyByte = transport.getKey().getBytes();
		int keyLength = keyByte.length;
		bufferLength += keyLength;
		// e beanName长度
		if(transport.getBeanName() == null){
			throw new IllegalArgumentException("transport beanName is null");
		}
		byte[] beanNameByte = transport.getBeanName().getBytes(Charset.forName("ascii"));
		int beanNameLength = beanNameByte.length;
		bufferLength += beanNameLength;
		//e 参数体长度
		if(transport.getBodyBuffer() == null){
			throw new IllegalArgumentException("transport beanName is null");
		}
		int bodyLength = transport.getBodyBuffer().length;
		bufferLength += bodyLength;

		ChannelBuffer requestBuffer = ChannelBuffers.buffer(bufferLength);
		requestBuffer.writeByte(0);// 默认同步
		requestBuffer.writeByte(transport.getProtocol().getIndex());
		requestBuffer.writeInt(keyLength); // 写入BeanName长度
		requestBuffer.writeBytes(keyByte);
		requestBuffer.writeInt(beanNameLength);
		requestBuffer.writeBytes(beanNameByte);// 写入BeanName
		requestBuffer.writeInt(bodyLength);// 写入Body长度
		requestBuffer.writeBytes(transport.getBodyBuffer());// 写入Body

		return requestBuffer;
	}

}
