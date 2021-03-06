package com.shihui.openpf.living.io3rd;

import com.shihui.commons.ApiLogger;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketTimeoutException;
import java.net.StandardSocketOptions;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.NotYetConnectedException;
import java.nio.channels.WritePendingException;
import java.util.concurrent.*;

//import me.weimi.api.commons.util.ApiLogger;

/*
 * 给交大发送报文的Socket封闭
 */
public class RequestSocket {

	private static final int SOCKET_SEND_BUFFER_LENGTH = 2048;
	private static final int SOCKET_TIMEOUT = 60*1000;
	
	private AsynchronousSocketChannel channel = null;
	
	private RequestSocket() {
	}
	
	private boolean connect(String remoteIp, int remotePort)
			throws IOException, SocketTimeoutException, IllegalArgumentException, 
			CancellationException, ExecutionException, 
			InterruptedException, TimeoutException {
		
		channel = AsynchronousSocketChannel.open();
			
		channel.setOption(StandardSocketOptions.SO_SNDBUF, SOCKET_SEND_BUFFER_LENGTH);
		channel.setOption(StandardSocketOptions.TCP_NODELAY, true);
		
		Future<Void> future = channel.connect(new InetSocketAddress(remoteIp, remotePort));
		future.get(SOCKET_TIMEOUT, TimeUnit.SECONDS);
		if(future.isDone())
			return true;

		channel.shutdownInput().shutdownOutput();
		channel.close();
		channel = null;

		return false;
	}
	
	private void send(String packet) 
			throws IOException, WritePendingException, NotYetConnectedException, 
					SocketTimeoutException, IllegalArgumentException, CancellationException, 
					ExecutionException, InterruptedException, TimeoutException {		
		ByteBuffer byteBuffer = ByteBuffer.wrap(packet.getBytes("GBK"));
		byteBuffer.clear();
		Future<Integer> future;
		while(byteBuffer.remaining() > 0) {
			future = channel.write(byteBuffer);
			future.get(SOCKET_TIMEOUT, TimeUnit.SECONDS);
			if(!future.isDone())
				break;
		}
	}
		
	private void close() throws IOException{
		if(channel != null && channel.isOpen()) {
			channel.shutdownInput().shutdownOutput();
			channel.close();
		}
		channel = null;
	}

	
	public static boolean sendPacket(String remoteIp, int remotePort, String packet) {
		ApiLogger.info("@@@@@@@@@@@@@@@@@@REQUEST : [" + packet + "]");
	
		boolean ret = false;
		RequestSocket cs = new RequestSocket();
		
		try {
			
			if(cs.connect(remoteIp, remotePort)) {
				cs.send(packet);
				cs.close();
				ret = true;
			}
			
		}catch(Exception e) {
			ApiLogger.info("RequestSocket error=====================" + e.getMessage());
		}

		try {
			cs.close();
		}catch(Exception e) {
			ApiLogger.info("RequestSocket error=====================" + e.getMessage());
		}
		cs = null;

		return ret;
	}



}
