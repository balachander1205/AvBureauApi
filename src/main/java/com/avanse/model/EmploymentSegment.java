package com.avanse.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(value= {"segmentTag"})
public class EmploymentSegment {
	
	private String segmentTag;
	private String accountType;
	private String dateReportedAndCertified;
	private String occupationCode;
	private String income;
	private String netOrGrossIncomeIndicator;
	private String monthlyOrAnnualIncomeIndicator;
	private String dateOfEntryForErrorCode;
	private String errorCode;
	private String dateOfEntryForCibilRemarksCode;
	private String cibilRemarksCode;
	private String dateOfEntryForErrorOrDisputeRemarksCode;
	private String errorOrDisputeRemarksCode1;
	private String errorOrDisputeRemarksCode2;
	
	public String getSegmentTag() {
		return segmentTag;
	}
	public String getDateOfEntryForErrorOrDisputeRemarksCode() {
		return dateOfEntryForErrorOrDisputeRemarksCode;
	}
	public void setDateOfEntryForErrorOrDisputeRemarksCode(
			String dateOfEntryForErrorOrDisputeRemarksCode) {
		this.dateOfEntryForErrorOrDisputeRemarksCode = dateOfEntryForErrorOrDisputeRemarksCode;
	}
	public String getErrorOrDisputeRemarksCode1() {
		return errorOrDisputeRemarksCode1;
	}
	public void setErrorOrDisputeRemarksCode1(String errorOrDisputeRemarksCode1) {
		this.errorOrDisputeRemarksCode1 = errorOrDisputeRemarksCode1;
	}
	public String getErrorOrDisputeRemarksCode2() {
		return errorOrDisputeRemarksCode2;
	}
	public void setErrorOrDisputeRemarksCode2(String errorOrDisputeRemarksCode2) {
		this.errorOrDisputeRemarksCode2 = errorOrDisputeRemarksCode2;
	}
	public void setSegmentTag(String segmentTag) {
		this.segmentTag = segmentTag;
	}
	public String getAccountType() {
		return accountType;
	}
	public void setAccountType(String accountType) {
		this.accountType = accountType;
	}
	public String getDateReportedAndCertified() {
		return dateReportedAndCertified;
	}
	public void setDateReportedAndCertified(String dateReportedAndCertified) {
		this.dateReportedAndCertified = dateReportedAndCertified;
	}
	public String getOccupationCode() {
		return occupationCode;
	}
	public void setOccupationCode(String occupationCode) {
		this.occupationCode = occupationCode;
	}
	public String getIncome() {
		return income;
	}
	public void setIncome(String income) {
		this.income = income;
	}
	public String getNetOrGrossIncomeIndicator() {
		return netOrGrossIncomeIndicator;
	}
	public void setNetOrGrossIncomeIndicator(String netOrGrossIncomeIndicator) {
		this.netOrGrossIncomeIndicator = netOrGrossIncomeIndicator;
	}
	public String getMonthlyOrAnnualIncomeIndicator() {
		return monthlyOrAnnualIncomeIndicator;
	}
	public void setMonthlyOrAnnualIncomeIndicator(
			String monthlyOrAnnualIncomeIndicator) {
		this.monthlyOrAnnualIncomeIndicator = monthlyOrAnnualIncomeIndicator;
	}
	public String getDateOfEntryForErrorCode() {
		return dateOfEntryForErrorCode;
	}
	public void setDateOfEntryForErrorCode(String dateOfEntryForErrorCode) {
		this.dateOfEntryForErrorCode = dateOfEntryForErrorCode;
	}
	public String getErrorCode() {
		return errorCode;
	}
	public void setErrorCode(String errorCode) {
		this.errorCode = errorCode;
	}
	public String getDateOfEntryForCibilRemarksCode() {
		return dateOfEntryForCibilRemarksCode;
	}
	public void setDateOfEntryForCibilRemarksCode(
			String dateOfEntryForCibilRemarksCode) {
		this.dateOfEntryForCibilRemarksCode = dateOfEntryForCibilRemarksCode;
	}
	public String getCibilRemarksCode() {
		return cibilRemarksCode;
	}
	public void setCibilRemarksCode(String cibilRemarksCode) {
		this.cibilRemarksCode = cibilRemarksCode;
	}

	
}
