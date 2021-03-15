package com.avanse.model;

import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "response")
public class CibilResponse {

	private HeaderSegment headerSegment;
	private NameSegment nameSegment;
	private IdentificationSegment identificationSegment;
	private TelephoneSegment telephoneSegment;
	private EmailContactSegment emailContactSegment;
	private EmploymentSegment employmentSegment;
	private AccountNumberSegment accNumSegment;
	private ScoreSegment scoreSegment;
	private AddressSegment addressSegment;
	private AccountSegment accountSegment;
	private EnquirySegment enquirySegment;
	private ConsumerDisputeRemarksSegment consumerDisputeRemarksSegment;
	private EndSegment endSegment;
	private ErrorSegment errorSegment;
	private UserReferenceErrorSegment userReferenceErrorSegment;
	private List<Error> errors;
	private String pdfContent;
	private String outputTuef;
	private Long srNo;
	private String remarks;
	private String band;
	private Boolean isMinCibilScore;
	private Boolean is60AboveDpd;
	private Boolean isAccWrittOffOrSettled;
	private Boolean isAccWillfulDefault;
	private Long totalObligation;
	// Added past enquries in cibil response - 12/3/2021
	public String past30daysEnq;
	public String past12MonthsEnq;
	public String past24MonthsEnq;
	public String description;

	public Long getTotalObligation() {
		return totalObligation;
	}

	public void setTotalObligation(Long totalObligation) {
		this.totalObligation = totalObligation;
	}

	public String getBand() {
		return band;
	}

	public void setBand(String band) {
		this.band = band;
	}

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

	public Long getSrNo() {
		return srNo;
	}

	public void setSrNo(Long srNo) {
		this.srNo = srNo;
	}

	public String getPdfContent() {
		return pdfContent;
	}

	public void setPdfContent(String pdfContent) {
		this.pdfContent = pdfContent;
	}

	public List<Error> getErrors() {
		return errors;
	}

	public void setErrors(List<Error> errors) {
		this.errors = errors;
	}

	public UserReferenceErrorSegment getUserReferenceErrorSegment() {
		return userReferenceErrorSegment;
	}

	public void setUserReferenceErrorSegment(UserReferenceErrorSegment userReferenceErrorSegment) {
		this.userReferenceErrorSegment = userReferenceErrorSegment;
	}

	public ErrorSegment getErrorSegment() {
		return errorSegment;
	}

	public void setErrorSegment(ErrorSegment errorSegment) {
		this.errorSegment = errorSegment;
	}

	public AccountSegment getAccountSegment() {
		return accountSegment;
	}

	public void setAccountSegment(AccountSegment accountSegment) {
		this.accountSegment = accountSegment;
	}

	public EndSegment getEndSegment() {
		return endSegment;
	}

	public void setEndSegment(EndSegment endSegment) {
		this.endSegment = endSegment;
	}

	public ConsumerDisputeRemarksSegment getConsumerDisputeRemarksSegment() {
		return consumerDisputeRemarksSegment;
	}

	public void setConsumerDisputeRemarksSegment(ConsumerDisputeRemarksSegment consumerDisputeRemarksSegment) {
		this.consumerDisputeRemarksSegment = consumerDisputeRemarksSegment;
	}

	public EnquirySegment getEnquirySegment() {
		return enquirySegment;
	}

	public void setEnquirySegment(EnquirySegment enquirySegment) {
		this.enquirySegment = enquirySegment;
	}

	public AddressSegment getAddressSegment() {
		return addressSegment;
	}

	public void setAddressSegment(AddressSegment addressSegment) {
		this.addressSegment = addressSegment;
	}

	public ScoreSegment getScoreSegment() {
		return scoreSegment;
	}

	public void setScoreSegment(ScoreSegment scoreSegment) {
		this.scoreSegment = scoreSegment;
	}

	public AccountNumberSegment getAccNumSegment() {
		return accNumSegment;
	}

	public void setAccNumSegment(AccountNumberSegment accNumSegment) {
		this.accNumSegment = accNumSegment;
	}

	public EmploymentSegment getEmploymentSegment() {
		return employmentSegment;
	}

	public void setEmploymentSegment(EmploymentSegment employmentSegment) {
		this.employmentSegment = employmentSegment;
	}

	public EmailContactSegment getEmailContactSegment() {
		return emailContactSegment;
	}

	public void setEmailContactSegment(EmailContactSegment emailContactSegment) {
		this.emailContactSegment = emailContactSegment;
	}

	public TelephoneSegment getTelephoneSegment() {
		return telephoneSegment;
	}

	public void setTelephoneSegment(TelephoneSegment telephoneSegment) {
		this.telephoneSegment = telephoneSegment;
	}

	public HeaderSegment getHeaderSegment() {
		return headerSegment;
	}

	public void setHeaderSegment(HeaderSegment headerSegment) {
		this.headerSegment = headerSegment;
	}

	public NameSegment getNameSegment() {
		return nameSegment;
	}

	public void setNameSegment(NameSegment nameSegment) {
		this.nameSegment = nameSegment;
	}

	public IdentificationSegment getIdentificationSegment() {
		return identificationSegment;
	}

	public void setIdentificationSegment(IdentificationSegment identificationSegment) {
		this.identificationSegment = identificationSegment;
	}

	public String getPast30daysEnq() {
		return past30daysEnq;
	}

	public void setPast30daysEnq(String past30daysEnq) {
		this.past30daysEnq = past30daysEnq;
	}

	public String getPast12MonthsEnq() {
		return past12MonthsEnq;
	}

	public void setPast12MonthsEnq(String past12MonthsEnq) {
		this.past12MonthsEnq = past12MonthsEnq;
	}

	public String getPast24MonthsEnq() {
		return past24MonthsEnq;
	}

	public void setPast24MonthsEnq(String past24MonthsEnq) {
		this.past24MonthsEnq = past24MonthsEnq;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

}
