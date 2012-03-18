package hkust.comp3111h.focus.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class TaskDbAdapter {
	// User Table info
	public static final String KEY_ACCOUNT_USERID = "uId";
	public static final String KEY_ACCOUNT_USERNAME = "username";
	public static final String KEY_ACCOUNT_PASSWORD = "password";

	// TaskList Table info
	public static final String KEY_TASKLIST_TLID = "taskListId";
	public static final String KEY_TASKLIST_TLNAME = "taskListName";

	// Task Table info
	public static final String KEY_TASK_TLID = "taskListId";
	public static final String KEY_TASK_TID = "taskId";
	public static final String KEY_TASK_TYPE = "taskType";
	public static final String KEY_TASK_NAME = "taskName";
	public static final String KEY_TASK_DUEDATE = "dueDate";
	public static final String KEY_TASK_STARTDATE = "startDate";
	public static final String KEY_TASK_ENDDATE = "endDate";

	// TODO:
	// Time should be added into record table.
	/*
	 * public static final String KEY_TASK_STARTTIME = "taskStartTime"; public
	 * static final String KEY_TASK_ENDTIME = "taskEndTime";
	 */

	// Definition of database and table info.
	private static final String DATABASE_NAME = "data";
	private static final String TABLE_ACCOUNT = "accountTable";
	private static final String TABLE_TASKLIST = "taskListTable";
	private static final String TABLE_TASK = "taskTable";
	/*
	 * private static final String DATABASE_TABLE_RECORD = "taskTable";
	 */

	/*
	 * TODO private static final String DATABASE_TABLE_TIMER= "timerTable";
	 */

	private static final int DATABASE_VERSION = 1;

	// SQL command for creating the tables.
	private static final String DATABASE_CREATE_ACCOUNT = "CREATE TABLE "
			+ TABLE_ACCOUNT + " (" + KEY_ACCOUNT_USERID
			+ " INT PRIMARY KEY AUTOINCREMENT, " + KEY_ACCOUNT_USERNAME
			+ " TEXT NOT NULL, " + KEY_ACCOUNT_PASSWORD + " TEXT NOT NULL"
			+ ");";

	private static final String DATABASE_CREATE_TASKLIST = "CREATE TABLE "
			+ TABLE_TASKLIST + " (" + KEY_TASKLIST_TLID
			+ " INTEGER PRIMARY KEY AUTOINCREMENT, " + KEY_TASKLIST_TLNAME
			+ "TEXT NOT NULL" + ");";

	private static final String DATABASE_CREATE_TASK = "CREATE TABLE "
			+ TABLE_TASK + " (" + KEY_TASK_TLID + "INTEGER, " + KEY_TASK_TID
			+ " INTEGER AUTOINCREMENT, " + KEY_TASK_TYPE + " TEXT NOT NULL, "
			+ KEY_TASK_NAME + " TEXT NOT NULL, " + KEY_TASK_DUEDATE + " TEXT,"
			+ KEY_TASK_STARTDATE + " TEXT, " + KEY_TASK_ENDDATE
			+ " TEXT NOT NULL, " + "PRIMARY KEY (" + KEY_TASK_TLID + ", "
			+ KEY_TASK_TID + ")" + ");";

	private static final String DATABASE_DESTROY_ACCOUNT = "DROP TABLE IF EXISTS "
			+ TABLE_ACCOUNT;
	private static final String DATABASE_DESTROY_TASKLIST = "DROP TABLE IF EXISTS "
			+ TABLE_TASKLIST;
	private static final String DATABASE_DESTROY_TASK = "DROP TABLE IF EXISTS "
			+ TABLE_TASK;

	// Declaration of members.
	private DatabaseHelper mDbHelper;
	private SQLiteDatabase mDb;

	private final Context mCtx;

	// Definition of private databasehelper class.
	private static class DatabaseHelper extends SQLiteOpenHelper {

		// Constructor.
		DatabaseHelper(Context ctx) {
			super(ctx, DATABASE_NAME, null, DATABASE_VERSION);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			Log.i("create account table", DATABASE_CREATE_ACCOUNT);
			db.execSQL(DATABASE_CREATE_ACCOUNT);
			Log.i("create tasklist table", DATABASE_CREATE_TASKLIST);
			db.execSQL(DATABASE_CREATE_TASKLIST);
			Log.i("create task table", DATABASE_CREATE_TASK);
			db.execSQL(DATABASE_CREATE_TASK);
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			Log.w("TaskDbAdapter", "Upgrading database from version "
					+ oldVersion + " to " + newVersion
					+ ", destroying all data.");
			db.execSQL(DATABASE_DESTROY_ACCOUNT);
			db.execSQL(DATABASE_DESTROY_TASKLIST);
			db.execSQL(DATABASE_DESTROY_TASK);
			onCreate(db);
		}
	}

	// ****************End of setting up members.*********************

	// ****************Start of declaring methods*********************
	/**
	 * 
	 * @param ctx
	 */
	public TaskDbAdapter(Context ctx) {
		this.mCtx = ctx;
	}

	/**
	 * Open the database and get a usable writer.
	 * 
	 * @return a writable TaskDbAdapter.
	 * @throws SQLException
	 */
	public TaskDbAdapter open() throws SQLException {
		mDbHelper = new DatabaseHelper(mCtx);
		mDb = mDbHelper.getWritableDatabase();
		return this;
	}

	/**
	 * Close the TaskDbAdapter.
	 */
	public void close() {
		mDbHelper.close();
	}

	// *******************METHODS FOR ACCOUNT********************

	/**
	 * Create a user and insert the record into table accountTable.
	 * 
	 * @param userId
	 * @param username
	 * @param password
	 * @return true if successfully inserted, false if not.
	 */
	public long createUser(String username, String password) {
		ContentValues initialValues = new ContentValues();
		initialValues.put(KEY_ACCOUNT_USERNAME, username);
		initialValues.put(KEY_ACCOUNT_PASSWORD, password);

		return mDb.insert(TABLE_ACCOUNT, null, initialValues);
	}

	/**
	 * Fetch the record of a specific user by providing the UserID.
	 * 
	 * @param userId
	 * @return the Cursor pointing to the record.
	 * @throws SQLException
	 */
	public Cursor fetchUser(long userId) throws SQLException {
		Cursor mCursor = mDb
				.query(true, TABLE_ACCOUNT, null, KEY_ACCOUNT_USERID + "="
						+ userId, null, null, null, null, null);
		if (mCursor != null) {
			mCursor.moveToFirst();
		}
		return mCursor;
	}

	/**
	 * Fetch all information stored in the account table.
	 * 
	 * @return the Cursor pointing at all the records.
	 */
	public Cursor fetchAllUsers() {
		return mDb.query(TABLE_ACCOUNT, new String[] { KEY_ACCOUNT_USERID,
				KEY_ACCOUNT_USERNAME, KEY_ACCOUNT_PASSWORD }, null, null, null,
				null, null);
	}

	public String[] getAccountSchema() {
		return new String[] { KEY_ACCOUNT_USERID, KEY_ACCOUNT_USERNAME,
				KEY_ACCOUNT_PASSWORD };
	}

	// *******************END METHODS FOR ACCOUNT********************

	// *******************METHODS FOR TASKLIST********************
	public long createTaskList(String taskListName) {
		ContentValues initialValues = new ContentValues();
		initialValues.put(KEY_TASKLIST_TLNAME, taskListName);

		return mDb.insert(TABLE_ACCOUNT, null, initialValues);
	}

	public Cursor fetchTaskList(long taskListId) throws SQLException {
		Cursor mCursor = mDb.query(true, TABLE_ACCOUNT, null, KEY_TASKLIST_TLID
				+ "=" + taskListId, null, null, null, null, null);
		if (mCursor != null) {
			mCursor.moveToFirst();
		}
		return mCursor;
	}

	public String[] getTaskListSchema() {
		return new String[] { KEY_TASKLIST_TLID, KEY_TASKLIST_TLNAME };
	}

	// ****************END METHODS OF TASKLIST**************************

	// ****************METHODS OF TASK**************************
	public long createTask(long taskListId, String taskType, String taskName,
			String dueDate, String startDate, String endDate) {
		ContentValues initialValues = new ContentValues();
		initialValues.put(KEY_TASK_TLID, taskListId);
		initialValues.put(KEY_TASK_TYPE, taskType);
		initialValues.put(KEY_TASK_NAME, taskName);
		initialValues.put(KEY_TASK_DUEDATE, dueDate);
		initialValues.put(KEY_TASK_STARTDATE, startDate);
		initialValues.put(KEY_TASK_ENDDATE, endDate);

		return mDb.insert(TABLE_TASK, null, initialValues);
	}

	public String[] getTaskSchema() {
		return new String[] { KEY_TASK_TLID, KEY_TASK_TID, KEY_TASK_TYPE,
				KEY_TASK_NAME, KEY_TASK_DUEDATE, KEY_TASK_STARTDATE,
				KEY_TASK_ENDDATE };
	}
}
