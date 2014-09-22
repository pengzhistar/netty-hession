package per.code.pz.client.netty;

import java.net.InetSocketAddress;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.jboss.netty.bootstrap.ClientBootstrap;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelFutureListener;
import org.jboss.netty.channel.SimpleChannelUpstreamHandler;
import org.jboss.netty.channel.socket.nio.NioClientSocketChannelFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import per.code.pz.rpc.transport.Transport;



/**
 * 功能描述：client proxy.
 *
 * @author  彭志(pengzhi@talkweb.com.cn)
 *
 * <p>修改历史：(修改人，修改时间，修改原因/内容)</p>
 */
public class Client {
	
	private static final Logger logger = LoggerFactory.getLogger(Client.class);
	
	public static final ConcurrentHashMap<String, Client> allClients = new ConcurrentHashMap<String, Client>();
	
	private final ConcurrentHashMap<String, ResultHandler> callBackHandlerMap = new ConcurrentHashMap<String, ResultHandler>();
	private ConnectionPool connectionPool = null; //连接池
	private String address; //服务端的地址
	private int port; //服务端的端口号
	
	public static int connectTimeout = 5000; //建立一个链接的最大耗时时间, 毫秒单位
	
	private ClientBootstrap bootstrap = null;
	
	private Client(String address, int port){
		this.address = address;
		this.port = port;
		
		this.connectionPool = new ConnectionPool();
		
		//default bootstrap
		SimpleChannelUpstreamHandler handler = new ClientHandler(callBackHandlerMap);
		this.bootstrap = new ClientBootstrap(new NioClientSocketChannelFactory(Executors.newCachedThreadPool(), Executors.newCachedThreadPool()));
		this.bootstrap.setPipelineFactory(new PipelineClientFactory(handler));
		this.bootstrap.setOption("tcpNoDelay", true);
		this.bootstrap.setOption("keepAlive", true);
	}
	
	/**
	 * 建立一个服务端代理
	 * @param address
	 * @param port
	 * @param connectTimeout
	 * @param writeTimeout
	 */
	private Client(String address, int port, int connectTimeout){
		this.address = address;
		this.port = port;
		this.connectTimeout = connectTimeout;
		this.connectionPool = new ConnectionPool();
	
		//default bootstrap
		SimpleChannelUpstreamHandler handler = new ClientHandler(callBackHandlerMap);
		this.bootstrap = new ClientBootstrap(new NioClientSocketChannelFactory(Executors.newCachedThreadPool(), Executors.newCachedThreadPool()));
		this.bootstrap.setPipelineFactory(new PipelineClientFactory(handler,this.connectTimeout));
		this.bootstrap.setOption("tcpNoDelay", true);
		this.bootstrap.setOption("keepAlive", true);
	}
	
	/**
	 * 根据服务端的地址和端口生成该client所对应的key
	 * @param address
	 * @param port
	 * @return
	 */
	private static String generateServerKey(String address, int port){
		return address+":"+port;
	}
	
	/**
	 * 生成该服务端地址和端口的客户端代理
	 * @param address
	 * @param port
	 * @return
	 */
	public static Client createClientProxy(String address, int port, int connectTimeout){
		synchronized (allClients) {
			String serverKeyString = generateServerKey(address, port);
			if(!allClients.containsKey(serverKeyString)){
				allClients.put(serverKeyString, new Client(address, port, connectTimeout));
			}
			return allClients.get(serverKeyString);
		}
	}
	
	public static Client createClientProxy(String address, int port){
		synchronized (allClients) {
			String serverKeyString = generateServerKey(address, port);
			if(!allClients.containsKey(serverKeyString)){
				allClients.put(serverKeyString, new Client(address, port));
			}
			return allClients.get(serverKeyString);
		}
	}
	
	
	/**
	 * 非阻塞访问方式
	 * 发送request，并返回client sender。
	 * 通过client sender获取结果
	 * @param request
	 * @return
	 */
	public ClientSender nonBlockingGet(Transport request){
		
		if(connectionPool==null || bootstrap==null){
			throw new IllegalStateException("(connectionPool==null || bootstrap==null) must invoke createClientProxy method first");
		}
		
		ClientSender sender = getClientSender();
		
		if(sender!=null){
			sender.preGet(request);
		}else{
			logger.error("getClientSender()==null");
		}
		
		return sender;
	}
	
	
	/**
	 * 阻塞式访问方式，在timeout里面返回结果，否则为null
	 * @param request
	 * @param timeout
	 * @return
	 */
	public byte[] blockingGet(Transport request, long timeout){
		if(connectionPool==null || bootstrap==null){
			throw new IllegalStateException("(connectionPool==null || bootstrap==null) must invoke createClientProxy method first");
		}
		
		ClientSender sender = getClientSender();
		if(sender!=null){
			return (byte[] )sender.blockingGet(request, timeout);		
		}else{
			logger.error("getClientSender()==null");
		}
		return null;
	}
	
	
	/**
	 * 发送消息，不获取返回结果，必须与服务端配合使用。
	 * 需确保服务端在处理该请求后不需要返回结果
	 * @param request
	 */
	public void sendNoBack(Transport request){
		if(connectionPool==null || bootstrap==null){
			throw new IllegalStateException("(connectionPool==null || bootstrap==null) must invoke createClientProxy method first");
		}
		ClientSender sender = getClientSender();
		if(sender!=null){
			sender.sendNoBack(request);		
		}else{
			logger.error("getClientSender()==null");
		}
	}
	
	
	/**
	 * 获取一个消息发送接受对象
	 * @return
	 */
	private synchronized ClientSender getClientSender(){
		
		Channel channel = connectionPool.getChannel();
		
		if (channel!=null && channel.isConnected()) { //如果拿到的channel合法
			return new ClientSender(channel, callBackHandlerMap, connectionPool);
		}else{ //非法channel, 则建立一个新的链接
			channel = createConnect(this.address, this.port);
			if (channel==null || !channel.isConnected()) {
				logger.error("get a connection failed or connection is not connected!");
				return null;
			}
			return new ClientSender(channel, callBackHandlerMap, connectionPool); 
		}
		
	}
	
	/**
	 * 新建一个连接
	 * @param address
	 * @param port
	 * @return
	 */
	private Channel createConnect(String address, int port){
		
		logger.debug("Create one new connection");
		
		ChannelFuture future = bootstrap.connect(new InetSocketAddress(address, port));
		
		final CountDownLatch latch = new CountDownLatch(1);
		
		future.addListener(new ChannelFutureListener() {
			
			public void operationComplete(ChannelFuture arg0) throws Exception {
				latch.countDown();
			}
		});
		
		try{
			latch.await(connectTimeout, TimeUnit.MILLISECONDS);
		}catch (Exception e) {
			logger.error("CountDownLatch await error.", e);
		}
		
		return future.getChannel();
	}
	
	/**
	 * 关闭所有的连接，并释放所有的资源
	 */
	public void close(){
		
		if(connectionPool!=null){
			connectionPool.close();
			connectionPool = null;
		}
		
		try{
			if(bootstrap!=null){
				bootstrap.releaseExternalResources();
			}
		}catch (Exception e) {
			e.printStackTrace();
			logger.error("bootstrap release external resources error.", e);
		}finally{
			bootstrap=null;
		}
		
		allClients.remove(generateServerKey(address, port));
	}
}
