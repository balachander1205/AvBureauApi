package com.avanse.model;

public class FFRRequest {

	private String partnerCode;
	private String apiAccessKey;
	private String applicationId;
	private String customerSource;
	private String sendEmail;
	
	//private String srNo;
	//private String custID;
	private String name;
	private String emailId;
	private String cibilString;
	
	
	
	
	/*public String getSrNo() {
		return srNo;
	}
	public void setSrNo(String srNo) {
		this.srNo = srNo;
	}
	
	public String getCustID() {
		return custID;
	}
	public void setCustID(String custID) {
		this.custID = custID;
	}*/
	public String getPartnerCode() {
		return partnerCode;
	}
	public void setPartnerCode(String partnerCode) {
		this.partnerCode = partnerCode;
	}
	public String getApiAccessKey() {
		return apiAccessKey;
	}
	public void setApiAccessKey(String apiAccessKey) {
		this.apiAccessKey = apiAccessKey;
	}
	public String getApplicationId() {
		return applicationId;
	}
	public void setApplicationId(String applicationId) {
		this.applicationId = applicationId.replace("/", "");
	}
	public String getCustomerSource() {
		return customerSource;
	}
	public void setCustomerSource(String customerSource) {
		this.customerSource = customerSource;
	}
	public String getSendEmail() {
		return sendEmail;
	}
	public void setSendEmail(String sendEmail) {
		this.sendEmail = sendEmail;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getEmailId() {
		return emailId;
	}
	public void setEmailId(String emailId) {
		this.emailId = emailId;
	}
	public String getCibilString() {
		return cibilString;
	}
	public void setCibilString(String cibilString) {
		this.cibilString = cibilString;
	}
	
	
}
