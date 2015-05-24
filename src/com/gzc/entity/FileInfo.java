package com.gzc.entity;

import java.io.Serializable;

/**
 * �����ļ�ʵ���࣬ʵ�����л��ӿڣ���������ֱ����intent�д��ݸö���
 * @author gzc
 *
 */
public class FileInfo implements Serializable {
	
	private int id;
	// ���ظ��ļ�����ַ
	private String url;
	private String fileName;
	// �Ѿ������˶����ֽ�
	private int finished;
	// �ļ��Ĵ�С
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
