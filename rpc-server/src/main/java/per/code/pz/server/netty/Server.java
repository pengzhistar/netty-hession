package per.code.pz.server.netty;

import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

import org.jboss.netty.bootstrap.ServerBootstrap;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;

/**
 * 功能描述：服务启动类
 *
 * @author  彭志(pengzhistar@sina.com.cn)
 *
 * <p>修改历史：(修改人，修改时间，修改原因/内容)</p>
 */
public class Server {
	
	private static final Logger logger = LoggerFactory.getLogger(Server.class);
	
	private static final ServerBootstrap bootstrap = new ServerBootstrap(new NioServerSocketChannelFactory(Executors.newCachedThreadPool(), Executors.newCachedThreadPool()));
	
	private int port;
	/**
	 * 通讯接收处理对象
	 */
	private AbstractServerHandler serverHandler;
	/**
	 * 通讯对象
	 */
	private Channel listenChannel;
	
	public Server(int port, AbstractServerHandler serverHandler){
		this.port = port;
		this.serverHandler = serverHandler;
	}
	
	public Server(int port) {
		try{
			Server server=new Server(8080, new ServerHandler());
			server.startUp();
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	
	/**
	 * 启动服务
	 * @throws Exception
	 */
	public void startUp() throws Exception{
		bootstrap.setPipelineFactory(new PipelineServerFactory(this.serverHandler));
		bootstrap.setOption("child.tcpNoDelay", true);
		bootstrap.setOption("child.keepAlive", true);
		bootstrap.setOption("reuseAddress", true);
		listenChannel=bootstrap.bind(new InetSocketAddress(this.port));
		logger.info("Server is started on port: "+this.port);
	}
	
	/**
	 * 关闭服务
	 * @throws Exception
	 */
	public void shutDown() throws Exception{
		
		try{
			listenChannel.close().awaitUninterruptibly(); //close listen channel
		}catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage(),e);
		}finally{
			serverHandler.close();
			logger.info("Server is shutdown on port: "+this.port);
		}
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	
	public static void main(String[] args){
		try{
			Server server=new Server(8080, new ServerHandler());
			server.startUp();
		}catch(Exception e){
			e.printStackTrace();
		}
	}
}
