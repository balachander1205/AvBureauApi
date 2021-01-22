package com.avanse.model;

import java.util.Date;

public class PennantRequest {
	
	private String customerName;
	private String pan;
	private Date dob;
	private String dateProcessed;
	private String memRefNo;
	private String timeProcessed;
	private String scoreName;
	private String scoreCardName;
	private String scoreCardVersion;
	private String score;
	private String cibilSrNo;
	private String cibilResponse;
	public String getCustomerName() {
		return customerName;
	}
	public void setCustomerName(String customerName) {
		this.customerName = customerName;
	}
	public String getPan() {
		return pan;
	}
	public void setPan(String pan) {
		this.pan = pan;
	}
	public Date getDob() {
		return dob;
	}
	public void setDob(Date dob) {
		this.dob = dob;
	}
	public String getDateProcessed() {
		return dateProcessed;
	}
	public void setDateProcessed(String dateProcessed) {
		this.dateProcessed = dateProcessed;
	}
	public String getMemRefNo() {
		return memRefNo;
	}
	public void setMemRefNo(String memRefNo) {
		this.memRefNo = memRefNo;
	}
	public String getTimeProcessed() {
		return timeProcessed;
	}
	public void setTimeProcessed(String timeProcessed) {
		this.timeProcessed = timeProcessed;
	}
	public String getScoreName() {
		return scoreName;
	}
	public void setScoreName(String scoreName) {
		this.scoreName = scoreName;
	}
	public String getScoreCardName() {
		return scoreCardName;
	}
	public void setScoreCardName(String scoreCardName) {
		this.scoreCardName = scoreCardName;
	}
	public String getScoreCardVersion() {
		return scoreCardVersion;
	}
	public void setScoreCardVersion(String scoreCardVersion) {
		this.scoreCardVersion = scoreCardVersion;
	}
	public String getScore() {
		return score;
	}
	public void setScore(String score) {
		this.score = score;
	}
	public String getCibilSrNo() {
		return cibilSrNo;
	}
	public void setCibilSrNo(String cibilSrNo) {
		this.cibilSrNo = cibilSrNo;
	}
	public String getCibilResponse() {
		return cibilResponse;
	}
	public void setCibilResponse(String cibilResponse) {
		this.cibilResponse = cibilResponse;
	}
	public PennantRequest(String customerName, String pan, Date dob, String dateProcessed, String memRefNo,
			String timeProcessed, String scoreName, String scoreCardName, String scoreCardVersion, String score,
			String cibilSrNo, String cibilResponse) {
		super();
		this.customerName = customerName;
		this.pan = pan;
		this.dob = dob;
		this.dateProcessed = dateProcessed;
		this.memRefNo = memRefNo;
		this.timeProcessed = timeProcessed;
		this.scoreName = scoreName;
		this.scoreCardName = scoreCardName;
		this.scoreCardVersion = scoreCardVersion;
		this.score = score;
		this.cibilSrNo = cibilSrNo;
		this.cibilResponse = cibilResponse;
	}

	
}
