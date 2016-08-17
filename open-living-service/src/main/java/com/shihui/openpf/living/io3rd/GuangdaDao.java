package com.shihui.openpf.living.io3rd;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import com.shihui.commons.ApiLogger;

@Repository
public class GuangdaDao {

	@Value("${guangda_destination_host_ip}")
	private String remoteIp;
	@Value("${guangda_destination_host_port}")
	private int remotePort;
	
	public boolean sendRequest(Object req) {
		String xml = FastXML.beanToXml(req);
		try {
		return RequestSocket.sendPacket(remoteIp, remotePort, 
				(req.getClass() == ReqKey.class) ? Codec.encodeNoMac(xml) : Codec.encode(xml) );
		}catch(Exception e) {
			ApiLogger.info("!!!GuangdaDao : Exception : " + e.getMessage());
		}
		return false;
	}

//	public Object recvResponse(AsynchronousSocketChannel channel) {
//		Object object = null;
//		StringBuilder xmlPacket = ResponseSocket.receivePacket(channel);
//		if(xmlPacket != null) {
//			try {
//				String xml = Codec.decode(xmlPacket);
//				object = FastXML.xmlToBean(xml, ResKey.class, ResQuery.class, ResPay.class, PacketNotify.class, PacketError.class);
//				PacketCheck pc = (PacketCheck)object;
//				if(!pc.check())
//					return null;
//			}catch(Exception e) {
//				e.printStackTrace();
//			}
//		}
//		return object;
//	}
}
