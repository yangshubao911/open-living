package com.shihui.openpf.living.io3rd;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.StandardSocketOptions;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class ServerAIO {
	
	private static final int SOCKET_RECV_BUFFER_LENGTH = 4096;
	private AsynchronousServerSocketChannel serverChannel = null;

	private ServerAIO(int port) throws IOException, SecurityException, IllegalArgumentException {
		
		serverChannel = AsynchronousServerSocketChannel.open().bind(new InetSocketAddress(port),1000);
		serverChannel.setOption(StandardSocketOptions.SO_RCVBUF, SOCKET_RECV_BUFFER_LENGTH);				
	}
	
	public AsynchronousSocketChannel accept() throws ExecutionException, InterruptedException{
		Future<AsynchronousSocketChannel> future = serverChannel.accept();
		AsynchronousSocketChannel channel = future.get();
		return (future.isDone() ? channel : null);
	}
	
	public static ServerAIO instance(int port) {
		try {
			ServerAIO serverAIO = new ServerAIO(port);
			return serverAIO;
		}catch(Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	public void destroy() {
		if(serverChannel.isOpen()) {
			try {
			serverChannel.close();
			}catch(IOException e) {
				e.printStackTrace();
			}
		}
	}
}
