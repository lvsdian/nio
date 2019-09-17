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
 * һ��ʹ��NIO�������ͨ�ŵ��������ģ�
 * 		1.ͨ��(Channel):��������
 * 			java.nio.channels.Channel�ӿڣ�
 * 				|--SelectableChannel
 * 					|--SocketChannel
 * 					|--ServerSocketChannel
 * 				`	|--DatagramChannel
 * 					
 * 					|--Pipe.SinkChannel
 * 					|--Pipe.SourceChannel
 * 
 * 		2.������(Buffer):�������ݴ�ȡ
 * 		3.ѡ����(Selector):��SelectableChannel�Ķ�·�����������ڼ��SelectableChannel��IO״��
 * @author LSD
 *
 */
public class TestBlockingNIO {

	public static void main(String[] args) throws IOException{
		//server();
		client();
	}
	
	//�ͻ���
	public static void client() throws IOException{
		//1.��ȡͨ��
		SocketChannel socketChannel = SocketChannel.open(new InetSocketAddress("127.0.0.1",9898));
		
		FileChannel inChannel  = FileChannel.open(Paths.get("1.jpg"), StandardOpenOption.READ);
		
		//2.��ȡָ����С�Ļ�����
		ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
		
		//3.��ȡ�����ļ�
		while(inChannel.read(byteBuffer)!= -1){
			byteBuffer.flip();
			socketChannel.write(byteBuffer);
			byteBuffer.clear();
		}
		inChannel.close();
		socketChannel.close();
	}
	
	//�����
	public static void server() throws IOException{
		//1.��ȡͨ��
		ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
		
		FileChannel outChannel  = FileChannel.open(Paths.get("6.jpg"), StandardOpenOption.WRITE,StandardOpenOption.CREATE);

		//2.������
		serverSocketChannel.bind(new InetSocketAddress(9898));
		
		//3.��ȡ�ͻ������ӵ�ͨ��
		SocketChannel socketChannel = serverSocketChannel.accept();
		
		//4.����ָ����С�Ļ�����
		ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
		
		//5.���տͻ��˵�����
		while(socketChannel.read(byteBuffer)!= -1){
			byteBuffer.flip();
			outChannel.write(byteBuffer);
			byteBuffer.clear();
		}
		
		//6.�ر�ͨ��
		socketChannel.close();
		outChannel.close();
		serverSocketChannel.close();
				
	}
}
