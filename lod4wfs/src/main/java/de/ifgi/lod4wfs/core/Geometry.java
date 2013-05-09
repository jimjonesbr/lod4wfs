package de.ifgi.lod4wfs.core;

public class Geometry {

	private String name;
	private String WKT;

	public Geometry() {
		super();
		// TODO Auto-generated constructor stub
	}

	public Geometry(String name, String wKT) {
		super();
		this.name = name;
		WKT = wKT;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getWKT() {
		return WKT;
	}

	public void setWKT(String wKT) {
		WKT = wKT;
	}


	
	
}
