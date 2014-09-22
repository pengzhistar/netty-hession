/**
 * Protocol.java	  V1.0   2014年9月18日 上午9:34:27
 *
 * Copyright pengzhistar@sina.com All rights reserved.
 *
 * Modification history(By    Time    Reason):
 * 
 * Description:
 */

package per.code.pz.rpc.transport;


public enum Protocol {
	tcp(80, (byte)0), http(80, (byte)1);

	private int defaltPort;
	private byte index;

	public static Protocol gen(byte index) {
		Protocol tp = Protocol.tcp;
		switch (index) {
		case (byte)0:
			tp = Protocol.tcp;
			break;
		case (byte)1:
			tp = Protocol.http;
			break;
		}
		return tp;
	}

	private Protocol(int defaltPort, byte index) {
		this.defaltPort = defaltPort;
		this.index = index;
	}

	public int getDefaultPort() {
		return defaltPort;
	}

	public byte getIndex() {
		return index;
	}
	
}
