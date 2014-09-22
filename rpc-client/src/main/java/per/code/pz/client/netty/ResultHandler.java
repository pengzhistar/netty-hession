package per.code.pz.client.netty;


/**
 * 功能描述：请求结果处理
 *
 * @author 彭志(pengzhistar@sina.com.cn)
 *
 * <p>修改历史：(修改人，修改时间，修改原因/内容)</p>
 */
public interface ResultHandler {
	
	public void processor(Object message);

}
