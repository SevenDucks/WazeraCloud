package eu.wauz.wazera.model.data.auth;

public enum Permission {
	
	ADMINISTRATE_ACCOUNTS(100001, PermissionScope.GLOBAL),
	
	EDIT_FOLDERS(200002, PermissionScope.GROUP),
	
	DELETE_FOLDERS(200003, PermissionScope.GROUP),
	
	VIEW_DOCUMENTS(201001, PermissionScope.GROUP),
	
	EDIT_DOCUMENTS(201002, PermissionScope.GROUP),
	
	DELETE_DOCUMENTS(201003, PermissionScope.GROUP),
	
	VIEW_WORKFLOWS(202001, PermissionScope.GROUP),
	
	EDIT_WORKFLOWS(202002, PermissionScope.GROUP),
	
	DELETE_WORKFLOWS(202003, PermissionScope.GROUP),
	
	VIEW_TASKS(202101, PermissionScope.GROUP),
	
	EDIT_TASKS(202102, PermissionScope.GROUP),
	
	DELETE_TASKS(202103, PermissionScope.GROUP),
	
	VIEW_ASSIGNED_TASKS(202201, PermissionScope.GROUP),
	
	EDIT_ASSIGNED_TASKS(202202, PermissionScope.GROUP),
	
	DELETE_ASSIGNED_TASKS(202203, PermissionScope.GROUP);
	
	private final int id;
	
	private final PermissionScope scope;
	
	private Permission(int id, PermissionScope scope) {
		this.id = id;
		this.scope = scope;
	}

	public int getId() {
		return id;
	}
	
	public PermissionScope getScope() {
		return scope;
	}
	
	@Override
	public String toString() {
		return this.name();
	}

}
