package eu.wauz.wazera.model.data.tasks;

import java.util.List;

public class WorkflowData {
	
	private Integer id;
	
	private Integer documentId;
	
	private List<WorkflowStateData> states;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getDocumentId() {
		return documentId;
	}

	public void setDocumentId(Integer documentId) {
		this.documentId = documentId;
	}

	public List<WorkflowStateData> getStates() {
		return states;
	}

	public void setStates(List<WorkflowStateData> states) {
		this.states = states;
	}
	
}
