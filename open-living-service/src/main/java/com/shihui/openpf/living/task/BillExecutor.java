package com.shihui.openpf.living.task;

import java.nio.channels.AsynchronousSocketChannel;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

//import me.weimi.api.commons.util.ApiLogger;
import com.shihui.commons.ApiLogger;
import com.shihui.openpf.living.cache.CacheDao;
import com.shihui.openpf.living.io3rd.Codec;
import com.shihui.openpf.living.io3rd.FastXML;
import com.shihui.openpf.living.io3rd.GuangdaResponse;
import com.shihui.openpf.living.io3rd.PacketCheck;
import com.shihui.openpf.living.io3rd.PacketError;
import com.shihui.openpf.living.io3rd.PacketHead;
import com.shihui.openpf.living.io3rd.PacketNotify;
import com.shihui.openpf.living.io3rd.ReqPay;
import com.shihui.openpf.living.io3rd.ReqQuery;
import com.shihui.openpf.living.io3rd.ResHead;
import com.shihui.openpf.living.io3rd.ResKey;
import com.shihui.openpf.living.io3rd.ResPay;
import com.shihui.openpf.living.io3rd.ResQuery;
import com.shihui.openpf.living.io3rd.ResponseSocket;
import com.shihui.openpf.living.io3rd.ServerAIO;
import com.shihui.openpf.living.mq.LivingMqProducer;
import com.shihui.openpf.living.util.LivingUtil;

@Component
public class BillExecutor {
	
	private static final int AWAITTERMINATION_SECONDS = 10;
	
	private static final ExecutorService LISTEN_EXECUTOR_SERVICE = Executors.newSingleThreadExecutor();
	
    private static final ExecutorService ANALYSIS_EXECUTOR_SERVICE = Executors.newFixedThreadPool(16);
    private static final ExecutorService RESPONSE_EXECUTOR_SERVICE = Executors.newFixedThreadPool(16);

    private ExecuteResponseListenTask RESPONSE_LISTEN_TASK = new ExecuteResponseListenTask();

	@Value("${response_host_port}")
	private int responsePort;    

    @Resource
    private LivingMqProducer mqProducer;
    
    @Resource
    GuangdaResponse guangdaResponse;



    @PostConstruct
    public void init() {
    	LISTEN_EXECUTOR_SERVICE.submit(RESPONSE_LISTEN_TASK);
//    	ApiLogger.info("BillExecutor : init()");
    }

    @PreDestroy
    public void predestroy() {
    	ApiLogger.info("BillExecutor : predestroy() : start ");
    	
    	RESPONSE_LISTEN_TASK.destroy();
    	
    	LISTEN_EXECUTOR_SERVICE.shutdown();
    	ANALYSIS_EXECUTOR_SERVICE.shutdown();
    	RESPONSE_EXECUTOR_SERVICE.shutdown();
    	
    	try {
    		LISTEN_EXECUTOR_SERVICE.awaitTermination(AWAITTERMINATION_SECONDS, TimeUnit.SECONDS);
    		RESPONSE_EXECUTOR_SERVICE.awaitTermination(AWAITTERMINATION_SECONDS, TimeUnit.SECONDS);
    		ANALYSIS_EXECUTOR_SERVICE.awaitTermination(AWAITTERMINATION_SECONDS, TimeUnit.SECONDS);
    	}catch(InterruptedException e) {
    		ApiLogger.info("!!!BillExecutor : Exception : predestroy() : " + e.getMessage());
    	}
    	ApiLogger.info("BillExecutor : predestroy() : end ");
    }

    public  class ExecuteResponseListenTask implements Runnable {

    	private ServerAIO serverAIO = null;

    	public ExecuteResponseListenTask() {

    	}
    	public void destroy() {
    		if(serverAIO != null)
    			serverAIO.destroy();
    	}

    	@Override
        public void run() {
        	
    		ApiLogger.info(">>>ExecuteResponseListenTask RUNNING");
    		try {    		
    			ApiLogger.info("ExecuteResponseListenTask : ServerAIO.instance(responsePort) : " + responsePort);
    			serverAIO = ServerAIO.instance(responsePort);
    		}catch(Exception e) {
    			ApiLogger.info("!!!ExecuteResponseListenTask : Exception : ExecuteResponseListenTask() : " + e.getMessage());
    		}
    		//
    		guangdaResponse.doReqKey();
    		//
            while(!Thread.currentThread().isInterrupted() && serverAIO.isOpen()) {
            	try {
            		AsynchronousSocketChannel channel = serverAIO.accept();
            		if(channel != null) {
	            		ExecuteReceiveTask ert = new ExecuteReceiveTask(channel);
	            		RESPONSE_EXECUTOR_SERVICE.submit(ert);
            		}
            	}catch(Exception e) {
        			ApiLogger.info("!!!ExecuteResponseListenTask : run() : Exception  : " + e.getMessage());
        		}

            }
            
            ApiLogger.info("<<<ExecuteResponseListenTask STOP");
            }
    	
