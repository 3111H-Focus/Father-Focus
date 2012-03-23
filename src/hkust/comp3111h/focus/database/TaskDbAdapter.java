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
	public static final String KEY_USER_USERID = "uId";
	public static final String KEY_USER_USERNAME = "username";
	public static final String KEY_USER_PASSWORD = "password";

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
	private static final String TABLE_USER = "user";
	private static final String TABLE_TASKLIST = "taskList";
	private static final String TABLE_TASK = "task";
	
	private static final String TAG = "DBCHECK";

	private static final int DATABASE_VERSION = 1;

	// SQL command for creating the tables.
	private static final String DATABASE_CREATE_USER = "CREATE TABLE "
			+ TABLE_USER + " (" + KEY_USER_USERID
			+ " INTEGER PRIMARY KEY, " + KEY_USER_USERNAME
			+ " TEXT NOT NULL, " + KEY_USER_PASSWORD + " TEXT NOT NULL" + ");";

	private static final String DATABASE_CREATE_TASKLIST = "CREATE TABLE "
			+ TABLE_TASKLIST + " (" + KEY_TASKLIST_TLID
			+ " INTEGER PRIMARY KEY, " + KEY_TASKLIST_TLNAME
			+ " TEXT NOT NULL" + ");";

	private static final String DATABASE_CREATE_TASK = "CREATE TABLE "
			+ TABLE_TASK + " (" + KEY_TASK_TID
			+ " INTEGER PRIMARY KEY, " + KEY_TASK_TLID + " INTEGER, " +  KEY_TASK_TYPE + " TEXT NOT NULL, "
			+ KEY_TASK_NAME + " TEXT NOT NULL, " + KEY_TASK_DUEDATE + " TEXT,"
			+ KEY_TASK_STARTDATE + " TEXT, " + KEY_TASK_ENDDATE
			+ " TEXT NOT NULL, " + "FOREIGN KEY (" + KEY_TASK_TLID
			+ ") REFERENCES " + TABLE_TASKLIST + "(" + KEY_TASKLIST_TLID
			+ ") ON UPDATE CASCADE ON DELETE CASCADE " + ");";

	private static final String DATABASE_DESTROY_USER = "DROP TABLE IF EXISTS "
			+ TABLE_USER;
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
			Log.i(TAG, DATABASE_CREATE_USER);
			db.execSQL(DATABASE_CREATE_USER);
			Log.i(TAG, DATABASE_CREATE_TASKLIST);
			db.execSQL(DATABASE_CREATE_TASKLIST);
			Log.i(TAG, DATABASE_CREATE_TASK);
			db.execSQL(DATABASE_CREATE_TASK);
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			Log.w("TaskDbAdapter", "Upgrading database from version "
					+ oldVersion + " to " + newVersion
					+ ", destroying all data.");
			db.execSQL(DATABASE_DESTROY_USER);
			db.execSQL(DATABASE_DESTROY_TASKLIST);
			db.execSQL(DATABASE_DESTROY_TASK);
			onCreate(db);
		}
		
		@Override
		public void onOpen(SQLiteDatabase db) {
		    super.onOpen(db);
		    if (!db.isReadOnly()) {
		        // Enable foreign key constraints 
		    	// so as to perform on cascade delete and update. 
		        db.execSQL("PRAGMA foreign_keys=ON;");
		    }
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

	// *******************METHODS FOR USER********************

	/**
	 * Create a user and insert the record into table userTable.
	 * 
	 * @param userId
	 * @param username
	 * @param password
	 * @return true if successfully inserted, false if not.
	 */
	public long createUser(String username, String password) {
		ContentValues initialValues = new ContentValues();
		initialValues.put(KEY_USER_USERNAME, username);
		initialValues.put(KEY_USER_PASSWORD, password);

		return mDb.insert(TABLE_USER, null, initialValues);
	}

	/**
	 * Fetch the record of a specific user by providing the UserID.
	 * 
	 * @param userId
	 * @return the Cursor pointing to the record.
	 * @throws SQLException
	 */
	public Cursor fetchUser(long userId) throws SQLException {
		Cursor mCursor = mDb.query(true, TABLE_USER, null, KEY_USER_USERID
				+ "=" + userId, null, null, null, null, null);
		if (mCursor != null) {
			mCursor.moveToFirst();
		}
		return mCursor;
	}

	/**
	 * Fetch all information stored in the user table.
	 * 
	 * @return the Cursor pointing at all the records.
	 */
	public Cursor fetchAllUsers() {
		return mDb.query(TABLE_USER, null, null, null, null, null, null);
	}

	public boolean deleteUser(long userId) {
		return mDb.delete(TABLE_USER, KEY_USER_USERID + "=" + userId, null) > 0;
	}

	public boolean updateUser(long userId, String username, String password) {
		ContentValues updatedInfo = new ContentValues();
		updatedInfo.put(KEY_USER_USERNAME, username);
		updatedInfo.put(KEY_USER_PASSWORD, password);

		return mDb.update(TABLE_USER, updatedInfo, KEY_USER_USERID + "="
				+ userId, null) > 0;
	}

	public String[] getUserSchema() {
		return new String[] { KEY_USER_USERID, KEY_USER_USERNAME,
				KEY_USER_PASSWORD };
	}

	// *******************END METHODS FOR USER********************

	// *******************METHODS FOR TASKLIST********************
	public long createTaskList(String taskListName) {
		ContentValues initialValues = new ContentValues();
		initialValues.put(KEY_TASKLIST_TLNAME, taskListName);

		return mDb.insert(TABLE_TASKLIST, null, initialValues);
	}

	public Cursor fetchTaskList(long taskListId) throws SQLException {
		Cursor mCursor = mDb.query(true, TABLE_TASKLIST, null, KEY_TASKLIST_TLID
				+ "=" + taskListId, null, null, null, null, null);
		if (mCursor != null) {
			mCursor.moveToFirst();
		}
		return mCursor;
	}

	public Cursor fetchAllTaskLists() {
		return mDb.query(TABLE_TASKLIST, null, null, null, null, null, null);
	}

	public boolean deleteTaskList(long taskListId) {
		return mDb.delete(TABLE_TASKLIST, KEY_TASKLIST_TLID + "=" + taskListId,
				null) > 0;
	}

	public boolean updateTaskList(long taskListId, String taskListName) {
		ContentValues updatedInfo = new ContentValues();
		updatedInfo.put(KEY_TASKLIST_TLNAME, taskListName);

		return mDb.update(TABLE_TASKLIST, updatedInfo, KEY_TASKLIST_TLID + "="
				+ taskListId, null) > 0;
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

	public Cursor fetchTask(long taskListId, long taskId) throws SQLException {
		return mDb.query(true, TABLE_TASK, null, KEY_TASK_TLID + "="
				+ taskListId + " AND " + KEY_TASK_TID + "=" + taskId, null,
				null, null, null, null);
	}

	public Cursor fetchAllTasks() {
		return mDb.query(TABLE_TASK, null, null, null, null, null, null);
	}

	public Cursor fetchAllTasksInList(long taskListId) {
		return mDb.query(TABLE_TASK, null, KEY_TASK_TLID + "=" + taskListId,
				null, null, null, null);
	}

	public boolean deleteTask(long taskListId, long taskId) {
		return mDb.delete(TABLE_TASK, KEY_TASK_TLID + "=" + taskListId
				+ " AND " + KEY_TASK_TID + "=" + taskId, null) > 0;
	}

	public boolean updateTask(long taskListId, long taskId, String taskType,
			String taskName, String dueDate, String startDate, String endDate) {
		ContentValues updatedInfo = new ContentValues();
		updatedInfo.put(KEY_TASK_TYPE, taskType);
		updatedInfo.put(KEY_TASK_NAME, taskName);
		updatedInfo.put(KEY_TASK_DUEDATE, dueDate);
		updatedInfo.put(KEY_TASK_STARTDATE, startDate);
		updatedInfo.put(KEY_TASK_ENDDATE, endDate);

		return mDb.update(TABLE_TASK, updatedInfo, KEY_TASK_TLID + "="
				+ taskListId + " AND " + KEY_TASK_TID + "=" + taskId, null) > 0;
	}

	public String[] getTaskSchema() {
		return new String[] { KEY_TASK_TLID, KEY_TASK_TID, KEY_TASK_TYPE,
				KEY_TASK_NAME, KEY_TASK_DUEDATE, KEY_TASK_STARTDATE,
				KEY_TASK_ENDDATE };
	}
}
