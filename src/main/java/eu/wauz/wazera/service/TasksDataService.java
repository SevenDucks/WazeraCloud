package eu.wauz.wazera.service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import eu.wauz.wazera.model.data.docs.DocumentData;
import eu.wauz.wazera.model.data.tasks.WorkflowData;
import eu.wauz.wazera.model.data.tasks.WorkflowStateData;
import eu.wauz.wazera.model.data.tasks.WorkflowTaskData;
import eu.wauz.wazera.model.entity.tasks.Workflow;
import eu.wauz.wazera.model.entity.tasks.WorkflowState;
import eu.wauz.wazera.model.entity.tasks.WorkflowTask;
import eu.wauz.wazera.model.repository.tasks.WorkflowRepository;
import eu.wauz.wazera.model.repository.tasks.WorkflowStateRepository;
import eu.wauz.wazera.model.repository.tasks.WorkflowTaskRepository;

@Service
@Scope("singleton")
public class TasksDataService {
	
	@Autowired
	private AuthDataService authService;
	
	@Autowired
	private WorkflowRepository workflowRepository;
	
	@Autowired
	private WorkflowStateRepository workflowStateRepository;
	
	@Autowired
	private WorkflowTaskRepository workflowTaskRepository;
	
	public WorkflowData saveNewWorkflow(DocumentData documentData) {
		WorkflowData workflowData = new WorkflowData();
		workflowData.setDocumentId(documentData.getId());
		List<WorkflowStateData> workflowStateDatas = new ArrayList<>();
		
		WorkflowStateData backlogState = new WorkflowStateData();
		backlogState.setName("Backlog");
		backlogState.setBacklog(true);
		workflowStateDatas.add(backlogState);
		
		WorkflowStateData plannedState = new WorkflowStateData();
		plannedState.setName("Planned");
		plannedState.setSortOrder(1);
		workflowStateDatas.add(plannedState);
		
		WorkflowStateData inProgressState = new WorkflowStateData();
		inProgressState.setName("In Progress");
		inProgressState.setSortOrder(2);
		workflowStateDatas.add(inProgressState);
		
		WorkflowStateData completedState = new WorkflowStateData();
		completedState.setName("Completed");
		completedState.setCompleted(true);
		workflowStateDatas.add(completedState);
		
		workflowData.setStates(workflowStateDatas);
		return saveWorkflow(workflowData);
	}
	
	public WorkflowData saveWorkflow(WorkflowData workflowData) {
		Workflow workflow;
		if(workflowData.getId() != null) {
			workflow = workflowRepository.findById(workflowData.getId()).orElse(new Workflow());
		}
		else {
			workflow = new Workflow();
		}
		workflow.setDocumentId(workflowData.getDocumentId());
		workflow = workflowRepository.save(workflow);
		int sortOrder = 0;
		Comparator<WorkflowStateData> comparator = Comparator
				.comparing(WorkflowStateData::isNotBacklog)
				.thenComparing(WorkflowStateData::isCompleted)
				.thenComparing(WorkflowStateData::getSortOrder);
		List<WorkflowStateData> workflowStateDatas = workflowData.getStates().stream()
				.sorted(comparator)
				.collect(Collectors.toList());
		for(WorkflowStateData workflowStateData : workflowStateDatas) {
			WorkflowState workflowState;
			if(workflowStateData.getId() != null) {
				workflowState = workflowStateRepository.findById(workflowStateData.getId()).orElse(new WorkflowState());
			}
			else {
				workflowState = new WorkflowState();
			}
			workflowState.setWorkflowId(workflow.getId());
			workflowState.setName(workflowStateData.getName());
			workflowState.setSortOrder(sortOrder++);
			workflowState.setBacklog(workflowStateData.isBacklog());
			workflowState.setCompleted(workflowStateData.isCompleted());
			workflowStateRepository.save(workflowState);
		}
		return getWorkflow(workflow);
	}
	
	public void saveWorkflowStateTasks(WorkflowStateData workflowStateData) {
		int sortOrder = 0;
		List<WorkflowTaskData> workflowTaskDatas = workflowStateData.getTasks().stream()
				.sorted((t1, t2) -> t1.getSortOrder().compareTo(t2.getSortOrder()))
				.collect(Collectors.toList());
		for(WorkflowTaskData workflowTaskData : workflowTaskDatas) {
			WorkflowTask workflowTask;
			if(workflowTaskData.getId() != null) {
				workflowTask = workflowTaskRepository.findById(workflowTaskData.getId()).orElse(new WorkflowTask());
			}
			else {
				workflowTask = new WorkflowTask();
			}
			workflowTask.setWorkflowId(workflowTaskData.getWorkflowId());
			workflowTask.setWorkflowStateId(workflowStateData.getId());
			workflowTask.setName(workflowTaskData.getName());
			workflowTask.setDescription(workflowTaskData.getDescription());
			workflowTask.setPriority(workflowTaskData.getPriority());
			workflowTask.setAuthorUserId(workflowTaskData.getAuthorUserId());
			workflowTask.setAssignedUserId(workflowTaskData.getAssignedUserId());
			workflowTask.setCreationDate(workflowTaskData.getCreationDate());
			workflowTask.setDeadlineDate(workflowTaskData.getDeadlineDate());
			if(workflowStateData.isCompleted()) {
				if(workflowTask.getCompletionDate() == null) {
					workflowTask.setCompletionDate(new Date());
				}
			}
			else {
				workflowTask.setCompletionDate(null);
			}
			workflowTask.setSortOrder(sortOrder++);
			workflowTaskRepository.save(workflowTask);
		}
	}
	
