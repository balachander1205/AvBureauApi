package com.avanse.model;

import javax.xml.bind.annotation.XmlElement;

public class NameDetails {
	
	private String nameField1;
	private String nameField2;
	private String nameField3;
	private String nameField4;
	private String nameField5;
	
	@XmlElement(nillable=true)
	public String getNameField1() {
		return nameField1;
	}
	public void setNameField1(String nameField1) {
		this.nameField1 = nameField1;
	}
	
	@XmlElement(nillable=true)
	public String getNameField2() {
		return nameField2;
	}
	public void setNameField2(String nameField2) {
		this.nameField2 = nameField2;
	}
	
	@XmlElement(nillable=true)
	public String getNameField3() {
		return nameField3;
	}
	public void setNameField3(String nameField3) {
		this.nameField3 = nameField3;
	}
	
	@XmlElement(nillable=true)
	public String getNameField4() {
		return nameField4;
	}
	public void setNameField4(String nameField4) {
		this.nameField4 = nameField4;
	}
	
	@XmlElement(nillable=true)
	public String getNameField5() {
		return nameField5;
	}
	public void setNameField5(String nameField5) {
		this.nameField5 = nameField5;
	}
	
}
