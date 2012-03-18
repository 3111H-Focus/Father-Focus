package hkust.comp3111h.focus.object;

public class TaskList {
	private long id;
	private String taskListName;
	
	public TaskList(){
		id = -1;
		taskListName = "";
	}
	
	public TaskList(long id, String taskListName){
		this.id = id;
		this.taskListName = taskListName;
	}
	
	public void setId(long id){
		this.id = id;
	}
	
	public long getId(){
		return this.id;
	}
	
	public void setTaskListName(String taskListName){
		this.taskListName = taskListName;
	}
	
	public String getTaskListName(){
		return this.taskListName;
	}
}
