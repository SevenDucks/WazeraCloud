package eu.wauz.wazera.model.data.tasks;

import java.util.List;

public class WorkflowData {
	
	private Integer id;
	
	private List<WorkflowStateData> states;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public List<WorkflowStateData> getStates() {
		return states;
	}

	public void setStates(List<WorkflowStateData> states) {
		this.states = states;
	}
	
}
