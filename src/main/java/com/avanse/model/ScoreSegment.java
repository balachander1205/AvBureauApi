package com.avanse.model;

import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="SCOREDETAILS")
public class ScoreSegment {
	
	private List<ScoreBean> scoreBeanList;

	public List<ScoreBean> getScoreBeanList() {
		return scoreBeanList;
	}

	public void setScoreBeanList(List<ScoreBean> scoreBeanList) {
		this.scoreBeanList = scoreBeanList;
	}

	
}
