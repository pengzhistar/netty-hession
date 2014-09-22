/**
 * BeanNoDefineException.java	  V1.0   2014年9月18日 下午2:16:32
 *
 * Copyright pengzhistar@sina.com All rights reserved.
 *
 * Modification history(By    Time    Reason):
 * 
 * Description:
 */

package per.code.pz.server.bean;

/**
 * 功能描述：服务未定义
 *
 * @author 彭志(pengzhistar@sina.com.cn)
 *
 * <p>修改历史：(修改人，修改时间，修改原因/内容)</p>
 */
public class BeanNoDefineException extends RuntimeException{

	public BeanNoDefineException(String string) {
		super(string);
	}
	
}
