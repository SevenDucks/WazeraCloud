package eu.wauz.wazera.controller.auth;

import java.io.Serializable;
import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.faces.context.FacesContext;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import eu.wauz.wazera.WazeraTool;
import eu.wauz.wazera.model.data.auth.Permission;
import eu.wauz.wazera.model.data.auth.UserData;
import eu.wauz.wazera.model.data.auth.UserRoleHandle;
import eu.wauz.wazera.service.AuthDataService;

@Controller
@Scope("view")
public class UserController implements Serializable {

	private static final long serialVersionUID = 1721467034890035898L;

	@Autowired
	private AuthDataService authService;

	private List<UserRoleHandle> userRoleHandles;
	
	private List<UserData> users;

	private UserData user;

	private String passwordInput1;

	private String passwordInput2;
	
	private WazeraTool wazeraTool;

	@PostConstruct
	private void init() {
		wazeraTool = new WazeraTool();

		String username = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("username");
		if(StringUtils.isNotBlank(username)) {
			user = authService.findUserByName(username);
		}
		if(user == null) {
			this.user = new UserData();
			user.setId(0);
			user.setUsername("???");
		}
	}

	public void changePassword() {
		if(StringUtils.isBlank(passwordInput1)) {
			wazeraTool.showInfoMessage("Password cannot be empty!");
			return;
		}
		if(!StringUtils.equals(passwordInput1, passwordInput2)) {
			wazeraTool.showInfoMessage("Passwords do not match!");
			return;
		}
		user.setPassword(passwordInput1);
		authService.saveUser(user);
		wazeraTool.showInfoMessage("Password was successfully changed!");
	}

	public void createNewUser() {
		String validationMessage = authService.validate(user);
		if(validationMessage.equals("Success")) {
			authService.saveUser(user);
			wazeraTool.showInfoMessage("User <" + user.getUsername() + "> was successfully created!");
			users = null;
		}
		else {
			wazeraTool.showInfoMessage(validationMessage);
		}
	}
	
	public void deleteUser(UserData user) {
		this.user = user;
		deleteUser();
	}

	public void deleteUser() {
		authService.deleteUser(user.getId());
		wazeraTool.showInfoMessage("User <" + user.getUsername() + "> was successfully deleted!");
		setNewUser();
		users = null;
	}
	
	public List<UserData> getUsersAlphabetically() {
		return getUsers().stream()
				.sorted((u1, u2) -> u1.getUsername().compareTo(u2.getUsername()))
				.collect(Collectors.toList());
	}

	public List<UserData> getUsers() {
		if(users == null) {
			users = authService.findAllUsers();
		}
		return users;
	}

	public void setUsers(List<UserData> users) {
		this.users = users;
	}

	public UserData getUser() {
		return user;
	}

	public void setUser(UserData user) {
		this.user = user;
	}

	public void setNewUser() {
		user = new UserData();
	}

	public String getPasswordInput1() {
		return passwordInput1;
	}

	public void setPasswordInput1(String passwordInput1) {
		this.passwordInput1 = passwordInput1;
	}

	public String getPasswordInput2() {
		return passwordInput2;
	}

	public void setPasswordInput2(String passwordInput2) {
		this.passwordInput2 = passwordInput2;
	}

	public List<UserRoleHandle> getUserRoleHandles() {
		if(userRoleHandles == null) {
			userRoleHandles = authService.getUserRoles(user.getId());
		}
		return userRoleHandles;
	}

	public void setUserRoleHandles(List<UserRoleHandle> userRoleHandles) {
		this.userRoleHandles = userRoleHandles;
	}
	
	public void updateRoles() {
		authService.updateUserRoles(user.getId(), userRoleHandles);
		wazeraTool.showInfoMessage("The Roles of '" + user.getUsername() + "' were successfully updated!");
	}

	public boolean isAuthAdmin() {
		return authService.hasPermission(wazeraTool.getUsername(), Permission.ADMINISTRATE_ACCOUNTS.getId());
	}

	public boolean mayChangePassword() {
		return wazeraTool.getUsername().equals(user.getUsername()) || isAuthAdmin();
	}

}
