package com.avanse.model;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AddressBean {

	private String addLine1;
	private String addLine2;
	private String addLine3;
	private String addLine4;
	private String addLine5;
	private String stateCode;
	private String pinCode;
	private String addCategory;
	private String residenceCode;
	private String fullAddress;
	
	//output response additional columns
	
	private String dateReported;
	private String memberShortName;
	private String enrichedThroughEnquiry;
	
	public String getDateReported() {
		return dateReported;
	}
	public void setDateReported(String dateReported) {
		this.dateReported = dateReported;
	}
	public String getMemberShortName() {
		return memberShortName;
	}
	public void setMemberShortName(String memberShortName) {
		this.memberShortName = memberShortName;
	}
	public String getEnrichedThroughEnquiry() {
		return enrichedThroughEnquiry;
	}
	public void setEnrichedThroughEnquiry(String enrichedThroughEnquiry) {
		this.enrichedThroughEnquiry = enrichedThroughEnquiry;
	}
	public String getFullAddress() {
		return fullAddress;
	}
	public void setFullAddress(String fullAddress) {
		this.fullAddress = fullAddress;
		assignAddressLineFields();
	}
	public String getAddLine1() {
		return addLine1;
	}
	public void setAddLine1(String addLine1) {
		this.addLine1 = addLine1;
	}
	public String getAddLine2() {
		return addLine2;
	}
	public void setAddLine2(String addLine2) {
		this.addLine2 = addLine2;
	}
	public String getAddLine3() {
		return addLine3;
	}
	public void setAddLine3(String addLine3) {
		this.addLine3 = addLine3;
	}
	public String getAddLine4() {
		return addLine4;
	}
	public void setAddLine4(String addLine4) {
		this.addLine4 = addLine4;
	}
	public String getAddLine5() {
		return addLine5;
	}
	public void setAddLine5(String addLine5) {
		this.addLine5 = addLine5;
	}
	public String getStateCode() {
		return stateCode;
	}
	public void setStateCode(String stateCode) {
		this.stateCode = stateCode;
	}
	public String getPinCode() {
		return pinCode;
	}
	public void setPinCode(String pinCode) {
		this.pinCode = pinCode;
	}
	public String getAddCategory() {
		return addCategory;
	}
	public void setAddCategory(String addCategory) {
		this.addCategory = addCategory;
	}
	public String getResidenceCode() {
		return residenceCode;
	}
	public void setResidenceCode(String residenceCode) {
		this.residenceCode = residenceCode;
	}
	
	public static List<String> splitString(String msg, int lineSize) {
        List<String> res = new ArrayList<>();

        Pattern p = Pattern.compile("\\b.{1," + (lineSize-1) + "}\\b\\W?");
        Matcher m = p.matcher(msg);
        
	while(m.find()) {
                res.add(m.group());
        }
        return res;
    }
	
	public void assignAddressLineFields(){
		
		if(this.getFullAddress()!=null && this.getFullAddress()!="")
		{
			List<String> addLinesList=splitString(this.getFullAddress(),40);
			
			int j=1;
			for(String addLine:addLinesList)
			{
				switch(j)
				{
				case 1:
					this.setAddLine1(addLine.trim());
					break;
					
				case 2:
					this.setAddLine2(addLine.trim());
					break;
					
				case 3:
					this.setAddLine3(addLine.trim());
					break;
				
				case 4:
					this.setAddLine4(addLine.trim());
					break;	
					
				case 5:
					this.setAddLine5(addLine.trim());
					break;	
				}
			
				j++;
			}
		}
	}
	
}
