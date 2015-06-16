package com.gzc.adapter;

import java.util.List;

import com.gzc.entity.FileInfo;
import com.gzc.service.DownloadService;
import com.gzc.servicemulthreaddownload.R;
import com.gzc.util.Const;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

/**
 * 下载文件列表适配器
 * 
 * @author gzc
 */
public class FileListAdapter extends CommonAdapter<FileInfo> {

	/**
	 * 构造函数
	 */
	public FileListAdapter(Context context, List<FileInfo> datas,
			int itemLayoutResId) {
		super(context, datas, itemLayoutResId);
	}

	@Override
	public void convert(final ViewHolder viewHolder, final FileInfo fileInfo) {
		// 设置文件名
		((TextView) viewHolder.getView(R.id.tvFileName)).setText(fileInfo
				.getFileName());
		ProgressBar pbProgress = ((ProgressBar) viewHolder
				.getView(R.id.pbProgress));
		pbProgress.setMax(100);
		// 更新下载进度
		pbProgress.setProgress(fileInfo.getFinished());

		Button btnStart = ((Button) viewHolder.getView(R.id.btnStart));
		Button btnStop = ((Button) viewHolder.getView(R.id.btnStop));
		// 设置按钮事件监听
		btnStart.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// 通过intent启动service，在service启动多线程下载文件
				Intent intent = new Intent(context, DownloadService.class);
				intent.setAction(DownloadService.ACTION_START);
				intent.putExtra(Const.FILE_INFO_KEY, fileInfo);
				// 启动service，注意，不要忘记在XML里注册service
				context.startService(intent);
			}
		});
		btnStop.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// 通过intent启动service，在service停止多线程下载文件
				Intent intent = new Intent(context, DownloadService.class);
				intent.setAction(DownloadService.ACTION_STOP);
				intent.putExtra(Const.FILE_INFO_KEY, fileInfo);
				// 启动service
				context.startService(intent);
			}
		});
	}

	// =========================================

	/**
	 * 更新该下载文件的下载进度
	 * 
	 * @param id
	 *            下载文件的ID
	 * @param progress
	 */
	public void updateProgress(int id, int progress) {
		FileInfo fileInfo = this.datas.get(id);
		fileInfo.setFinished(progress);
		// 该方法可以在修改适配器绑定的数组后不用重新刷新activity，通知activity更新ListView
		// 调用notifyDataSetChanged函数后，getView回调函数会重新被调用一遍
		this.notifyDataSetChanged();
	}

}
