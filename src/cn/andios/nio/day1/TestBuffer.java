package cn.andios.nio.day1;

import java.io.BufferedReader;
import java.nio.ByteBuffer;

import org.junit.Test;

/**
 * һ�� ������(BUffer)���� java nio �и������ݵĴ�ȡ���ײ�������ʵ�֣����ڴ洢��ͬ���͵�����
 * 		
 * 		�����������Ͳ�ͬ(boolean����) �ṩ����Ӧ���͵Ļ�����
 * 			ByteBuffer
 * 			CharBuffer
 * 			ShortBuffer
 * 			IntBuffer
 * 			LongBuffer
 * 			FloatBuffer
 * 			DoubleBuffer
 * 
 * 		��������������ʽ����һ�£�ͨ��allocate() ��ȡ������
 * 
 * �����������洢���ݵ��������ķ���
 * 		put()�������ݵ�������
 * 		get()����ȡ����������
 * 
 * ����������(java.nio.Buffer)���ĸ���������
 * 			capacity����������ʾ���������洢����������һ���������ܸı�
 * 			limit�����ޣ���ʾ�������п��Բ������ݵĴ�С��(limit��������ݲ��ܽ��ж�д)
 * 			position��λ�ã���ʾ�����������ڲ�����λ��
 * 			mark����ǣ���ʾ��ǰ��¼position��λ�ã�����ͨ��reset()�ָ���markλ��
 * 
 * �ġ�ֱ�ӻ��������ֱ�ӻ�����
 * 		��ֱ�ӻ�������ͨ��allocate()�������仺��������������������JVM���ڴ���
 * 		ֱ�ӻ�������ͨ��allocateDirect()����ֱ�ӷ��仺����,�����������ڴ���
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
		
		//1.����һ��ָ����С�Ļ�����
		ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
		
		System.out.println("----------------allocate()-------------------");
		System.out.println("position��" + byteBuffer.position());
		System.out.println("imit��" + byteBuffer.limit());
		System.out.println("capacity��" + byteBuffer.capacity());
		
		//2.����put()�������ݵ�������
		byteBuffer.put(str.getBytes());

		System.out.println("-------------------put()----------------------");
		System.out.println("position��" + byteBuffer.position());
		System.out.println("imit��" + byteBuffer.limit());
		System.out.println("capacity��" + byteBuffer.capacity());
		
		//3.�л���������ģʽ
		byteBuffer.flip();

		System.out.println("-------------------flip()----------------------");
		System.out.println("position��" + byteBuffer.position());
		System.out.println("imit��" + byteBuffer.limit());
		System.out.println("capacity��" + byteBuffer.capacity());
		
		//4.����get()��ȡ����������
		byte [] dst = new byte[byteBuffer.limit()];
		byteBuffer.get(dst);
		
		System.out.println("-------------------get()----------------------");
		System.out.println("position��" + byteBuffer.position());
		System.out.println("imit��" + byteBuffer.limit());
		System.out.println("capacity��" + byteBuffer.capacity());
		
		//5.rewind()�����ظ�������
		byteBuffer.rewind();
		
		System.out.println("-------------------get()----------------------");
		System.out.println("position��" + byteBuffer.position());
		System.out.println("imit��" + byteBuffer.limit());
		System.out.println("capacity��" + byteBuffer.capacity());
		
		//6.clear():��ջ�����,������������Ȼ���ڣ�ֻ�Ǵ��ڡ���������״̬
		byteBuffer.clear();
		System.out.println("-------------------clear()----------------------");
		System.out.println("position��" + byteBuffer.position());
		System.out.println("imit��" + byteBuffer.limit());
		System.out.println("capacity��" + byteBuffer.capacity());
		
	}
	
	public static void test2(){
		String str = "javanio";
		ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
		
		byteBuffer.put(str.getBytes());
		
		byteBuffer.flip();
		
		byte[] dst = new byte[byteBuffer.limit()];
		byteBuffer.get(dst, 0, 2);
		
		System.out.println(new String(dst,0,2));
		System.out.println("�������ֽں��position��" + byteBuffer.position());

		
		//mark()�����
		byteBuffer.mark();
		System.out.println("��ǵ�ǰposition");
		
		byteBuffer.get(dst, 2, 2);
		System.out.println("�ٶ������ֽں��position��" + byteBuffer.position());
		
		//reset():�ָ���markλ��
		byteBuffer.reset();
		
		System.out.println("reset()�ָ����position��" + byteBuffer.position());
		
		//�жϻ������Ƿ���ʣ�������
		if(byteBuffer.hasRemaining()){			
			System.out.println("���ɲ������ֽ�������" + byteBuffer.remaining());
		}
	}
	
	public static void test3(){
		//����ֱ�ӻ�����
		ByteBuffer byteBuffer = ByteBuffer.allocateDirect(1024);
		
		System.out.println(byteBuffer.isDirect());
	}
}
