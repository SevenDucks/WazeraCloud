package eu.wauz.wazera.controller.auth;

import java.io.Serializable;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import eu.wauz.wazera.WazeraTool;

@Controller
@Scope("session")
public class SessionController implements Serializable {
	
	private static final long serialVersionUID = 6410079263923624297L;
	
	private WazeraTool wazeraTool;
	
	@PostConstruct
	private void init() {
		wazeraTool = new WazeraTool();
	}
	
	public String getVersion() {
		return getClass().getPackage().getImplementationVersion();
	}
	
	public boolean isChrome() {
		return StringUtils.contains(wazeraTool.getBrowser(), "Chrome");
	}

}
