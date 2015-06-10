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
 * �����ļ��б�������
 * 
 * @author gzc
 */
public class FileListAdapter extends BaseAdapter {

	private Context mContext = null;
	private List<FileInfo> mFileInfos = null;

	/**
	 * ���캯��
	 */
	public FileListAdapter(List<FileInfo> fileInfos, Context context) {
		mFileInfos = fileInfos;
		mContext = context;
	}

	// ���ڷ�����Ҫ��ListView����ʾ�����ݵ�����Ҫ���ļ������е��ļ���ʾ���б��ϣ����Է��ص����ļ����϶����size���������������ļ�����
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
	 * [Android����] ViewHolderģʽ�����д������cool!
	 * http://www.eoeandroid.com/thread-321547-1-1.html
	 * 
	 * Android������ViewHolder�Ż��Զ���Adapter�ĵ���д��
	 * http://www.cnblogs.com/mengdd/p/3254323.html
	 * 
	 * [Android]��BaseAdapter��ViewHolder��д��(ת)
	 * http://www.cnblogs.com/ycxyyzw/p/3812060.html
	 * 
	 * Android����������(Adapter)�Ż�����ЧViewHolder
	 * http://mobile.51cto.com/aprogram-460521.htm
	 */

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final FileInfo fileInfo = this.mFileInfos.get(position);
		System.out.printf("�������position = %d\n", position);
		
		if (null == convertView) {			
			// ��XML�м�����ͼ
			convertView = LayoutInflater.from(mContext).inflate(
					R.layout.list_item, null);
		}
		// ��ÿؼ�
		TextView tvFileName = ViewHolder.get(convertView, R.id.tvFileName);
		Button btnStart = ViewHolder.get(convertView, R.id.btnStart);
		Button btnStop = ViewHolder.get(convertView, R.id.btnStop);
		ProgressBar pbProgress = ViewHolder.get(convertView, R.id.pbProgress);

		// ������ͼ�еĿؼ�
		pbProgress.setMax(100);
		tvFileName.setText(fileInfo.getFileName());
		// �������ؽ���
		pbProgress.setProgress(fileInfo.getFinished());
		// ���ð�ť�¼�����
		btnStart.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// ͨ��intent����service����service�������߳������ļ�
				Intent intent = new Intent(mContext, DownloadService.class);
				intent.setAction(DownloadService.ACTION_START);
				intent.putExtra(Const.FILE_INFO_KEY, fileInfo);
				// ����service��ע�⣬��Ҫ������XML��ע��service
				mContext.startService(intent);
			}
		});
		btnStop.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// ͨ��intent����service����serviceֹͣ���߳������ļ�
				Intent intent = new Intent(mContext, DownloadService.class);
				intent.setAction(DownloadService.ACTION_STOP);
				intent.putExtra(Const.FILE_INFO_KEY, fileInfo);
				// ����service
				mContext.startService(intent);
			}
		});

		return convertView;
	}

	// =========================================

	/**
	 * �ڲ��࣬�����static�࣬��ֻ֤���ظ���һ�Σ���ʡ�ڴ濪�� ��ʱ�洢���� ��ÿ��getView������ÿ�η��ص�View���������������´����á�
	 * �������ĺô��ǣ�����ÿ�ζ��������ļ��������ҿؼ�
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
	 * ���¸������ļ������ؽ���
	 * 
	 * @param id
	 *            �����ļ���ID
	 * @param progress
	 */
	public void updateProgress(int id, int progress) {
		FileInfo fileInfo = this.mFileInfos.get(id);
		fileInfo.setFinished(progress);
		// �÷����������޸��������󶨵������������ˢ��activity��֪ͨactivity����ListView
		// ����notifyDataSetChanged������getView�ص����������±�����һ��
		this.notifyDataSetChanged();
	}

}
