package com.shihui.openpf.living.task;

//import com.alibaba.fastjson.JSONObject;
//import com.shihui.api.order.vo.MerchantCancelParam;
//import me.weimi.api.commons.config.ConfigLoader;
//import me.weimi.api.commons.config.DefaultConfigLoader;
import me.weimi.api.commons.util.ApiLogger;
import org.springframework.stereotype.Component;

import com.shihui.openpf.living.io3rd.Codec;
import com.shihui.openpf.living.io3rd.FastXML;
import com.shihui.openpf.living.io3rd.PacketCheck;
import com.shihui.openpf.living.io3rd.PacketError;
import com.shihui.openpf.living.io3rd.PacketNotify;
import com.shihui.openpf.living.io3rd.RequestSocket;
import com.shihui.openpf.living.io3rd.ResKey;
import com.shihui.openpf.living.io3rd.ResPay;
import com.shihui.openpf.living.io3rd.ResQuery;
import com.shihui.openpf.living.io3rd.ResponseSocket;
//import com.shihui.openpf.living.service.OrderService;

import javax.annotation.PostConstruct;
//import javax.annotation.Resource;
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
import java.util.concurrent.Executors;
//import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import org.springframework.beans.factory.annotation.Value;

//import java.net.ServerSocket;
//import java.net.Socket;
//import java.net.SocketTimeoutException;
import com.shihui.openpf.living.io3rd.ServerAIO;
import java.nio.channels.AsynchronousSocketChannel;
import javax.annotation.PreDestroy;

/**
 * Created by zhoutc on 2015/9/30.
 */
@Component
public class BillExecutor {
	
	private static final int AWAITTERMINATION_SECONDS = 10;
	
    private static final ExecutorService REQUEST_LISTEN_EXECUTOR_SERVICE = Executors.newFixedThreadPool(16);
    private static final ExecutorService RESPONSE_LISTEN_EXECUTOR_SERVICE = Executors.newFixedThreadPool(16);
    
    private ExecuteRequestListenTask REQUEST_LISTEN_TASK = new ExecuteRequestListenTask();
    private ExecuteResponseListenTask RESPONSE_LISTEN_TASK = new ExecuteResponseListenTask();
    
    @Value("${interval_buy_bill}")
    private long INTERVAL_BUYBILL = 10L;

	@Value("${guangda_destination_host_ip}")
	private String remoteIp;
	@Value("${guangda_destination_host_port}")
	private static int remotePort;

	@Value("${request_host_ip}")
	private String requestIp;
	@Value("${request_host_port}")
	private int requestPort;
	@Value("${response_host_port}")
	private int responsePort;    

//    @Resource
//    OrderService orderService;
//    @Resource
//    RequestService requestService;
//    @Resource
//    ProduceService produceService;
//    @Resource
//    LogicFactory logicFactory;
//    @Resource
//    RechargeMerchantService rechargeMerchantService;
//    @Resource
//    MerchantOperatorService merchantOperatorService;
//    @Resource
//    MerchantOperatorPriceService merchantOperatorPriceService;
//    @Resource
//    MerchantGoodsService merchantGoodsService;
//    @Resource
//    OrderSystemService orderSystemService;
//    @Resource
//    LockService lockService;
//    @Resource
//    NoticeAssemble noticeAssemble;


    @PostConstruct
    public void init() {
    	RESPONSE_LISTEN_EXECUTOR_SERVICE.submit(RESPONSE_LISTEN_TASK);
    	REQUEST_LISTEN_EXECUTOR_SERVICE.submit(REQUEST_LISTEN_TASK);
    }

    @PreDestroy
    public void predestroy() {
    	REQUEST_LISTEN_TASK.destroy();
    	RESPONSE_LISTEN_TASK.destroy();
    	
    	RESPONSE_LISTEN_EXECUTOR_SERVICE.shutdown();
    	REQUEST_LISTEN_EXECUTOR_SERVICE.shutdown();
    	
    	try {
    		RESPONSE_LISTEN_EXECUTOR_SERVICE.awaitTermination(AWAITTERMINATION_SECONDS, TimeUnit.SECONDS);
    		REQUEST_LISTEN_EXECUTOR_SERVICE.awaitTermination(AWAITTERMINATION_SECONDS, TimeUnit.SECONDS);
    	}catch(InterruptedException e) {
    		ApiLogger.info("!!!BillExecutor Exception : predestroy() : " + e.getMessage());
    	}
    }

    public class ExecuteRequestListenTask implements Runnable {

    	private ServerAIO serverAIO = null;
    	
    	public ExecuteRequestListenTask() {
    		try {    			
    			serverAIO = ServerAIO.instance(requestPort);
    		}catch(Exception e) {
    			ApiLogger.info("!!!ExecuteRequestListenTask Exception : ExecuteRequestListenTask() : " + e.getMessage());
    		}
    	}
    	public void destroy() {
    		if(serverAIO != null)
    			serverAIO.destroy();
    	}
    	
