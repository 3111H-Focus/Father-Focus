package hkust.comp3111h.focus.object;

public class Task {

  private long taskListId;
  private long taskId;
  private String taskType;
  private String taskName;
  private String dueDate;
  private String startDate;
  private String endDate;

  public Task() {
    this.setTaskListId(-1);
    this.setTaskId(-1);
    this.setTaskType("");
    this.setTaskName("");
    this.setDueDate("");
    this.setStartDate("");
    this.setEndDate("");
  }

  /**
   * 
   * @param taskListId
   * @param taskId
   * @param taskType
   * @param taskName
   * @param dueDate
   * @param startDate
   * @param endDate
   */
  public Task(long taskListId, long taskId, String taskType,
      String taskName, String dueDate, String startDate,
      String endDate) {
    this.setTaskListId(taskListId);
    this.setTaskId(taskId);
    this.setTaskType(taskType);
    this.setTaskName(taskName);
    this.setDueDate(dueDate);
    this.setStartDate(startDate);
    this.setEndDate(endDate);
  }

  /**
   * @return the taskListId
   */
  public long getTaskListId() {
    return taskListId;
  }

  /**
   * @param taskListId
   *          the taskListId to set
   */
  public void setTaskListId(long taskListId) {
    this.taskListId = taskListId;
  }

  /**
   * @return the taskId
   */
  public long getTaskId() {
    return taskId;
  }

  /**
   * @param taskId
   *          the taskId to set
   */
  public void setTaskId(long taskId) {
    this.taskId = taskId;
  }

  /**
   * @return the taskType
   */
  public String getTaskType() {
    return taskType;
  }

  /**
   * @param taskType
   *          the taskType to set
   */
  public void setTaskType(String taskType) {
    this.taskType = taskType;
  }

  /**
   * @return the taskName
   */
  public String getTaskName() {
    return taskName;
  }

  /**
   * @param taskName
   *          the taskName to set
   */
  public void setTaskName(String taskName) {
    this.taskName = taskName;
  }

  /**
   * @return the dueDate
   */
  public String getDueDate() {
    return dueDate;
  }

  /**
   * @param dueDate
   *          the dueDate to set
   */
  public void setDueDate(String dueDate) {
    this.dueDate = dueDate;
  }

  /**
   * @return the startDate
   */
  public String getStartDate() {
    return startDate;
  }

  /**
   * @param startDate
   *          the startDate to set
   */
  public void setStartDate(String startDate) {
    this.startDate = startDate;
  }

  /**
   * @return the endDate
   */
  public String getEndDate() {
    return endDate;
  }

  /**
   * @param endDate
   *          the endDate to set
   */
  public void setEndDate(String endDate) {
    this.endDate = endDate;
  }
}
