package de.ifgi.lod4wfs.core;


public class WFSFeature {

	private String name;
	private String title;
	private String keywords;
	private String featureAbstract;
	private String defaultCRS;
	private String lowerCorner;
	private String upperCorner;
	
	private boolean dynamic;
	private String query;
	private String geometryVariable;
	private String endpoint;
	private String fileName;
	
	public WFSFeature() {
		super();

	}


	public WFSFeature(String name, String title, String keywords,
			String featureAbstract, String defaultCRS, String lowerCorner,
			String upperCorner, boolean dynamic, String query,
			String geometryVariable, String endpoint, String fileName) {
		super();
		this.name = name;
		this.title = title;
		this.keywords = keywords;
		this.featureAbstract = featureAbstract;
		this.defaultCRS = defaultCRS;
		this.lowerCorner = lowerCorner;
		this.upperCorner = upperCorner;
		this.dynamic = dynamic;
		this.query = query;
		this.geometryVariable = geometryVariable;
		this.endpoint = endpoint;
		this.fileName = fileName;
	}


	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getKeywords() {
		return keywords;
	}

	public void setKeywords(String keywords) {
		this.keywords = keywords;
	}

	public String getFeatureAbstract() {
		return featureAbstract;
	}

	public void setFeatureAbstract(String featureAbstract) {
		this.featureAbstract = featureAbstract;
	}

	public String getCRS() {
		return defaultCRS;
	}

	public void setDefaultCRS(String defaultCRS) {
		this.defaultCRS = defaultCRS;
	}

	public String getLowerCorner() {
		return lowerCorner;
	}

	public void setLowerCorner(String lowerCorner) {
		this.lowerCorner = lowerCorner;
	}

	public String getUpperCorner() {
		return upperCorner;
	}

	public void setUpperCorner(String upperCorner) {
		this.upperCorner = upperCorner;
	}

	public boolean isFDAFeature() {
		return dynamic;
	}

	public void setAsFDA(boolean dynamic) {
		this.dynamic = dynamic;
	}

	public String getQuery() {
		return query;
	}

	public void setQuery(String query) {
		this.query = query;
	}

	public String getGeometryVariable() {
		return geometryVariable;
	}

	public void setGeometryVariable(String geometryVariable) {
		this.geometryVariable = geometryVariable;
	}

	public String getEndpoint() {
		return endpoint;
	}

	public void setEndpoint(String endpoint) {
		this.endpoint = endpoint;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	
	
}



