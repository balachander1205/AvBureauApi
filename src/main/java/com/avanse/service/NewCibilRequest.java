package com.avanse.service;

import com.avanse.model.AccountNumberSegment;
import com.avanse.model.AddressSegment;
import com.avanse.model.EndSegment;
import com.avanse.model.HeaderSegment;
import com.avanse.model.IdentificationSegment;
import com.avanse.model.NameSegment;
import com.avanse.model.TelephoneSegment;

public class NewCibilRequest {
	
	private HeaderSegment headerSegment;
	private NameSegment nameSegment;
	private IdentificationSegment identificationSegment;
	private TelephoneSegment telephoneSegment;
	private AddressSegment addressSegment;
	private AccountNumberSegment accNumSegment;
	private EndSegment endSegment;
	
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
	public TelephoneSegment getTelephoneSegment() {
		return telephoneSegment;
	}
	public void setTelephoneSegment(TelephoneSegment telephoneSegment) {
		this.telephoneSegment = telephoneSegment;
	}
	public AddressSegment getAddressSegment() {
		return addressSegment;
	}
	public void setAddressSegment(AddressSegment addressSegment) {
		this.addressSegment = addressSegment;
	}
	public AccountNumberSegment getAccNumSegment() {
		return accNumSegment;
	}
	public void setAccNumSegment(AccountNumberSegment accNumSegment) {
		this.accNumSegment = accNumSegment;
	}
	public EndSegment getEndSegment() {
		return endSegment;
	}
	public void setEndSegment(EndSegment endSegment) {
		this.endSegment = endSegment;
	}
}
