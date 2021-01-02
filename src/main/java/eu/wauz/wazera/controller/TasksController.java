package eu.wauz.wazera.controller;

import java.io.Serializable;

import javax.annotation.PostConstruct;

import org.primefaces.model.DashboardColumn;
import org.primefaces.model.DashboardModel;
import org.primefaces.model.DefaultDashboardColumn;
import org.primefaces.model.DefaultDashboardModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import eu.wauz.wazera.WazeraTool;
import eu.wauz.wazera.model.data.docs.DocumentData;
import eu.wauz.wazera.model.data.tasks.WorkflowData;
import eu.wauz.wazera.model.data.tasks.WorkflowStateData;
import eu.wauz.wazera.service.TasksDataService;

@Controller
@Scope("view")
public class TasksController implements Serializable {

	private static final long serialVersionUID = -5819818885113415867L;
	
	@Autowired
	private TasksDataService tasksService;
	
	private WorkflowData workflow;
	
	private WorkflowStateData workflowState;
	
	private DashboardModel model;
	
	private WazeraTool wazeraTool;

	@PostConstruct
	private void init() {
		wazeraTool = new WazeraTool();
	}

	public WorkflowData getWorkflow() {
		return workflow;
	}

	public void setWorkflow(WorkflowData workflow) {
		this.workflow = workflow;
		updateDashboardModel();
	}
	
	public void setWorkflowFromDocument(DocumentData documentData) {
		this.workflow = tasksService.getWorkflow(documentData);
		updateDashboardModel();
	}
	
	public void resetWorkflow() {
		setWorkflow(tasksService.getWorkflow(workflow.getId()));
		setNewWorkflowState();
	}
	
	public WorkflowStateData getWorkflowState() {
		return workflowState;
	}

	public void setWorkflowState(WorkflowStateData workflowState) {
		this.workflowState = workflowState;
	}
	
	public void setNewWorkflowState() {
		this.workflowState = new WorkflowStateData();
		workflowState.setName("New Workflow State");
		workflowState.setSortOrder(Integer.MAX_VALUE);
	}
	
	public boolean canWorkflowStateMove(WorkflowStateData workflowstate, boolean left) {
		int index = workflow.getStates().indexOf(workflowstate);
		if(index <= (left ? 1 : 0) || index >= workflow.getStates().size() - (left ? 1 : 2)) {
			return false;
		}
		return true;
	}
	
	public void moveWorkflowState(WorkflowStateData workflowstate, boolean left) {
		int index = workflow.getStates().indexOf(workflowstate);
		if(index <= (left ? 1 : 0) || index >= workflow.getStates().size() - (left ? 1 : 2)) {
			return;
		}
		int newIndex = left ? index - 1 : index + 1;
		WorkflowStateData swapWorkflowState = workflow.getStates().get(newIndex);
		swapWorkflowState.setSortOrder(index);
		workflowstate.setSortOrder(newIndex);
		try {
			tasksService.saveWorkflow(workflow);
			resetWorkflow();
		}
		catch (Exception e) {
			wazeraTool.showErrorMessage(e.getMessage());
		}
	}
	
	public void saveWorkflowState() {
		try {
			if(workflowState.isTransient()) {
				workflow.getStates().add(workflowState);
			}
			tasksService.saveWorkflow(workflow);
			resetWorkflow();
		}
		catch (Exception e) {
			wazeraTool.showErrorMessage(e.getMessage());
		}
	}
	
	public void deleteWorkflowState(WorkflowStateData workflowStateData) {
		try {
			tasksService.deleteWorkflowState(workflowStateData.getId());
			resetWorkflow();
		}
		catch (Exception e) {
			wazeraTool.showErrorMessage(e.getMessage());
		}
	}

	public DashboardModel getModel() {
		return model;
	}

	public void setModel(DashboardModel model) {
		this.model = model;
	}

	public void updateDashboardModel() {
		model = new DefaultDashboardModel();
		for(WorkflowStateData stateData : workflow.getStates()) {
			DashboardColumn column = new DefaultDashboardColumn();
			column.addWidget("state" + stateData.getId());
			column.addWidget("task" + stateData.getId());
			model.addColumn(column);
		}
	}

}
