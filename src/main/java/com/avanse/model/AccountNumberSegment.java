package com.avanse.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(value= {"segmentTag"})
public class AccountNumberSegment {
	private String segmentTag;
	private List<AccountNumberBean> accNumBeanList;
	
	public String getSegmentTag() {
		return segmentTag;
	}
	public void setSegmentTag(String segmentTag) {
		this.segmentTag = segmentTag;
	}
	public List<AccountNumberBean> getAccNumBeanList() {
		return accNumBeanList;
	}
	public void setAccNumBeanList(List<AccountNumberBean> accNumBeanList) {
		this.accNumBeanList = accNumBeanList;
	}
	
	public StringBuilder generateAccountNumberSegment(){
		StringBuilder accNumSegment=new StringBuilder();
	
		int i=1;
		
		for(AccountNumberBean bean:accNumBeanList)
		{
			accNumSegment.append("PI03I"+String.format("%02d", i));
			
			if(bean.getAccountNumber()!=null && bean.getAccountNumber()!=""){
				accNumSegment.append("01"+String.format("%02d",bean.getAccountNumber().length())+bean.getAccountNumber());
			}		
			
			i++;	
		}
		return accNumSegment;
	}
	
	public String validateAccountNumberSegment()
	{
		StringBuilder sb=new StringBuilder("");
		
		int i=1;
		
		if(this.getAccNumBeanList().size()>4)
		{
			sb.append("Account Number Segment should not have more than 4 account numbers \n");
		}
		
		if(this.getAccNumBeanList().size()>0){
		for(AccountNumberBean accNumBean:this.getAccNumBeanList())
		{
			if(accNumBean.getAccountNumber()==null || accNumBean.getAccountNumber().equals(""))
			{
				sb.append("Account Number Field"+i+"is  null or blank\n");
			}else{
				if(accNumBean.getAccountNumber().length()>25)
				{
					sb.append("Account Number "+accNumBean.getAccountNumber()+"length is more than 25 characters\n");
				}
			}
			i++;
		}
		}
		
		return sb.toString();
		
		
		
	}
}
