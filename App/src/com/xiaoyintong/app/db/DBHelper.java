package com.xiaoyintong.app.db;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.xiaoyintong.app.bean.BuildingBrief;
import com.xiaoyintong.app.bean.IdAndType;
import com.xiaoyintong.app.bean.OrderUnit;
import com.xiaoyintong.app.common.StringUtils;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;
import android.util.Log;

public class DBHelper extends SQLiteOpenHelper {

	private static final String TAG = "DBHelper";

	private static final String DATABASE_NAME = "xiaoyintong.db";
	private static final int DATABASE_VERSION = 1;

	private static final String DB_TABLE_ORDERUNIT = "orderunit";
	// OrderUnit(int tid ,int upload_time, String userName ,String phone ,
	// int pos_loc , int pos_build ,int pos_dorm ,
	// String sendMsg ,String file_name ,int page_number ,String money
	// ,int send_time , int binder ,String page_type ,int type , int file_others
	// , String page_size)
	private static final String DB_CREATE_TABLE_APP = "CREATE TABLE IF NOT EXISTS "
			+ DB_TABLE_ORDERUNIT
			+ "("
			+ APP_COLUMNS._ID
			+ " INTEGER PRIMARY KEY AUTOINCREMENT,"
			+ APP_COLUMNS.tid
			+ " INTEGER ,"
			+ APP_COLUMNS.UID
			+ " VARCHAR(10),"
			+ APP_COLUMNS.USERNAME
			+ " CHAR(10),"
			+ APP_COLUMNS.PHONE
			+ " CHAR(12),"
			+ APP_COLUMNS.POS_LOC
			+ " INGETER,"
			+ APP_COLUMNS.POS_BUILD
			+ " INTEGER,"
			+ APP_COLUMNS.POS_DORM
			+ " INTEGER,"
			+ APP_COLUMNS.SEND_TIME
			+ " INTEGER,"
			+ APP_COLUMNS.SEND_MSG
			+ " TEXT,"
			+ APP_COLUMNS.FILENAME
			+ " VARCHAR(20),"
			+ APP_COLUMNS.FILE_MSG
			+ " TEXT,"
			// + APP_COLUMNS.PAGE_SIZE
			// + " CHAR(5),"
			// + APP_COLUMNS.PAGE_NUMBER
			// + " INTEGER,"
			// + APP_COLUMNS.PAGE_TYPE
			// + " CHAR(5),"
			+ APP_COLUMNS.MONEY
			+ " CHAR(15),"
			// + APP_COLUMNS.FILE_OTHERS
			// + " INTEGER,"
			// + APP_COLUMNS.BINDER
			// + " INTEGER,"
			// + APP_COLUMNS.TYPE
			// + " INTEGER,"
			+ APP_COLUMNS.ISDELIVERED
			+ " INTEGER,"
			+ APP_COLUMNS.UPLOAD_TIME
			+ " INTEGER,"
			+ APP_COLUMNS.DATE
			+ " date,"
			+ " CONSTRAINT the_only UNIQUE(tid))";

	// TimeStamp NOT NULL DEFAULT CURRENT_TIMESTAMP
	private static DBHelper mDbHelper;

	protected DBHelper(Context context) {
		// CursorFactory设置为null,使用默认值
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	public synchronized static DBHelper getInstance(Context context) {
		if (mDbHelper == null) {
			mDbHelper = new DBHelper(context);
		}
		return mDbHelper;
	}

	public synchronized void destroyInstance() {
		if (mDbHelper == null) {
			mDbHelper.close();
		}
	}

	// 数据库第一次被创建时onCreate会被调用
	@Override
	public void onCreate(SQLiteDatabase db) {
		Log.v(TAG, "onCreate");
		db.execSQL(DB_CREATE_TABLE_APP);
	}

	// 如果DATABASE_VERSION值被改为2,系统发现现有数据库版本不同,即会调用onUpgrade
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// db.execSQL("ALTER TABLE person ADD COLUMN other STRING");
		Log.v(TAG, "数据库更新");
		db.execSQL("DROP TABLE IF EXISTS OrderUnits");
		db.execSQL("DROP TABLE IF EXISTS SimpleOrders");
		onCreate(db);
	}

