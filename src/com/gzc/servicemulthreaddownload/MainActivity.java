package com.gzc.servicemulthreaddownload;

import java.util.ArrayList;
import java.util.List;

import com.gzc.entity.FileInfo;
import com.gzc.service.DownloadService;
import com.gzc.util.Const;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;
import android.widget.Toast;

/**
 * 
 * @author gzc
 *
 */
public class MainActivity extends Activity {

	private ListView mLvFileName = null;
	private FileListAdapter mAdapter = null;
	private List<FileInfo> mFileInfos = null;

	/**
	 * 广播接受器，接受service发出的广播
	 */
	private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			if (DownloadService.ACTION_UPDATE.equals(intent.getAction())) {
				// 接受DownloadService发出的广播，得到下载进度，更新UI进度条
				int finished = intent.getIntExtra(Const.FINISHED_KEY, 0);
				int id = intent.getIntExtra(Const.ID_KEY, 0);
				// 更新该下载文件的下载进度UI
				mAdapter.updateProgress(id, finished);
				Log.i("MainActivity，接受广播", "ID=" + id + "，下载进度=" + finished);

			} else if (DownloadService.ACTION_FINISH.equals(intent.getAction())) {
				FileInfo fileInfo = (FileInfo) intent
						.getSerializableExtra(Const.FILE_INFO_KEY);
				// 提示下载完毕
				Toast.makeText(MainActivity.this,
						fileInfo.getFileName() + "下载完毕", Toast.LENGTH_SHORT)
						.show();
			}
		}
	};

	// ================================================

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		this.setContentView(R.layout.activity_main);
		this.initUi();
		initBroadcastReceiver();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		// activity销毁时，销毁广播接收器
		this.unregisterReceiver(mBroadcastReceiver);
	}

	// ================================================

	private void initUi() {
		mLvFileName = (ListView) this.findViewById(R.id.lvFileName);

//		String url = this.getString(R.string.file_url);  
//		String fileName = this.getString(R.string.file_name);
		// 创建下载文件实体类
		// ID得从0开始，因为ListView的项pos是从0开始的
		// final FileInfo fileInfo = new FileInfo(0, url, fileName, 0, 0);
		// final FileInfo fileInfo1 = new FileInfo(
		// 1,
		// "http://img5.duitang.com/uploads/item/201406/17/20140617143047_W4JSR.thumb.700_0.jpeg",
		// "20140617143047_W4JSR.thumb.700_0.jpeg", 0, 0);
		// final FileInfo fileInfo2 = new FileInfo(
		// 2,
		// "http://img6.cache.netease.com/cnews/2015/4/29/20150429083755285d6_550.jpg",
		// "20150429083755285d6_550.jpg", 0, 0);
		// final FileInfo fileInfo3 = new FileInfo(
		// 3,
		// "http://img1.cache.netease.com/cnews/2015/4/29/201504290902259b46b_550.jpg",
		// "201504290902259b46b_550.jpg", 0, 0);
		// final FileInfo fileInfo4 = new FileInfo(
		// 4,
		// "http://img6.cache.netease.com/cnews/2015/4/29/20150429090354c462c_550.jpg",
		// "20150429090354c462c_550.jpg", 0, 0);
		// final FileInfo fileInfo5 = new FileInfo(
		// 5,
		// "http://img4.cache.netease.com/cnews/2015/4/29/20150429083554543a1_550.png",
		// "20150429083554543a1_550.png", 0, 0);
		// final FileInfo fileInfo6 = new FileInfo(
		// 6,
		// "http://img3.cache.netease.com/cnews/2015/4/29/20150429083556f2110_550.png",
		// "20150429083556f2110_550.png", 0, 0);
		// final FileInfo fileInfo7 = new FileInfo(
		// 7,
		// "http://img1.cache.netease.com/cnews/2015/4/29/201504290835590ddb8_550.png",
		// "201504290835590ddb8_550.png", 0, 0);
		String strIp = "http://192.168.56.1:8080/";
		final FileInfo fileInfo1 = new FileInfo(0,
				strIp+"DianCan/img/1.jpg", "1.jpg", 0, 0);
		final FileInfo fileInfo2 = new FileInfo(1,
				strIp+"DianCan/img/2.jpg", "2.jpg", 0, 0);
		final FileInfo fileInfo3 = new FileInfo(2,
				strIp+"DianCan/img/3.jpg", "3.jpg", 0, 0);
		final FileInfo fileInfo4 = new FileInfo(3,
				strIp+"DianCan/img/4.jpg", "4.jpg", 0, 0);
		final FileInfo fileInfo5 = new FileInfo(4,
				strIp+"DianCan/img/5.jpg", "5.jpg", 0, 0);
		final FileInfo fileInfo6 = new FileInfo(5,
				strIp+"DianCan/img/6.jpg", "6.jpg", 0, 0);
		final FileInfo fileInfo7 = new FileInfo(6,
				strIp+"DianCan/img/7.jpg", "7.jpg", 0, 0);
		final FileInfo fileInfo8 = new FileInfo(7,
				strIp+"DianCan/img/8.jpg", "8.jpg", 0, 0);
		final FileInfo fileInfo9 = new FileInfo(8,
				strIp+"DianCan/img/a.mp3", "a.mp3", 0, 0);

		// 创建文件集合
		mFileInfos = new ArrayList<FileInfo>();

		mFileInfos.add(fileInfo1);
		mFileInfos.add(fileInfo2);
		mFileInfos.add(fileInfo3);
		mFileInfos.add(fileInfo4);
		mFileInfos.add(fileInfo5);
		mFileInfos.add(fileInfo6);
		mFileInfos.add(fileInfo7);
		 mFileInfos.add(fileInfo8);
		 mFileInfos.add(fileInfo9);

		this.mAdapter = new FileListAdapter(mFileInfos, this);
		// 给ListView设置适配器
		mLvFileName.setAdapter(mAdapter);
	}

	private void initBroadcastReceiver() {
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(DownloadService.ACTION_UPDATE);
		intentFilter.addAction(DownloadService.ACTION_FINISH);
		// 注册广播接收器
		this.registerReceiver(mBroadcastReceiver, intentFilter);
	}

}
