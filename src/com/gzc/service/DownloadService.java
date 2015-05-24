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
 * 在Service启动多线程进行下载
 * 
 * @author gzc
 *
 */
public class DownloadService extends Service {

	// 存放下载文件的文件夹路径
	public final static String DOWNLOAD_PATH = Environment
			.getExternalStorageDirectory().getAbsolutePath() + "/gzc_download/";

	public final static String ACTION_START = "ACTION_START";
	public final static String ACTION_STOP = "ACTION_STOP";
	public final static String ACTION_UPDATE = "ACTION_UPDATE";
	public final static String ACTION_FINISH = "ACTION_FINISH";

	public final static int MSG_INIT = 0;
	
	/**
	 * 下载任务集合
	 */
	private final Map<Integer, DownloadTask> mTasks = new LinkedHashMap<Integer, DownloadTask>();

	private Handler mHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case MSG_INIT:
				// 得到初始化OK的消息，使用下载任务类的对象下载文件。
				FileInfo fileInfo = (FileInfo) msg.obj;
				float size = fileInfo.getLength();	// 字节大小
				float size_kb = size / 1024F;	// kb大小
				float size_mb = size_kb / 1024F;		// Mb大小
				Log.i("DownloadService", "文件大小 = " + size
						+ "字节, KB = " + size_kb + ", MB = " + size_mb);
				
				// 启动下载任务，下载文件，最后一个参数是用3个线程下载这个文件，这里只是测试可任意填
				DownloadTask downloadTask = new DownloadTask(DownloadService.this, fileInfo, 3);	
				// 开启多个线程下载该文件
				downloadTask.download();
				// 把该下载任务放到任务集合中
				mTasks.put(fileInfo.getId(), downloadTask);
				break;
			default:
				Log.e("DownloadService", "未知消息，Handler无法处理");
				break;
			}
		}
	};

	// =========================================

	// 调用startService启动Service时，回调该函数
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// 判断从Intent传过来的ACTION是下载还是停止
		if (ACTION_START.equals(intent.getAction())) {
			Log.i("Service, onStartCommand", "开始下载");
			FileInfo fileInfo = (FileInfo) intent
					.getSerializableExtra(Const.FILE_INFO_KEY);

			// 启动初始化线程，初始化本地文件
			InitThread initThread = new InitThread(fileInfo);
			// 从线程池中启动线程
			DownloadTask.sExecutorService.execute(initThread);
			
		} else if (ACTION_STOP.equals(intent.getAction())) {
			
			FileInfo fileInfo = (FileInfo) intent
					.getSerializableExtra(Const.FILE_INFO_KEY);

			DownloadTask downloadTask = this.mTasks.get(fileInfo.getId());
			if (null != downloadTask) {
				// 停止下载
				downloadTask.isPause = true;
				Log.i("Service, onStartCommand", "停止下载文件："+fileInfo.getFileName());
			}else{
				Log.e("Service, onStartCommand", "停止下载任务："+fileInfo.getFileName() + "不存在");
			}

		} else {
			Log.e("Service, onStartCommand", "未知ACTION");
		}

		return super.onStartCommand(intent, flags, startId);
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	// =========================================

	/**
	 * 内部类，在子线程中对要下载文件初始化，在本地创建一个与要下载文件一样大小的空文件
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

			// 连接网络
			try {
				URL url = new URL(mFileInfo.getUrl());
				conn = (HttpURLConnection) url.openConnection();
				conn.setConnectTimeout(3 * 1000);
				conn.setRequestMethod("GET");
				
				if (200 == conn.getResponseCode()) {
					// 获得要下载文件的长度
					length = conn.getContentLength();
				}
				if (length < 0) {
					Log.e("DownloadService, InitThread", "文件的长度<0");
					return;
				}
				File dir = new File(DOWNLOAD_PATH);
				if (!dir.exists()) {
					// 创建文件夹
					dir.mkdir();
				}
				// 在本地创建文件
				File file = new File(dir, mFileInfo.getFileName());
				// rwd，表示 读，写，删
				raf = new RandomAccessFile(file, "rwd");

				mFileInfo.setLength(length);
				// 设置文件长度
				raf.setLength(mFileInfo.getLength());
				// 发送消息给Handler
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