    	@Override
        public void run() {
        	
    		ApiLogger.info(">>>ExecuteRequestListenTask RUNNING");
                        
            while(!Thread.currentThread().isInterrupted()) {

            	try {
            		
            		AsynchronousSocketChannel channel = serverAIO.accept();
            		if(channel != null) {
	            		ExecuteTransferTask ert = new ExecuteTransferTask(channel);
	            		REQUEST_LISTEN_EXECUTOR_SERVICE.submit(ert);
            		}
        		}catch(Exception e) {
        			ApiLogger.info("!!!ExecuteRequestListenTask Exception : run() : " + e.getMessage());
        		}
            }
            ApiLogger.info("<<<ExecuteRequestListenTask STOP");

            }
    	
    	public class ExecuteTransferTask implements Runnable {

    		private AsynchronousSocketChannel channel;
    		
    		public ExecuteTransferTask(AsynchronousSocketChannel channel) {
    			this.channel = channel;
    		}
    	    @Override
    	    public void run() {
    	    	ApiLogger.info(">>>ExecuteTransferTask RUNNING");
    	        try {
    	        	StringBuilder sb = ResponseSocket.receivePacket(channel);
    	        	if(sb != null) {
    	        		RequestSocket.sendPacket(remoteIp, remotePort, sb.toString());
    	        	}
    	        }catch (Exception e){
    	        	ApiLogger.info("!!!ExecuteTransferTask Exception : run() : " + e.getMessage());
    	        }
    	        ApiLogger.info("<<<ExecuteTransferTask STOP");
    	    }
    	}
    }

    public  class ExecuteResponseListenTask implements Runnable {

    	private ServerAIO serverAIO = null;

    	public ExecuteResponseListenTask() {
    		try {    		
    			serverAIO = ServerAIO.instance(responsePort);
    		}catch(Exception e) {
    			ApiLogger.info("!!!ExecuteResponseListenTask Exception : ExecuteResponseListenTask() : " + e.getMessage());
    		}
    	}
    	public void destroy() {
    		if(serverAIO != null)
    			serverAIO.destroy();
    	}

    	@Override
        public void run() {
        	
    		ApiLogger.info(">>>ExecuteResponseListenTask RUNNING");
                        
            while(!Thread.currentThread().isInterrupted()) {

            	try {
            		AsynchronousSocketChannel channel = serverAIO.accept();
            		if(channel != null) {
            		ExecuteReceiveTask ert = new ExecuteReceiveTask(channel);
            		RESPONSE_LISTEN_EXECUTOR_SERVICE.submit(ert);
            		}
            		
            	}catch(Exception e) {
        			ApiLogger.info("!!!ExecuteResponseListenTask Exception : run() : " + e.getMessage());
        		}

            }
            
            System.out.println("<<<ExecuteResponseListenTask STOP");
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
    	        		ExecuteAnalysePacketTask eapt =new ExecuteAnalysePacketTask(sb);
    	        		RESPONSE_LISTEN_EXECUTOR_SERVICE.submit(eapt);
    	        	}
    	        }catch (Exception e){
    	        	ApiLogger.info("!!!ExecuteReceiveTask Exception : run() : " + e.getMessage());
    	        }
    	        ApiLogger.info("<<<ExecuteReceiveTask STOP");
    	    }
    	}
    	
    	public class ExecuteAnalysePacketTask implements Runnable {

    		private StringBuilder packet;
    		
    		public ExecuteAnalysePacketTask(StringBuilder packet) {
    			this.packet = packet;
    		}
    	    @Override
    	    public void run() {
    	    	ApiLogger.info(">>>ExecuteAnalysePacketTask RUNNING");
    	        try {
    				String xml = Codec.decode(packet);
    				Object object = FastXML.xmlToBean(xml, ResKey.class, ResQuery.class, ResPay.class, PacketNotify.class, PacketError.class);
    				if( object == null ) 
    					ApiLogger.info("!!!ExecuteAnalysePacketTask Exception : run() : object == null \n");
    				else {
	    				PacketCheck pc = (PacketCheck)object;
	    				if(!pc.check())
	    					ApiLogger.info("!!!ExecuteAnalysePacketTask Exception : run() : !pc.check() \n");
	    				else {
	    					if(object.getClass() == ResPay.class)
	    						doResPay((ResPay)object);
	    					else if(object.getClass() == ResQuery.class)
    							doResQuery((ResQuery)object);
    						else if(object.getClass() == PacketError.class)
    							doPacketError((PacketError)object);
    						else if(object.getClass() == PacketNotify.class)
    							doPacketNotify((PacketNotify)object);
    						else if(object.getClass() == ResKey.class)
    							doResKey((ResKey)object);
    						else 
    							ApiLogger.info("!!!ExecuteAnalysePacketTask Exception : run() : object.getClass() == ? \n");
	    				}
	    			}
    	        }catch (Exception e){
    	        	ApiLogger.info("!!!ExecuteAnalysePacketTask Exception : run() : " + e.getMessage());
    	        }
    	        ApiLogger.info("<<<ExecuteAnalysePacketTask STOP");
    	    }
    	    
    	    private void doResPay(ResPay resPay) {
    	    	//TODO
    	    }
    	    private void doResQuery(ResQuery resQuery) {
    	    	//TODO
    	    }
    	    private void doPacketError(PacketError packetError) {
    	    	//TODO
    	    }
    	    private void doPacketNotify(PacketNotify packetNotify) {
    	    	//TODO
    	    }
    	    private void doResKey(ResKey resKey) {
    	    	//TODO
    	    }
    	}
    	
    }
}
