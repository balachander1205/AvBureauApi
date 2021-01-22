package com.avanse.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(value= {"segmentTag"})
public class NameSegment {

	private String segmentTag="PN03N01";
	private String fName;
	private String mName;
	private String lName;
	private String dob;
	private String gender;
	
	//output response additional columns
	
	private String nameField4;
	private String nameField5;
	private String dateOfEntryForErrorCode;
	private String errorSegmentTag;
	private String errorCode;
	private String dateOfEntryForCibilRemarksCode;
	private String cibilRemarksCode;
	private String dateOfEntryForErrorOrDisputeRemarksCode;
	private String errorOrDisputeRemarksCode1;
	private String errorOrDisputeRemarksCode2;
	
	public String getNameField4() {
		return nameField4;
	}
	public void setNameField4(String nameField4) {
		this.nameField4 = nameField4;
	}
	public String getNameField5() {
		return nameField5;
	}
	public void setNameField5(String nameField5) {
		this.nameField5 = nameField5;
	}
	public String getDateOfEntryForErrorCode() {
		return dateOfEntryForErrorCode;
	}
	public void setDateOfEntryForErrorCode(String dateOfEntryForErrorCode) {
		this.dateOfEntryForErrorCode = dateOfEntryForErrorCode;
	}
	public String getErrorSegmentTag() {
		return errorSegmentTag;
	}
	public void setErrorSegmentTag(String errorSegmentTag) {
		this.errorSegmentTag = errorSegmentTag;
	}
	public String getErrorCode() {
		return errorCode;
	}
	public void setErrorCode(String errorCode) {
		this.errorCode = errorCode;
	}
	public String getDateOfEntryForCibilRemarksCode() {
		return dateOfEntryForCibilRemarksCode;
	}
	public void setDateOfEntryForCibilRemarksCode(
			String dateOfEntryForCibilRemarksCode) {
		this.dateOfEntryForCibilRemarksCode = dateOfEntryForCibilRemarksCode;
	}
	public String getCibilRemarksCode() {
		return cibilRemarksCode;
	}
	public void setCibilRemarksCode(String cibilRemarksCode) {
		this.cibilRemarksCode = cibilRemarksCode;
	}
	public String getDateOfEntryForErrorOrDisputeRemarksCode() {
		return dateOfEntryForErrorOrDisputeRemarksCode;
	}
	public void setDateOfEntryForErrorOrDisputeRemarksCode(
			String dateOfEntryForErrorOrDisputeRemarksCode) {
		this.dateOfEntryForErrorOrDisputeRemarksCode = dateOfEntryForErrorOrDisputeRemarksCode;
	}
	public String getErrorOrDisputeRemarksCode1() {
		return errorOrDisputeRemarksCode1;
	}
	public void setErrorOrDisputeRemarksCode1(String errorOrDisputeRemarksCode1) {
		this.errorOrDisputeRemarksCode1 = errorOrDisputeRemarksCode1;
	}
	public String getErrorOrDisputeRemarksCode2() {
		return errorOrDisputeRemarksCode2;
	}
	public void setErrorOrDisputeRemarksCode2(String errorOrDisputeRemarksCode2) {
		this.errorOrDisputeRemarksCode2 = errorOrDisputeRemarksCode2;
	}
	public String getfName() {
		return fName;
	}
	public void setfName(String fName) {
		this.fName = fName;
	}
	public String getmName() {
		return mName;
	}
	public void setmName(String mName) {
		this.mName = mName;
	}
	public String getlName() {
		return lName;
	}
	public void setlName(String lName) {
		this.lName = lName;
	}
	public String getSegmentTag() {
		return segmentTag;
	}
	public void setSegmentTag(String segmentTag) {
		this.segmentTag = segmentTag;
	}
	
	
	public String getDob() {
		return dob;
	}
	public void setDob(String dob) {
		this.dob = dob;
	}
	public String getGender() {
		return gender;
	}
	public void setGender(String gender) {
		this.gender = gender;
	}
	
	public String validateNameSegment()
	{
		StringBuilder sb=new StringBuilder("");
		
		if(this.getfName()==null || this.getfName().equals(""))
		{
			sb.append("FirstName is  null or blank\n");
		}
		
		if(this.getfName()!=null && this.getfName().length()>26)
		{
			sb.append("FirstName length is greater than 26 characters\n");
		}
		
		if(this.getmName()!=null && this.getmName().length()>26)
		{
			sb.append("MiddleName length is greater than 26 characters\n");
		}
		
		if(this.getlName()!=null && this.getlName().length()>26)
		{
			sb.append("LastName length is greater than 26 characters\n");
		}
		
		if(this.getDob()==null || this.getDob().equals(""))
		{
			sb.append("Date of Birth is  null or blank\n");
		}
			
		if(this.getGender()==null || this.getGender().equals(""))
		{
			sb.append("Gender is  null or blank\n");
		}
		
		return sb.toString();
		
		
		
	}
	
	public StringBuilder generateNameSegment(){
		StringBuilder nameSegment=new StringBuilder();
		
		nameSegment.append(this.getSegmentTag());
		
		/*String name = this.getConsumerName();
	    List<String> consumerNameFieldList=new ArrayList<String>();
		
		int len=26;
		int k=(name.length()/len);
		int m=0;
		for(int i=0;i<(k+1);i++)
		  {
			if(i!=0) m=m+len;
			if(name.length()>=m+len)
			   {
				consumerNameFieldList.add(name.substring(m,m+len));
			   }
			else
			   {
				   consumerNameFieldList.add(name.substring(m,name.length()));
			   }
		  }
		
		String names="";
		int i=1;
		for(String splitName:consumerNameFieldList){
			if(i<6){
				names+=String.format("%02d", i)+String.format("%02d", splitName.length())+splitName;
				i++;
			}
		}
		
		nameSegment.append(names);*/
		
		int i=1;
		
		String names="";
		
		if(this.getfName()!=null && !this.getfName().equals(""))
		{
			names+=String.format("%02d", i)+String.format("%02d", this.getfName().length())+this.getfName();
			i++;
		}
		
		if(this.getmName()!=null && !this.getmName().equals(""))
		{
			names+=String.format("%02d", i)+String.format("%02d", this.getmName().length())+this.getmName();
			i++;
		}
		
		if(this.getlName()!=null && !this.getlName().equals(""))
		{
			names+=String.format("%02d", i)+String.format("%02d", this.getlName().length())+this.getlName();
			i++;
		}
		
		/*if(this.getNameField4()!=null && this.getNameField4().length()<=26)
		{
			names+=String.format("%02d", i)+String.format("%02d", this.getNameField4().length())+this.getNameField4();
			i++;
		}
		
		if(this.getNameField5()!=null && this.getNameField5().length()<=26)
		{
			names+=String.format("%02d", i)+String.format("%02d", this.getNameField5().length())+this.getNameField5();
			i++;
		}*/
		
		if(names!=null){
			nameSegment.append(names.trim());
		}
		
		if(this.getDob()!=null){
			nameSegment.append("0708"+this.getDob());
		}
		
		if(this.getGender()!=null){
			nameSegment.append("0801"+this.getGender());
		}
		
		return nameSegment;
	}
		
}
