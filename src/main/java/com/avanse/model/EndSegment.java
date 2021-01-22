package com.avanse.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(value= {"segmentTag"})
public class EndSegment {

	private String segmentTag;
	private int lenOfRecord;
	private String endChars;
	
	private String endSegmentString;
	
	public EndSegment() {
		this.segmentTag="ES05";
		this.endChars="0102**";
	}
	

	public String getSegmentTag() {
		return segmentTag;
	}

	public void setSegmentTag(String segmentTag) {
		this.segmentTag = segmentTag;
	}

	public int getLenOfRecord() {
		return lenOfRecord;
	}

	public void setLenOfRecord(int lenOfRecord) {
		this.lenOfRecord = lenOfRecord;
	}

	public String getEndChars() {
		return endChars;
	}

	public void setEndChars(String endChars) {
		this.endChars = endChars;
	}

	public String getEndSegmentString() {
		return endSegmentString;
	}

	public void setEndSegmentString(int lenOfRecord) {
		//this.setLenOfRecord(lenOfRecord);
		this.endSegmentString = this.getSegmentTag()+String.format("%05d",lenOfRecord)+this.getEndChars();
	}

}
