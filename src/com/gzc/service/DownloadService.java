package com.gzc.service;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.LinkedHashMap;
import java.util.Map;

import com.gzc.entity.FileInfo;
import com.gzc.util.Const;

import android.app.Service;
import android.content.Intent;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;

/**
 * ��Service�������߳̽�������
 * 
 * @author gzc
 *
 */
public class DownloadService extends Service {

	// ��������ļ����ļ���·��
	public final static String DOWNLOAD_PATH = Environment
			.getExternalStorageDirectory().getAbsolutePath() + "/gzc_download/";

	public final static String ACTION_START = "ACTION_START";
	public final static String ACTION_STOP = "ACTION_STOP";
	public final static String ACTION_UPDATE = "ACTION_UPDATE";
	public final static String ACTION_FINISH = "ACTION_FINISH";

	public final static int MSG_INIT = 0;
	
	/**
	 * �������񼯺�
	 */
	private final Map<Integer, DownloadTask> mTasks = new LinkedHashMap<Integer, DownloadTask>();

	private Handler mHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case MSG_INIT:
				// �õ���ʼ��OK����Ϣ��ʹ������������Ķ��������ļ���
				FileInfo fileInfo = (FileInfo) msg.obj;
				float size = fileInfo.getLength();	// �ֽڴ�С
				float size_kb = size / 1024F;	// kb��С
				float size_mb = size_kb / 1024F;		// Mb��С
				Log.i("DownloadService", "�ļ���С = " + size
						+ "�ֽ�, KB = " + size_kb + ", MB = " + size_mb);
				
				// �����������������ļ������һ����������3���߳���������ļ�������ֻ�ǲ��Կ�������
				DownloadTask downloadTask = new DownloadTask(DownloadService.this, fileInfo, 3);	
				// ��������߳����ظ��ļ�
				downloadTask.download();
				// �Ѹ���������ŵ����񼯺���
				mTasks.put(fileInfo.getId(), downloadTask);
				break;
			default:
				Log.e("DownloadService", "δ֪��Ϣ��Handler�޷�����");
				break;
			}
		}
	};

	// =========================================

	// ����startService����Serviceʱ���ص��ú���
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// �жϴ�Intent��������ACTION�����ػ���ֹͣ
		if (ACTION_START.equals(intent.getAction())) {
			Log.i("Service, onStartCommand", "��ʼ����");
			FileInfo fileInfo = (FileInfo) intent
					.getSerializableExtra(Const.FILE_INFO_KEY);

			// ������ʼ���̣߳���ʼ�������ļ�
			InitThread initThread = new InitThread(fileInfo);
			// ���̳߳��������߳�
			DownloadTask.sExecutorService.execute(initThread);
			
		} else if (ACTION_STOP.equals(intent.getAction())) {
			
			FileInfo fileInfo = (FileInfo) intent
					.getSerializableExtra(Const.FILE_INFO_KEY);

			DownloadTask downloadTask = this.mTasks.get(fileInfo.getId());
			if (null != downloadTask) {
				// ֹͣ����
				downloadTask.isPause = true;
				Log.i("Service, onStartCommand", "ֹͣ�����ļ���"+fileInfo.getFileName());
			}else{
				Log.e("Service, onStartCommand", "ֹͣ��������"+fileInfo.getFileName() + "������");
			}

		} else {
			Log.e("Service, onStartCommand", "δ֪ACTION");
		}

		return super.onStartCommand(intent, flags, startId);
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	// =========================================

	/**
	 * �ڲ��࣬�����߳��ж�Ҫ�����ļ���ʼ�����ڱ��ش���һ����Ҫ�����ļ�һ����С�Ŀ��ļ�
	 * 
	 * @author gzc
	 *
	 */
	class InitThread extends Thread {

		private FileInfo mFileInfo = null;

		public InitThread(FileInfo mFileInfo) {
			super();
			this.mFileInfo = mFileInfo;
		}

		@Override
		public void run() {
			HttpURLConnection conn = null;
			int length = -1;
			RandomAccessFile raf = null;

			// ��������
			try {
				URL url = new URL(mFileInfo.getUrl());
				conn = (HttpURLConnection) url.openConnection();
				conn.setConnectTimeout(3 * 1000);
				conn.setRequestMethod("GET");
				
				if (200 == conn.getResponseCode()) {
					// ���Ҫ�����ļ��ĳ���
					length = conn.getContentLength();
				}
				if (length < 0) {
					Log.e("DownloadService, InitThread", "�ļ��ĳ���<0");
					return;
				}
				File dir = new File(DOWNLOAD_PATH);
				if (!dir.exists()) {
					// �����ļ���
					dir.mkdir();
				}
				// �ڱ��ش����ļ�
				File file = new File(dir, mFileInfo.getFileName());
				// rwd����ʾ ����д��ɾ
				raf = new RandomAccessFile(file, "rwd");

				mFileInfo.setLength(length);
				// �����ļ�����
				raf.setLength(mFileInfo.getLength());
				// ������Ϣ��Handler
				mHandler.obtainMessage(MSG_INIT, mFileInfo).sendToTarget();

			} catch (MalformedURLException e) {
				Log.e("DownloadService, InitThread", "MalformedURLException");
				e.printStackTrace();
			} catch (ProtocolException e) {
				Log.e("DownloadService, InitThread", "ProtocolException");
				e.printStackTrace();
			} catch (IOException e) {
				Log.e("DownloadService, InitThread", "IOException");
				e.printStackTrace();
			} finally {
				if (null != raf) {
					try {
						raf.close();
					} catch (IOException e) {
						Log.e("DownloadService, InitThread", "IOException");
						e.printStackTrace();
					}
				}
				if (null != conn) {
					conn.disconnect();
				}
			}
		}
	} // end class InitThread

} // end class DownloadService
