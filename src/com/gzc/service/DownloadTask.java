package com.gzc.service;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.gzc.db.IThreadDAO;
import com.gzc.db.ImpThreadDAO;
import com.gzc.entity.FileInfo;
import com.gzc.entity.ThreadInfo;
import com.gzc.util.BufferedRandomAccessFile;
import com.gzc.util.Const;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * ���������࣬�������߳̽����ļ�������
 * 
 * @author gzc
 *
 */
public class DownloadTask {

	private Context mContext = null;
	private FileInfo mFileInfo = null;
	private IThreadDAO mDAO = null;
	private int mFinished;
	// ���ظ��ļ����̵߳�����
	private int mThreadCount;
	private List<DownloadThread> mDownloadThreads = new ArrayList<DownloadThread>();
	
	/**
	 * �̳߳�
	 */
	public static final ExecutorService sExecutorService = Executors.newCachedThreadPool();

	/**
	 * �Ƿ�ֹͣ����
	 */
	public boolean isPause = false;

	public DownloadTask(Context context, FileInfo fileInfo, int threadCount) {		
		this.mContext = context;
		this.mFileInfo = fileInfo;
		Log.i("DownloadTask���캯��","�ļ�ID=" + mFileInfo.getId());
		
		mDAO = new ImpThreadDAO(context);
		mFinished = 0;
		mThreadCount = threadCount;
	}

	/**
	 * ��������߳����ظ��ļ�
	 */
	public void download() {
		// ��ȡ���ݿ�������ļ�URL�������߳���Ϣ
		List<ThreadInfo> threadInfos = mDAO.getThreads(mFileInfo.getUrl());
		// ������ݿ���û�и������ļ�URL���߳�������Ϣ����ʼ���߳�������Ϣ
		if (0 == threadInfos.size()) {
			// ���ÿ���̵߳������ļ��ֽڷ�Χ
			int length = mFileInfo.getLength() / mThreadCount;
			for (int i = 0; i < mThreadCount; i++) {
				ThreadInfo threadInfo = new ThreadInfo(i, mFileInfo.getUrl(), i
						* length, (i + 1) * length - 1, 0);
				// ���һ���̳߳������������ֱ�����������ļ�
				if (i == mThreadCount - 1) {
					threadInfo.setEnd(mFileInfo.getLength());
				}
				// ��ӵ�������
				threadInfos.add(threadInfo);
				// �����ݿ�����߳���Ϣ			
				mDAO.insertThread(threadInfo);		
			}
		}

		// ��������߳����ظ��ļ�
		for(int i = 0; i < threadInfos.size(); i++){
			DownloadThread downloadThread = new DownloadThread(threadInfos.get(i));
			mDownloadThreads.add(downloadThread);
//			downloadThread.start();		
			// ���̳߳��������߳�
			sExecutorService.execute(downloadThread);
		}
	}
	
	/**
	 * ����Ƿ������߳�������ϣ������������ļ�������ɣ����͹㲥֪ͨUI���£����ļ��������������
	 */
	private synchronized void checkAllThreadsFinished(){
		boolean allFinished = true;
		// ��������Ƿ������߳��������
		for(DownloadThread downloadThread : this.mDownloadThreads){
			if(!downloadThread.mIsDownloadFinished){
				allFinished = false;
				break;
			}
		}
		if(allFinished){
			Intent intent = new Intent(DownloadService.ACTION_FINISH);
			intent.putExtra(Const.FILE_INFO_KEY, this.mFileInfo);
			// ���͹㲥֪ͨUI���£����ļ��������������
			this.mContext.sendBroadcast(intent);
			Log.i("DownloadTask", mFileInfo.getFileName() + "������ɣ����͹㲥֪ͨUI����");
			
			// ���������ļ�URLɾ����Ӧ���е������߳����ݿ���Ϣ		
			mDAO.deleteThread(mFileInfo.getUrl());
		}		
	}
	

	// ========================================

	/**
	 * �ڲ��࣬�����߳�
	 * 
	 * @author gzc
	 *
	 */
	class DownloadThread extends Thread {

