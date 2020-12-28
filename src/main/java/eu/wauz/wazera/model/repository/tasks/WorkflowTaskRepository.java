package eu.wauz.wazera.model.repository.tasks;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import eu.wauz.wazera.model.entity.tasks.WorkflowTask;

public interface WorkflowTaskRepository extends CrudRepository<WorkflowTask, Integer> {
	
	List<WorkflowTask> findByWorkflowIdOrderBySortOrderAsc(Integer workflowId);
	
	List<WorkflowTask> findByWorkflowStateIdOrderBySortOrderAsc(Integer workflowStateId);

}
