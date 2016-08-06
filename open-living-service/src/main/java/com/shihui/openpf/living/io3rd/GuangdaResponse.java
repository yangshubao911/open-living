package com.shihui.openpf.living.io3rd;

import javax.annotation.PostConstruct;

import org.springframework.stereotype.Component;

import me.weimi.api.commons.util.ApiLogger;

@Component
public class GuangdaResponse {

	@PostConstruct
	public void init() {
		
	}

    public void doResPay(ResPay resPay) {
    	//TODO
    }
    
    public void doResQuery(ResQuery resQuery) {
    	//TODO
    }
    
    public void doPacketError(PacketError packetError) {
    	//TODO
    }
    
    public void doPacketNotify(PacketNotify packetNotify) {
    	//TODO
    }
    
    public void doResKey(ResKey resKey) {
    	for(int i=0; i< 3; i++) {
	    	try {
	    		Codec.writeKey(resKey.tout.keyValue, resKey.tout.verifyValue, resKey.tout.keyValue1, resKey.tout.verifyValue1);
	    		return;
	    	}catch(Exception e) {
	    		ApiLogger.info("!!!ExecuteAnalysePacketTask Exception : doResKey() : " + e.getMessage());
	    	}
	    	try {
	    		Thread.sleep(3000);
	    	}catch(Exception e) {
	    	}
    	}
    }

}
