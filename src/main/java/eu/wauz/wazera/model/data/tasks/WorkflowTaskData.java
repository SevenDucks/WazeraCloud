package eu.wauz.wazera.model.data.tasks;

import java.util.Date;
import java.util.concurrent.TimeUnit;

import eu.wauz.wazera.model.data.auth.UserData;

public class WorkflowTaskData {
	
	private Integer id;
	
	private Integer workflowId;
	
	private Integer workflowStateId;
	
	private String workflowName;
	
	private String workflowStateName;
	
	private String name;
	
	private String description;
	
	private Priority priority;
	
	private Integer authorUserId;
	
	private UserData authorUser;
	
	private Integer assignedUserId;
	
	private UserData assignedUser;
	
	private Date creationDate;
	
	private Date deadlineDate;
	
	private Date completionDate;
	
	private Date editDate;
	
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

	public String getWorkflowName() {
		return workflowName;
	}

	public void setWorkflowName(String workflowName) {
		this.workflowName = workflowName;
	}

	public String getWorkflowStateName() {
		return workflowStateName;
	}

	public void setWorkflowStateName(String workflowStateName) {
		this.workflowStateName = workflowStateName;
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

	public Integer getAuthorUserId() {
		return authorUserId;
	}
	
	public void setAuthorUserId(Integer authorUserId) {
		this.authorUserId = authorUserId;
	}
	
	public UserData getAuthorUser() {
		return authorUser;
	}
	

	public void setAuthorUser(UserData authorUser) {
		this.authorUser = authorUser;
		if(authorUser != null) {
			this.authorUserId = authorUser.getId();
		}
	}

	public Integer getAssignedUserId() {
		return assignedUserId;
	}

	public void setAssignedUserId(Integer assignedUserId) {
		this.assignedUserId = assignedUserId;
	}

	public UserData getAssignedUser() {
		return assignedUser;
	}
	
	public String getAssignedUserName() {
		return assignedUser != null ? assignedUser.getUsername() : "Not Assigned";
	}

	public void setAssignedUser(UserData assignedUser) {
		this.assignedUser = assignedUser;
		if(assignedUser != null) {
			this.assignedUserId = assignedUser.getId();
		}
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

	public Date getEditDate() {
		return editDate;
	}

	public void setEditDate(Date editDate) {
		this.editDate = editDate;
	}

	public Integer getSortOrder() {
		return sortOrder;
	}

	public void setSortOrder(Integer sortOrder) {
		this.sortOrder = sortOrder;
	}
	
	public String getStatusText() {
		if(completionDate != null) {
			return "Completed";
		}
		else if(deadlineDate != null) {
			if(deadlineDate.before(new Date())) {
				return "Overdue";
			}
			else {
				long time = deadlineDate.getTime() - new Date().getTime();
				return (TimeUnit.DAYS.convert(time, TimeUnit.MILLISECONDS) + 1) + " Days Left";
			}
		}
		else {
			return "No Deadline";
		}
	}
	
	public String getStatusColor() {
		if(completionDate != null) {
			return "lightseagreen";
		}
		else if(deadlineDate != null) {
			return deadlineDate.before(new Date()) ? "lightsalmon" : "moccasin";
		}
		else {
			return "plum";
		}
	}

}
