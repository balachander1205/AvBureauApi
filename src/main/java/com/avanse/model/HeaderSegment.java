package com.avanse.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(value= {"segmentTag","headerBeanString"})
public class HeaderSegment {

	
	private String segmentTag;
	//private String srNo;
	//private String fileNo;
	//private String custID;
	//private String app_pers_api_ref_code;
	private String leadId;
	private String mobileRefNo;
	private String version;
	private String memberRefNumber;
	private String futureUse1;
	private String enqMemberUserId;
	private String enqPassword;
	private String enqPurpose;
	private String enqAmount;
	private String futureUse2;
	private String scoreType;
	private String outputFormat;
	private String responseSize;
	private String inoutMedia;
	private String authMethod;
	private String headerBeanString;
	//private String initiatedBy;
	
	//output response additional columns
	
	private String subReasonCode;
	private String enqControlNumber;
	private String dateProcessed;
	private String timeProcessed;
	
	
	
	/*public String getFileNo() {
		return fileNo;
	}

	public void setFileNo(String fileNo) {
		this.fileNo = fileNo;
	}*/

	/*public String getInitiatedBy() {
		return initiatedBy;
	}

	public void setInitiatedBy(String initiatedBy) {
		this.initiatedBy = initiatedBy;
	}*/

	/*public String getApp_pers_api_ref_code() {
		return app_pers_api_ref_code;
	}

	public void setApp_pers_api_ref_code(String app_pers_api_ref_code) {
		this.app_pers_api_ref_code = app_pers_api_ref_code;
	}*/

	public String getLeadId() {
		return leadId;
	}

	public void setLeadId(String leadId) {
		this.leadId = leadId;
	}

	public String getMobileRefNo() {
		return mobileRefNo;
	}

	public void setMobileRefNo(String mobileRefNo) {
		this.mobileRefNo = mobileRefNo;
	}

	public String getSubReasonCode() {
		return subReasonCode;
	}

	public void setSubReasonCode(String subReasonCode) {
		this.subReasonCode = subReasonCode;
	}

	public String getEnqControlNumber() {
		return enqControlNumber;
	}

	public void setEnqControlNumber(String enqControlNumber) {
		this.enqControlNumber = enqControlNumber;
	}

	public String getDateProcessed() {
		return dateProcessed;
	}

	public void setDateProcessed(String dateProcessed) {
		this.dateProcessed = dateProcessed;
	}

	public String getTimeProcessed() {
		return timeProcessed;
	}

	public void setTimeProcessed(String timeProcessed) {
		this.timeProcessed = timeProcessed;
	}

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
	}

	public String getFileNo() {
		return fileNo;
	}

	public void setFileNo(String fileNo) {
		this.fileNo = fileNo;
	}*/

	/*public int getHeaderSegmentLength(){
		return headerBeanString.length();
	}*/
	
	
	public String getHeaderBeanString() {
		return headerBeanString;
	}
	
	public String validateHeaderSegment()
	{
		if(this.getEnqAmount()!=null && !this.getEnqAmount().equals("")){
			if(this.getEnqAmount().matches("-?\\d+(\\.\\d+)?"))
			{
				return "";
				//return true;
			}else{
				return this.getEnqAmount()+" is not a number";
				//return false;
			}
		}
		return "Enq Amount is null or blank";
	}
	public void setHeaderBeanString() {
		this.headerBeanString = this.getSegmentTag()+this.getVersion()+this.getMemberRefNumber()+this.getFutureUse1()+this.getEnqMemberUserId()
			+this.getEnqPassword()+this.getEnqPurpose()+this.getEnqAmount()+this.getFutureUse2()+this.getScoreType()+this.getOutputFormat()
			+this.getResponseSize()+this.getInoutMedia()+this.getAuthMethod();
	}
	public String getFutureUse1() {
		return futureUse1;
	}
	public void setFutureUse1(String futureUse1) {
		this.futureUse1 = futureUse1;
	}
	public String getFutureUse2() {
		return futureUse2;
	}
	public void setFutureUse2(String futureUse2) {
		this.futureUse2 = futureUse2;
	}
	public String getSegmentTag() {
		return segmentTag;
	}
	public void setSegmentTag(String segmentTag) {
		this.segmentTag = segmentTag;
	}
	public String getVersion() {
		return version;
	}
	public void setVersion(String version) {
		this.version = version;
	}
	public String getMemberRefNumber() {
		return memberRefNumber;
	}
	public void setMemberRefNumber(String memberRefNumber) {
		this.memberRefNumber = memberRefNumber;
	}
	
	public String getEnqMemberUserId() {
		return enqMemberUserId;
	}
	public void setEnqMemberUserId(String enqMemberUserId) {
		this.enqMemberUserId = enqMemberUserId;
	}
	public String getEnqPassword() {
		return enqPassword;
	}
	public void setEnqPassword(String enqPassword) {
		this.enqPassword = enqPassword;
	}
	public String getEnqPurpose() {
		return enqPurpose;
	}
	public void setEnqPurpose(String enqPurpose) {
		this.enqPurpose = enqPurpose;
	}
	public String getEnqAmount() {
		return enqAmount;
	}
	public void setEnqAmount(String enqAmount) {
		this.enqAmount = enqAmount;
	}
	public String getScoreType() {
		return scoreType;
	}
	public void setScoreType(String scoreType) {
		this.scoreType = scoreType;
	}
	public String getOutputFormat() {
		return outputFormat;
	}
	public void setOutputFormat(String outputFormat) {
		this.outputFormat = outputFormat;
	}
	public String getResponseSize() {
		return responseSize;
	}
	public void setResponseSize(String responseSize) {
		this.responseSize = responseSize;
	}
	public String getInoutMedia() {
		return inoutMedia;
	}
	public void setInoutMedia(String inoutMedia) {
		this.inoutMedia = inoutMedia;
	}
	public String getAuthMethod() {
		return authMethod;
	}
	public void setAuthMethod(String authMethod) {
		this.authMethod = authMethod;
	}

}
