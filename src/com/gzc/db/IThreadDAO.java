/**
 * 
 */
package com.gzc.db;

import java.util.List;

import com.gzc.entity.ThreadInfo;

/**
 * ���ݿ����DAO�ӿڣ������ݿ����ɾ�Ĳ��߳���Ϣ����
 * @author gzc
 *
 */
public interface IThreadDAO {
	
	/**
	 * �����߳���Ϣ
	 * @param threadInfo
	 */
	void insertThread(ThreadInfo threadInfo);
	/**
	 * ���������ļ�URLɾ�����������߳���Ϣ
	 * @param url
	 */
	void deleteThread(String url);
	/**
	 * �����߳���Ϣ
	 * @param url
	 * @param thread_id
	 * @param finished
	 */
	void updateThread(String url, int thread_id, int finished);
	/**
	 * ��ѯ�õ������߳���Ϣ
	 * @param url
	 * @return
	 */
	List<ThreadInfo> getThreads(String url);
	/**
	 * ��ѯ�жϸ��߳���Ϣ�Ƿ�������ݿ���
	 * @param url
	 * @param thread_id
	 * @return
	 */
	boolean isExists(String url, int thread_id);

}
