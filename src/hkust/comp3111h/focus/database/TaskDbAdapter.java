package hkust.comp3111h.focus.database;

/**
 * TaskDbAdapter is the class to store all the informations. 
 * 
 * Foreign key enabled, so when you delete a taskList, ALL the tasks that belong to the taskList will also be deleted. 
 * 
 * 
 */

import java.util.ArrayList;

import org.joda.time.DateTime;
import org.joda.time.Duration;

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
  public static final String KEY_TASK_STATUS = "status";
  public static final String KEY_TASK_TSEQUENCE = "taskSequence";
  // Enum declaration for KEY_TASK_STATUS
  // new added task's default value, indicated that task added but not started
  // yet.
  public static final int TASKSTATUS_NOT_START = 0;
  // task now in progress, i.e, counting
  public static final int TASKSTATUS_IN_PROGRESS = 1;
  // task started already, and paused.
  public static final int TASKSTATUS_PAUSE = 2;
  // task already finished.
  public static final int TASKSTATUS_DONE = 3;

  // Time Table Key info
  public static final String KEY_TIME_TIMEID = "timeId";
  public static final String KEY_TIME_STARTTIME = "startTime";
  public static final String KEY_TIME_ENDTIME = "endTime";
  public static final String KEY_TIME_STATUS = "status";
  public static final String KEY_TIME_TID = "taskId";
  // Enum declaration for KEY_TIME_STATUS
  public static final int TIMESTATUS_DONE = 0;
  public static final int TIMESTATUS_RUNNING = 1;

  // Definition of database and table info.
  private static final String DATABASE_NAME = "data";
  private static final String TABLE_USER = "user";
  private static final String TABLE_TASKLIST = "taskList";
  private static final String TABLE_TASK = "task";
  private static final String TABLE_TIME = "time";

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
      + KEY_TASK_STATUS + " INTEGER NOT NULL, " + KEY_TASK_TSEQUENCE
      + " INTEGER, " + "FOREIGN KEY (" + KEY_TASK_TLID + ") REFERENCES "
      + TABLE_TASKLIST + "(" + KEY_TASKLIST_TLID
      + ") ON UPDATE CASCADE ON DELETE CASCADE " + ");";

  private static final String DATABASE_CREATE_TIME = "CREATE TABLE "
      + TABLE_TIME + " (" + KEY_TIME_TIMEID + " INTEGER PRIMARY KEY, "
      + KEY_TIME_STARTTIME + " TEXT NOT NULL, " + KEY_TIME_ENDTIME + " TEXT, "
      + KEY_TIME_STATUS + " INTEGER, " + KEY_TIME_TID + " INTEGER, "
      + "FOREIGN KEY (" + KEY_TIME_TID + ") REFERENCES " + TABLE_TASK + "("
      + KEY_TASK_TID + ") ON UPDATE CASCADE ON DELETE CASCADE " + ");";

  // SQL commands to destroy the tables.
  private static final String DATABASE_DESTROY_USER = "DROP TABLE IF EXISTS "
      + TABLE_USER;
  private static final String DATABASE_DESTROY_TASKLIST = "DROP TABLE IF EXISTS "
      + TABLE_TASKLIST;
  private static final String DATABASE_DESTROY_TASK = "DROP TABLE IF EXISTS "
      + TABLE_TASK;
  private static final String DATABASE_DESTROY_TIME = "DROP TABLE IF EXISTS "
      + TABLE_TIME;

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
      Log.i(TAG, DATABASE_CREATE_TIME);
      db.execSQL(DATABASE_CREATE_TIME);
      db.execSQL("INSERT INTO taskList VALUES (1,'other',1)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
      Log.w("TaskDbAdapter", "Upgrading database from version " + oldVersion
          + " to " + newVersion + ", destroying all data.");
      db.execSQL(DATABASE_DESTROY_USER);
      db.execSQL(DATABASE_DESTROY_TASKLIST);
      db.execSQL(DATABASE_DESTROY_TASK);
      db.execSQL(DATABASE_DESTROY_TIME);
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
   * Fetch info of taskList given the ID
   */
  public TaskListItem fetchTaskListObj(long taskListId) throws SQLException {
    return taskListObjFromCursor(fetchTaskList(taskListId));
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
   * Convert a cursor to a single item
   */
  public TaskListItem taskListObjFromCursor(Cursor cursor){
    return new TaskListItem(cursor.getLong(cursor
          .getColumnIndex(KEY_TASKLIST_TLID)), cursor.getString(cursor
          .getColumnIndex(KEY_TASKLIST_TLNAME)), cursor.getLong(cursor
          .getColumnIndex(KEY_TASKLIST_TLSEQUENCE)));
  }

  /**
   * @param dataCursor a cursor pointng to the task table
   * 
   * @return arraly list containing all the tasklists pointing by the cursor
   */
  public ArrayList<TaskListItem> taskListItemsFromCursor(Cursor cursor) {
    ArrayList<TaskListItem> items = new ArrayList<TaskListItem>();
    for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
      // create a item and add it to the list
      items.add(taskListObjFromCursor(cursor));
    }
    cursor.close();
    return items;
  }

  /**
   * Return the databse items as objects
   * 
   * @param taskListId
   * @return a arraylist containing all the TaskListItem ojbects
   */
  public ArrayList<TaskListItem> fetchAllTaskListsObjs(boolean orderBySequence)
      throws SQLException {
    Cursor mCursor = fetchAllTaskLists(orderBySequence);
    return taskListItemsFromCursor(mCursor);
  }

  /**
   * Fetch all tasklists info ordered by ROWID.
   * 
   * @return a Cursor pointing to all the records.
   */
  public Cursor fetchAllTaskLists() {
    return fetchAllTaskLists(false);
  }

  /**
   * Fetch all tasklists info.
   * 
   * @param orderBySequence
   *          whether order the query by sequence. On default order by ROWID.
   * @return cursor pointing to the results.
   */
  public Cursor fetchAllTaskLists(boolean orderBySequence) {
    if (orderBySequence) {
      return mDb.query(TABLE_TASKLIST, null, null, null, null, null,
          KEY_TASKLIST_TLSEQUENCE);
    } else {
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
    interval.close();

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
    if (dragOrigSeq < dropOrigSeq) { // 1, Drag from up to down. e.g, drag 2nd
                                     // to 5th.
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
   * Private function to help updateTaskListSequence Given an interval specified
   * by sequence ID, return the taskLists that are inside this interval.
   * boundaryA <= RESULT <= boundaryB or boundaryB <= RESULT <= boundaryA The
   * function will take care of the boundaries. Don't need to specify which is
   * bigger. NOTE: the return value will by order by sequence. NOT original id.
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
    long seq = mCursor.getLong(mCursor
        .getColumnIndexOrThrow(KEY_TASKLIST_TLSEQUENCE));
    mCursor.close();
    return seq;
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
   * Overloading method to add a task.
   * 
   * @param newTask
   *          the TaskItem object.
   * @return the id of the task.
   */
  public long createTask(TaskItem newTask) {
    return createTask(newTask.taskListId(), newTask.taskType(),
        newTask.taskName(), newTask.status(), newTask.dueDate());
  }

  /**
   * Create a tasklist given the info.
   * 
   * @param taskListId
   * @param taskType
   * @param taskName
   * @param dueDate
   * @return the ID of the newly inserted item, or -1 if an error occurred.
   */
  public long createTask(long taskListId, String taskType, String taskName,
      int status, String dueDate) {
    ContentValues initialValues = new ContentValues();
    initialValues.put(KEY_TASK_TLID, taskListId);
    initialValues.put(KEY_TASK_TYPE, taskType);
    initialValues.put(KEY_TASK_NAME, taskName);
    initialValues.put(KEY_TASK_STATUS, status);
    initialValues.put(KEY_TASK_DUEDATE, dueDate);

    // Initialize sequence by the value of the newId.
    // i.e, seq == id as initialization.
    long newId = mDb.insert(TABLE_TASK, null, initialValues);
    ContentValues seqInfo = new ContentValues();
    seqInfo.put(KEY_TASK_TSEQUENCE, newId);
    mDb.update(TABLE_TASK, seqInfo, KEY_TASK_TID + "=" + newId, null);

    return newId;
  }

  /**
   * Construct a taskItem given a cursor. 
   * @param cursor
   * @return
   */
  public TaskItem taskObjFormCursor(Cursor cursor) {
    return new TaskItem(
          cursor.getLong(cursor.getColumnIndex(KEY_TASK_TID)), 
          cursor.getLong(cursor.getColumnIndex(KEY_TASK_TLID)),
          cursor.getString(cursor.getColumnIndex(KEY_TASK_NAME)),
          cursor.getString(cursor.getColumnIndex(KEY_TASK_TYPE)), 
          cursor.getInt(cursor.getColumnIndex(KEY_TASK_STATUS)), 
          cursor.getString(cursor.getColumnIndex(KEY_TASK_DUEDATE)), 
          cursor.getLong(cursor.getColumnIndex(KEY_TASK_TSEQUENCE)));
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
   * given a taskId, return task as an object. 
   * if taskId not exists, return a default TaskItem object. 
   * @param taskId
   * @return
   * @throws SQLException
   */
  public TaskItem fetchTaskObj(long taskId) throws SQLException {
    Cursor cursor = fetchTask(taskId);

    Log.d("inside fetchTaskObj, taskId = ", String.valueOf(taskId));
    Log.d("inside fetchTaskObj: ", cursor.getString(cursor.getColumnIndex(KEY_TASK_NAME)));
    if(cursor.moveToFirst()){
      TaskItem item = taskObjFormCursor(cursor);
      cursor.close();
      return item;
    }
    else{
      TaskItem item = new TaskItem();
      cursor.close();
      return item;
    }

  }

  /**
   * return a list of TaskItem given a cursor. 
   * @param cursor
   * @return
   */
  public ArrayList<TaskItem> taskItemsFromCursor(Cursor cursor) {
    ArrayList<TaskItem> items = new ArrayList<TaskItem>();
    for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
      items.add(taskObjFormCursor(cursor));
    }
    cursor.close();
    return items;
  }

  /**
   * Fetch all tasks info, containing in a array list
   * @param bySequence
   * @return
   */
  public ArrayList<TaskItem> fetchAllTaskObjs(boolean bySequence) {
    return taskItemsFromCursor(fetchAllTasks(bySequence));
  }

  /**
   * Fetch info of all the tasks ordered by ROWID.
   * 
   * @return the Cursor pointing to all the records in task table.
   */
  public Cursor fetchAllTasks() {
    return fetchAllTasks(false);
  }

  /**
   * Fetch all tasks info.
   * 
   * @param orderBySequence
   *          whether order the query by sequence. On default order by ROWID
   * @return Cursor pointing to all the records.
   */
  public Cursor fetchAllTasks(boolean orderBySequence) {
    if (orderBySequence) {
      return mDb.query(TABLE_TASK, null, null, null, null, null,
          KEY_TASK_TSEQUENCE);
    } else {
      return mDb.query(TABLE_TASK, null, null, null, null, null, null);
    }
  }

  /**
   * Fetch all tasks in a specific taskList given the taskListId.
   * 
   * @param taskListId
   * @return the Cursor pointing to all the records in the specified taskList.
   */
  public Cursor fetchAllTasksInList(long taskListId,boolean orderBySequence) {
    if(orderBySequence) {
      return mDb.query(TABLE_TASK, null, KEY_TASK_TLID + "=" + taskListId, null,
        null, null, KEY_TASK_TSEQUENCE);
    } else {
      return mDb.query(TABLE_TASK, null, KEY_TASK_TLID + "=" + taskListId, null,
        null, null, null);
    }
  }

  /**
   * fetch all tasks by a cursor. 
   * @param taskListId
   * @return
   * @throws SQLException
   */
  public ArrayList<TaskItem> fetchTasksObjInList(long taskListId,boolean orderBySequence)

      throws SQLException {
    Cursor cur = fetchAllTasksInList(taskListId,orderBySequence);
    ArrayList<TaskItem> items = taskItemsFromCursor(cur);
    cur.close();
    return items;
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
   * @return successfully updated or not.
   */
  public boolean updateTask(long taskId, String taskType, String taskName,
      int status, String dueDate) {
    ContentValues updatedInfo = new ContentValues();
    updatedInfo.put(KEY_TASK_TYPE, taskType);
    updatedInfo.put(KEY_TASK_NAME, taskName);
    updatedInfo.put(KEY_TASK_STATUS, status);
    updatedInfo.put(KEY_TASK_DUEDATE, dueDate);

    return mDb.update(TABLE_TASK, updatedInfo, KEY_TASK_TID + "=" + taskId,
        null) > 0;
  }

  /**
   * update task's status.
   * 
   * @param taskId
   * @param status
   * @return
   */
  public boolean updateTaskStatus(long taskId, int status) {
    ContentValues updatedInfo = new ContentValues();
    updatedInfo.put(KEY_TASK_STATUS, status);

    return mDb.update(TABLE_TASK, updatedInfo, KEY_TASK_TID + "=" + taskId,
        null) > 0;
  }

  /**
   * Update task sequence according to the given ids. This function will update
   * the sequence between two ids, both included. Notice: the id will not
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

    Log.d("inside updateTaskSequence", "dragId: " + dragId + " dropId: "
        + dropId);
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
    Long tempSeq;
    Long tempId;
    // Log.d("Going to retrieve", "");
    for (interval.moveToFirst(); !interval.isAfterLast(); interval.moveToNext()) {
      tempSeq = interval.getLong(interval
          .getColumnIndexOrThrow(KEY_TASK_TSEQUENCE));
      // Log.d("tempSeq: ", String.valueOf(tempSeq));
      seqList.add(tempSeq);

      tempId = interval.getLong(interval.getColumnIndexOrThrow(KEY_TASK_TID));
      // Log.d("tempId: ", String.valueOf(tempId));
      idList.add(tempId);
    }
    interval.close();
    // Log.d("End retrieving info", "");

    if (seqList.size() != idList.size()) {
      return false;
    }

    // For debug use.
    String seqlistbefore_db = new String();
    String idlistbefore_db = new String();
    for (int i = 0; i < seqList.size(); ++i) {
      seqlistbefore_db += String.valueOf(seqList.get(i));
    }
    Log.d("seq list before", seqlistbefore_db);
    for (int i = 0; i < idList.size(); ++i) {
      idlistbefore_db += String.valueOf(idList.get(i));
    }
    Log.d("id list before", idlistbefore_db);

    // Now, seqList's sequence are 1-to-1 corresponding to the idList.
    // Handle two situations.
    if (dragOrigSeq < dropOrigSeq) { // 1, Drag from up to down. e.g, drag 2nd
                                     // to 5th.
      idList.remove(0);
      idList.add(dragId);
    } else { // 2, Drag from down to up. e.g, drag 5th to 2nd.
      idList.remove(idList.size() - 1); // remove the last.
      idList.add(0, dragId);
    }

    // Now idList and the sequence should be 1-to-1 corresponding.
    boolean status = true;
    for (int i = 0; i < idList.size(); ++i) {
      status = status && updateTaskSequenceById(idList.get(i), seqList.get(i));
    }

    // For debug use.
    String seqlistafter_db = new String();
    String idlistafter_db = new String();
    for (int i = 0; i < seqList.size(); ++i) {
      seqlistafter_db += String.valueOf(seqList.get(i));
    }
    Log.d("seq list after", seqlistafter_db);
    for (int i = 0; i < idList.size(); ++i) {
      idlistafter_db += String.valueOf(idList.get(i));
    }
    Log.d("id list after", idlistafter_db);

    Log.d("leaving updateTaskSequence", "dragId: " + dragId + " dropId: "
        + dropId);
    return status;
  }

  /**
   * Private function to help updateTaskSequence Given an interval specified by
   * sequence ID, return the tasks that are inside this interval. boundaryA <=
   * RESULT <= boundaryB or boundaryB <= RESULT <= boundaryA The function will
   * take care of the boundaries. Don't need to specify which is bigger. NOTE:
   * the return value will by order by sequence, NOT original id.
   * 
   * @param boundaryA
   * @param boundaryB
   * @return the rows between the two sequence id.
   */
  private Cursor getTaskByInterval(long boundaryA, long boundaryB) {
    long startSeq = min(boundaryA, boundaryB);
    long endSeq = max(boundaryB, boundaryA);

    return mDb.query(TABLE_TASK, new String[] { KEY_TASK_TID,
        KEY_TASK_TSEQUENCE }, KEY_TASK_TSEQUENCE + ">=" + startSeq + " AND "
        + KEY_TASK_TSEQUENCE + "<=" + endSeq, null, null, null,
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

    return mDb.update(TABLE_TASK, info, KEY_TASK_TID + "=" + taskId, null) > 0;
  }

  /**
   * private function to help updateTaskSequence. get the sequence of the item
   * given its id.
   * 
   * @param id
   * @return
   */
  private long getTaskSequenceById(long taskId) {
    Cursor mCursor = mDb.query(true, TABLE_TASK,
        new String[] { KEY_TASK_TSEQUENCE }, KEY_TASK_TID + "=" + taskId, null,
        null, null, null, null);
    mCursor.moveToFirst();
    long seq = mCursor.getLong(mCursor
        .getColumnIndexOrThrow(KEY_TASK_TSEQUENCE));
    mCursor.close();
    return seq;
  }

  /**
   * return id of the in-progress task if found, -1 if not found.
   * 
   * @return
   */
  public long getInProgressTaskId() {
    Cursor cursor = mDb.query(TABLE_TASK, null, KEY_TASK_STATUS + "="
        + TASKSTATUS_IN_PROGRESS, null, null, null, null);
    if (cursor.moveToFirst()) {
      cursor.close();
      return cursor.getLong(cursor.getColumnIndex(KEY_TASK_TID));
    } else {
      // Not found.
      cursor.close();
      return -1;
    }
  }

  /**
   * Function to return TaskItem that are currently in progress. At most 1 task
   * in progress in the same time, so return single TaskItem. CAUTIONS: If no
   * tasks in progress, return NULL.
   * 
   * @return
   */
  public TaskItem getInProgressTask() {
    Cursor cursor = mDb.query(TABLE_TASK, null, KEY_TASK_STATUS + "="
        + TASKSTATUS_IN_PROGRESS, null, null, null, null);
    if (cursor.moveToFirst()) {
      TaskItem result = new TaskItem(cursor.getLong(cursor
          .getColumnIndex(KEY_TASK_TID)), cursor.getLong(cursor
          .getColumnIndex(KEY_TASK_TLID)), cursor.getString(cursor
          .getColumnIndex(KEY_TASK_NAME)), cursor.getString(cursor
          .getColumnIndex(KEY_TASK_TYPE)), cursor.getInt(cursor
          .getColumnIndex(KEY_TASK_STATUS)), cursor.getString(cursor
          .getColumnIndex(KEY_TASK_DUEDATE)), cursor.getLong(cursor
          .getColumnIndex(KEY_TASK_TSEQUENCE)));
      cursor.close();
      return result;
    } else {
      cursor.close();
      return null;
    }
  }

  /**
   * Get the schema of task table.
   * 
   * @return an array of string containing all the attributes in task table.
   */
  public String[] getTaskSchema() {
    return new String[] { KEY_TASK_TLID, KEY_TASK_TID, KEY_TASK_TYPE,
        KEY_TASK_NAME, KEY_TASK_STATUS, KEY_TASK_DUEDATE };
  }

  // ***********************METHODS FOR TIME********************

  /**
   * General function to create a time record.
   * 
   * @param startTime
   * @param endTime
   * @param taskId
   * @return the newly inserted time record id.
   */
  public long createTime(DateTime startTime, DateTime endTime, int status,
      long taskId) {
    ContentValues initialValues = new ContentValues();
    initialValues.put(KEY_TIME_STARTTIME, startTime.toString());
    if(endTime!=null) {
      initialValues.put(KEY_TIME_ENDTIME, endTime.toString());
    }
    initialValues.put(KEY_TIME_STATUS, status);
    initialValues.put(KEY_TIME_TID, taskId);

    return mDb.insert(TABLE_TIME, null, initialValues);
  }

  /**
   * overloaded function to createTime. No need to provide endTime which will
   * automatically set to null.
   * 
   * @param startTime
   * @param taskId
   * @return
   */
  public long createTime(DateTime startTime, int status, long taskId) {
    return createTime(startTime, null, status, taskId);
  }

  /**
   * overloaded function of createTime. automatically set startTime to current
   * time and endTime to null.
   * 
   * @param taskId
   * @return
   */
  public long createTime(long taskId) {
    DateTime startTime = new DateTime();
    return createTime(startTime, TIMESTATUS_RUNNING, taskId);
  }

  /**
   * fetch a specific time record given its id.
   * 
   * @param timeId
   * @return
   * @throws SQLException
   */
  public Cursor fetchTime(long timeId) throws SQLException {
    Cursor mCursor = mDb.query(true, TABLE_TIME, null, KEY_TIME_TIMEID + "="
        + timeId, null, null, null, null, null);
    if (mCursor != null) {
      mCursor.moveToFirst();
    }
    return mCursor;
  }
  /*
  public TimeItem fetchTimeObj(long timeId) throws SQLException {
    Cursor cur = timeObjFromCursor(timeId);
    TimeItem item = timeObjFromCursor(cur);
    cur.close();
    return item;
  }
  public TimeItem timeObjFromCursor(Cursor cursor) {
    return new TimeItem(
        cursor.getLong(cursor.getColumnIndex(KEY_TIME_TIMEID)),
        cursor.getString(cursor.getColumnIndex(KEY_TIME_STARTTIME)),
        cursor.getString(cursor.getColumnIndex(KEY_TIME_ENDTIME)),
        cursor.getLong(cursor.getColumnIndex(KEY_TASK_STATUS)),
        cursor.getLong(cursor.getColumnIndex(KEY_TIME_TID)));
  }
  */

  /**
   * return all the time records.
   * 
   * @return
   */
  public Cursor fetchAllTimes() {
    return mDb.query(TABLE_TIME, null, null, null, null, null, null);
  }

  /**
   * return time records specified by its taskId.
   * 
   * @param taskId
   * @return
   */
  public Cursor fetchAllTimesOfTask(long taskId) {
    return mDb.query(TABLE_TIME, null, KEY_TIME_TID + "=" + taskId, null, null,
        null, null);
  }

  /**
   * delete a time record specified by its id.
   * 
   * @param timeId
   * @return
   */
  public boolean deleteTime(long timeId) {
    return mDb.delete(TABLE_TIME, KEY_TIME_TIMEID + "=" + timeId, null) > 0;
  }

  /**
   * General function to update a time record.
   * 
   * @param timeId
   * @param startTime
   * @param endTime
   * @param taskId
   * @return
   */
  public boolean updateTime(long timeId, DateTime startTime, DateTime endTime,
      int status, long taskId) {
    ContentValues updatedInfo = new ContentValues();
    updatedInfo.put(KEY_TIME_STARTTIME, startTime.toString());
    updatedInfo.put(KEY_TIME_ENDTIME, endTime.toString());
    updatedInfo.put(KEY_TIME_STATUS, status);
    updatedInfo.put(KEY_TIME_TID, taskId);

    return mDb.update(TABLE_TIME, updatedInfo, KEY_TIME_TIMEID + "=" + timeId,
        null) > 0;
  }

  /**
   * given the timeId, update the endTime to current time and set the status to
   * done.
   * 
   * @param timeId
   * @return
   */
  public boolean updateEndTime(long timeId) {
    DateTime endTime = new DateTime();

    ContentValues updatedInfo = new ContentValues();
    updatedInfo.put(KEY_TIME_ENDTIME, endTime.toString());
    updatedInfo.put(KEY_TIME_STATUS, TIMESTATUS_DONE);
    return mDb.update(TABLE_TIME, updatedInfo, KEY_TIME_TIMEID + "=" + timeId,
        null) > 0;
  }

  /**
   * update status of a specified time by its id.
   * 
   * @param timeId
   * @param status
   * @return
   */
  public boolean updateTimeStatus(long timeId, int status) {
    ContentValues updatedInfo = new ContentValues();
    updatedInfo.put(KEY_TIME_STATUS, status);
    return mDb.update(TABLE_TIME, updatedInfo, KEY_TIME_TIMEID + "=" + timeId,
        null) > 0;
  }

  /**
   * Get duration of a specified timeId. If no such record, return a duration of
   * 0. If the time is still running, calculate the duration until now.
   * 
   * @param timeId
   * @return
   */
  public Duration getDurationByTimeId(long timeId) {
    Duration duration = new Duration(0);
    Cursor mCursor = this.fetchTime(timeId);
    if (mCursor.moveToFirst()) {
      if (mCursor.getInt(mCursor.getColumnIndex(KEY_TIME_STATUS)) == TIMESTATUS_DONE) {
        // Time is done. EndTime not null.
        String startTimeStr = mCursor.getString(mCursor
            .getColumnIndex(KEY_TIME_STARTTIME));
        String endTimeStr = mCursor.getString(mCursor
            .getColumnIndex(KEY_TIME_ENDTIME));
        DateTime startTime = DateTime.parse(startTimeStr);
        DateTime endTime = DateTime.parse(endTimeStr);
        duration.plus(new Duration(startTime, endTime));

        mCursor.close();
        return duration;
      } else {
        // Time is still running. Set endTime as current time.
        String startTimeStr = mCursor.getString(mCursor
            .getColumnIndex(KEY_TIME_STARTTIME));
        DateTime startTime = DateTime.parse(startTimeStr);
        DateTime endTime = new DateTime();
        duration.plus(new Duration(startTime, endTime));

        mCursor.close();
        return duration;
      }
    } else {
      mCursor.close();
      return duration;
    }
  }

  /**
   * given a starttime and taskid, return the time spent on the specific task.
   * 
   * @return
   */
  public Duration timeSpentOnTaskFromSpecifiedDate(long taskId,
      DateTime startTime) {
    Cursor mCursor = this.fetchAllTimes();
    // Initialize an empty duration.
    Duration duration = new Duration(0);
    for (mCursor.moveToFirst(); !mCursor.isAfterLast(); mCursor.moveToNext()) {
      if (mCursor.getLong(mCursor.getColumnIndexOrThrow(KEY_TIME_TID)) == taskId) {
        // this is the task we want.
        String startStr = mCursor.getString(mCursor
            .getColumnIndexOrThrow(KEY_TIME_STARTTIME));
        String endStr = mCursor.getString(mCursor
            .getColumnIndexOrThrow(KEY_TIME_ENDTIME));

        DateTime start = DateTime.parse(startStr);
        DateTime end;
        if (endStr != null) {
          // the task is not running.
          end = DateTime.parse(endStr);
        } else {
          // the task is still running, so the endTime is null.
          // take the current time as the temporary endtime.
          end = new DateTime();
        }

        duration.plus(new Duration(start, end));
      }
    }
    mCursor.close();
    return duration;
  }
  
  /**
   * all time spent on a specified task
   * @param taskId
   * @return
   */
  public Duration timeSpentOnTask(long taskId){
    return timeSpentOnTaskFromSpecifiedDate(taskId, new DateTime(1970, 1, 1, 1, 1, 1, 1));
  }

  /**
   * Return timeId that are currently in progress. At most 1 task in progress at
   * the same time, so return TimeItem. CAUTIONS: If no time in progress, return
   * -1.
   * 
   * @return
   */
  public long getRunningTimeId() {
    Cursor mCursor = mDb.query(TABLE_TIME, null, KEY_TIME_STATUS + "="
        + TIMESTATUS_RUNNING, null, null, null, null);
    if (mCursor.getCount() == 1 && mCursor.moveToFirst()) {
      long result = mCursor.getLong(mCursor.getColumnIndex(KEY_TIME_TIMEID));
      mCursor.close();
      return result;
    } else {
      // Not found.
      mCursor.close();
      return -1;
    }
  }

  /**
   * return TimeItem that are currently in progress. At most 1 task in progress
   * at the same time, so return TimeItem. CAUTIONS: If no time in progress,
   * return NULL.
   * 
   * @return
   */
  public TimeItem getRunningTimeItem() {
    Cursor mCursor = mDb.query(TABLE_TIME, null, KEY_TIME_STATUS + "="
        + TIMESTATUS_RUNNING, null, null, null, null);
    if (mCursor.getCount() == 1 && mCursor.moveToFirst()) {
      TimeItem result = new TimeItem(mCursor.getLong(mCursor
          .getColumnIndex(KEY_TIME_TIMEID)), mCursor.getString(mCursor
          .getColumnIndex(KEY_TIME_STARTTIME)), mCursor.getString(mCursor
          .getColumnIndex(KEY_TIME_ENDTIME)), mCursor.getInt(mCursor
          .getColumnIndex(KEY_TIME_STATUS)), mCursor.getLong(mCursor
          .getColumnIndex(KEY_TIME_TID)));
      mCursor.close();
      return result;
    } else {
      // No found.
      mCursor.close();
      return null;
    }
  }

  /**
   * return whether there is a time in progress.
   * 
   * @return
   */
  public boolean isAnyTimeRunning() {
    return getRunningTimeId() > 0;
  }

  /**
   * return the schema of Time.
   * 
   * @return
   */
  public String[] getTimeSchema() {
    return new String[] { KEY_TIME_TIMEID, KEY_TIME_STARTTIME,
        KEY_TIME_ENDTIME, KEY_TIME_STATUS, KEY_TIME_TID };
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
