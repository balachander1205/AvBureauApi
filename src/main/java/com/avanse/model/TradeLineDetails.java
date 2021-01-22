package com.avanse.model;

import javax.xml.bind.annotation.XmlElement;

public class TradeLineDetails {

	private String accountNumber;
	private String ownershipIndicator;
	private String accountType;
	private String dateOpenedOrDisbursed;
	private String creditLimit;
	private String cashLimit;
	private String highCreditOrSanctionedAmount;
	private String paymentHistory1;
	private String paymentHistory2;
	private String paymentHistoryStartDate;
	private String paymentHistoryEndDate;
	private String currentBalance;
	private String amountOverdue;
	private String dateReportedAndCertified;
	private String dateClosed;
	private String dateOfLastPayment;
	private String suitFiledOrWilfulDefault;
	private String writtenOffAndSettledStatus;
	private String settlementAmount;
	private String valueOfCollateral;
	private String typeOfCollateral;
	private String writtenOfAmountTotal;
	private String writtenOfAmountPrincipal;
	private String rateOfInterest;
	private String repaymentTenure;
	private String paymentFrequency;
	private String emiAmount;
	private String actualPaymentAmount;
	
	@XmlElement(nillable=true)
	public String getAccountNumber() {
		return accountNumber;
	}
	public void setAccountNumber(String accountNumber) {
		this.accountNumber = accountNumber;
	}
	
	@XmlElement(nillable=true)
	public String getOwnershipIndicator() {
		return ownershipIndicator;
	}
	public void setOwnershipIndicator(String ownershipIndicator) {
		this.ownershipIndicator = ownershipIndicator;
	}
	
	@XmlElement(nillable=true)
	public String getAccountType() {
		return accountType;
	}
	public void setAccountType(String accountType) {
		this.accountType = accountType;
	}
	
	@XmlElement(nillable=true)
	public String getDateOpenedOrDisbursed() {
		return dateOpenedOrDisbursed;
	}
	public void setDateOpenedOrDisbursed(String dateOpenedOrDisbursed) {
		this.dateOpenedOrDisbursed = dateOpenedOrDisbursed;
	}
	
	@XmlElement(nillable=true)
	public String getCreditLimit() {
		return creditLimit;
	}
	public void setCreditLimit(String creditLimit) {
		this.creditLimit = creditLimit;
	}
	
	@XmlElement(nillable=true)
	public String getCashLimit() {
		return cashLimit;
	}
	public void setCashLimit(String cashLimit) {
		this.cashLimit = cashLimit;
	}
	
	@XmlElement(nillable=true)
	public String getHighCreditOrSanctionedAmount() {
		return highCreditOrSanctionedAmount;
	}
	public void setHighCreditOrSanctionedAmount(String highCreditOrSanctionedAmount) {
		this.highCreditOrSanctionedAmount = highCreditOrSanctionedAmount;
	}
	
	@XmlElement(nillable=true)
	public String getPaymentHistory1() {
		return paymentHistory1;
	}
	public void setPaymentHistory1(String paymentHistory1) {
		this.paymentHistory1 = paymentHistory1;
	}
	
	@XmlElement(nillable=true)
	public String getPaymentHistory2() {
		return paymentHistory2;
	}
	public void setPaymentHistory2(String paymentHistory2) {
		this.paymentHistory2 = paymentHistory2;
	}
	
	@XmlElement(nillable=true)
	public String getPaymentHistoryStartDate() {
		return paymentHistoryStartDate;
	}
	public void setPaymentHistoryStartDate(String paymentHistoryStartDate) {
		this.paymentHistoryStartDate = paymentHistoryStartDate;
	}
	
	@XmlElement(nillable=true)
	public String getPaymentHistoryEndDate() {
		return paymentHistoryEndDate;
	}
	public void setPaymentHistoryEndDate(String paymentHistoryEndDate) {
		this.paymentHistoryEndDate = paymentHistoryEndDate;
	}
	
	@XmlElement(nillable=true)
	public String getCurrentBalance() {
		return currentBalance;
	}
	public void setCurrentBalance(String currentBalance) {
		this.currentBalance = currentBalance;
	}
	
