package eu.wauz.wazera.model.data.docs;

import org.apache.commons.lang3.StringUtils;

public enum DocType {
	
	ROOT("rootNode", "fa fa-th-large"),
	
	DIRECTORY("directoryNode", "fa fa-folder-open"),
	
	DOCUMENT("documentNode", "fa fa-file"),
	
	WORKFLOW("workflowNode", "fa fa-share-alt");
	
	private final String id;
	
	private final String icon;
	
	private DocType(String id, String icon) {
		this.id = id;
		this.icon = icon;
	}

	public final String getId() {
		return id;
	}

	public final String getIcon() {
		return icon;
	}
	
	public static DocType getFromString(String typeString) {
		if(StringUtils.isBlank(typeString)) {
			return DocType.DOCUMENT;
		}
		return valueOf(typeString.toUpperCase().replace("NODE", ""));
	}

}