	/**
	 * // * 处理订单元信息（分区代理） // *
	 */
	public synchronized void processOrderUnits(List<OrderUnit> orderUnits,
			String uid, long date) {

		String mDate = StringUtils.formatData(new Date(date));
		final SQLiteDatabase db = getWritableDatabase();

		db.beginTransaction();// 开始事务
		try {
			for (OrderUnit orderUnit : orderUnits) {

//				Log.d(TAG, "replace into order id = " + orderUnit.getTid());

				Object[] bindArgs = new Object[] { null, orderUnit.getTid(),
						uid, orderUnit.getUserName(), orderUnit.getPhone(),
						orderUnit.getLoc(), orderUnit.getBuildingNum(),
						orderUnit.getDormNum(), orderUnit.getSendTime(),
						orderUnit.getNote(), orderUnit.getFileName(),
						orderUnit.getFileMsg(), orderUnit.getMoney(),
						orderUnit.getDelivered(), orderUnit.getUploadTime(),
						mDate };
				db.execSQL(
						"REPLACE INTO "
								+ DB_TABLE_ORDERUNIT
								+ " VALUES(? , ? ,? ,? ,? ,? ,? , ? ,? ,? ,? ,? ,? , ? ,? ,?)",
						bindArgs);
			}
			db.setTransactionSuccessful();// 调用此方法会在执行到endTransaction()
											// 时提交当前事务，如果不调用此方法会回滚事务
		} finally {
			db.endTransaction();// 由事务的标志决定是提交事务，还是回滚事务
		}

	}

	public List<OrderUnit> queryOrderUnits(String uid, long date) {

		// Log.i(TAG, "queryOrderunits");
		List<OrderUnit> orderUnits = new ArrayList<OrderUnit>();

		String mDate = StringUtils.formatData(new Date(date));
		// System.out.println(" ----------> date = " + mDate);

		final SQLiteDatabase db = getReadableDatabase();

		Cursor cursor = db
				.rawQuery(
						"SELECT username,phone,pos_loc,pos_build,pos_dorm,message,filename,money,send_time,file_msg,isDelivered ,upload_time , tid FROM orderunit WHERE uid = ? AND date = ?",
						new String[] { uid, mDate });

		if (cursor.moveToFirst()) {
			do {
				OrderUnit orderUnit = new OrderUnit();
				orderUnit.setBuildingNum(cursor
						.getInt(APP_COLUMNS.BUILDING_INDEX));
				orderUnit.setDormNum(cursor.getInt(APP_COLUMNS.DORM_INDEX));
				orderUnit.setFileName(cursor
						.getString(APP_COLUMNS.FILENAME_INDEX));
				orderUnit.setTid(cursor.getString(cursor
						.getShort(APP_COLUMNS.TID_INDEX)));
				orderUnit.setIsDelivered(cursor
						.getInt(APP_COLUMNS.ISDELIVERED_INDEX));
				orderUnit.setLocT(cursor.getInt(APP_COLUMNS.LOC_INDEX) + "," + cursor.getInt(APP_COLUMNS.BUILDING_INDEX) + "," + cursor.getInt(APP_COLUMNS.DORM_INDEX));
				orderUnit.setMoney(cursor.getString(APP_COLUMNS.MONEY_INDEX));
				orderUnit.setNote(cursor.getString(APP_COLUMNS.SEND_MSG_INDEX));
				orderUnit.setFileMsg(cursor
						.getString(APP_COLUMNS.FILE_MSG_INDEX));
				orderUnit.setSendTime(cursor
						.getString(APP_COLUMNS.SEND_TIME_INDEX));
				orderUnit.setUploadTime(cursor
						.getString(APP_COLUMNS.UPLOAD_TIME_INDEX));
				orderUnit.setUser(cursor.getString(APP_COLUMNS.USERNAME_INDEX)
						+ "," + cursor.getString(APP_COLUMNS.PHONENUM_INDEX));
				orderUnit.setTid(cursor.getString(APP_COLUMNS.TID_INDEX));
				orderUnits.add(orderUnit);
			} while (cursor.moveToNext());
		}
		cursor.close();

		return orderUnits;
	}

