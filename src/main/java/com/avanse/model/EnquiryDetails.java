package com.avanse.model;

import javax.xml.bind.annotation.XmlElement;

public class EnquiryDetails {

	private String enquiryDate;
	private String purpose;
	private String amount;
	
	@XmlElement(nillable=true)
	public String getEnquiryDate() {
		return enquiryDate;
	}
	public void setEnquiryDate(String enquiryDate) {
		this.enquiryDate = enquiryDate;
	}
	
	@XmlElement(nillable=true)
	public String getPurpose() {
		return purpose;
	}
	public void setPurpose(String purpose) {
		this.purpose = purpose;
	}
	
	@XmlElement(nillable=true)
	public String getAmount() {
		return amount;
	}
	public void setAmount(String amount) {
		this.amount = amount;
	}
	
}
