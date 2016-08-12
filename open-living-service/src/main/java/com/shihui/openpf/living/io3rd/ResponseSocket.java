package com.shihui.openpf.living.io3rd;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.SocketTimeoutException;
import java.net.StandardSocketOptions;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.IllegalBlockingModeException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import me.weimi.api.commons.util.ApiLogger;

public class ResponseSocket {

	private static final int PACKET_LENGTH_MAX = 1024*1024;
	private static final int PACKET_HEAD_LENGTH = 6;
	private static final int SOCKET_RECV_BUFFER_LENGTH = 4096;
	private static final int SOCKET_TIMEOUT = 60*1000;
	
	private AsynchronousSocketChannel channel = null;
	
	private ResponseSocket(AsynchronousSocketChannel channel) 
			throws IOException, SocketTimeoutException, IllegalBlockingModeException, IllegalArgumentException {
		this.channel = channel;
		channel.setOption(StandardSocketOptions.SO_RCVBUF, SOCKET_RECV_BUFFER_LENGTH);
		channel.setOption(StandardSocketOptions.TCP_NODELAY, true);
	}

	private void close() throws IOException{
		if(channel != null && channel.isOpen()) {
			channel.shutdownInput().shutdownOutput();
			channel.close();
		}
		channel = null;
	}

	private StringBuilder recv() 
			throws IOException, UnsupportedEncodingException, NumberFormatException,
			ExecutionException, InterruptedException, TimeoutException {
		StringBuilder sb = new StringBuilder();
		
		ByteBuffer byteBuffer = ByteBuffer.allocate(6);
		Future<Integer> future = channel.read(byteBuffer);
		future.get(SOCKET_TIMEOUT, TimeUnit.SECONDS);
		if(future.isDone()) {
			byteBuffer.flip();
			if(byteBuffer.remaining() == PACKET_HEAD_LENGTH) {
				sb.append(new String(byteBuffer.array(),"GBK"));
				
				int packetLength = Integer.parseInt(sb.toString());
				if( packetLength > PACKET_LENGTH_MAX )
					return null;
				byteBuffer = ByteBuffer.allocate(packetLength);
	
				while(byteBuffer.position() < packetLength) {
					future = channel.read(byteBuffer);
					future.get(SOCKET_TIMEOUT, TimeUnit.SECONDS);
					if(!future.isDone())
						return null;
				}
				byteBuffer.flip();
				sb.append(new String(byteBuffer.array(),"GBK"));
				return sb;
			}
		}
		return null;
	}

	public static StringBuilder receivePacket(AsynchronousSocketChannel channel) {
		StringBuilder ret = null;
		ResponseSocket rs = null;
		
		try {
				rs = new ResponseSocket(channel);
				ret = rs.recv();
		}catch(Exception e) {
			e.printStackTrace();
		}

		try {
			if( rs!=null)
				rs.close();
		}catch(Exception e) {
			e.printStackTrace();
		}
		
		rs = null;
		ApiLogger.debug("RESPONSE : " + ret);
		return ret;
	}

}
