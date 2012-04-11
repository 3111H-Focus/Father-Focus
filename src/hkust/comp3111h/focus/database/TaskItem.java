/**
 * A simple class to present the task item
 */

package hkust.comp3111h.focus.database;
import java.lang.Comparable;

public class TaskItem implements Comparable<TaskItem> {
  //Follow the schema
  long mTaskListId;
  long mTaskId;
  String mTaskName;
  String mTaskType;
  int mStatus;
  String mDueDate;
  long mSequence;
  public TaskItem() {
    mTaskListId = 0;
    mTaskId = 0;
    mTaskName = null;
    mTaskType= null;
    mStatus = 0;
    mDueDate = null;
    mSequence = 0;
  }
  public TaskItem(
      long tid,
      long tlid,
      String tname,
      String ttype,
      int tstatus,
      String tduedate,
      long tsequence) {
    mTaskListId = tlid;
    mTaskId = tid;
    mTaskName = tname;
    mTaskType = ttype;
    mStatus = tstatus;
    mDueDate = tduedate;
    mSequence = tsequence;
  }
  public String taskType() {
    return mTaskType;
  }
  public void taskType(String ttype) {
    mTaskType = ttype;
  }
  //the getter and setters are overloaded functions to each other
  public long taskListId() {
    return mTaskListId;
  }
  public void taskListId(long tlId) {
    mTaskListId = tlId;
  }

  public long taskId() {
    return mTaskId;
  }
  public void taskId(long tId) {
    mTaskId = tId;
  }
  public String taskName() {
    return mTaskName;
  }
  public void taskName(String tName) {
    mTaskName = tName;
  }
  public long sequence() {
    return mSequence;
  }
  public void sequence(long seq) {
    mSequence = seq;
  }
  public void status(int tstatus) {
    mStatus = tstatus;
  }
  public int status(){
    return mStatus;
  }
  public void dueDate(String ddate) {
    mDueDate = ddate;
  }
  public String dueDate() {
    return mDueDate;
  }
  @Override
  public int compareTo(TaskItem other) {
    return (int)(mSequence - other.sequence());
  }
}
