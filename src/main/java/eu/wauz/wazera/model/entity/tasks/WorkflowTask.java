package eu.wauz.wazera.model.entity.tasks;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;

import eu.wauz.wazera.model.data.tasks.Priority;

@Entity
@Table(name = "WorkflowTask")
public class WorkflowTask implements Serializable {

	private static final long serialVersionUID = -7130167590925201821L;
	
	@Id
	@Column
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	
	@Column
	private Integer workflowId;
	
	@Column
	private Integer workflowStateId;
	
	@Column
	private String name;
	
	@Lob
	@Column
	private String description;
	
	@Column
	private Priority priority;
	
	@Column
	private Integer authorUserId;
	
	@Column
	private Integer assignedUserId;
	
	@Column
	private Date creationDate;
	
	@Column
	private Date deadlineDate;
	
	@Column
	private Date completionDate;
	
	@Column
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

	public Integer getAuthorUserId() {
		return authorUserId;
	}

	public void setAuthorUserId(Integer authorUserId) {
		this.authorUserId = authorUserId;
	}

	public Integer getAssignedUserId() {
		return assignedUserId;
	}

	public void setAssignedUserId(Integer assignedUserId) {
		this.assignedUserId = assignedUserId;
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
	
	@Override
	public String toString() {
		return "WorkflowTask(" + (getId() != null ? String.valueOf(getId()) : "transient") + ") " + getName();
	}

}
