package com.shihui.openpf.living.io3rd;

import java.nio.channels.AsynchronousSocketChannel;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

@Repository
public class GuangdaDao {

	@Value("${request_host_ip}")
	private String requestIp;
	@Value("${request_host_port}")
	private int requestPort;
	
	public boolean sendRequest(Object req) {
		String xml = FastXML.beanToXml(req);

		try {
		return RequestSocket.sendPacket(requestIp, requestPort, 
				(req.getClass() == ReqKey.class) ? Codec.encodeNoMac(xml) : Codec.encode(xml) );
		}catch(Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	public Object recvResponse(AsynchronousSocketChannel channel) {
		Object object = null;
		StringBuilder xmlPacket = ResponseSocket.receivePacket(channel);
		if(xmlPacket != null) {
			try {
				String xml = Codec.decode(xmlPacket);
				object = FastXML.xmlToBean(xml, ResKey.class, ResQuery.class, ResPay.class, PacketNotify.class, PacketError.class);
				PacketCheck pc = (PacketCheck)object;
				if(!pc.check())
					return null;
			}catch(Exception e) {
				e.printStackTrace();
			}
		}
		return object;
	}
}
