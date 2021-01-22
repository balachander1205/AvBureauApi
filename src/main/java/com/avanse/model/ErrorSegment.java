package com.avanse.model;

public class ErrorSegment {

	private String dateProcessed;
	private String timeProcessed;
	private UserReferenceErrorSegment userReferenceErrorSegment;
	
	public UserReferenceErrorSegment getUserReferenceErrorSegment() {
		return userReferenceErrorSegment;
	}
	public void setUserReferenceErrorSegment(
			UserReferenceErrorSegment userReferenceErrorSegment) {
		this.userReferenceErrorSegment = userReferenceErrorSegment;
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
	
	
}
