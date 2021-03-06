package eu.wauz.wazera.model.data.tasks;

import java.util.ArrayList;
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
	
	public int getTaskCount() {
		int taskCount = 0;
		for(WorkflowStateData state : states) {
			taskCount += state.getTasks().size();
		}
		return taskCount;
	}
	
	public List<WorkflowTaskData> getTasks() {
		List<WorkflowTaskData> tasks = new ArrayList<>();
		for(WorkflowStateData state : states) {
			tasks.addAll(state.getTasks());
		}
		return tasks;
	}

	public List<WorkflowStateData> getStates() {
		return states;
	}

	public void setStates(List<WorkflowStateData> states) {
		this.states = states;
	}
	
}
