package de.ifgi.lod4wfs.core;

public class GeographicLayer {

	private String name;
	private String title;
	private String keywords;
	private String featureAbstract;
	private String defaultCRS;
	private String lowerCorner;
	private String upperCorner;
	
	public GeographicLayer() {
		super();

	}

	public GeographicLayer(String name, String title, String keywords,
			String featureAbstract, String defaultCRS, String lowerCorner,
			String upperCorner) {
		super();
		this.name = name;
		this.title = title;
		this.keywords = keywords;
		this.featureAbstract = featureAbstract;
		this.defaultCRS = defaultCRS;
		this.lowerCorner = lowerCorner;
		this.upperCorner = upperCorner;
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
	
	
}



