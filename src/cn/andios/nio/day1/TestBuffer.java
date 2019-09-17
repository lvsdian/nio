package cn.andios.nio.day1;

import java.io.BufferedReader;
import java.nio.ByteBuffer;

import org.junit.Test;

/**
 * 一、 缓冲区(BUffer)：在 java nio 中负责数据的存取，底层由数组实现，用于存储不同类型的数据
 * 		
 * 		根据数据类型不同(boolean除外) 提供了相应类型的缓冲区
 * 			ByteBuffer
 * 			CharBuffer
 * 			ShortBuffer
 * 			IntBuffer
 * 			LongBuffer
 * 			FloatBuffer
 * 			DoubleBuffer
 * 
 * 		上述缓冲区管理方式几乎一致，通过allocate() 获取缓冲区
 * 
 * 二、缓冲区存储数据的两个核心方法
 * 		put()：存数据到缓冲区
 * 		get()：获取缓冲区数据
 * 
 * 三、缓冲区(java.nio.Buffer)的四个核心属性
 * 			capacity：容量，表示缓冲区最大存储数据容量，一旦声明不能改变
 * 			limit：界限，表示缓冲区中可以操作数据的大小。(limit后面的数据不能进行读写)
 * 			position：位置，表示缓冲区中正在操作的位置
 * 			mark：标记，表示当前记录position的位置，可以通过reset()恢复到mark位置
 * 
 * 四、直接缓冲区与非直接缓冲区
 * 		非直接缓冲区：通过allocate()方法分配缓冲区，将缓冲区建立在JVM的内存中
 * 		直接缓冲区：通过allocateDirect()方法直接分配缓冲区,建立再物理内存中
 * @author LSD
 *
 */
public class TestBuffer {

	public static void main(String[] args) {
		//test1();
		//test2();
		test3();
	}
	public static void test1(){
		
		String str = "javanio";
		
		//1.分配一个指定大小的缓冲区
		ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
		
		System.out.println("----------------allocate()-------------------");
		System.out.println("position：" + byteBuffer.position());
		System.out.println("imit：" + byteBuffer.limit());
		System.out.println("capacity：" + byteBuffer.capacity());
		
		//2.利用put()存入数据到缓冲区
		byteBuffer.put(str.getBytes());

		System.out.println("-------------------put()----------------------");
		System.out.println("position：" + byteBuffer.position());
		System.out.println("imit：" + byteBuffer.limit());
		System.out.println("capacity：" + byteBuffer.capacity());
		
		//3.切换到读数据模式
		byteBuffer.flip();

		System.out.println("-------------------flip()----------------------");
		System.out.println("position：" + byteBuffer.position());
		System.out.println("imit：" + byteBuffer.limit());
		System.out.println("capacity：" + byteBuffer.capacity());
		
		//4.利用get()读取缓冲区数据
		byte [] dst = new byte[byteBuffer.limit()];
		byteBuffer.get(dst);
		
		System.out.println("-------------------get()----------------------");
		System.out.println("position：" + byteBuffer.position());
		System.out.println("imit：" + byteBuffer.limit());
		System.out.println("capacity：" + byteBuffer.capacity());
		
		//5.rewind()：可重复读数据
		byteBuffer.rewind();
		
		System.out.println("-------------------get()----------------------");
		System.out.println("position：" + byteBuffer.position());
		System.out.println("imit：" + byteBuffer.limit());
		System.out.println("capacity：" + byteBuffer.capacity());
		
		//6.clear():清空缓冲区,缓冲区数据依然存在，只是处于“被遗忘”状态
		byteBuffer.clear();
		System.out.println("-------------------clear()----------------------");
		System.out.println("position：" + byteBuffer.position());
		System.out.println("imit：" + byteBuffer.limit());
		System.out.println("capacity：" + byteBuffer.capacity());
		
	}
	
	public static void test2(){
		String str = "javanio";
		ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
		
		byteBuffer.put(str.getBytes());
		
		byteBuffer.flip();
		
		byte[] dst = new byte[byteBuffer.limit()];
		byteBuffer.get(dst, 0, 2);
		
		System.out.println(new String(dst,0,2));
		System.out.println("读两个字节后的position：" + byteBuffer.position());

		
		//mark()：标记
		byteBuffer.mark();
		System.out.println("标记当前position");
		
		byteBuffer.get(dst, 2, 2);
		System.out.println("再读两个字节后的position：" + byteBuffer.position());
		
		//reset():恢复到mark位置
		byteBuffer.reset();
		
		System.out.println("reset()恢复后的position：" + byteBuffer.position());
		
		//判断缓冲区是否还有剩余的数据
		if(byteBuffer.hasRemaining()){			
			System.out.println("还可操作的字节数量：" + byteBuffer.remaining());
		}
	}
	
	public static void test3(){
		//分配直接缓冲区
		ByteBuffer byteBuffer = ByteBuffer.allocateDirect(1024);
		
		System.out.println(byteBuffer.isDirect());
	}
}
