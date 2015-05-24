/**
 * 
 */
package com.gzc.db;

import java.util.List;

import com.gzc.entity.ThreadInfo;

/**
 * 数据库访问DAO接口，往数据库里，增删改查线程信息对象
 * @author gzc
 *
 */
public interface IThreadDAO {
	
	/**
	 * 插入线程信息
	 * @param threadInfo
	 */
	void insertThread(ThreadInfo threadInfo);
	/**
	 * 根据下载文件URL删除所有下载线程信息
	 * @param url
	 */
	void deleteThread(String url);
	/**
	 * 更新线程信息
	 * @param url
	 * @param thread_id
	 * @param finished
	 */
	void updateThread(String url, int thread_id, int finished);
	/**
	 * 查询得到所有线程信息
	 * @param url
	 * @return
	 */
	List<ThreadInfo> getThreads(String url);
	/**
	 * 查询判断该线程信息是否存在数据库中
	 * @param url
	 * @param thread_id
	 * @return
	 */
	boolean isExists(String url, int thread_id);

}
