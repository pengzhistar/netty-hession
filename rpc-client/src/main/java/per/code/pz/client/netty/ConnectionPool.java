package per.code.pz.client.netty;

import java.util.concurrent.LinkedBlockingQueue;

import org.jboss.netty.channel.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 功能描述：连接池，clientProxy从该池中获取一个客户端发送对象
 *
 * @author  彭志(pengzhi@talkweb.com.cn)
 *
 * <p>修改历史：(修改人，修改时间，修改原因/内容)</p>
 */
public class ConnectionPool {
	
	private static final Logger logger = LoggerFactory.getLogger(ConnectionPool.class);
	
	private LinkedBlockingQueue<Channel> poolBlockingQueue; //阻塞队列
	
	public ConnectionPool(){
		this.poolBlockingQueue = new LinkedBlockingQueue<Channel>();
	}
	
	/**
	 * 从队列中获取一个channel
	 * @return
	 */
	public Channel getChannel(){
		try{
			return poolBlockingQueue.poll();
		}catch (Exception e) {
			logger.error("Pop a channel error.", e);
			return null;
		}
	}
	
	/**
	 * 将channel插入到队列的尾部
	 * @param channel
	 */
	public void addLast(Channel channel){
		try{
			poolBlockingQueue.put(channel);
		}catch (Exception e) {
			logger.error("Add a channel error.", e);
		}
		
	}
	
	/**
	 * 关闭连接池
	 * 将连接池中的链接全部关闭，并清空连接池
	 */
	public synchronized void close(){
		
		Channel channel = null;
		int count = 0;
		
		while(true){
			
			try{
				channel = poolBlockingQueue.poll();
				if(channel!=null){
					count++;
					try{
						channel.close().awaitUninterruptibly();
					}catch (Exception e) {
						logger.error("Close one channel in pool.", e);
					}
					
				}else{
					break;
				}
				
			}catch (Exception e) {
				logger.error("Close all channel in pool.", e);
			}
		}
		
		logger.warn("close the count of all channels is {}", count);
		
		poolBlockingQueue.clear();
	}

}
