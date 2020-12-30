package eu.wauz.wazera.service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import eu.wauz.wazera.model.data.docs.DocumentData;
import eu.wauz.wazera.model.data.tasks.WorkflowData;
import eu.wauz.wazera.model.data.tasks.WorkflowStateData;
import eu.wauz.wazera.model.entity.tasks.Workflow;
import eu.wauz.wazera.model.entity.tasks.WorkflowState;
import eu.wauz.wazera.model.repository.tasks.WorkflowRepository;
import eu.wauz.wazera.model.repository.tasks.WorkflowStateRepository;
import eu.wauz.wazera.model.repository.tasks.WorkflowTaskRepository;

@Service
@Scope("singleton")
public class TasksDataService {
	
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
		workflowStateDatas.add(plannedState);
		
		WorkflowStateData inProgressState = new WorkflowStateData();
		inProgressState.setName("In Progress");
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
		for(WorkflowStateData workflowStateData : workflowData.getStates()) {
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
	
	public WorkflowData getWorkflow(DocumentData documentData) {
		Workflow workflow = workflowRepository.findByDocumentId(documentData.getId());
		return getWorkflow(workflow);
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
	
	private WorkflowData readWorkflowData(Workflow workflow) {
		WorkflowData workflowData = new WorkflowData();
		workflowData.setId(workflow.getId());
		List<WorkflowState> workflowStates = workflowStateRepository.findByWorkflowIdOrderBySortOrderAsc(workflow.getId());
		workflowData.setStates(workflowStates.stream()
				.map(ws -> readWorkflowStateData(ws))
				.collect(Collectors.toList()));
		return workflowData;
	}
	
	private WorkflowStateData readWorkflowStateData(WorkflowState workflowState) {
		WorkflowStateData workflowStateData = new WorkflowStateData();
		workflowStateData.setId(workflowState.getId());
		workflowStateData.setName(workflowState.getName());
		workflowStateData.setSortOrder(workflowState.getSortOrder());
		workflowStateData.setBacklog(workflowState.isBacklog());
		workflowStateData.setCompleted(workflowState.isCompleted());
		return workflowStateData;
	}

}