		private ThreadInfo mThreadInfo = null;
		private boolean mIsDownloadFinished;

		public DownloadThread(ThreadInfo threadInfo) {
			super();
			this.mThreadInfo = threadInfo;
			mIsDownloadFinished = false;
		}

		@Override
		public void run() {
			HttpURLConnection conn = null;
			RandomAccessFile raf = null;
			InputStream is = null;
			BufferedInputStream bis = null;
			try {
				URL url = new URL(mThreadInfo.getUrl());
				conn = (HttpURLConnection) url.openConnection();
				conn.setConnectTimeout(3 * 1000);
				conn.setRequestMethod("GET");
				// ��������λ�ã����߳������ļ��ĴӶ����ֽڿ�ʼ���������ֽ�Ϊֹ
				int start = mThreadInfo.getStart() + mThreadInfo.getFinished();
				// ���ø��̵߳����ط�Χ
				conn.setRequestProperty("Range", "bytes=" + start + "-"
						+ mThreadInfo.getEnd());

				Log.i("DownloadThread", mThreadInfo.toString());

				// ����д��λ��
				File file = new File(DownloadService.DOWNLOAD_PATH,
						mFileInfo.getFileName());
				// ʹ���Զ���ģ��̳�RandomAccessFile��Ĵ���������BufferedRandomAccessFile�࣬���IO�ٶȡ�
				raf = new BufferedRandomAccessFile(file, "rwd");	//new RandomAccessFile(file, "rwd");
				// ���������ֽ�
				raf.seek(start);

				mFinished += mThreadInfo.getFinished();

				// ��ʼ���أ����ﲻ����200����Ϊ�����Ƕ��ļ���Range����
				if (206 == conn.getResponseCode()) {
					Intent intent = new Intent(DownloadService.ACTION_UPDATE);

					// �������ȡ�ļ�������
					is = conn.getInputStream();
					bis = new BufferedInputStream(is);
					byte[] buffer = new byte[8 * 1024];
					int len = -1;
					long time = System.currentTimeMillis();

					while ((len = bis.read(buffer, 0, buffer.length)) > 0) {
						// д�뱾���ļ�
						raf.write(buffer, 0, len);
						// �ۼ������ļ�������ɵĽ���
						mFinished += len;
						// �ۼ�ÿ���߳���ɵĽ���
						mThreadInfo.setFinished(mThreadInfo.getFinished() + len);

						// ÿ��1��ŷ�һ�ι㲥������UI
						if (System.currentTimeMillis() - time > (1 * 1000) ) {
							// �����ؽ���(�ٷֱ�)�����͹㲥��activity���½�����UI
							intent.putExtra(Const.FINISHED_KEY, 100 * mFinished
									/ mFileInfo.getLength());
							intent.putExtra(Const.ID_KEY, mFileInfo.getId());
							// ���͹㲥
							mContext.sendBroadcast(intent);
							Log.i("DownloadTask", "���͹㲥,�����ļ�ID="+mFileInfo.getId() + "���ؽ���Ϊ"+100 * mFinished
									/ mFileInfo.getLength());
						}
						// ģ�����ٺ���
						Thread.sleep(500);

						if (isPause) {
							// ��������ͣʱ���������ؽ��ȵ����ݿ�
							mDAO.updateThread(mThreadInfo.getUrl(),
									mThreadInfo.getId(),
									mThreadInfo.getFinished());
							Log.i("DownloadThread",
									"�������ؽ��ȣ�" + mThreadInfo.toString());
							// ����ѭ�������������������̣߳�����ͣ����
							return; // break;
						}
					} // end while

					// whileִ����ϣ�˵�����̵߳����������Ѿ����
					mIsDownloadFinished = true;
					
//					����Ƿ������߳��������
					checkAllThreadsFinished();
				} else {
					Log.e("DownloadThread", "����ҳʧ��");
				}

			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				// �ͷ���Դ
				try {
					if (null != bis) {
						bis.close();
					}
					if (null != is) {
						is.close();
					}
					if (null != raf) {
						raf.close();
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
				conn.disconnect();
			}
		} // end run( )

	} // end class DownloadThread

} // end class DownloadTask