    	public class ExecuteReceiveTask implements Runnable {

    		private AsynchronousSocketChannel channel;
    		
    		public ExecuteReceiveTask(AsynchronousSocketChannel channel) {
    			this.channel = channel;
    		}
    	    @Override
    	    public void run() {
    	    	ApiLogger.info(">>>ExecuteReceiveTask RUNNING");
    	        try {
    	        	StringBuilder sb = ResponseSocket.receivePacket(channel);
    	        	if(sb != null) {
    	        		mqProducer.sendResponse(java.util.UUID.randomUUID().toString(), sb.toString());
    	        	}
    	        }catch (Exception e){
    	        	ApiLogger.info("!!!ExecuteReceiveTask Exception : run() : " + e.getMessage());
    	        }
    	        ApiLogger.info("<<<ExecuteReceiveTask STOP");
    	    }
    	}
    }

	public boolean newExecuteAnalysePacketTask(String xml) {
		ExecuteAnalysePacketTask eapt =new ExecuteAnalysePacketTask(xml);
		try {
			ANALYSIS_EXECUTOR_SERVICE.submit(eapt);
			return true;
		} catch(Exception e) {
			ApiLogger.info("!!!BillExecutor : Exception : newExecuteAnalysePacketTask() : " + e.getMessage());
		}
		return false;
	}
	
	public class ExecuteAnalysePacketTask implements Runnable {

		private String packet;
		
		public ExecuteAnalysePacketTask(String packet) {
			this.packet = packet;
		}
		
	    @Override
	    public void run() {
	    	ApiLogger.info(">>>ExecuteAnalysePacketTask RUNNING");
	        try {
				String xml = Codec.decode(packet);
				//TODO XXX
				LivingUtil.log(xml);
				//
				Object object = FastXML.xmlToBean(xml, ResHead.class);
				if( object == null ) 
					ApiLogger.info("!!!ExecuteAnalysePacketTask : run() : object == null \n");
				else {
//					ApiLogger.info("!!!ExecuteAnalysePacketTask : run() : pc.check() ready \n");
    				PacketCheck pc = (PacketCheck)object;
    				if(!pc.check())
    					ApiLogger.info("ExecuteAnalysePacketTask : run() : !pc.check() = true \n");
    				else {
//    					ApiLogger.info("ExecuteAnalysePacketTask : run() : pc.check() : start");
    					
    					ResHead rh = (ResHead)object;
    					ApiLogger.info("\nExecuteAnalysePacketTask : run() : AnsTranCode:[" + rh.head.AnsTranCode + "] InstId:[" + rh.head.InstId +"] TrmSeqNum:[" +rh.head.TrmSeqNum +"] version:[" + rh.head.version + "]\n");

    					String ansTranCode = ((ResHead)object).head.AnsTranCode.trim();
    					if(ansTranCode.compareTo(PacketHead.ANSTRANCODE_PAY) == 0)
    						guangdaResponse.doResPay((ResPay)FastXML.xmlToBean(xml, ResPay.class));
    					else if(ansTranCode.compareTo(PacketHead.ANSTRANCODE_QUERY) == 0)
    						guangdaResponse.doResQuery((ResQuery)FastXML.xmlToBean(xml, ResQuery.class));
						else if(ansTranCode.compareTo(PacketHead.ANSTRANCODE_ERROR) == 0)
							guangdaResponse.doPacketError((PacketError)FastXML.xmlToBean(xml, PacketError.class));
						else if(ansTranCode.compareTo(PacketHead.ANSTRANCODE_NOTIFY) == 0)
							guangdaResponse.doPacketNotify((PacketNotify)FastXML.xmlToBean(xml, PacketNotify.class));
						else if(ansTranCode.compareTo(PacketHead.ANSTRANCODE_KEY) == 0)
							guangdaResponse.doResKey((ResKey)FastXML.xmlToBean(xml, ResKey.class));
						else 
							ApiLogger.info("ERR: ExecuteAnalysePacketTask : run() : " + ansTranCode);
//    					ApiLogger.info("ExecuteAnalysePacketTask : run() : pc.check() : end");
    				}
    			}
	        }catch (Exception e){
	        	ApiLogger.info("!!!ExecuteAnalysePacketTask Exception : run() :: " + e.getMessage());
	        }
	        ApiLogger.info("<<<ExecuteAnalysePacketTask STOP");
	    }
	}   
}
