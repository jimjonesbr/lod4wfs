package de.ifgi.lod4wfs.core;

public class NameSpace {

	private String name;
	private String prefix;
	private String description;
	
	public NameSpace() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	public NameSpace(String name, String prefix, String description) {
		super();
		this.name = name;
		this.prefix = prefix;
		this.description = description;
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getPrefix() {
		return prefix;
	}
	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	
	
}
