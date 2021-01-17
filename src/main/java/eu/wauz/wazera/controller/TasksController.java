package eu.wauz.wazera.controller;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;

import org.primefaces.event.DashboardReorderEvent;
import org.primefaces.model.DashboardColumn;
import org.primefaces.model.DashboardModel;
import org.primefaces.model.DefaultDashboardColumn;
import org.primefaces.model.DefaultDashboardModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import eu.wauz.wazera.WazeraTool;
import eu.wauz.wazera.model.data.auth.Permission;
import eu.wauz.wazera.model.data.docs.DocumentData;
import eu.wauz.wazera.model.data.tasks.Priority;
import eu.wauz.wazera.model.data.tasks.WorkflowData;
import eu.wauz.wazera.model.data.tasks.WorkflowStateData;
import eu.wauz.wazera.model.data.tasks.WorkflowTaskData;
import eu.wauz.wazera.service.AuthDataService;
import eu.wauz.wazera.service.TasksDataService;

@Controller
@Scope("view")
public class TasksController implements Serializable {

	private static final long serialVersionUID = -5819818885113415867L;
	
	@Autowired
	private TasksDataService tasksService;
	
	@Autowired
	private AuthDataService authService;
	
	private WorkflowData workflow;
	
	private WorkflowStateData workflowState;
	
	private WorkflowTaskData workflowTask;
	
	private DashboardModel model;
	
	private boolean allowEditing;
	
	private boolean allowToggleEditing;
	
	private WazeraTool wazeraTool;

	@PostConstruct
	private void init() {
		wazeraTool = new WazeraTool();
		workflowState = new WorkflowStateData();
		workflowTask = new WorkflowTaskData();
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
	
	public void resetWorkflowModel(boolean resetAll) {
		setWorkflow(tasksService.getWorkflow(workflow.getId()));
		if(resetAll) {
			setNewWorkflowState();
			setNewWorkflowTask();
		}
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
			wazeraTool.showInfoMessage("Your Workflow State was reordered!");
			resetWorkflowModel(true);
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
			wazeraTool.showInfoMessage("Your Workflow States were updated!");
			resetWorkflowModel(true);
		}
		catch (Exception e) {
			wazeraTool.showErrorMessage(e.getMessage());
		}
	}
	
	public void deleteWorkflowState(WorkflowStateData workflowStateData) {
		try {
			tasksService.deleteWorkflowState(workflowStateData.getId());
			wazeraTool.showInfoMessage("Your Workflow State was deleted!");
			resetWorkflowModel(true);
		}
		catch (Exception e) {
			wazeraTool.showErrorMessage(e.getMessage());
		}
	}
	
	public List<WorkflowTaskData> getAssignedTasks() {
		return tasksService.getAssignedTasks();
	}
	
	public WorkflowTaskData getWorkflowTask() {
		return workflowTask;
	}

	public void setWorkflowTask(WorkflowTaskData workflowTask, boolean ownTask) {
		this.workflowTask = workflowTask;
		setWorkflow(tasksService.getWorkflow(workflowTask.getWorkflowId()));
		allowEditing = false;
		allowToggleEditing = ownTask ? canEditAssignedTasks() : canEditTasks();
	}

	public void setNewWorkflowTask() {
		this.workflowTask = new WorkflowTaskData();
		workflowTask.setWorkflowId(workflow.getId());
		workflowTask.setWorkflowStateId(workflow.getStates().get(0).getId());
		workflowTask.setName("New Task");
		workflowTask.setDescription("");
		workflowTask.setPriority(Priority.NORMAL);
		workflowTask.setAuthorUser(authService.getLoggedInUser());
		workflowTask.setAssignedUser(null);
		workflowTask.setCreationDate(new Date());
		workflowTask.setDeadlineDate(null);
		workflowTask.setCompletionDate(null);
		workflowTask.setSortOrder(-1);
		allowEditing = true;
	}
	
	public void saveWorkflowTask(boolean exit) {
		try {
			for(WorkflowStateData stateData : workflow.getStates()) {
				if(stateData.getId().equals(workflowTask.getWorkflowStateId())) {
					workflowState = stateData;
					break;
				}
			}
			workflowTask.setEditDate(new Date());
			workflowState.getTasks().add(workflowTask);
			tasksService.saveWorkflowStateTasks(workflowState);
			wazeraTool.showInfoMessage("Your Task was saved!");
			resetWorkflowModel(exit);
			allowEditing = !exit;
		}
		catch (Exception e) {
			wazeraTool.showErrorMessage(e.getMessage());
		}
	}
	
	public void handleWorkflowTaskReorder(DashboardReorderEvent event) {
		try {
			Integer taskId = Integer.parseInt(event.getWidgetId().replace("task", ""));
			Integer stateId = workflow.getStates().get(event.getColumnIndex()).getId();
			tasksService.reorderWorkflowTasks(taskId, stateId, event.getItemIndex());
			wazeraTool.showInfoMessage("Your Task was reordered!");
			resetWorkflowModel(true);
		}
		catch (Exception e) {
			wazeraTool.showErrorMessage(e.getMessage());
		}
    }
	
	public void deleteWorkflowTask(WorkflowTaskData workflowTask) {
		try {
			tasksService.deleteWorkflowTask(workflowTask.getId());
			wazeraTool.showInfoMessage("Your Task was deleted!");
			resetWorkflowModel(true);
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
			for(WorkflowTaskData taskData : stateData.getTasks()) {
				column.addWidget("task" + taskData.getId());
			}
			model.addColumn(column);
		}
	}
	
	public boolean isAllowEditing() {
		return allowEditing;
	}

	public void setAllowEditing(boolean allowEditing) {
		this.allowEditing = allowEditing;
	}

	public boolean isAllowToggleEditing() {
		return allowToggleEditing;
	}

	public void setAllowToggleEditing(boolean allowToggleEditing) {
		this.allowToggleEditing = allowToggleEditing;
	}

	public Priority[] getPriorities() {
		return Priority.values();
	}
	
	public boolean canViewTasks() {
		return authService.hasPermission(wazeraTool.getUsername(), Permission.VIEW_TASKS.getId());
	}
	
	public boolean canEditTasks() {
		return authService.hasPermission(wazeraTool.getUsername(), Permission.EDIT_TASKS.getId());
	}
	
	public boolean canDeleteTasks() {
		return authService.hasPermission(wazeraTool.getUsername(), Permission.DELETE_TASKS.getId());
	}
	
	public boolean canViewAssignedTasks() {
		return authService.hasPermission(wazeraTool.getUsername(), Permission.VIEW_ASSIGNED_TASKS.getId());
	}
	
	public boolean canEditAssignedTasks() {
		return authService.hasPermission(wazeraTool.getUsername(), Permission.EDIT_ASSIGNED_TASKS.getId());
	}
	
	public boolean canDeleteAssignedTasks() {
		return authService.hasPermission(wazeraTool.getUsername(), Permission.DELETE_ASSIGNED_TASKS.getId());
	}

}
