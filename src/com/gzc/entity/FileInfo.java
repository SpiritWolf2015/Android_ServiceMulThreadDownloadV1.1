package com.gzc.entity;

import java.io.Serializable;

/**
 * 下载文件实体类，实现序列化接口，这样可以直接在intent中传递该对象
 * @author gzc
 *
 */
public class FileInfo implements Serializable {
	
	private int id;
	// 下载该文件的网址
	private String url;
	private String fileName;
	// 已经下载了多少字节
	private int finished;
	// 文件的大小
	private int length;
	
	public FileInfo(int id, String url, String fileName, int finished,
			int length) {		
		this.id = id;
		this.url = url;
		this.fileName = fileName;
		this.finished = finished;
		this.length = length;
	}	

	@Override
	public String toString() {
		return "FileInfo [id=" + id + ", url=" + url + ", fileName=" + fileName
				+ ", finished=" + finished + ", length=" + length + "]";
	}
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	public int getFinished() {
		return finished;
	}
	public void setFinished(int finished) {
		this.finished = finished;
	}
	public int getLength() {
		return length;
	}
	public void setLength(int length) {
		this.length = length;
	}

}
