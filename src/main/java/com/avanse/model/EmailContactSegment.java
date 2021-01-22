package com.avanse.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(value= {"segmentTag"})
public class EmailContactSegment {

private String segmentTag;
private List<String> emailList;
public String getSegmentTag() {
	return segmentTag;
}
public void setSegmentTag(String segmentTag) {
	this.segmentTag = segmentTag;
}
public List<String> getEmailList() {
	return emailList;
}
public void setEmailList(List<String> emailList) {
	this.emailList = emailList;
}



}
