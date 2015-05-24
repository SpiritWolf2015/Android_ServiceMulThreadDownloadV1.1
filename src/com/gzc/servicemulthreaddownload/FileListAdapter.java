package com.gzc.servicemulthreaddownload;

import java.util.List;

import com.gzc.entity.FileInfo;
import com.gzc.service.DownloadService;
import com.gzc.util.Const;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

/**
 * 下载文件列表适配器
 * @author gzc
 */
public class FileListAdapter extends BaseAdapter {

	private Context mContext = null;
	private List<FileInfo> mFileInfos = null;

	/**
	 * 构造函数
	 */
	public FileListAdapter(List<FileInfo> fileInfos, Context context) {
		mFileInfos = fileInfos;
		mContext = context;
	}

	// 用于返回需要在ListView上显示的数据的总数要让文件集合中的文件显示在列表上，所以返回的是文件集合对象的size（）函数，返回文件个数
	@Override
	public int getCount() {
		return mFileInfos.size();
	}

	@Override
	public Object getItem(int position) {		
		return mFileInfos.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {				
		final FileInfo fileInfo = this.mFileInfos.get(position);
		System.out.printf("点击的项position = %d\n", position);
		ViewHolder holder = null;
		
		if (null == convertView) {
			holder = new ViewHolder();
			// 从XML中加载视图
			convertView = LayoutInflater.from(mContext).inflate(
					R.layout.list_item, null);			
			
			// 获得控件
			holder.tvFileName = (TextView) convertView
					.findViewById(R.id.tvFileName);
			holder.btnStart = (Button) convertView.findViewById(R.id.btnStart);
			holder.btnStop = (Button) convertView.findViewById(R.id.btnStop);			
			holder.pbProgress = (ProgressBar) convertView
					.findViewById(R.id.pbProgress);	
			
			// 设置Tag
			convertView.setTag(holder);						
		} else {
			// 得到Tag重用
			holder = (ViewHolder) convertView.getTag();
		}				
		// 设置视图中的控件	
		holder.pbProgress.setMax(100);		
		holder.btnStart.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// 通过intent启动service，在service启动多线程下载文件
				Intent intent = new Intent(mContext, DownloadService.class);
				intent.setAction(DownloadService.ACTION_START);
				intent.putExtra(Const.FILE_INFO_KEY, fileInfo);
				// 启动service，注意，不要忘记在XML里注册service
				mContext.startService(intent);
			}
		});
		holder.btnStop.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// 通过intent启动service，在service停止多线程下载文件
				Intent intent = new Intent(mContext,
						DownloadService.class);
				intent.setAction(DownloadService.ACTION_STOP);
				intent.putExtra(Const.FILE_INFO_KEY, fileInfo);
				// 启动service
				mContext.startService(intent);
			}
		});
		holder.tvFileName.setText(fileInfo.getFileName());		
		// 更新下载进度
		holder.pbProgress.setProgress(fileInfo.getFinished());
		
		return convertView;
	}

	// =========================================

	/**
	 * 内部类，定义成static类，保证只加载该类一次，节省内存开销 临时存储器，
	 * 把每次getView方法中每次返回的View缓冲起来，可以下次再用。
	 * 这样做的好处是，不必每次都到布局文件中来查找控件
	 * 
	 * @author gzc
	 */
	static class ViewHolder {
		TextView tvFileName;
		Button btnStart, btnStop;
		ProgressBar pbProgress;
	}
	
	// =========================================
	
	/**
	 * 更新该下载文件的下载进度
	 * @param id 下载文件的ID
	 * @param progress
	 */
	public void  updateProgress(int id, int progress){
		FileInfo fileInfo = this.mFileInfos.get(id);
		fileInfo.setFinished(progress);
		// 该方法可以在修改适配器绑定的数组后不用重新刷新activity，通知activity更新ListView
		// 调用notifyDataSetChanged函数后，getView回调函数会重新被调用一遍
		this.notifyDataSetChanged();
	}

}