	public List<BuildingBrief> queryBriefs(String uid, long date) {

		Log.i(TAG, "queryBriefs");
		Log.i(TAG, StringUtils.friendlyTime(date));
		String mDate = StringUtils.formatData(new Date(date));
//		System.out.println(" mDate = " + mDate);
		List<BuildingBrief> briefs = new ArrayList<BuildingBrief>();

		final SQLiteDatabase db = getReadableDatabase();

		Cursor cursor = db
				.rawQuery(
						"SELECT pos_loc,pos_build,money FROM orderunit WHERE uid = ? AND date = ?",
						new String[] { uid, mDate });
		Log.i(TAG, "Count = " + cursor.getCount());
		if (cursor.moveToFirst()) {
			do {
				int loc = cursor.getInt(0);
				int build = cursor.getInt(1);
				String money = cursor.getString(2);
				boolean isExist = false;
				int size = briefs.size();
				for (int i = 0; i < size; i++) {
					if (loc == briefs.get(i).getLocNum()
							&& build == briefs.get(i).getBuildingNum()) {
						briefs.get(i).addMoney(Double.valueOf(money));
						isExist = true;
						break;
					}
				}
				if (!isExist) {
					BuildingBrief brief = new BuildingBrief(loc, build,
							Double.valueOf(money));
					briefs.add(brief);
				}
			} while (cursor.moveToNext());
		}
		cursor.close();

		return briefs;
	}

	/**
	 * 更新订单信息，主要更新派送状态
	 */
	public synchronized void updateOrderUnit(String uid, List<String> tids,
			int isDelivered) {
		Log.d(TAG, "updateOrderUnit--");
		final SQLiteDatabase db = getWritableDatabase();
		db.beginTransaction();
		try {
			for (int i = 0; i < tids.size(); i++) {
				String tid = tids.get(i);
				Object[] bindArgs = new Object[] { isDelivered, uid, tid,
						isDelivered };
				db.execSQL(
						"UPDATE orderunit SET isDelivered = ? WHERE uid = ? AND tid = ? AND isDelivered != ?",
						bindArgs);
			}
			db.setTransactionSuccessful();
		} finally {
			db.endTransaction();
		}

	}

	public synchronized void updateOrderByBuilding(String uid, int pos_loc,
			int pos_build, int isDelivered) {
		final SQLiteDatabase db = getWritableDatabase();
		Object[] bindArgs = new Object[] { isDelivered, uid, pos_loc,
				pos_build, isDelivered };
		db.execSQL(
				"UPDATE orderunit SET isDelivered = ? WHERE uid = ? AND pos_loc = ? AND pos_build = ? AND isDelivered != ?",
				bindArgs);
	}

	public static final class APP_COLUMNS implements BaseColumns {
		public static final String _ID = "_id";
		public static final String tid = "tid";
		public static final String UPLOAD_TIME = "upload_time";
		public static final String USERNAME = "username";
		public static final String PHONE = "phone";
		public static final String POS_LOC = "pos_loc";
		public static final String POS_BUILD = "pos_build";
		public static final String POS_DORM = "pos_dorm";
		public static final String SEND_MSG = "message";
		public static final String FILENAME = "filename";
		// public static final String PAGE_NUMBER = "page_number";
		public static final String MONEY = "money";
		public static final String SEND_TIME = "send_time";
		public static final String FILE_MSG = "file_msg";
		// public static final String BINDER = "binder";
		// public static final String PAGE_TYPE = "page_type";
		// public static final String TYPE = "type";
		// public static final String FILE_OTHERS = "file_others";
		// public static final String PAGE_SIZE = "page_size";
		public static final String ISDELIVERED = "isDelivered";
		public static final String UID = "uid";
		public static final String DATE = "date";

		
		public static final int UID_INDEX = 1;
		public static final int USERNAME_INDEX = 0;
		public static final int PHONENUM_INDEX = 1;
		public static final int LOC_INDEX = 2;
		public static final int BUILDING_INDEX = 3;
		public static final int DORM_INDEX = 4;
		public static final int SEND_MSG_INDEX = 5;
		public static final int FILENAME_INDEX = 6;
		public static final int MONEY_INDEX = 7;
		public static final int SEND_TIME_INDEX = 8;
		public static final int FILE_MSG_INDEX = 9;
		public static final int ISDELIVERED_INDEX = 10;
		public static final int UPLOAD_TIME_INDEX = 11;
		public static final int TID_INDEX = 12;
		public static final int DATE_INDEX = 19;
		
	}
}
