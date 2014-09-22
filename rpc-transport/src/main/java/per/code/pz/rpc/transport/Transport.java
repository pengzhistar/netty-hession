package per.code.pz.rpc.transport;



/**
 * 功能描述：消息传递基础信息
 *
 * @author  彭志(pengzhi@talkweb.com.cn)
 *
 * <p>修改历史：(修改人，修改时间，修改原因/内容)</p>
 */
public class Transport{
	
	private boolean async;// 标识位   1个字节
	private Protocol protocol;//协议类型，1个字节
	private String key;
	private String beanName;// 服务器端代理调用的Bean对象
	private byte[] bodyBuffer;//

	public boolean isAsync() {
		return async;
	}

	public void setAsync(boolean async) {
		this.async = async;
	}
	
	public void setProtocol(Protocol protocol) {
		this.protocol = protocol;
	}
	
	public Protocol getProtocol() {
		return protocol;
	}
	
	public void setKey(String key) {
		this.key = key;
	}
	
	public String getKey() {
		return key;
	}
	
	public String getBeanName() {
		return beanName;
	}

	public void setBeanName(String beanName) {
		this.beanName = beanName;
	}
	
	public byte[] getBodyBuffer() {
		return bodyBuffer;
	}

	public void setBodyBuffer(byte[] bodyBuffer) {
		this.bodyBuffer = bodyBuffer;
	}
}
