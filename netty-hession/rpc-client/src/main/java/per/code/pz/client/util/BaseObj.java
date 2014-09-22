/**
 * StringUtil.java	  V1.0   2014年9月17日 下午2:26:18
 *
 * Copyright pengzhistar@sina.com All rights reserved.
 *
 * Modification history(By    Time    Reason):
 * 
 * Description:
 */

package per.code.pz.client.util;

import java.io.Serializable;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;
import java.util.List;

public class BaseObj implements Serializable{
	@Override
	public String toString() {
		StringBuffer buffer = new StringBuffer();
		String objClassName = this.getClass().getName();
		buffer.append(objClassName).append("{");
		// 暂无数组
		// if(this.getClass().isArray()){
		//
		// }
		if (objClassName.equals(List.class.getName())) {
			List list = (List) this;
			buffer.append("[");
			if (list != null)
				for (Object item : list) {
					if (item.getClass().equals(List.class.getName())) {
						item.toString();
					}
				}
			buffer.append("]");
		} else {
			Field[] fields = this.getClass().getDeclaredFields();
			AccessibleObject.setAccessible(fields, true);
			if (fields != null) {
				for (Field field : fields) {
					buffer.append("\n");
					buffer.append("\t").append(field.getName()).append("=");
					try {
						buffer.append(field.get(this)).append(",");
					} catch (IllegalArgumentException e) {
						throw e;
					} catch (IllegalAccessException e) {
						throw new IllegalArgumentException(e);
					}
				}
			}
		}
		buffer.append("\n}");
		return buffer.toString();
	}
}
