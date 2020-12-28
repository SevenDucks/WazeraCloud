package eu.wauz.wazera.controller;

import java.io.Serializable;

import org.primefaces.model.DashboardColumn;
import org.primefaces.model.DashboardModel;
import org.primefaces.model.DefaultDashboardColumn;
import org.primefaces.model.DefaultDashboardModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import eu.wauz.wazera.model.data.docs.DocumentData;
import eu.wauz.wazera.model.data.tasks.WorkflowData;
import eu.wauz.wazera.model.data.tasks.WorkflowStateData;
import eu.wauz.wazera.service.TasksDataService;

@Controller
@Scope("view")
public class TasksController implements Serializable {

	private static final long serialVersionUID = -5819818885113415867L;
	
	private WorkflowData workflow;
	
	private DashboardModel model;
	
	@Autowired
	private TasksDataService tasksService;

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
