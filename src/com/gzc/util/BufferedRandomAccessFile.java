package com.gzc.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * 通过扩展RandomAccessFile,为其提供缓冲可增强IO的性能。
 * 增强RandomAccessFile的IO性能 : http://my.oschina.net/u/866190/blog/200110
 * 花1K内存实现高效IO的RandomAccessFile类 : http://wenku.baidu.com/view/5af38f23482fb4daa58d4bac.html
 * @author gzc
 * 
 */
public class BufferedRandomAccessFile extends RandomAccessFile {
	
	private int bufSize;
	private byte[] buf;
	private long bufStart;
	private int bufPos;
	private int bufEnd;
	private long realPos;
	private boolean bufNeedWrite;
	private final int DEFAULT_BUFFER_SIZE = 2048;
	
	public BufferedRandomAccessFile(File file, String mode)
			throws FileNotFoundException {
		super(file, mode);
		// 默认缓冲大小
		this.bufSize = DEFAULT_BUFFER_SIZE;
		initBuf();
	}
	
	/**
	 * @param file
	 * @param mode
	 * @throws FileNotFoundException
	 */
	public BufferedRandomAccessFile(File file, String mode, int bufSize)
			throws FileNotFoundException {
		super(file, mode);
		this.bufSize = bufSize;
		initBuf();
	}

	public BufferedRandomAccessFile(String fileName, String mode)
			throws FileNotFoundException {
		super(fileName, mode);
		// 默认缓冲大小
		this.bufSize = DEFAULT_BUFFER_SIZE;
		initBuf();
	}

	/**
	 * @param fileName
	 * @param mode
	 * @throws FileNotFoundException
	 */
	public BufferedRandomAccessFile(String fileName, String mode, int bufSize)
			throws FileNotFoundException {
		super(fileName, mode);
		this.bufSize = bufSize;
		initBuf();
	}
	
	@Override
	public void close() throws IOException {
		flush();
		super.close();
	}

	private void fillBuf() throws IOException {		 
        flush();
 
        bufStart =  super.getFilePointer() ;
        int n = super.read(buf, 0, bufSize);
 
        bufPos = 0 ;
         
        if (n >= 0) {
            bufEnd = n;
        }else{
            bufEnd = 0  ;
        } 
    }

	/**
	 * bufNeedWrite为真，把buf[]中尚未写入磁盘的数据，写入磁盘。
	 * @throws IOException
	 */
	private void flush() throws IOException {		 
	        if (bufNeedWrite) {
	        	if(super.getFilePointer() != bufStart){
	        		  seek(bufStart );
	        	}	          
	            super.write(buf, 0, bufPos);
	            bufNeedWrite = false;
	        }	 
	    }

	@Override
	public long getFilePointer() throws IOException {
		return realPos;
	}
	
	private void initBuf(){
		this.buf = new byte[this.bufSize];
		this.bufNeedWrite = false;
		this.bufPos = 0;
		this.bufEnd = 0;
		this.bufPos = 0;
	}
	
	
	
	 @Override
	public int read() throws IOException {
		if (bufPos >= bufEnd) {
            fillBuf();
        }
 
        if (bufEnd == 0)
            return -1;
 
        realPos++;
        return buf[bufPos++];
	}

	@Override
	public int read(byte[] b) throws IOException {
		return this.read(b, 0, b.length );
	}

	@Override
	public int read(byte[] b, int off, int len)
			throws IOException {
		   int bufLef = bufEnd - bufPos;
	        if (len <= bufLef) {
	            System.arraycopy(buf, bufPos, b, off, len);
	            bufPos += len;
	            realPos += len;
	            return len;
	        }
	 
	        if (bufLef > 0) {
	            System.arraycopy(buf, bufPos, b, off, bufLef);
	        }
	 
	        int n = super.read(b, off + bufLef, len - bufLef);
	 
	        if (n >= 0) {
	            int i = bufLef + n;
	 
	            bufPos += i;
	            realPos += i;
	            return i;
	        } else {
	            if (bufLef == 0) {
	                return -1;
	            } else {
	                bufPos += bufLef;
	                realPos += bufLef;
	                return bufLef;
	            }	 
	        }
	}

	/**
	 * 移动文件指针到pos位置，并把buf[]映射填充至POS 所在的文件块。
	 */
	@Override
	public void seek(long pos) throws IOException {
		 if(bufStart < pos || bufStart+bufSize > pos ){
	            bufEnd  = 0 ;
	        }
	         
	        realPos = pos;
		super.seek(pos);
	}

	@Override
	public void write(byte[] b) throws IOException {
		write(b,0 ,b.length);
	}

	@Override
	public void write(byte[] b, int off, int len)
			throws IOException {
		 int bufLef = bufEnd - bufPos;
	        if (len <= bufLef) {
	            System.arraycopy(b, off, buf, bufPos, len);
	            bufPos += len;
	            realPos += len ;
	            bufNeedWrite = true;
	            flush();
	            return;
	        }
	 
	        if (bufLef > 0) {
	            System.arraycopy(b, off, buf, bufPos, bufLef);
	            bufPos += bufLef;
	            //realPos += bufLef ;
	            bufNeedWrite = true;
	            flush();
	        }
	 
	        realPos += len ;
	        super.write(b, off + bufLef, len - bufLef);
	}

	@Override
	public void write(int oneByte) throws IOException {
		 if (bufPos >= bufEnd) {
	            fillBuf();
	        }
	 
	        if (bufEnd == 0) {
	            bufEnd++;
	        }
	        realPos++;
	        buf[bufPos++] = (byte) oneByte;
	 
	        bufNeedWrite = true;
	}	

}
