package eu.wauz.wazera.model.data.auth;

public enum Permission {
	
	ADMINISTRATE_ACCOUNTS(100001, PermissionScope.GLOBAL),
	
	VIEW_DOCUMENTS(201001, PermissionScope.GROUP),
	
	EDIT_DOCUMENTS(201002, PermissionScope.GROUP),
	
	DELETE_DOCUMENTS(201003, PermissionScope.GROUP),
	
	VIEW_TASKS(202001, PermissionScope.GROUP),
	
	EDIT_TASKS(202002, PermissionScope.GROUP),
	
	DELETE_TASKS(202003, PermissionScope.GROUP),
	
	VIEW_ASSIGNED_TASKS(203001, PermissionScope.GLOBAL),
	
	EDIT_ASSIGNED_TASKS(203002, PermissionScope.GLOBAL),
	
	DELETE_ASSIGNED_TASKS(203003, PermissionScope.GLOBAL);
	
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