	public void reorderWorkflowTasks(Integer taskId, Integer stateId, int itemIndex) {
		if(taskId == null || stateId == null) {
			return;
		}
		WorkflowTask task = workflowTaskRepository.findById(taskId).orElse(null);
		WorkflowState state = workflowStateRepository.findById(stateId).orElse(null);
		if(task == null || state == null) {
			return;
		}
		WorkflowStateData stateData = readWorkflowStateData(state, taskId);
		if(itemIndex < 0 || itemIndex > stateData.getTasks().size()) {
			itemIndex = 0;
		}
		stateData.getTasks().add(itemIndex, readWorkflowTaskData(task));
		int sortOrder = 0;
		for(WorkflowTaskData taskData : stateData.getTasks()) {
			taskData.setSortOrder(sortOrder++);
		}
		saveWorkflowStateTasks(stateData);
	}
	
	public WorkflowData getWorkflow(Integer workflowId) {
		return getWorkflow(workflowRepository.findById(workflowId).orElse(null));
	}
	
	public WorkflowData getWorkflow(DocumentData documentData) {
		return getWorkflow(workflowRepository.findByDocumentId(documentData.getId()));
	}
	
	public WorkflowData getWorkflow(Workflow workflow) {
		return workflow != null ? readWorkflowData(workflow) : null;
	}
	
	public void deleteWorkflow(Integer documentId) {
		if(documentId == null) {
			return;
		}
		Workflow workflow = workflowRepository.findByDocumentId(documentId);
		if(workflow == null) {
			return;
		}
		workflowRepository.delete(workflow);
		List<WorkflowState> workflowStates = workflowStateRepository.findByWorkflowIdOrderBySortOrderAsc(workflow.getId());
		workflowStateRepository.deleteAll(workflowStates);
	}
	
	public void deleteWorkflowState(Integer workflowStateId) {
		if(workflowStateId == null) {
			return;
		}
		WorkflowState workflowState = workflowStateRepository.findById(workflowStateId).orElse(null);
		if(workflowState == null) {
			return;
		}
		workflowStateRepository.delete(workflowState);
	}
	
	public void deleteWorkflowTask(Integer workflowTaskId) {
		if(workflowTaskId == null) {
			return;
		}
		WorkflowTask workflowTask = workflowTaskRepository.findById(workflowTaskId).orElse(null);
		if(workflowTask == null) {
			return;
		}
		workflowTaskRepository.delete(workflowTask);
	}
	
	private WorkflowData readWorkflowData(Workflow workflow) {
		WorkflowData workflowData = new WorkflowData();
		workflowData.setId(workflow.getId());
		workflowData.setDocumentId(workflow.getDocumentId());
		List<WorkflowState> workflowStates = workflowStateRepository.findByWorkflowIdOrderBySortOrderAsc(workflow.getId());
		workflowData.setStates(workflowStates.stream()
				.map(ws -> readWorkflowStateData(ws, null))
				.collect(Collectors.toList()));
		return workflowData;
	}
	
	private WorkflowStateData readWorkflowStateData(WorkflowState workflowState, Integer excludeTaskId) {
		WorkflowStateData workflowStateData = new WorkflowStateData();
		workflowStateData.setId(workflowState.getId());
		workflowStateData.setName(workflowState.getName());
		workflowStateData.setSortOrder(workflowState.getSortOrder());
		workflowStateData.setBacklog(workflowState.isBacklog());
		workflowStateData.setCompleted(workflowState.isCompleted());
		List<WorkflowTask> workflowTasks = workflowTaskRepository.findByWorkflowStateIdOrderBySortOrderAsc(workflowState.getId());
		workflowStateData.setTasks(workflowTasks.stream()
				.filter(wt -> wt.getId() != excludeTaskId)
				.map(wt -> readWorkflowTaskData(wt))
				.collect(Collectors.toList()));
		return workflowStateData;
	}
	
	private WorkflowTaskData readWorkflowTaskData(WorkflowTask workflowTask) {
		WorkflowTaskData workflowTaskData = new WorkflowTaskData();
		workflowTaskData.setId(workflowTask.getId());
		workflowTaskData.setWorkflowId(workflowTask.getWorkflowId());
		workflowTaskData.setWorkflowStateId(workflowTask.getWorkflowStateId());
		workflowTaskData.setName(workflowTask.getName());
		workflowTaskData.setDescription(workflowTask.getDescription());
		workflowTaskData.setPriority(workflowTask.getPriority());
		workflowTaskData.setAuthorUser(authService.findUserById(workflowTask.getAuthorUserId()));
		workflowTaskData.setAssignedUser(authService.findUserById(workflowTask.getAssignedUserId()));
		workflowTaskData.setCreationDate(workflowTask.getCreationDate());
		workflowTaskData.setDeadlineDate(workflowTask.getDeadlineDate());
		workflowTaskData.setCompletionDate(workflowTask.getCompletionDate());
		workflowTaskData.setSortOrder(workflowTask.getSortOrder());
		return workflowTaskData;
	}

}
