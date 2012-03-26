package hkust.comp3111h.focus.object;

public class TaskList {
  private long id;
  private String taskListName;

  public TaskList() {
    this.setId(-1);
    this.setTaskListName("");
  }

  public TaskList(long id, String taskListName) {
    this.setId(id);
    this.setTaskListName(taskListName);
  }

  public void setId(long id) {
    this.id = id;
  }

  public long getId() {
    return this.id;
  }

  public void setTaskListName(String taskListName) {
    this.taskListName = taskListName;
  }

  public String getTaskListName() {
    return this.taskListName;
  }
}
