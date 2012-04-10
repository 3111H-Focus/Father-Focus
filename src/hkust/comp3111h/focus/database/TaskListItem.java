package hkust.comp3111h.focus.database;
import java.lang.Comparable;

public class TaskListItem implements Comparable<TaskListItem> {
  long mTaskListId = 0;
  String mTaskListName = null;
  long mTaskListSequence = 0;
//Default, do nothing
  public TaskListItem() { }
  //Initialize all data
  public TaskListItem(
      long tlId,
      String tlName,
      long seq) {
    mTaskListId = tlId;
    mTaskListName = tlName;
    mTaskListSequence = seq;
  }

  public long taskListId() {
    return mTaskListId;
  }

  public void taskListId(long tlid) {
    mTaskListId = tlid;
  }
  public String taskListName() {
    return mTaskListName;
  }
  public void taskListName(String tlName) {
    mTaskListName = tlName;
  }
  public long taskListSequence() {
    return mTaskListSequence;
  }
  public void taskListSequence(long tlseq) {
    mTaskListSequence = tlseq;
  }
  @Override
  public int compareTo(TaskListItem other) {
    return (int)(mTaskListSequence - other.taskListSequence());
  }

}
