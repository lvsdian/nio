package cn.andios.nio.day2;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Scanner;

public class TestBlockingNIO2 {

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
		
		socketChannel.shutdownOutput();
		
		//4.���շ���˷���
		int len = 0;
		while((len = socketChannel.read(byteBuffer) )!= -1){
			byteBuffer.flip();
			System.out.println(new String (byteBuffer.array(),0,len));
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
		
		//���͸��ͻ���
		byteBuffer.flip();
		socketChannel.write(byteBuffer);
		
		//6.�ر�ͨ��
		socketChannel.close();
		outChannel.close();
		serverSocketChannel.close();
				
	}
}
