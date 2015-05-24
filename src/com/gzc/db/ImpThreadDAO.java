package com.gzc.db;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.gzc.entity.ThreadInfo;

/**
 * 数据库访问接口实现类
 * @author gzc
 */
public class ImpThreadDAO implements IThreadDAO {

	private DbHelper mHelper = null;

	public ImpThreadDAO(Context context) {
		mHelper = DbHelper.getInstance(context);
	}

	@Override
	public synchronized void insertThread(ThreadInfo threadInfo) {
		SQLiteDatabase db = mHelper.getWritableDatabase();
		db.execSQL(
				"insert into thread_info(thread_id, url, start, end, finished) values(?, ?, ?, ?, ?)",
				new Object[] { threadInfo.getId(), threadInfo.getUrl(),
						threadInfo.getStart(), threadInfo.getEnd(),
						threadInfo.getFinished() });
		// 释放资源
		closeDb(db);
	}

	@Override
	public synchronized void deleteThread(String url) {
		SQLiteDatabase db = mHelper.getWritableDatabase();
		db.execSQL("delete from thread_info where url = ?",
				new Object[] { url });
		
		// 释放资源
		closeDb(db);
	}

	@Override
	public synchronized void updateThread(String url, int thread_id, int finished) {
		SQLiteDatabase db = mHelper.getWritableDatabase();
		db.execSQL(
				"update thread_info set finished = ? where url = ? and thread_id = ?",
				new Object[] { finished, url, thread_id });
		// 释放资源
		closeDb(db);
	}

	@Override
	public List<ThreadInfo> getThreads(String url) {
		SQLiteDatabase db = mHelper.getReadableDatabase();
		List<ThreadInfo> list = new ArrayList<ThreadInfo>();

		Cursor cursor = db.rawQuery("select * from thread_info where url = ?",
				new String[] { url });
		while(cursor.moveToNext()){
			ThreadInfo ti = new ThreadInfo();
			ti.setId(cursor.getInt(cursor.getColumnIndex("thread_id")));
			ti.setUrl(cursor.getString(cursor.getColumnIndex("url")));
			ti.setStart(cursor.getInt(cursor.getColumnIndex("start")));
			ti.setEnd(cursor.getInt(cursor.getColumnIndex("end")));
			ti.setFinished(cursor.getInt(cursor.getColumnIndex("finished")));
			
			list.add(ti);
		}		
		// 释放资源
		closeCursor(cursor);
		closeDb(db);
		
		return list;
	}

	@Override
	public boolean isExists(String url, int thread_id) {
		SQLiteDatabase db = mHelper.getReadableDatabase();	
		Cursor cursor = db.rawQuery("select * from thread_info where url = ? and thread_id = ?",
				new String[] { url, thread_id + "" });		
		boolean exists = cursor.moveToNext();		
		
		// 释放资源
		closeCursor(cursor);
		closeDb(db);
		
		return exists;
	}
	
	void closeCursor(Cursor cursor){
		if (null != cursor) {
			cursor.close();
		}
	}
	void closeDb(SQLiteDatabase db){
		if (null != db) {
			db.close();
		}
	}

}
