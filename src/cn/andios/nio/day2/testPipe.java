package cn.andios.nio.day2;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.Pipe;

public class testPipe {

	public static void main(String[] args) throws Exception{
		test1();
	}

	public static void test1() throws IOException {
		//1.��ȡ�ܵ�
		Pipe pipe = Pipe.open();
		
		//2.��������������д��ܵ�
		ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
		
		Pipe.SinkChannel sinkChannel = pipe.sink();
		
		byteBuffer.put("ͨ������ܵ���������".getBytes());
		
		byteBuffer.flip();
		
		sinkChannel.write(byteBuffer);
		
		//3.��ȡ�������е�����
		Pipe.SourceChannel sourceChannel = pipe.source();
		byteBuffer.flip();
		
		int len = sourceChannel.read(byteBuffer);
		System.out.println(new String (byteBuffer.array(),0,len));
		
		sourceChannel.close();
		sinkChannel.close();
	}
	
}
