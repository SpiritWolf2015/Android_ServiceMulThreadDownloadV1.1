/**
 * 
 */
package com.gzc.adapter;

import android.content.Context;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * @author gzc
 *
 */
public class ViewHolder {

	/**
     * �洢item�����ÿؼ����õ�����
     * 
     * Key - ��ԴID 
     * Value - �ؼ�������
     */
    private SparseArray<View> views = null;
    private View convertView = null;
    private int position = 0;
	
    /**
     * ˽�л��Ĺ��캯���������ڲ��������ʵ��
     * 
     * @param context �����Ķ���
     * @param itemLayoutResId item�Ĳ����ļ�����ԴID
     * @param position BaseAdapter.getView()�Ĵ������
     * @param parent BaseAdapter.getView()�Ĵ������
     */
    private ViewHolder(Context context, int itemLayoutResId, int position, ViewGroup parent) {
        this.views = new SparseArray<View>();
        this.position = position;
        this.convertView = LayoutInflater.from(context).inflate(itemLayoutResId, parent, false);

        convertView.setTag(this);
    }
    
    /**
     * �õ�һ��ViewHolder����
     * 
     * @param context �����Ķ���
     * @param itemLayoutResId item�Ĳ����ļ�����ԴID
     * @param position BaseAdapter.getView()�Ĵ������
     * @param convertView BaseAdapter.getView()�Ĵ������
     * @param parent BaseAdapter.getView()�Ĵ������
     * @return һ��ViewHolder����
     */
    public static ViewHolder getViewHolder(Context context, int itemLayoutResId, int position,
            View convertView, ViewGroup parent) {
        if (convertView == null) {
            return new ViewHolder(context, itemLayoutResId, position, parent);
        } else {
            ViewHolder viewHolder = (ViewHolder) convertView.getTag();
            viewHolder.position = position; // ����Ҫ����һ��position����Ϊpositionһֱ�����仯
            return viewHolder;
        }        
    }

    public View getConvertView() {
        return convertView;
    }
    
    /**
     * �����Ĳ��֡�
     * ���ݿؼ�����ԴID����ȡ�ؼ�
     * 
     * @param viewResId �ؼ�����ԴID
     * @return �ؼ�������
     */
    public <T extends View> T getView(int viewResId) {
        View view = views.get(viewResId);

        if (view == null) {
            view = convertView.findViewById(viewResId);
            views.put(viewResId, view);
        }

        return (T) view;
    }


}
