package cn.andios.nio.day2;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.Date;
import java.util.Iterator;
import java.util.Scanner;

public class TestNonBlockingNIO2 {

	public static void main(String[] args)throws Exception  {
		send();
		//receive();
	}

	private static void receive() throws Exception {
		DatagramChannel dc = DatagramChannel.open();
		dc.configureBlocking(false);
		
		dc.bind(new InetSocketAddress(9898));
		
		Selector selector = Selector.open();
		
		dc.register(selector, SelectionKey.OP_READ);
		
		while(selector.select() > 0){
			Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
			
			while(iterator.hasNext()){
				SelectionKey sk = iterator.next();
				
				if(sk.isReadable()){
					ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
					
					dc.receive(byteBuffer);
					byteBuffer.flip();
				}
			}
		}
	}

	private static void send() throws Exception {
		DatagramChannel dc = DatagramChannel.open();
		dc.configureBlocking(false);
		
		ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
		
		Scanner input = new Scanner(System.in);
		
		while(input.hasNext()){
			String str = input.next();
			byteBuffer.put((new Date().toString() + ":\n" + str).getBytes());
			byteBuffer.flip();
			dc.send(byteBuffer, new InetSocketAddress("127.0.0.1",9898));
			byteBuffer.clear();
		}
		dc.close();
	}
	
}
