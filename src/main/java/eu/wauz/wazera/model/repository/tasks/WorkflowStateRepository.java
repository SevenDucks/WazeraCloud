package eu.wauz.wazera.model.repository.tasks;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import eu.wauz.wazera.model.entity.tasks.WorkflowState;

public interface WorkflowStateRepository extends CrudRepository<WorkflowState, Integer> {
	
	List<WorkflowState> findByWorkflowIdOrderBySortOrderAsc(Integer workflowId);

}
