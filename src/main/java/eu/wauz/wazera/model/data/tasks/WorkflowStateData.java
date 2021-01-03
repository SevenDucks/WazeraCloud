package eu.wauz.wazera.model.data.tasks;

import java.util.List;

public class WorkflowStateData {
	
	private Integer id;
	
	private String name;
	
	private Integer sortOrder;
	
	private boolean backlog;
	
	private boolean completed;
	
	private List<WorkflowTaskData> tasks;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Integer getSortOrder() {
		return sortOrder;
	}

	public void setSortOrder(Integer sortOrder) {
		this.sortOrder = sortOrder;
	}

	public boolean isBacklog() {
		return backlog;
	}
	
	public boolean isNotBacklog() {
		return !backlog;
	}

	public void setBacklog(boolean backlog) {
		this.backlog = backlog;
	}

	public boolean isCompleted() {
		return completed;
	}
	
	public boolean isNotCompleted() {
		return !completed;
	}

	public void setCompleted(boolean completed) {
		this.completed = completed;
	}
	
	public List<WorkflowTaskData> getTasks() {
		return tasks;
	}

	public void setTasks(List<WorkflowTaskData> tasks) {
		this.tasks = tasks;
	}

	public boolean isTransient() {
		return getId() == null;
	}
	
}
