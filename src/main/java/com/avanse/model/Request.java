package com.avanse.model;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="request")
public class Request {
	/*
	
	private Request request;

	public Request getRequest() {
		return request;
	}

	public void setRequest(Request request) {
		this.request = request;
	}
*/
	//private String fileNo;
	//private String initiated_by;
	//private String app_pers_api_ref_code;
	private String srNo;
	private String mobileRefNo;
	private String leadId;
	private String enqAmount;
	private String fName;
	private String mName;
	private String lName;
	private String dob;
	private String gender;
	private String panNo;
	private String passport;
	private String voterID;
	private String drivLicNo;
	private String adharID;
	private String mobile;
	private String homePhone;
	private String officePhone;
	private String resiAddress;
	private String landmark;
	private String city;
	private String state;
	private String pin;
	private String country;
	private String stateCode;
	private String resiCode;
	private String inputTuef;
	private String loanType;
	private String productType;
	
	public String getLoanType() {
		return loanType;
	}
	public void setLoanType(String loanType) {
		this.loanType = loanType;
	}
	public String getProductType() {
		return productType;
	}
	public void setProductType(String productType) {
		this.productType = productType;
	}
	public String getSrNo() {
		return srNo;
	}
	public void setSrNo(String srNo) {
		this.srNo = srNo;
	}
	/*public String getFileNo() {
		return fileNo;
	}
	public void setFileNo(String fileNo) {
		this.fileNo = fileNo;
	}*/
	public String getCountry() {
		return country;
	}
	public String getMobileRefNo() {
		return mobileRefNo;
	}
	public void setMobileRefNo(String mobileRefNo) {
		this.mobileRefNo = mobileRefNo;
	}
	public String getLeadId() {
		return leadId;
	}
	public void setLeadId(String leadId) {
		this.leadId = leadId;
	}
	public void setCountry(String country) {
		this.country = country;
	}
	public String getResiAddress() {
		return resiAddress;
	}
	public void setResiAddress(String resiAddress) {
		this.resiAddress = resiAddress;
	}
	public String getEnqAmount() {
		return enqAmount;
	}
	public void setEnqAmount(String enqAmount) {
		this.enqAmount = enqAmount;
	}
	public String getfName() {
		return fName;
	}
	public void setfName(String fName) {
		this.fName = fName;
	}
	public String getmName() {
		return mName;
	}
	public void setmName(String mName) {
		this.mName = mName;
	}
	public String getlName() {
		return lName;
	}
	public void setlName(String lName) {
		this.lName = lName;
	}
	public String getDob() {
		return dob;
	}
	public void setDob(String dob) {
		this.dob = dob;
	}
	public String getGender() {
		return gender;
	}
	public void setGender(String gender) {
		this.gender = gender;
	}
	public String getPanNo() {
		return panNo;
	}
	public void setPanNo(String panNo) {
		this.panNo = panNo;
	}
	public String getPassport() {
		return passport;
	}
	public void setPassport(String passport) {
		this.passport = passport;
	}
	public String getVoterID() {
		return voterID;
	}
	public void setVoterID(String voterID) {
		this.voterID = voterID;
	}
	public String getDrivLicNo() {
		return drivLicNo;
	}
	public void setDrivLicNo(String drivLicNo) {
		this.drivLicNo = drivLicNo;
	}
	public String getAdharID() {
		return adharID;
	}
	public void setAdharID(String adharID) {
		this.adharID = adharID;
	}
	public String getMobile() {
		return mobile;
	}
	public void setMobile(String mobile) {
		this.mobile = mobile;
	}
	public String getHomePhone() {
		return homePhone;
	}
	public void setHomePhone(String homePhone) {
		this.homePhone = homePhone;
	}
	public String getOfficePhone() {
		return officePhone;
	}
	public void setOfficePhone(String officePhone) {
		this.officePhone = officePhone;
	}
	public String getLandmark() {
		return landmark;
	}
	public void setLandmark(String landmark) {
		this.landmark = landmark;
	}
	public String getCity() {
		return city;
	}
	public void setCity(String city) {
		this.city = city;
	}
	public String getState() {
		return state;
	}
	public void setState(String state) {
		this.state = state;
	}
	public String getPin() {
		return pin;
	}
	public void setPin(String pin) {
		this.pin = pin;
	}
	public String getStateCode() {
		return stateCode;
	}
	public void setStateCode(String stateCode) {
		this.stateCode = stateCode;
	}
	public String getResiCode() {
		return resiCode;
	}
	public void setResiCode(String resiCode) {
		this.resiCode = resiCode;
	}
	public String getInputTuef() {
		return inputTuef;
	}
	public void setInputTuef(String inputTuef) {
		this.inputTuef = inputTuef;
	}
	
}