package com.avanse.model;

import java.util.List;

public class UserReferenceErrorSegment {
	
	private String memberReferenceNumber;
	private String invalidVersion;
	private String invalidFieldLength;
	private String invalidTotalLength;
	private String invalidEnquiryPurpose;
	private String invalidEnquiryAmount;
	private String invalidEnquiryMemberUserIDOrPassword;
	private String requiredEnquirySegmentMissing;
	private List<String> invalidEnquiryData;
	private String cibilSystemError;
	private String invalidSegmentTag;
	private String invalidSegmentOrder;
	private String invalidFieldTagOrder;
	private List<String> missingRequiredField;
	private String requestedResponseSizeExceeded;
	private String invalidInputOrOutputMedia;
	
	public List<String> getInvalidEnquiryData() {
		return invalidEnquiryData;
	}
	public void setInvalidEnquiryData(List<String> invalidEnquiryData) {
		this.invalidEnquiryData = invalidEnquiryData;
	}
	public List<String> getMissingRequiredField() {
		return missingRequiredField;
	}
	public void setMissingRequiredField(List<String> missingRequiredField) {
		this.missingRequiredField = missingRequiredField;
	}
	public String getMemberReferenceNumber() {
		return memberReferenceNumber;
	}
	public void setMemberReferenceNumber(String memberReferenceNumber) {
		this.memberReferenceNumber = memberReferenceNumber;
	}
	public String getInvalidVersion() {
		return invalidVersion;
	}
	public void setInvalidVersion(String invalidVersion) {
		this.invalidVersion = invalidVersion;
	}
	public String getInvalidFieldLength() {
		return invalidFieldLength;
	}
	public void setInvalidFieldLength(String invalidFieldLength) {
		this.invalidFieldLength = invalidFieldLength;
	}
	public String getInvalidTotalLength() {
		return invalidTotalLength;
	}
	public void setInvalidTotalLength(String invalidTotalLength) {
		this.invalidTotalLength = invalidTotalLength;
	}
	public String getInvalidEnquiryPurpose() {
		return invalidEnquiryPurpose;
	}
	public void setInvalidEnquiryPurpose(String invalidEnquiryPurpose) {
		this.invalidEnquiryPurpose = invalidEnquiryPurpose;
	}
	public String getInvalidEnquiryAmount() {
		return invalidEnquiryAmount;
	}
	public void setInvalidEnquiryAmount(String invalidEnquiryAmount) {
		this.invalidEnquiryAmount = invalidEnquiryAmount;
	}
	public String getInvalidEnquiryMemberUserIDOrPassword() {
		return invalidEnquiryMemberUserIDOrPassword;
	}
	public void setInvalidEnquiryMemberUserIDOrPassword(
			String invalidEnquiryMemberUserIDOrPassword) {
		this.invalidEnquiryMemberUserIDOrPassword = invalidEnquiryMemberUserIDOrPassword;
	}
	public String getRequiredEnquirySegmentMissing() {
		return requiredEnquirySegmentMissing;
	}
	public void setRequiredEnquirySegmentMissing(
			String requiredEnquirySegmentMissing) {
		this.requiredEnquirySegmentMissing = requiredEnquirySegmentMissing;
	}
	public String getCibilSystemError() {
		return cibilSystemError;
	}
	public void setCibilSystemError(String cibilSystemError) {
		this.cibilSystemError = cibilSystemError;
	}
	public String getInvalidSegmentTag() {
		return invalidSegmentTag;
	}
	public void setInvalidSegmentTag(String invalidSegmentTag) {
		this.invalidSegmentTag = invalidSegmentTag;
	}
	public String getInvalidSegmentOrder() {
		return invalidSegmentOrder;
	}
	public void setInvalidSegmentOrder(String invalidSegmentOrder) {
		this.invalidSegmentOrder = invalidSegmentOrder;
	}
	public String getInvalidFieldTagOrder() {
		return invalidFieldTagOrder;
	}
	public void setInvalidFieldTagOrder(String invalidFieldTagOrder) {
		this.invalidFieldTagOrder = invalidFieldTagOrder;
	}
	public String getRequestedResponseSizeExceeded() {
		return requestedResponseSizeExceeded;
	}
	public void setRequestedResponseSizeExceeded(
			String requestedResponseSizeExceeded) {
		this.requestedResponseSizeExceeded = requestedResponseSizeExceeded;
	}
	public String getInvalidInputOrOutputMedia() {
		return invalidInputOrOutputMedia;
	}
	public void setInvalidInputOrOutputMedia(String invalidInputOrOutputMedia) {
		this.invalidInputOrOutputMedia = invalidInputOrOutputMedia;
	}
	
}
