package eu.wauz.wazera.model.entity.tasks;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "WorkflowState")
public class WorkflowState implements Serializable {

	private static final long serialVersionUID = -5653732128492830930L;
	
	@Id
	@Column
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	
	@Column
	private Integer workflowId;
	
	@Column
	private String name;
	
	@Column
	private Integer sortOrder;
	
	@Column
	private boolean backlog;
	
	@Column
	private boolean completed;

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

	public void setBacklog(boolean backlog) {
		this.backlog = backlog;
	}

	public boolean isCompleted() {
		return completed;
	}

	public void setCompleted(boolean completed) {
		this.completed = completed;
	}

	@Override
	public String toString() {
		return "WorkflowState(" + (getId() != null ? String.valueOf(getId()) : "transient") + ") " + getName();
	}

}
