package com.avanse.model;

import javax.xml.bind.annotation.XmlElement;

public class EmploymentDetails {

	private String income;
	private String occupationCode;
	private String netOrGrossIncomeIndicator;
	private String monthlyOrAnnualIncomeIndicator;
	
	@XmlElement(nillable=true)
	public String getIncome() {
		return income;
	}
	public void setIncome(String income) {
		this.income = income;
	}
	
	@XmlElement(nillable=true)
	public String getOccupationCode() {
		return occupationCode;
	}
	public void setOccupationCode(String occupationCode) {
		this.occupationCode = occupationCode;
	}
	
	@XmlElement(nillable=true)
	public String getNetOrGrossIncomeIndicator() {
		return netOrGrossIncomeIndicator;
	}
	public void setNetOrGrossIncomeIndicator(String netOrGrossIncomeIndicator) {
		this.netOrGrossIncomeIndicator = netOrGrossIncomeIndicator;
	}
	
	@XmlElement(nillable=true)
	public String getMonthlyOrAnnualIncomeIndicator() {
		return monthlyOrAnnualIncomeIndicator;
	}
	public void setMonthlyOrAnnualIncomeIndicator(String monthlyOrAnnualIncomeIndicator) {
		this.monthlyOrAnnualIncomeIndicator = monthlyOrAnnualIncomeIndicator;
	}
	
	
}
