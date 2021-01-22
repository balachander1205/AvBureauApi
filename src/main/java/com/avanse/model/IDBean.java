package com.avanse.model;

public class IDBean {
	
	private String idType;
	private String idNumber;
	
	//output response additional columns
	
	private String issueDate;
	private String expirationDate;
	private String enrichedThroughEnquiry;
	
	public String getIssueDate() {
		return issueDate;
	}
	public void setIssueDate(String issueDate) {
		this.issueDate = issueDate;
	}
	public String getExpirationDate() {
		return expirationDate;
	}
	public void setExpirationDate(String expirationDate) {
		this.expirationDate = expirationDate;
	}
	public String getEnrichedThroughEnquiry() {
		return enrichedThroughEnquiry;
	}
	public void setEnrichedThroughEnquiry(String enrichedThroughEnquiry) {
		this.enrichedThroughEnquiry = enrichedThroughEnquiry;
	}
	public String getIdType() {
		return idType;
	}
	public void setIdType(String idType) {
		this.idType = idType;
	}
	public String getIdNumber() {
		return idNumber;
	}
	public void setIdNumber(String idNumber) {
		this.idNumber = idNumber;
	}
	
	public IDBean(String idType,String idNumber){
		this.idType=idType;
		this.idNumber=idNumber;
	}
	
	public IDBean(){
		
	}

}
