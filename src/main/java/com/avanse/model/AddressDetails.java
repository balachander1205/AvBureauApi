package com.avanse.model;

import javax.xml.bind.annotation.XmlElement;

public class AddressDetails {

	private String pinCode;
	private String addCategory;
	private String addLine1;
	private String addLine2;
	private String addLine3;
	private String addLine4;
	private String addLine5;
	private String stateCode;
	private String dateReported;
	
	@XmlElement(nillable=true)
	public String getPinCode() {
		return pinCode;
	}
	public void setPinCode(String pinCode) {
		this.pinCode = pinCode;
	}
	
	@XmlElement(nillable=true)
	public String getAddCategory() {
		return addCategory;
	}
	public void setAddCategory(String addCategory) {
		this.addCategory = addCategory;
	}
	
	@XmlElement(nillable=true)
	public String getAddLine1() {
		return addLine1;
	}
	public void setAddLine1(String addLine1) {
		this.addLine1 = addLine1;
	}
	
	@XmlElement(nillable=true)
	public String getAddLine2() {
		return addLine2;
	}
	public void setAddLine2(String addLine2) {
		this.addLine2 = addLine2;
	}
	
	@XmlElement(nillable=true)
	public String getAddLine3() {
		return addLine3;
	}
	public void setAddLine3(String addLine3) {
		this.addLine3 = addLine3;
	}
	
	@XmlElement(nillable=true)
	public String getAddLine4() {
		return addLine4;
	}
	public void setAddLine4(String addLine4) {
		this.addLine4 = addLine4;
	}
	
	@XmlElement(nillable=true)
	public String getAddLine5() {
		return addLine5;
	}
	public void setAddLine5(String addLine5) {
		this.addLine5 = addLine5;
	}
	
	@XmlElement(nillable=true)
	public String getStateCode() {
		return stateCode;
	}
	public void setStateCode(String stateCode) {
		this.stateCode = stateCode;
	}
	
	@XmlElement(nillable=true)
	public String getDateReported() {
		return dateReported;
	}
	public void setDateReported(String dateReported) {
		this.dateReported = dateReported;
	}
	
	
	
}
