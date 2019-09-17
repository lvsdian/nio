package cn.andios.nio.day2;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Date;
import java.util.Iterator;

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
public class TestNonBlockingNIO {

	public static void main(String[] args) throws IOException{
		//server();
		client();
	}
	
	//�ͻ���
	public static void client() throws IOException{
		//1.��ȡͨ��
		SocketChannel socketChannel = SocketChannel.open(new InetSocketAddress("127.0.0.1",9898));
		
		//2.�л�������ģʽ
		socketChannel.configureBlocking(false);
		
		//3.����ָ����С������
		ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
		
		//4.�������ݸ������
		byteBuffer.put(new Date().toString().getBytes());
		byteBuffer.flip();
		socketChannel.write(byteBuffer);
		byteBuffer.clear();
		
		
		socketChannel.close();
	}
	
	//�����
	public static void server() throws IOException{
		//1.��ȡͨ��
		ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
		
		//2.�л�������ģʽ
		serverSocketChannel.configureBlocking(false);
		
		//3.������
		serverSocketChannel.bind(new InetSocketAddress(9898));
		
		//4.��ȡѡ����
		Selector selector = Selector.open();
		
		//5.��ͨ��ע�ᵽѡ������,ָ�������������¼���
		serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
		
		//6.��ѯʽ�Ļ�ȡѡ�������Ѿ���׼�����������¼�
		while(selector.select() > 0){
			//7.��ȡ��ǰѡ����������ע��ġ�ѡ���(�Ѿ����ļ����¼�)��
			Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
			while(iterator.hasNext()){
				//8.��ȡ׼�����������¼�
				SelectionKey selectionKey = iterator.next();
				
				//9.�жϾ���ʲô�¼�׼������
				if(selectionKey.isAcceptable()){
					//10.�����վ���,��ȡ�ͻ�������
					SocketChannel socketChannel = serverSocketChannel.accept();
					
					//11.�л�������ģʽ
					socketChannel.configureBlocking(false);
					
					//12.����ͨ��ע�ᵽѡ������
					socketChannel.register(selector, SelectionKey.OP_READ);
				}else if(selectionKey.isReadable()){
					//13.��ȡ��ǰ������
					SocketChannel socketChannel = (SocketChannel)selectionKey.channel();
					
					//14.��ȡ����
					ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
					
					int len = 0;
					while((len = socketChannel.read(byteBuffer)) >0){
						byteBuffer.flip();
						System.out.println(new String (byteBuffer.array(),0,len));
						byteBuffer.clear();
					}
				}
				//15.ȡ��ѡ���
				iterator.remove();
			}
		}
	}
}


