package eu.wauz.wazera.model.repository.tasks;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import eu.wauz.wazera.model.entity.tasks.WorkflowTask;

public interface WorkflowTaskRepository extends CrudRepository<WorkflowTask, Integer> {
	
	List<WorkflowTask> findByWorkflowId(Integer workflowId);
	
	List<WorkflowTask> findByWorkflowStateId(Integer workflowStateId);
	
	@Query("select task from WorkflowTask task where task.workflowStateId is :workflowStateId and (task.completionDate = null or task.completionDate > :date) order by task.sortOrder asc")
	List<WorkflowTask> findByWorkflowStateId(Integer workflowStateId, Date date);
	
	@Query("select task from WorkflowTask task where task.assignedUserId is :assignedUserId and (task.completionDate = null or task.completionDate > :date) order by task.completionDate desc nulls first, task.priority asc, task.deadlineDate asc nulls last")
	List<WorkflowTask> findByAssignedUserId(Integer assignedUserId, Date date);

}
