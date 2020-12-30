package eu.wauz.wazera.model.repository.auth;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;

import eu.wauz.wazera.model.entity.auth.RolePermissionLink;

@Transactional
public interface RolePermissionLinkRepository extends CrudRepository<RolePermissionLink, Integer> {

	List<RolePermissionLink> findByPermissionId(Integer permissionId);

	RolePermissionLink findByRoleIdAndPermissionId(Integer roleId, Integer permissionId);
	
	void deleteByRoleId(Integer roleId);

}
