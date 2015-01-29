package de.ifgi.lod4wfs.core;

/**
 * @author Jim Jones
 */

public class Triple {

	private String subject;
	private String predicate;
	private String object;
	private String objectDataType;
	

	public Triple() {
		super();
	}


	public Triple(String subject, String predicate, String object,
			String objectDataType) {
		super();
		this.subject = subject;
		this.predicate = predicate;
		this.object = object;
		this.objectDataType = objectDataType;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getPredicate() {
		return predicate;
	}

	public void setPredicate(String predicate) {
		this.predicate = predicate;
	}

	public String getObject() {
		return object;
	}

	public void setObject(String object) {
		this.object = object;
	}

	public String getObjectDataType() {
		return objectDataType;
	}

	public void setObjectDataType(String objectDataType) {
		this.objectDataType = objectDataType;
	}
	
	
	
}
