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
 * �����ļ��б�������
 * 
 * @author gzc
 */
public class FileListAdapter extends CommonAdapter<FileInfo> {

	/**
	 * ���캯��
	 */
	public FileListAdapter(Context context, List<FileInfo> datas,
			int itemLayoutResId) {
		super(context, datas, itemLayoutResId);
	}

	@Override
	public void convert(final ViewHolder viewHolder, final FileInfo fileInfo) {
		// �����ļ���
		((TextView) viewHolder.getView(R.id.tvFileName)).setText(fileInfo
				.getFileName());
		ProgressBar pbProgress = ((ProgressBar) viewHolder
				.getView(R.id.pbProgress));
		pbProgress.setMax(100);
		// �������ؽ���
		pbProgress.setProgress(fileInfo.getFinished());

		Button btnStart = ((Button) viewHolder.getView(R.id.btnStart));
		Button btnStop = ((Button) viewHolder.getView(R.id.btnStop));
		// ���ð�ť�¼�����
		btnStart.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// ͨ��intent����service����service�������߳������ļ�
				Intent intent = new Intent(context, DownloadService.class);
				intent.setAction(DownloadService.ACTION_START);
				intent.putExtra(Const.FILE_INFO_KEY, fileInfo);
				// ����service��ע�⣬��Ҫ������XML��ע��service
				context.startService(intent);
			}
		});
		btnStop.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// ͨ��intent����service����serviceֹͣ���߳������ļ�
				Intent intent = new Intent(context, DownloadService.class);
				intent.setAction(DownloadService.ACTION_STOP);
				intent.putExtra(Const.FILE_INFO_KEY, fileInfo);
				// ����service
				context.startService(intent);
			}
		});
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
		FileInfo fileInfo = this.datas.get(id);
		fileInfo.setFinished(progress);
		// �÷����������޸��������󶨵������������ˢ��activity��֪ͨactivity����ListView
		// ����notifyDataSetChanged������getView�ص����������±�����һ��
		this.notifyDataSetChanged();
	}

}
