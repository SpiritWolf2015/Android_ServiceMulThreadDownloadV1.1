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
 * �����ļ��б�������
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
		System.out.printf("�������position = %d\n", position);
		ViewHolder holder = null;
		
		if (null == convertView) {
			holder = new ViewHolder();
			// ��XML�м�����ͼ
			convertView = LayoutInflater.from(mContext).inflate(
					R.layout.list_item, null);			
			
			// ��ÿؼ�
			holder.tvFileName = (TextView) convertView
					.findViewById(R.id.tvFileName);
			holder.btnStart = (Button) convertView.findViewById(R.id.btnStart);
			holder.btnStop = (Button) convertView.findViewById(R.id.btnStop);			
			holder.pbProgress = (ProgressBar) convertView
					.findViewById(R.id.pbProgress);	
			
			// ����Tag
			convertView.setTag(holder);						
		} else {
			// �õ�Tag����
			holder = (ViewHolder) convertView.getTag();
		}				
		// ������ͼ�еĿؼ�	
		holder.pbProgress.setMax(100);		
		holder.btnStart.setOnClickListener(new OnClickListener() {
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
		holder.btnStop.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// ͨ��intent����service����serviceֹͣ���߳������ļ�
				Intent intent = new Intent(mContext,
						DownloadService.class);
				intent.setAction(DownloadService.ACTION_STOP);
				intent.putExtra(Const.FILE_INFO_KEY, fileInfo);
				// ����service
				mContext.startService(intent);
			}
		});
		holder.tvFileName.setText(fileInfo.getFileName());		
		// �������ؽ���
		holder.pbProgress.setProgress(fileInfo.getFinished());
		
		return convertView;
	}

	// =========================================

	/**
	 * �ڲ��࣬�����static�࣬��ֻ֤���ظ���һ�Σ���ʡ�ڴ濪�� ��ʱ�洢����
	 * ��ÿ��getView������ÿ�η��ص�View���������������´����á�
	 * �������ĺô��ǣ�����ÿ�ζ��������ļ��������ҿؼ�
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
	 * ���¸������ļ������ؽ���
	 * @param id �����ļ���ID
	 * @param progress
	 */
	public void  updateProgress(int id, int progress){
		FileInfo fileInfo = this.mFileInfos.get(id);
		fileInfo.setFinished(progress);
		// �÷����������޸��������󶨵������������ˢ��activity��֪ͨactivity����ListView
		// ����notifyDataSetChanged������getView�ص����������±�����һ��
		this.notifyDataSetChanged();
	}

}
