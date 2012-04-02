package hkust.comp3111h.focus.database;

/**
 * TaskDbAdapter is the class to store all the informations. 
 * 
 * Currently (Mar 24) it has 3 tables: user, taskList, and task. 
 * Each table holds the ID as its single primary key.
 * In task table, it has a foreign key which references tasklistid. 
 * 
 * WARNING:
 * Foreign key enabled, so when you delete a taskList, ALL the tasks that belong to the taskList will also be deleted. 
 * 
 * 
 */

import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class TaskDbAdapter {

  // *******************DECLARATION OF CONSTANT
  // STRINGS*****************************

  // User Table Key info
  public static final String KEY_USER_USERID = "uId";
  public static final String KEY_USER_USERNAME = "username";
  public static final String KEY_USER_PASSWORD = "password";

  // TaskList Table Key info
  public static final String KEY_TASKLIST_TLID = "taskListId";
  public static final String KEY_TASKLIST_TLNAME = "taskListName";
  public static final String KEY_TASKLIST_TLSEQUENCE = "taskListSequence";

  // Task Table Key info
  public static final String KEY_TASK_TLID = "taskListId";
  public static final String KEY_TASK_TID = "taskId";
  public static final String KEY_TASK_TYPE = "taskType";
  public static final String KEY_TASK_NAME = "taskName";
  public static final String KEY_TASK_DUEDATE = "dueDate";
  public static final String KEY_TASK_STARTDATE = "startDate";
  public static final String KEY_TASK_ENDDATE = "endDate";
  public static final String KEY_TASK_TSEQUENCE = "taskSequence";

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

  // Tag for LogCat output.
  private static final String TAG = "DBCHECK";

  // Define the Database Version.
  private static final int DATABASE_VERSION = 1;

  // SQL commands for creating the tables.
  private static final String DATABASE_CREATE_USER = "CREATE TABLE "
      + TABLE_USER + " (" + KEY_USER_USERID + " INTEGER PRIMARY KEY, "
      + KEY_USER_USERNAME + " TEXT NOT NULL, " + KEY_USER_PASSWORD
      + " TEXT NOT NULL" + ");";

  private static final String DATABASE_CREATE_TASKLIST = "CREATE TABLE "
      + TABLE_TASKLIST + " (" + KEY_TASKLIST_TLID + " INTEGER PRIMARY KEY, "
      + KEY_TASKLIST_TLNAME + " TEXT NOT NULL, " + KEY_TASKLIST_TLSEQUENCE
      + " INTEGER" + ");";

  private static final String DATABASE_CREATE_TASK = "CREATE TABLE "
      + TABLE_TASK + " (" + KEY_TASK_TID + " INTEGER PRIMARY KEY, "
      + KEY_TASK_TLID + " INTEGER, " + KEY_TASK_TYPE + " TEXT NOT NULL, "
      + KEY_TASK_NAME + " TEXT NOT NULL, " + KEY_TASK_DUEDATE + " TEXT,"
      + KEY_TASK_STARTDATE + " TEXT, " + KEY_TASK_ENDDATE + " TEXT NOT NULL, "
      + KEY_TASK_TSEQUENCE + " INTEGER, " + "FOREIGN KEY ("
      + KEY_TASK_TLID + ") REFERENCES " + TABLE_TASKLIST + "("
      + KEY_TASKLIST_TLID + ") ON UPDATE CASCADE ON DELETE CASCADE " + ");";

  // SQL commands to destroy the tables.
  private static final String DATABASE_DESTROY_USER = "DROP TABLE IF EXISTS "
      + TABLE_USER;
  private static final String DATABASE_DESTROY_TASKLIST = "DROP TABLE IF EXISTS "
      + TABLE_TASKLIST;
  private static final String DATABASE_DESTROY_TASK = "DROP TABLE IF EXISTS "
      + TABLE_TASK;

  // **************************************END************************************************

  // ***************************DECLARATION OF
  // MEMBERS****************************************

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
      Log.w("TaskDbAdapter", "Upgrading database from version " + oldVersion
          + " to " + newVersion + ", destroying all data.");
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

  // ***************************END*****************************

  // ****************DEFINITION OF METHODS*********************
  /**
   * Constructor of TaskDbAdapter.
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
   * Fetch a user's info given the userId.
   * 
   * @param userId
   * @return the Cursor pointing to the record.
   * @throws SQLException
   */
  public Cursor fetchUser(long userId) throws SQLException {
    Cursor mCursor = mDb.query(true, TABLE_USER, null, KEY_USER_USERID + "="
        + userId, null, null, null, null, null);
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

  /**
   * Delete a user given the userId.
   * 
   * @param userId
   * @return successfully deleted or not.
   */
  public boolean deleteUser(long userId) {
    return mDb.delete(TABLE_USER, KEY_USER_USERID + "=" + userId, null) > 0;
  }

  /**
   * Update a user's information given the userId
   * 
   * @param userId
   * @param username
   * @param password
   * @return successfully updated or not.
   */
  public boolean updateUser(long userId, String username, String password) {
    ContentValues updatedInfo = new ContentValues();
    updatedInfo.put(KEY_USER_USERNAME, username);
    updatedInfo.put(KEY_USER_PASSWORD, password);

    return mDb.update(TABLE_USER, updatedInfo, KEY_USER_USERID + "=" + userId,
        null) > 0;
  }

  /**
   * Get the schema, i.e, names of all columns in the user table.
   * 
   * @return array of string containing all the attributes in the user table.
   */
  public String[] getUserSchema() {
    return new String[] { KEY_USER_USERID, KEY_USER_USERNAME, KEY_USER_PASSWORD };
  }

  // *******************END METHODS FOR USER********************

  // *******************METHODS FOR TASKLIST********************

  /**
   * Create a new tasklist given its name.
   * 
   * @param taskListName
   * @return the id of the newly created taskList, or -1 if error occurred.
   */
  public long createTaskList(String taskListName) {
    ContentValues initialValues = new ContentValues();
    initialValues.put(KEY_TASKLIST_TLNAME, taskListName);

    // Initialize sequence by the value of the newId. 
    // i.e, seq == id as initialization. 
    long newId = mDb.insert(TABLE_TASKLIST, null, initialValues);
    ContentValues seqInfo = new ContentValues();
    seqInfo.put(KEY_TASKLIST_TLSEQUENCE, newId);
    mDb.update(TABLE_TASKLIST, seqInfo, KEY_TASKLIST_TLID + "=" + newId, null);
    
    return newId;
  }

  /**
   * Fetch info of a tasklist given the ID.
   * 
   * @param taskListId
   * @return the Cursor pointing to the record.
   * @throws SQLException
   */
  public Cursor fetchTaskList(long taskListId) throws SQLException {
    Cursor mCursor = mDb.query(true, TABLE_TASKLIST, null, KEY_TASKLIST_TLID
        + "=" + taskListId, null, null, null, null, null);
    if (mCursor != null) {
      mCursor.moveToFirst();
    }
    return mCursor;
  }

  /**
   * Fetch all tasklists info ordered by ROWID. 
   * @return a Cursor pointing to all the records.
   */
  public Cursor fetchAllTaskLists() {
    return fetchAllTaskLists(false);
  }
  
  /**
   * Fetch all tasklists info.
   * @param orderBySequence whether order the query by sequence. On default order by ROWID. 
   * @return cursor pointing to the results. 
   */
  public Cursor fetchAllTaskLists(boolean orderBySequence) {
    if(orderBySequence){
      return mDb.query(TABLE_TASKLIST, null, null, null, null, null, KEY_TASKLIST_TLSEQUENCE);
    }
    else{
      return mDb.query(TABLE_TASKLIST, null, null, null, null, null, null);
    }
  }

  /**
   * Delete a tasklist given the ID.
   * 
   * @param taskListId
   * @return successfully deleted or not.
   */
  public boolean deleteTaskList(long taskListId) {
    return mDb.delete(TABLE_TASKLIST, KEY_TASKLIST_TLID + "=" + taskListId,
        null) > 0;
  }

  /**
   * Update info of a tasklist given the ID.
   * 
   * @param taskListId
   * @param taskListName
   * @return successfully deleted or not.
   */
  public boolean updateTaskList(long taskListId, String taskListName) {
    ContentValues updatedInfo = new ContentValues();
    updatedInfo.put(KEY_TASKLIST_TLNAME, taskListName);

    return mDb.update(TABLE_TASKLIST, updatedInfo, KEY_TASKLIST_TLID + "="
        + taskListId, null) > 0;
  }

  /**
   * Update taskList sequence according to the given ids. This function will
   * update the sequence between two ids, both included. Notice: the id will not
   * change, just the sequcne attribute. Please sort the result if you want
   * updated views.
   * 
   * @param dragId
   *          The id of the item you drag.
   * @param dropId
   *          The id of the item where you drop in.
   * @return whether it successfully updates.
   */
  public boolean updateTaskListSequence(long dragId, long dropId) {
    if (dragId == dropId) {
      return true; // Same item. No need to update.
    }

    // Get sequence of the specified id.
    long dragOrigSeq = getTaskListSequenceById(dragId);
    long dropOrigSeq = getTaskListSequenceById(dropId);

    // find the records that will be influence by this update.
    // i.e, those items with sequence inside dragOrigSeq and dropOrigSeq, both
    // included.
    Cursor interval = getTaskListByInterval(dragOrigSeq, dropOrigSeq);

    ArrayList<Long> seqList = new ArrayList<Long>();
    ArrayList<Long> idList = new ArrayList<Long>();

    // Get all values and map them into origId and origSeq arraylist.
    for (interval.moveToFirst(); !interval.isAfterLast(); interval.moveToNext()) {
      seqList.add(interval.getLong(interval
          .getColumnIndexOrThrow(KEY_TASKLIST_TLSEQUENCE)));
      idList.add(interval.getLong(interval
          .getColumnIndexOrThrow(KEY_TASKLIST_TLID)));
    }

    if (seqList.size() != idList.size()) {
      return false;
    }

    // For debug use.
    for (int i = 0; i < seqList.size(); ++i) {
      Log.d("seq list", String.valueOf(seqList.get(i)));
    }
    for (int i = 0; i < idList.size(); ++i) {
      Log.d("id list", String.valueOf(idList.get(i)));
    }

    // Now, seqList's sequence are 1-to-1 corresponding to the idList.
    // Handle two situations.
    if (dragId < dropId) { // 1, Drag from up to down. e.g, drag 2nd to 5th.
      idList.remove(0);
      idList.add(dragId);
    } else { // 2, Drag from down to up. e.g, drag 5th to 2nd.
      idList.remove(idList.size() - 1); // remove the last.
      idList.add(0, dragId);
    }

    // Now idList and the sequence should be 1-to-1 corresponding.
    boolean status = true;
    for (int i = 0; i < idList.size(); ++i) {
      status = status
          && updateTaskListSequenceById(idList.get(i), seqList.get(i));
    }

    return status;
  }

  /**
   * Private function to help updateTaskListSequence Given an interval specified by sequence ID, return
   * the taskLists that are inside this interval. boundaryA <= RESULT <=
   * boundaryB or boundaryB <= RESULT <= boundaryA The function will take care
   * of the boundaries. Don't need to specify which is bigger. NOTE: the return
   * value will by order by sequence. NOT original id.
   * 
   * @param boundaryA
   * @param boundaryB
   * @return the rows between the two sequence id.
   */
  private Cursor getTaskListByInterval(long boundaryA, long boundaryB) {
    long startSeq = min(boundaryA, boundaryB);
    long endSeq = max(boundaryB, boundaryA);

    return mDb.query(TABLE_TASKLIST, new String[] { KEY_TASKLIST_TLID,
        KEY_TASKLIST_TLSEQUENCE }, KEY_TASKLIST_TLSEQUENCE + ">=" + startSeq
        + " AND " + KEY_TASKLIST_TLSEQUENCE + "<=" + endSeq, null, null, null,
        KEY_TASKLIST_TLSEQUENCE);
  }

  /**
   * private function to help updateTaskListSequence. update the sequence of the
   * item given its id.
   * 
   * @param taskListId
   * @param seq
   * @return whether it successfully updates or not.
   */
  private boolean updateTaskListSequenceById(long taskListId, long seq) {
    ContentValues info = new ContentValues();
    info.put(KEY_TASKLIST_TLSEQUENCE, seq);

    return mDb.update(TABLE_TASKLIST, info, KEY_TASKLIST_TLID + "="
        + taskListId, null) > 0;
  }

  /**
   * private function to help updateTaskListSequence. get the sequence of the
   * item given its id.
   * 
   * @param id
   * @return
   */
  private long getTaskListSequenceById(long taskListId) {
    Cursor mCursor = mDb.query(true, TABLE_TASKLIST,
        new String[] { KEY_TASKLIST_TLSEQUENCE }, KEY_TASKLIST_TLID + "="
            + taskListId, null, null, null, null, null);
    mCursor.moveToFirst();
    return mCursor.getLong(mCursor
        .getColumnIndexOrThrow(KEY_TASKLIST_TLSEQUENCE));
  }

  /**
   * Get the schema of the taskList table.
   * 
   * @return an array of string containing all the attributes in taskList table.
   */
  public String[] getTaskListSchema() {
    return new String[] { KEY_TASKLIST_TLID, KEY_TASKLIST_TLNAME };
  }

  // ****************END METHODS OF TASKLIST**************************

  // ****************METHODS OF TASK**************************
  /**
   * Create a tasklist given the info.
   * 
   * @param taskListId
   * @param taskType
   * @param taskName
   * @param dueDate
   * @param startDate
   * @param endDate
   * @return the ID of the newly inserted item, or -1 if an error occurred.
   */
  public long createTask(long taskListId, String taskType, String taskName,
      String dueDate, String startDate, String endDate) {
    ContentValues initialValues = new ContentValues();
    initialValues.put(KEY_TASK_TLID, taskListId);
    initialValues.put(KEY_TASK_TYPE, taskType);
    initialValues.put(KEY_TASK_NAME, taskName);
    initialValues.put(KEY_TASK_DUEDATE, dueDate);
    initialValues.put(KEY_TASK_STARTDATE, startDate);
    initialValues.put(KEY_TASK_ENDDATE, endDate);

    // Initialize sequence by the value of the newId. 
    // i.e, seq == id as initialization. 
    long newId = mDb.insert(TABLE_TASK, null, initialValues);
    ContentValues seqInfo = new ContentValues();
    seqInfo.put(KEY_TASKLIST_TLSEQUENCE, newId);
    mDb.update(TABLE_TASKLIST, seqInfo, KEY_TASKLIST_TLID + "=" + newId, null);
    
    return newId;
  }

  /**
   * Fetch a task given the ID
   * 
   * @param taskId
   * @return the Cursor pointing to the record.
   * @throws SQLException
   */
  public Cursor fetchTask(long taskId) throws SQLException {
    return mDb.query(true, TABLE_TASK, null, KEY_TASK_TID + "=" + taskId, null,
        null, null, null, null);
  }

  /**
   * Fetch info of all the tasks ordered by ROWID. 
   * @return the Cursor pointing to all the records in task table.
   */
  public Cursor fetchAllTasks() {
    return fetchAllTasks(false);
  }
  
  /**
   * Fetch all tasks info.
   * @param orderBySequence whether order the query by sequence. On default order by ROWID
   * @return Cursor pointing to all the records. 
   */
  public Cursor fetchAllTasks(boolean orderBySequence){
    if(orderBySequence){
      return mDb.query(TABLE_TASK, null, null, null, null, null, KEY_TASK_TSEQUENCE);
    }
    else{
      return mDb.query(TABLE_TASK, null, null, null, null, null, null);
    }
  }

  /**
   * Fetch all tasks in a specific taskList given the taskListId.
   * 
   * @param taskListId
   * @return the Cursor pointing to all the records in the specified taskList.
   */
  public Cursor fetchAllTasksInList(long taskListId) {
    return mDb.query(TABLE_TASK, null, KEY_TASK_TLID + "=" + taskListId, null,
        null, null, null);
  }

  /**
   * Delete a task given the id.
   * 
   * @param taskId
   * @return successfully deleted or not.
   */
  public boolean deleteTask(long taskId) {
    return mDb.delete(TABLE_TASK, KEY_TASK_TID + "=" + taskId, null) > 0;
  }

  /**
   * Update a task given all the info and the taskId.
   * 
   * @param taskId
   * @param taskType
   * @param taskName
   * @param dueDate
   * @param startDate
   * @param endDate
   * @return successfully updated or not.
   */
  public boolean updateTask(long taskId, String taskType, String taskName,
      String dueDate, String startDate, String endDate) {
    ContentValues updatedInfo = new ContentValues();
    updatedInfo.put(KEY_TASK_TYPE, taskType);
    updatedInfo.put(KEY_TASK_NAME, taskName);
    updatedInfo.put(KEY_TASK_DUEDATE, dueDate);
    updatedInfo.put(KEY_TASK_STARTDATE, startDate);
    updatedInfo.put(KEY_TASK_ENDDATE, endDate);

    return mDb.update(TABLE_TASK, updatedInfo, KEY_TASK_TID + "=" + taskId,
        null) > 0;
  }
  
  /**
   * Update task sequence according to the given ids. This function will
   * update the sequence between two ids, both included. Notice: the id will not
   * change, just the sequcne attribute. Please sort the result if you want
   * updated views, or call fetchAllTasks(true). 
   * 
   * @param dragId
   *          The id of the item you drag.
   * @param dropId
   *          The id of the item where you drop in.
   * @return whether it successfully updates.
   */
  public boolean updateTaskSequence(long dragId, long dropId) {
    if (dragId == dropId) {
      return true; // Same item. No need to update.
    }

    // Get sequence of the specified id.
    long dragOrigSeq = getTaskSequenceById(dragId);
    long dropOrigSeq = getTaskSequenceById(dropId);

    // find the records that will be influence by this update.
    // i.e, those items with sequence inside dragOrigSeq and dropOrigSeq, both
    // included.
    Cursor interval = getTaskByInterval(dragOrigSeq, dropOrigSeq);

    ArrayList<Long> seqList = new ArrayList<Long>();
    ArrayList<Long> idList = new ArrayList<Long>();

    // Get all values and map them into origId and origSeq arraylist.
    for (interval.moveToFirst(); !interval.isAfterLast(); interval.moveToNext()) {
      seqList.add(interval.getLong(interval
          .getColumnIndexOrThrow(KEY_TASK_TSEQUENCE)));
      idList.add(interval.getLong(interval
          .getColumnIndexOrThrow(KEY_TASK_TID)));
    }

    if (seqList.size() != idList.size()) {
      return false;
    }

    // For debug use.
    for (int i = 0; i < seqList.size(); ++i) {
      Log.d("seq list", String.valueOf(seqList.get(i)));
    }
    for (int i = 0; i < idList.size(); ++i) {
      Log.d("id list", String.valueOf(idList.get(i)));
    }

    // Now, seqList's sequence are 1-to-1 corresponding to the idList.
    // Handle two situations.
    if (dragId < dropId) { // 1, Drag from up to down. e.g, drag 2nd to 5th.
      idList.remove(0);
      idList.add(dragId);
    } else { // 2, Drag from down to up. e.g, drag 5th to 2nd.
      idList.remove(idList.size() - 1); // remove the last.
      idList.add(0, dragId);
    }

    // Now idList and the sequence should be 1-to-1 corresponding.
    boolean status = true;
    for (int i = 0; i < idList.size(); ++i) {
      status = status
          && updateTaskSequenceById(idList.get(i), seqList.get(i));
    }

    return status;
  }

  /**
   * Private function to help updateTaskSequence Given an interval specified by sequence ID, return
   * the tasks that are inside this interval. boundaryA <= RESULT <=
   * boundaryB or boundaryB <= RESULT <= boundaryA The function will take care
   * of the boundaries. Don't need to specify which is bigger. NOTE: the return
   * value will by order by sequence, NOT original id.
   * 
   * @param boundaryA
   * @param boundaryB
   * @return the rows between the two sequence id.
   */
  private Cursor getTaskByInterval(long boundaryA, long boundaryB) {
    long startSeq = min(boundaryA, boundaryB);
    long endSeq = max(boundaryB, boundaryA);

    return mDb.query(TABLE_TASK, new String[] { KEY_TASK_TID,
        KEY_TASK_TSEQUENCE }, KEY_TASK_TSEQUENCE + ">=" + startSeq
        + " AND " + KEY_TASK_TSEQUENCE + "<=" + endSeq, null, null, null,
        KEY_TASK_TSEQUENCE);
  }

  /**
   * private function to help updateTaskSequence. update the sequence of the
   * item given its id.
   * 
   * @param taskId
   * @param seq
   * @return whether it successfully updates or not.
   */
  private boolean updateTaskSequenceById(long taskId, long seq) {
    ContentValues info = new ContentValues();
    info.put(KEY_TASK_TSEQUENCE, seq);

    return mDb.update(TABLE_TASK, info, KEY_TASK_TID + "="
        + taskId, null) > 0;
  }

  /**
   * private function to help updateTaskSequence. get the sequence of the
   * item given its id.
   * 
   * @param id
   * @return
   */
  private long getTaskSequenceById(long taskId) {
    Cursor mCursor = mDb.query(true, TABLE_TASK,
        new String[] { KEY_TASK_TSEQUENCE }, KEY_TASK_TID + "="
            + taskId, null, null, null, null, null);
    mCursor.moveToFirst();
    return mCursor.getLong(mCursor
        .getColumnIndexOrThrow(KEY_TASK_TSEQUENCE));
  }


  /**
   * Get the schema of task table.
   * 
   * @return an array of string containing all the attributes in task table.
   */
  public String[] getTaskSchema() {
    return new String[] { KEY_TASK_TLID, KEY_TASK_TID, KEY_TASK_TYPE,
        KEY_TASK_NAME, KEY_TASK_DUEDATE, KEY_TASK_STARTDATE, KEY_TASK_ENDDATE };
  }
  

  // all helper functions. 

  private long max(long a, long b) {
    if (a > b) {
      return a;
    } else {
      return b;
    }
  }

  private long min(long a, long b) {
    if (a < b) {
      return a;
    } else {
      return b;
    }
  }

  
}
