/**
 * 
 */
package com.gzc.adapter;

import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

/**
 * ͨ�õ� Adapter
 * 
 * @author gzc
 *
 */
public abstract class CommonAdapter<T> extends BaseAdapter {
	
	/**
     * ����Դ
     */
    protected List<T> datas = null;
    /**
     * �����Ķ���
     */
    protected Context context = null;
    /**
     * item�����ļ�����ԴID
     */
    protected int itemLayoutResId = 0;
    
    //=================================

    public CommonAdapter(Context context, List<T> datas, int itemLayoutResId) {
        this.context = context;
        this.datas = datas;
        this.itemLayoutResId = itemLayoutResId;
    }

  //=================================
	
	@Override
	public int getCount() {		
		return this.datas.size();
	}
	
	@Override
	public T getItem(int position) {		
		return datas.get(position);
	}

	@Override
	public long getItemId(int position) {		
		return position;
	}
	
	@Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = ViewHolder.getViewHolder(context, itemLayoutResId, position, convertView, parent);
        convert(viewHolder, getItem(position));

        return viewHolder.getConvertView();
    }
	
	//=================================
	
	/**
     * ������ʵ�ָ÷���������ҵ����
     */
    public abstract void convert(final ViewHolder viewHolder, final T item);

}