	@XmlElement(nillable=true)
	public String getAmountOverdue() {
		return amountOverdue;
	}
	public void setAmountOverdue(String amountOverdue) {
		this.amountOverdue = amountOverdue;
	}
	
	@XmlElement(nillable=true)
	public String getDateReportedAndCertified() {
		return dateReportedAndCertified;
	}
	public void setDateReportedAndCertified(String dateReportedAndCertified) {
		this.dateReportedAndCertified = dateReportedAndCertified;
	}
	
	@XmlElement(nillable=true)
	public String getDateClosed() {
		return dateClosed;
	}
	public void setDateClosed(String dateClosed) {
		this.dateClosed = dateClosed;
	}
	
	@XmlElement(nillable=true)
	public String getDateOfLastPayment() {
		return dateOfLastPayment;
	}
	public void setDateOfLastPayment(String dateOfLastPayment) {
		this.dateOfLastPayment = dateOfLastPayment;
	}
	
	@XmlElement(nillable=true)
	public String getSuitFiledOrWilfulDefault() {
		return suitFiledOrWilfulDefault;
	}
	public void setSuitFiledOrWilfulDefault(String suitFiledOrWilfulDefault) {
		this.suitFiledOrWilfulDefault = suitFiledOrWilfulDefault;
	}
	
	@XmlElement(nillable=true)
	public String getWrittenOffAndSettledStatus() {
		return writtenOffAndSettledStatus;
	}
	public void setWrittenOffAndSettledStatus(String writtenOffAndSettledStatus) {
		this.writtenOffAndSettledStatus = writtenOffAndSettledStatus;
	}
	
	@XmlElement(nillable=true)
	public String getSettlementAmount() {
		return settlementAmount;
	}
	public void setSettlementAmount(String settlementAmount) {
		this.settlementAmount = settlementAmount;
	}
	
	@XmlElement(nillable=true)
	public String getValueOfCollateral() {
		return valueOfCollateral;
	}
	public void setValueOfCollateral(String valueOfCollateral) {
		this.valueOfCollateral = valueOfCollateral;
	}
	
	@XmlElement(nillable=true)
	public String getTypeOfCollateral() {
		return typeOfCollateral;
	}
	public void setTypeOfCollateral(String typeOfCollateral) {
		this.typeOfCollateral = typeOfCollateral;
	}
	
	@XmlElement(nillable=true)
	public String getWrittenOfAmountTotal() {
		return writtenOfAmountTotal;
	}
	public void setWrittenOfAmountTotal(String writtenOfAmountTotal) {
		this.writtenOfAmountTotal = writtenOfAmountTotal;
	}
	
	@XmlElement(nillable=true)
	public String getWrittenOfAmountPrincipal() {
		return writtenOfAmountPrincipal;
	}
	public void setWrittenOfAmountPrincipal(String writtenOfAmountPrincipal) {
		this.writtenOfAmountPrincipal = writtenOfAmountPrincipal;
	}
	
	@XmlElement(nillable=true)
	public String getRateOfInterest() {
		return rateOfInterest;
	}
	public void setRateOfInterest(String rateOfInterest) {
		this.rateOfInterest = rateOfInterest;
	}
	
	@XmlElement(nillable=true)
	public String getRepaymentTenure() {
		return repaymentTenure;
	}
	public void setRepaymentTenure(String repaymentTenure) {
		this.repaymentTenure = repaymentTenure;
	}
	
	@XmlElement(nillable=true)
	public String getPaymentFrequency() {
		return paymentFrequency;
	}
	public void setPaymentFrequency(String paymentFrequency) {
		this.paymentFrequency = paymentFrequency;
	}
	
	@XmlElement(nillable=true)
	public String getEmiAmount() {
		return emiAmount;
	}
	public void setEmiAmount(String emiAmount) {
		this.emiAmount = emiAmount;
	}
	
	@XmlElement(nillable=true)
	public String getActualPaymentAmount() {
		return actualPaymentAmount;
	}
	public void setActualPaymentAmount(String actualPaymentAmount) {
		this.actualPaymentAmount = actualPaymentAmount;
	}
	
	
}
