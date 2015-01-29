package de.ifgi.lod4wfs.core;

/**
 * @author Jim Jones
 */

public class SOLRRecord {

	private String type;
	private String schema;
	private String index;
	private int docs;
	private String name;
	
	
	public SOLRRecord() {
		super();
		// TODO Auto-generated constructor stub
	}

		
	public SOLRRecord(String type, String schema, String index, int docs) {
		super();
		this.type = type;
		this.schema = schema;
		this.index = index;
		this.docs = docs;
	}


	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getSchema() {
		return schema;
	}

	public void setSchema(String schema) {
		this.schema = schema;
	}

	public String getIndex() {
		return index;
	}

	public void setIndex(String index) {
		this.index = index;
	}

	public int getDocs() {
		return docs;
	}

	public void setDocs(int docs) {
		this.docs = docs;
	}
	
	public String getName() {
		return name;
	}


	public void setName(String name) {
		this.name = name;
	}	
}

