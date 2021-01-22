package com.avanse.model;

public class TelephoneBean {
	private String telephoneNumber;
	private String telephoneExt;
	private String telephoneType;
	
	//output response additional columns
	private String enrichedThroughEnquiry;
	
	public String getEnrichedThroughEnquiry() {
		return enrichedThroughEnquiry;
	}
	public void setEnrichedThroughEnquiry(String enrichedThroughEnquiry) {
		this.enrichedThroughEnquiry = enrichedThroughEnquiry;
	}
	public String getTelephoneNumber() {
		return telephoneNumber;
	}
	public void setTelephoneNumber(String telephoneNumber) {
		this.telephoneNumber = telephoneNumber;
	}
	public String getTelephoneExt() {
		return telephoneExt;
	}
	public void setTelephoneExt(String telephoneExt) {
		this.telephoneExt = telephoneExt;
	}
	public String getTelephoneType() {
		return telephoneType;
	}
	public void setTelephoneType(String telephoneType) {
		this.telephoneType = telephoneType;
	}
	public TelephoneBean(String telephoneNumber, String telephoneExt,
			String telephoneType) {
		this.telephoneNumber = telephoneNumber;
		this.telephoneExt = telephoneExt;
		this.telephoneType = telephoneType;
	}
	
	public TelephoneBean()
	{
		
	}
	

	
	
}
