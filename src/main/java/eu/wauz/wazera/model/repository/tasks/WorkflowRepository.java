package eu.wauz.wazera.model.repository.tasks;

import org.springframework.data.repository.CrudRepository;

import eu.wauz.wazera.model.entity.tasks.Workflow;

public interface WorkflowRepository extends CrudRepository<Workflow, Integer> {
	
	Workflow findByDocumentId(Integer documentId);

}
