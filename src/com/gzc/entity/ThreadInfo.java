/**
 * 
 */
package com.gzc.entity;

/**
 * 线程信息实体类
 * @author gzc
 *
 */
public class ThreadInfo {
	
	private int id;
	// 该线程下载文件的URL，这个与FileInfo里的URL是一致的
	private String url;
	// 该线程从文件哪开始下载
	private int start;
	// 下载到哪结束
	private int end;
	// 已经下载了多少
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
