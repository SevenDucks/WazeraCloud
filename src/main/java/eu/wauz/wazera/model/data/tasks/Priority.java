package eu.wauz.wazera.model.data.tasks;

public enum Priority {
	
	CRITICAL("1 - Critical", "fa fa-bolt", "lightsalmon"),
	
	VERY_HIGH("2 - Very High", "fa fa-angle-double-up", "orange"),
	
	HIGH("3 - High", "fa fa-angle-up", "moccasin"),
	
	NORMAL("4 - Normal", "fa fa-asterisk", "forestgreen"),
	
	LOW("5 - Low", "fa fa-angle-down", "lightseagreen"),
	
	VERY_LOW("6 - Very Low", "fa fa-angle-double-down", "skyblue"),
	
	INSIGNIFICANT("7 - Insignificant", "fa fa-minus", "plum");
	
	private final String name;
	
	private final String icon;
	
	private final String color;
	
	private Priority(String name, String icon, String color) {
		this.name = name;
		this.icon = icon;
		this.color = color;
	}

	public String getName() {
		return name;
	}

	public String getIcon() {
		return icon;
	}

	public String getColor() {
		return color;
	}

}
