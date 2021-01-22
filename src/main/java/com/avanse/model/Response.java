package com.avanse.model;

import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="response")
public class Response {

	private Request request;
	private List<ScoreDetails> scoreDetails;
	private List<AddressDetails> addressDetails;
	private NameDetails nameDetails;
	private EmploymentDetails employmentDetails;
	private List<EnquiryDetails> enquiryDetails;
	private List<TradeLineDetails> tradeLineDetails;
	private List<Error> errorDetails;
	private String pdfContent;
	private String outputTuef;
	private Long srNo;
	private String remarks;
	private Boolean isMinCibilScore;
	private Boolean is60AboveDpd;
	private Boolean isAccWrittOffOrSettled;
	private Boolean isAccWillfulDefault;
	
	public Boolean getIsMinCibilScore() {
		return isMinCibilScore;
	}
	public void setIsMinCibilScore(Boolean isMinCibilScore) {
		this.isMinCibilScore = isMinCibilScore;
	}
	public Boolean getIs60AboveDpd() {
		return is60AboveDpd;
	}
	public void setIs60AboveDpd(Boolean is60AboveDpd) {
		this.is60AboveDpd = is60AboveDpd;
	}
	public Boolean getIsAccWrittOffOrSettled() {
		return isAccWrittOffOrSettled;
	}
	public void setIsAccWrittOffOrSettled(Boolean isAccWrittOffOrSettled) {
		this.isAccWrittOffOrSettled = isAccWrittOffOrSettled;
	}
	public Boolean getIsAccWillfulDefault() {
		return isAccWillfulDefault;
	}
	public void setIsAccWillfulDefault(Boolean isAccWillfulDefault) {
		this.isAccWillfulDefault = isAccWillfulDefault;
	}
	public String getRemarks() {
		return remarks;
	}
	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}
	public String getOutputTuef() {
		return outputTuef;
	}
	public void setOutputTuef(String outputTuef) {
		this.outputTuef = outputTuef;
	}
	public String getPdfContent() {
		return pdfContent;
	}
	public void setPdfContent(String pdfContent) {
		this.pdfContent = pdfContent;
	}
	public Long getSrNo() {
		return srNo;
	}
	public void setSrNo(Long srNo) {
		this.srNo = srNo;
	}
	public List<Error> getErrorDetails() {
		return errorDetails;
	}
	public void setErrorDetails(List<Error> errorDetails) {
		this.errorDetails = errorDetails;
	}
	public List<TradeLineDetails> getTradeLineDetails() {
		return tradeLineDetails;
	}
	public void setTradeLineDetails(List<TradeLineDetails> tradeLineDetails) {
		this.tradeLineDetails = tradeLineDetails;
	}
	public Request getRequest() {
		return request;
	}
	public void setRequest(Request request) {
		this.request = request;
	}
	public List<ScoreDetails> getScoreDetails() {
		return scoreDetails;
	}
	public void setScoreDetails(List<ScoreDetails> scoreDetails) {
		this.scoreDetails = scoreDetails;
	}
	public List<AddressDetails> getAddressDetails() {
		return addressDetails;
	}
	public void setAddressDetails(List<AddressDetails> addressDetails) {
		this.addressDetails = addressDetails;
	}
	
	public NameDetails getNameDetails() {
		return nameDetails;
	}
	public void setNameDetails(NameDetails nameDetails) {
		this.nameDetails = nameDetails;
	}
	
	public EmploymentDetails getEmploymentDetails() {
		return employmentDetails;
	}
	public void setEmploymentDetails(EmploymentDetails employmentDetails) {
		this.employmentDetails = employmentDetails;
	}
	public List<EnquiryDetails> getEnquiryDetails() {
		return enquiryDetails;
	}
	public void setEnquiryDetails(List<EnquiryDetails> enquiryDetails) {
		this.enquiryDetails = enquiryDetails;
	}
	
}
