/**
 * 
 */
package com.gzc.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


/**
 * ���ݿ�����࣬������������߳����ؽ�����Ϣ
 * @author gzc
 */
public class DbHelper extends SQLiteOpenHelper {
	
	private static final String DB_NAME = "gzc_mul_thread_download.db";
	private static final int VERSION = 1;
	
	// ����SQL���
	private static final String SQL_CREATE = "create table thread_info(_id integer primary key autoincrement, "
			+ "thread_id integer, url text, start integer, end integer, finished integer)";
	// ɾ��SQL���
	private static final String SQL_DELETE = "drap table if exists thread_info";
	
	//=================����==================
		private static DbHelper sInstance;
		/**
		 * ����
		 * @param context
		 * @return
		 */
		public static DbHelper getInstance(Context context){			
			if(sInstance == null){
				sInstance = new DbHelper(context);
			}
			return sInstance;
		}
		// ˽�й��캯��
		private DbHelper(Context context) {
			super(context, DB_NAME, null, VERSION);
		}
	//=================����==================

	//=========��дSQLiteOpenHelper�ĺ���==============		
	// �������ݿ�ʱ�ص�	
	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(SQL_CREATE);		
	}

	// �������ݿ�ʱ�ص�	
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// ֱ��ɾ���ˣ����´�����
		db.execSQL(SQL_DELETE);		
		db.execSQL(SQL_CREATE);		
	}
	//=========��дSQLiteOpenHelper�ĺ���==============

}
