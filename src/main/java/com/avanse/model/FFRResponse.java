package com.avanse.model;

import java.util.List;

public class FFRResponse {
private String clientId;
private String insightsURL;
private String pdfContent;
private List<Error> errorList;
public String getClientId() {
	return clientId;
}
public void setClientId(String clientId) {
	this.clientId = clientId;
}
public String getInsightsURL() {
	return insightsURL;
}
public void setInsightsURL(String insightsURL) {
	this.insightsURL = insightsURL;
}
public String getPdfContent() {
	return pdfContent;
}
public void setPdfContent(String pdfContent) {
	this.pdfContent = pdfContent;
}
public List<Error> getErrorList() {
	return errorList;
}
public void setErrorList(List<Error> errorList) {
	this.errorList = errorList;
}

public void getResponseAsString()
{
	
	for(Error e:this.getErrorList())
	{

	}
	
}

}
