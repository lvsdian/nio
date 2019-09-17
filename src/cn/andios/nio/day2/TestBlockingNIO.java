package cn.andios.nio.day2;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

/**
 * 一、使用NIO完成网络通信的三个核心：
 * 		1.通道(Channel):负责连接
 * 			java.nio.channels.Channel接口：
 * 				|--SelectableChannel
 * 					|--SocketChannel
 * 					|--ServerSocketChannel
 * 				`	|--DatagramChannel
 * 					
 * 					|--Pipe.SinkChannel
 * 					|--Pipe.SourceChannel
 * 
 * 		2.缓冲区(Buffer):负责数据存取
 * 		3.选择器(Selector):是SelectableChannel的多路复用器，用于监控SelectableChannel的IO状况
 * @author LSD
 *
 */
public class TestBlockingNIO {

	public static void main(String[] args) throws IOException{
		//server();
		client();
	}
	
	//客户端
	public static void client() throws IOException{
		//1.获取通道
		SocketChannel socketChannel = SocketChannel.open(new InetSocketAddress("127.0.0.1",9898));
		
		FileChannel inChannel  = FileChannel.open(Paths.get("1.jpg"), StandardOpenOption.READ);
		
		//2.获取指定大小的缓冲区
		ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
		
		//3.读取本地文件
		while(inChannel.read(byteBuffer)!= -1){
			byteBuffer.flip();
			socketChannel.write(byteBuffer);
			byteBuffer.clear();
		}
		inChannel.close();
		socketChannel.close();
	}
	
	//服务端
	public static void server() throws IOException{
		//1.获取通道
		ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
		
		FileChannel outChannel  = FileChannel.open(Paths.get("6.jpg"), StandardOpenOption.WRITE,StandardOpenOption.CREATE);

		//2.绑定连接
		serverSocketChannel.bind(new InetSocketAddress(9898));
		
		//3.获取客户端连接的通道
		SocketChannel socketChannel = serverSocketChannel.accept();
		
		//4.分配指定大小的缓冲区
		ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
		
		//5.接收客户端的数据
		while(socketChannel.read(byteBuffer)!= -1){
			byteBuffer.flip();
			outChannel.write(byteBuffer);
			byteBuffer.clear();
		}
		
		//6.关闭通道
		socketChannel.close();
		outChannel.close();
		serverSocketChannel.close();
				
	}
}
