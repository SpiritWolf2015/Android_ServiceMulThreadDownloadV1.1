package com.gzc.servicemulthreaddownload;

import java.util.List;

import com.gzc.entity.FileInfo;
import com.gzc.service.DownloadService;
import com.gzc.util.Const;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.util.SparseArray;
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
 * 
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
		if (null != mFileInfos && mFileInfos.size() > 0) {
			return mFileInfos.size();
		} else {
			Log.e("FileListAdapter->getCount( )",
					"mFileInfos is null or size is 0");
		}
		return 0;
	}

	@Override
	public Object getItem(int position) {
		if (null != mFileInfos && mFileInfos.size() > 0) {
			return mFileInfos.get(position);
		} else {
			Log.e("FileListAdapter->getItem( )",
					"mFileInfos is null or size is 0");
		}
		return null;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	/*
	 * [Android分享] ViewHolder模式超简洁写法，很cool!
	 * http://www.eoeandroid.com/thread-321547-1-1.html
	 * 
	 * Android中利用ViewHolder优化自定义Adapter的典型写法
	 * http://www.cnblogs.com/mengdd/p/3254323.html
	 * 
	 * [Android]对BaseAdapter中ViewHolder编写简化(转)
	 * http://www.cnblogs.com/ycxyyzw/p/3812060.html
	 * 
	 * Android数据适配器(Adapter)优化：高效ViewHolder
	 * http://mobile.51cto.com/aprogram-460521.htm
	 */

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final FileInfo fileInfo = this.mFileInfos.get(position);
		System.out.printf("点击的项position = %d\n", position);
		
		if (null == convertView) {			
			// 从XML中加载视图
			convertView = LayoutInflater.from(mContext).inflate(
					R.layout.list_item, null);
		}
		// 获得控件
		TextView tvFileName = ViewHolder.get(convertView, R.id.tvFileName);
		Button btnStart = ViewHolder.get(convertView, R.id.btnStart);
		Button btnStop = ViewHolder.get(convertView, R.id.btnStop);
		ProgressBar pbProgress = ViewHolder.get(convertView, R.id.pbProgress);

		// 设置视图中的控件
		pbProgress.setMax(100);
		tvFileName.setText(fileInfo.getFileName());
		// 更新下载进度
		pbProgress.setProgress(fileInfo.getFinished());
		// 设置按钮事件监听
		btnStart.setOnClickListener(new OnClickListener() {
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
		btnStop.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// 通过intent启动service，在service停止多线程下载文件
				Intent intent = new Intent(mContext, DownloadService.class);
				intent.setAction(DownloadService.ACTION_STOP);
				intent.putExtra(Const.FILE_INFO_KEY, fileInfo);
				// 启动service
				mContext.startService(intent);
			}
		});

		return convertView;
	}

	// =========================================

	/**
	 * 内部类，定义成static类，保证只加载该类一次，节省内存开销 临时存储器， 把每次getView方法中每次返回的View缓冲起来，可以下次再用。
	 * 这样做的好处是，不必每次都到布局文件中来查找控件
	 * 
	 * @author gzc
	 */
	static class ViewHolder {

		@SuppressWarnings("unchecked")
		public static <T extends View> T get(View view, int id) {
			SparseArray<View> viewHolder = (SparseArray<View>) view.getTag();
			if (viewHolder == null) {
				viewHolder = new SparseArray<View>();
				view.setTag(viewHolder);
			}
			View childView = viewHolder.get(id);
			if (childView == null) {
				childView = view.findViewById(id);
				viewHolder.put(id, childView);
			}
			return (T) childView;
		}
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
		FileInfo fileInfo = this.mFileInfos.get(id);
		fileInfo.setFinished(progress);
		// 该方法可以在修改适配器绑定的数组后不用重新刷新activity，通知activity更新ListView
		// 调用notifyDataSetChanged函数后，getView回调函数会重新被调用一遍
		this.notifyDataSetChanged();
	}

}
