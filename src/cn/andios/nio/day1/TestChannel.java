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
 * 一、通道(Channel):用于源节点与目标节点的连接，再java nio中负责缓冲区中数据的传输
 * 		channel本身不存在数据存储,因此需要配合缓冲区进行传输
 * 
 * 二、通道的主要实现类
 * 		java.nio.channels.Channel接口：
 * 			|--FileChannel
 * 			|--SocketChannel
 * 			|--ServerSocketChannel
 * 			|--DatagramChannel
 * 
 * 三、获取通道
 * 		1.java对支持通道的类提供了getChannek()方法
 * 			本地IO:
 * 				FileInputStream/FileOutputStream
 * 				RandomAccessFile
 * 			
 * 			网络IO:
 * 				Socket
 * 				ServerSocket
 * 				DatagramSocket
 * 		
 * 		2.在jdk7中的NIO2针对各个通道提供静态方法open();
 * 		3.在jdk7中的NIO2的Files工具类的newByteChannel();
 * 
 * 四、通道之间的数据传输
 * 		transferFrom()
 * 		transferTo()
 * 
 * 五、分散(Scatter)与聚集(Gather)
 * 		分散读取(Scattering Reads):将通道中的数据分散到多个缓冲区
 * 		聚集写入(Gathering Writes):将多个缓冲区中的数据聚集到通道中
 * 
 * 六、字符集：charset
 * 		编码：字符串      --> 字节数组
 * 		解码：字符数组   --> 字符串
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
		
		//获取编码器
		CharsetEncoder charsetEncoder = charset1.newEncoder();
		
		//获取解码器
		CharsetDecoder charsetDecoder = charset1.newDecoder();
		
		CharBuffer charBuffer = CharBuffer.allocate(1024);
		charBuffer.put("安卓");
		charBuffer.flip();
		
		//编码
		ByteBuffer byteBuffer = charsetEncoder.encode(charBuffer);
		
		for (int i = 0; i < 12; i++) {
			System.out.println(byteBuffer.get());
		}
		
		//解码
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
		
		//1.获取通道
		FileChannel fileChannel1 = randomAccessFile.getChannel();
		
		//2.分配指定大小缓冲区
		ByteBuffer byteBuffer1 = ByteBuffer.allocate(1024);
		ByteBuffer byteBuffer2 = ByteBuffer.allocate(1024);
		
		//3.分散读取
		ByteBuffer [] byteBuffers = {byteBuffer1,byteBuffer2};
		
		fileChannel1.read(byteBuffers);
		
		for(ByteBuffer byteBuffer:byteBuffers){
			byteBuffer.flip();
		}
		
		System.out.println(new String(byteBuffers[0].array(),0,byteBuffers[0].limit()));
		System.out.println("---------------------------------------------");
		System.out.println(new String(byteBuffers[1].array(),0,byteBuffers[1].limit()));
		
		//4.聚集写入
		RandomAccessFile randomAccessFile2 = new RandomAccessFile("5.jpg","rw");
		FileChannel fileChannel = randomAccessFile2.getChannel();
		
		fileChannel.write(byteBuffers);
	}

	//直接缓冲区
	public static void test3() throws Exception{
		FileChannel inChannel = FileChannel.open(Paths.get("1.jpg"), StandardOpenOption.READ);
		FileChannel outChannel = FileChannel.open(Paths.get("4.jpg"), StandardOpenOption.WRITE,StandardOpenOption.READ,StandardOpenOption.CREATE);
		
		inChannel.transferTo(0, inChannel.size(), outChannel);
		outChannel.transferFrom(inChannel, 0, inChannel.size());
		
		inChannel.close();
		outChannel.close();
	}

	//使用直接缓存区（只有ByteBuffer支持）完成复制(内存映射文件)
	public static void test1() throws Exception{
		FileChannel inChannel = FileChannel.open(Paths.get("1.jpg"), StandardOpenOption.READ);
		FileChannel outChannel = FileChannel.open(Paths.get("3.jpg"), StandardOpenOption.WRITE,StandardOpenOption.READ,StandardOpenOption.CREATE);
		
		//内存映射文件
		MappedByteBuffer inMappedBuff = inChannel.map(MapMode.READ_ONLY, 0, inChannel.size());
		MappedByteBuffer outMappeBuff = outChannel.map(MapMode.READ_WRITE, 0, inChannel.size());
		
		//直接对缓冲区进行数据的读写操作
		byte[] dst = new byte[inMappedBuff.limit()];
		inMappedBuff.get(dst);
		
		outMappeBuff.put(dst);
		
		inChannel.close();
		outChannel.close();
	}
	
	//利用通道完成复制(非直接缓冲区)
	public static void test2(){
		FileInputStream fis = null;
		FileOutputStream fos = null;
		
		//1.获取通道
		FileChannel inChannel = null ;
		FileChannel outChannel = null ;
		
		try {
			fis = new FileInputStream("1.jpg");
			fos = new FileOutputStream("2.jpg");
			inChannel = fis.getChannel();
			outChannel = fos.getChannel();
			//2.分配指定大小的缓冲区
			ByteBuffer byteBuffer = ByteBuffer.allocate(102400);
			//3.将通道中的数据存入缓冲区
			while (inChannel.read(byteBuffer) != -1) {
				//切换读取数据的模式
				byteBuffer.flip();
				//将缓冲区中的数据写入通道
				outChannel.write(byteBuffer);
				//清空缓冲区
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
