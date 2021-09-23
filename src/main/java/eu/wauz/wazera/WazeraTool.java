package eu.wauz.wazera;

import javax.faces.application.FacesMessage;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;

import org.apache.commons.lang3.StringUtils;
import org.primefaces.PrimeFaces;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class WazeraTool {
	
	private static final char[] INVALID_CHARS = new char[] {'|', '/', '\\', ':', '*', '?', '"', '\'', '<', '>'};

	public void checkForValidFileName(String fileName) throws Exception {
		if(StringUtils.containsAny(fileName, INVALID_CHARS)) {
			throw new Exception("The input contained invalid characters!");
		}
	}
	
	public String getUsername() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		return authentication != null ? authentication.getName() : null;
	}
	
	public String getBrowser() {
		ExternalContext externalContext = FacesContext.getCurrentInstance().getExternalContext();
		return externalContext.getRequestHeaderMap().get("User-Agent");
	}
	
	public void setTitle(String title) {
		title = StringUtils.replaceChars(title, String.valueOf(INVALID_CHARS), null);
		PrimeFaces.current().executeScript("document.title='" + title + " - Wazera Cloud'");
	}
	
    public void showInfoMessage(String infoMessage) {
		final FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Info: ", infoMessage);
		FacesContext.getCurrentInstance().addMessage(null, msg);
	}
    
	public void showErrorMessage(String errorMessage) {
		final FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error: ", errorMessage);
		FacesContext.getCurrentInstance().addMessage(null, msg);
	}

}
