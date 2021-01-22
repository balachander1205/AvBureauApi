package com.avanse.model;

import javax.xml.bind.annotation.XmlElement;

public class ScoreDetails {
	private String score;
	private String scoreDate;
	
	@XmlElement(nillable=true)
	public String getScore() {
		return score;
	}
	public void setScore(String score) {
		this.score = score;
	}
	@XmlElement(nillable=true)
	public String getScoreDate() {
		return scoreDate;
	}
	public void setScoreDate(String scoreDate) {
		this.scoreDate = scoreDate;
	}

}
