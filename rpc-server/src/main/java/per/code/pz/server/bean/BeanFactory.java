package per.code.pz.server.bean;

import java.util.HashMap;

public class BeanFactory {
	/**
	 * 服务工厂
	 */
	private static HashMap<String, Object> beansMap = new HashMap<String, Object>();
	/**
	 * 功能描述：获取bean
	 *
	 * @author 彭志(pengzhistar@sina.com.cn)
	 * <p>创建日期 ：2014年9月18日 下午1:56:55</p>
	 *
	 * @param beanName
	 * @return
	 *
	 * <p>修改历史 ：(修改人，修改时间，修改原因/内容)</p>
	 */
	public static Object getBean(String beanName){
		Object bean = beansMap.get(beanName);
		if(bean == null){
			throw new BeanNoDefineException("no bean named '"+beanName+"'");
		}
		return bean;
	}
	
	public static void defineBean(String key,Object value){
		if(key.equals("nettyServer")){
			return;
		}
		beansMap.put(key, value);
	}
	
}
