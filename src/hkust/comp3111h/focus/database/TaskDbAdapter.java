package hkust.comp3111h.focus.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class TaskDbAdapter {
	//User Table info
	public static final String KEY_USERID = "uid";
	public static final String KEY_USERNAME = "username";
	public static final String KEY_PASSWORD = "password";
	
	// Waiting for implementation after successful test with USER.
	/*
	//TODO: Task info
	public static final String KEY_TASK_ID = "taskId";
	public static final String KEY_TASK_TYPE = "taskType";
	public static final String KEY_TASK_NAME = "taskName";
	public static final String KEY_TASK_DURATION = "taskDuration";
	*/

	// Definition of database and table infos.
	private static final String DATABASE_NAME = "data";
	private static final String DATABASE_TABLE_ACCOUNT = "accountTable";
	/*
	 * TODO
	private static final String DATABASE_TABLE_TASK = "taskTable";
	private static final String DATABASE_TABLE_TIMER= "timerTable";
	*/
	private static final int DATABASE_VERSION = 1;

	// SQL command for creating the tables.
	private static final String DATABASE_CREATE_ACCOUNT = 
			"CREATE TABLE " + DATABASE_TABLE_ACCOUNT + 
			" (" + 
			       KEY_USERID + " TEXT NOT NULL PRIMARY KEY, " + 
			       KEY_USERNAME + " TEXT NOT NULL, " + 
			       KEY_PASSWORD + " TEXT NOT NULL" + 
	        ");";
	
	//TODO
	/*
	private static final String DATABASE_CREATE_TASK = 
			"CREATE TABLE " + DATABASE_TABLE_TASK +
			" (" + KEY_ROWID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
				   KEY_TASK
			;
	private static final String DATABASE_CREATE_TIMER = 
			;
	*/
	
	private static final String DATABASE_DESTROY_ACCOUNT = 
			"DROP TABLE IF EXISTS " + DATABASE_TABLE_ACCOUNT;

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
			db.execSQL(DATABASE_CREATE_ACCOUNT);
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			Log.w("TaskDbAdapter", "Upgrading database from version " + 
				oldVersion + " to " + newVersion + ", destroying all data.");
			db.execSQL(DATABASE_DESTROY_ACCOUNT);
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

	/**
	 * Create a user and insert the record into table accountTable. 
	 * @param userId
	 * @param username
	 * @param password
	 * @return true if successfully inserted, false if not. 
	 */
	public boolean createUser(String userId, String username, String password) {
		ContentValues initialValues = new ContentValues();
		initialValues.put(KEY_USERID, userId);
		initialValues.put(KEY_USERNAME, username);
		initialValues.put(KEY_PASSWORD, password);
		
		return mDb.insert(DATABASE_TABLE_ACCOUNT, null, initialValues) > 0;
	}
	
	/**
	 * Fetch the record of a specific user by providing the UserID.
	 * @param userId
	 * @return the Cursor pointing to the record. 
	 * @throws SQLException
	 */
	public Cursor fetchUser(String userId) throws SQLException{
		Cursor mCursor = 
				mDb.query(true, DATABASE_TABLE_ACCOUNT, null, KEY_USERID + "= '" + userId + "'", null, null, null, null, null);
		if (mCursor != null){
			mCursor.moveToFirst();
		}
		return mCursor;
	}
	
	/**
	 * Fetch all information stored in the account table. 
	 * @return the Cursor pointing at all the records.
	 */
	public Cursor fetchAllUsers(){
		return mDb.query(DATABASE_TABLE_ACCOUNT, new String[]{KEY_USERID, KEY_USERNAME, KEY_PASSWORD}, null, null, null, null, null);
	}

	/*
	 * Isolated functions. Implement later. 
	// Delete user and return status.
	public boolean deleteUser(long rowId) {
		return mDb.delete(DATABASE_TABLE_ACCOUNT, KEY_ROWID + "=" + rowId, null) > 0;
	}

	// Update task and return status.
	public boolean updateUser(long rowId, String userId, String username, String password) {
		ContentValues updateValues = new ContentValues();
		updateValues.put(KEY_USERID, userId);
		updateValues.put(KEY_USERNAME, username);
		updateValues.put(KEY_PASSWORD, password);
		
		return mDb.update(DATABASE_TABLE_ACCOUNT, updateValues, KEY_ROWID + "=" + rowId, null) > 0;
	}
	*/

}
