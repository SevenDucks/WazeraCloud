package eu.wauz.wazera.model.data.tasks;

import java.util.Date;

import eu.wauz.wazera.model.data.auth.UserData;

public class WorkflowTaskData {
	
	private Integer id;
	
	private Integer workflowId;
	
	private Integer workflowStateId;
	
	private String name;
	
	private String description;
	
	private Priority priority;
	
	private UserData authorUser;
	
	private UserData assignedUser;
	
	private Date creationDate;
	
	private Date deadlineDate;
	
	private Date completionDate;
	
	private Integer sortOrder;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getWorkflowId() {
		return workflowId;
	}

	public void setWorkflowId(Integer workflowId) {
		this.workflowId = workflowId;
	}

	public Integer getWorkflowStateId() {
		return workflowStateId;
	}

	public void setWorkflowStateId(Integer workflowStateId) {
		this.workflowStateId = workflowStateId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Priority getPriority() {
		return priority;
	}

	public void setPriority(Priority priority) {
		this.priority = priority;
	}

	public UserData getAuthorUser() {
		return authorUser;
	}
	
	public Integer getAuthorUserId() {
		return authorUser == null ? null : authorUser.getId();
	}

	public void setAuthorUser(UserData authorUser) {
		this.authorUser = authorUser;
	}

	public UserData getAssignedUser() {
		return assignedUser;
	}
	
	public Integer getAssignedUserId() {
		return assignedUser == null ? null : assignedUser.getId();
	}

	public void setAssignedUser(UserData assignedUser) {
		this.assignedUser = assignedUser;
	}

	public Date getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}

	public Date getDeadlineDate() {
		return deadlineDate;
	}

	public void setDeadlineDate(Date deadlineDate) {
		this.deadlineDate = deadlineDate;
	}

	public Date getCompletionDate() {
		return completionDate;
	}

	public void setCompletionDate(Date completionDate) {
		this.completionDate = completionDate;
	}

	public Integer getSortOrder() {
		return sortOrder;
	}

	public void setSortOrder(Integer sortOrder) {
		this.sortOrder = sortOrder;
	}

}
