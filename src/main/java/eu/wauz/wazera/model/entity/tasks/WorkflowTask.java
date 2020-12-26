package eu.wauz.wazera.model.entity.tasks;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

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
	private String name;
	
	@Column
	private String description;
	
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
