package cn.andios.nio.day1;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileChannel.MapMode;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

/**
 * һ��ͨ��(Channel):����Դ�ڵ���Ŀ��ڵ�����ӣ���java nio�и��𻺳��������ݵĴ���
 * 		channel�����������ݴ洢,�����Ҫ��ϻ��������д���
 * 
 * ����ͨ������Ҫʵ����
 * 		java.nio.channels.Channel�ӿڣ�
 * 			|--FileChannel
 * 			|--SocketChannel
 * 			|--ServerSocketChannel
 * 			|--DatagramChannel
 * 
 * ������ȡͨ��
 * 		1.java��֧��ͨ�������ṩ��getChannek()����
 * 			����IO:
 * 				FileInputStream/FileOutputStream
 * 				RandomAccessFile
 * 			
 * 			����IO:
 * 				Socket
 * 				ServerSocket
 * 				DatagramSocket
 * 		
 * 		2.��jdk7�е�NIO2��Ը���ͨ���ṩ��̬����open();
 * 		3.��jdk7�е�NIO2��Files�������newByteChannel();
 * 
 * �ġ�ͨ��֮������ݴ���
 * 		transferFrom()
 * 		transferTo()
 * 
 * �塢��ɢ(Scatter)��ۼ�(Gather)
 * 		��ɢ��ȡ(Scattering Reads):��ͨ���е����ݷ�ɢ�����������
 * 		�ۼ�д��(Gathering Writes):������������е����ݾۼ���ͨ����
 * 
 * �����ַ�����charset
 * 		���룺�ַ���      --> �ֽ�����
 * 		���룺�ַ�����   --> �ַ���
 * @author LSD
 *
 */
public class TestChannel {

	public static void main(String[] args) throws Exception{
		//test1();
		//test2();
		//test3();
		//test4();
		//test5();
		test6();
	}
	
	public static void test6() throws IOException {
		Charset charset1 = Charset.forName("GBK");
		
		//��ȡ������
		CharsetEncoder charsetEncoder = charset1.newEncoder();
		
		//��ȡ������
		CharsetDecoder charsetDecoder = charset1.newDecoder();
		
		CharBuffer charBuffer = CharBuffer.allocate(1024);
		charBuffer.put("��׿");
		charBuffer.flip();
		
		//����
		ByteBuffer byteBuffer = charsetEncoder.encode(charBuffer);
		
		for (int i = 0; i < 12; i++) {
			System.out.println(byteBuffer.get());
		}
		
		//����
		byteBuffer.flip();
		CharBuffer charBuffer2 = charsetDecoder.decode(byteBuffer);
		
		System.out.println(charBuffer2.toString());
	}

	public static void test5() {
		Map<String,Charset> map = Charset.availableCharsets();
		
		Set<Entry<String,Charset>> set = map.entrySet();
		
		for(Entry<String,Charset> entry:set){
			System.out.println(entry.getKey() + "---" + entry.getValue());
		}
		
	}

	public static void test4() throws Exception{
		RandomAccessFile randomAccessFile = new RandomAccessFile("1.jpg","rw");
		
		//1.��ȡͨ��
		FileChannel fileChannel1 = randomAccessFile.getChannel();
		
		//2.����ָ����С������
		ByteBuffer byteBuffer1 = ByteBuffer.allocate(1024);
		ByteBuffer byteBuffer2 = ByteBuffer.allocate(1024);
		
		//3.��ɢ��ȡ
		ByteBuffer [] byteBuffers = {byteBuffer1,byteBuffer2};
		
		fileChannel1.read(byteBuffers);
		
		for(ByteBuffer byteBuffer:byteBuffers){
			byteBuffer.flip();
		}
		
		System.out.println(new String(byteBuffers[0].array(),0,byteBuffers[0].limit()));
		System.out.println("---------------------------------------------");
		System.out.println(new String(byteBuffers[1].array(),0,byteBuffers[1].limit()));
		
		//4.�ۼ�д��
		RandomAccessFile randomAccessFile2 = new RandomAccessFile("5.jpg","rw");
		FileChannel fileChannel = randomAccessFile2.getChannel();
		
		fileChannel.write(byteBuffers);
	}

	//ֱ�ӻ�����
	public static void test3() throws Exception{
		FileChannel inChannel = FileChannel.open(Paths.get("1.jpg"), StandardOpenOption.READ);
		FileChannel outChannel = FileChannel.open(Paths.get("4.jpg"), StandardOpenOption.WRITE,StandardOpenOption.READ,StandardOpenOption.CREATE);
		
		inChannel.transferTo(0, inChannel.size(), outChannel);
		outChannel.transferFrom(inChannel, 0, inChannel.size());
		
		inChannel.close();
		outChannel.close();
	}

	//ʹ��ֱ�ӻ�������ֻ��ByteBuffer֧�֣���ɸ���(�ڴ�ӳ���ļ�)
	public static void test1() throws Exception{
		FileChannel inChannel = FileChannel.open(Paths.get("1.jpg"), StandardOpenOption.READ);
		FileChannel outChannel = FileChannel.open(Paths.get("3.jpg"), StandardOpenOption.WRITE,StandardOpenOption.READ,StandardOpenOption.CREATE);
		
		//�ڴ�ӳ���ļ�
		MappedByteBuffer inMappedBuff = inChannel.map(MapMode.READ_ONLY, 0, inChannel.size());
		MappedByteBuffer outMappeBuff = outChannel.map(MapMode.READ_WRITE, 0, inChannel.size());
		
		//ֱ�ӶԻ������������ݵĶ�д����
		byte[] dst = new byte[inMappedBuff.limit()];
		inMappedBuff.get(dst);
		
		outMappeBuff.put(dst);
		
		inChannel.close();
		outChannel.close();
	}
	
	//����ͨ����ɸ���(��ֱ�ӻ�����)
	public static void test2(){
		FileInputStream fis = null;
		FileOutputStream fos = null;
		
		//1.��ȡͨ��
		FileChannel inChannel = null ;
		FileChannel outChannel = null ;
		
		try {
			fis = new FileInputStream("1.jpg");
			fos = new FileOutputStream("2.jpg");
			inChannel = fis.getChannel();
			outChannel = fos.getChannel();
			//2.����ָ����С�Ļ�����
			ByteBuffer byteBuffer = ByteBuffer.allocate(102400);
			//3.��ͨ���е����ݴ��뻺����
			while (inChannel.read(byteBuffer) != -1) {
				//�л���ȡ���ݵ�ģʽ
				byteBuffer.flip();
				//���������е�����д��ͨ��
				outChannel.write(byteBuffer);
				//��ջ�����
				byteBuffer.clear();
			} 
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			if(outChannel != null){
				try {
					outChannel.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if(inChannel != null){
				try {
					outChannel.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if(fos != null){
				try {
					fos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if(fis != null){
				try {
					fis.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
}
