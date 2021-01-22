package com.avanse.model;

public class PennantRequest_bkp {
	
	private String finReference;
	private String docCategory;
	private String custDocTitle;
	private String custDocIssuedCountry;
	private String custDocIssuedOn;
	private String custDocExpDate;
	private String docPurpose;
	private String custDocIssuedAuth;
	private String docName;
	private String docFormat;
	private String docContent;
	
	public PennantRequest_bkp(String finReference, String docCategory, String custDocTitle, String custDocIssuedCountry,
			String custDocIssuedOn, String custDocExpDate, String docPurpose, String custDocIssuedAuth, String docName,
			String docFormat, String docContent) {
		super();
		this.finReference = finReference;
		this.docCategory = docCategory;
		this.custDocTitle = custDocTitle;
		this.custDocIssuedCountry = custDocIssuedCountry;
		this.custDocIssuedOn = custDocIssuedOn;
		this.custDocExpDate = custDocExpDate;
		this.docPurpose = docPurpose;
		this.custDocIssuedAuth = custDocIssuedAuth;
		this.docName = docName;
		this.docFormat = docFormat;
		this.docContent = docContent;
	}
	public String getFinReference() {
		return finReference;
	}
	public void setFinReference(String finReference) {
		this.finReference = finReference;
	}
	public String getDocCategory() {
		return docCategory;
	}
	public void setDocCategory(String docCategory) {
		this.docCategory = docCategory;
	}
	public String getCustDocTitle() {
		return custDocTitle;
	}
	public void setCustDocTitle(String custDocTitle) {
		this.custDocTitle = custDocTitle;
	}
	public String getCustDocIssuedCountry() {
		return custDocIssuedCountry;
	}
	public void setCustDocIssuedCountry(String custDocIssuedCountry) {
		this.custDocIssuedCountry = custDocIssuedCountry;
	}
	public String getCustDocIssuedOn() {
		return custDocIssuedOn;
	}
	public void setCustDocIssuedOn(String custDocIssuedOn) {
		this.custDocIssuedOn = custDocIssuedOn;
	}
	public String getCustDocExpDate() {
		return custDocExpDate;
	}
	public void setCustDocExpDate(String custDocExpDate) {
		this.custDocExpDate = custDocExpDate;
	}
	public String getDocPurpose() {
		return docPurpose;
	}
	public void setDocPurpose(String docPurpose) {
		this.docPurpose = docPurpose;
	}
	public String getCustDocIssuedAuth() {
		return custDocIssuedAuth;
	}
	public void setCustDocIssuedAuth(String custDocIssuedAuth) {
		this.custDocIssuedAuth = custDocIssuedAuth;
	}
	public String getDocName() {
		return docName;
	}
	public void setDocName(String docName) {
		this.docName = docName;
	}
	public String getDocFormat() {
		return docFormat;
	}
	public void setDocFormat(String docFormat) {
		this.docFormat = docFormat;
	}
	public String getDocContent() {
		return docContent;
	}
	public void setDocContent(String docContent) {
		this.docContent = docContent;
	}

	
}
