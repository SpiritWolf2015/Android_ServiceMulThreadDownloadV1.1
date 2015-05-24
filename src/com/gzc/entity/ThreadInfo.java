/**
 * 
 */
package com.gzc.entity;

/**
 * �߳���Ϣʵ����
 * @author gzc
 *
 */
public class ThreadInfo {
	
	private int id;
	// ���߳������ļ���URL�������FileInfo���URL��һ�µ�
	private String url;
	// ���̴߳��ļ��Ŀ�ʼ����
	private int start;
	// ���ص��Ľ���
	private int end;
	// �Ѿ������˶���
	private int finished;
	
	public ThreadInfo(int id, String url, int start, int end, int finished) {
		super();
		this.id = id;
		this.url = url;
		this.start = start;
		this.end = end;
		this.finished = finished;
	}
	
	public ThreadInfo() {
		super();
	}

	@Override
	public String toString() {
		return "ThreadInfo [id=" + id + ", url=" + url + ", start=" + start
				+ ", end=" + end + ", finished=" + finished + "]";
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
	public int getStart() {
		return start;
	}
	public void setStart(int start) {
		this.start = start;
	}
	public int getEnd() {
		return end;
	}
	public void setEnd(int end) {
		this.end = end;
	}
	public int getFinished() {
		return finished;
	}
	public void setFinished(int finished) {
		this.finished = finished;
	}

}
