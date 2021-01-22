package com.avanse.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(value= {"segmentTag"})
public class TelephoneSegment {
	private  String segmentTag;
	private List<TelephoneBean> telBeanList;
	
	public String getSegmentTag() {
		return segmentTag;
	}
	public void setSegmentTag(String segmentTag) {
		this.segmentTag = segmentTag;
	}
	public List<TelephoneBean> getTelBeanList() {
		return telBeanList;
	}
	public void setTelBeanList(List<TelephoneBean> telBeanList) {
		this.telBeanList = telBeanList;
	}
	
	public String validateTelephoneSegment()
	{
		StringBuilder sb=new StringBuilder("");
		
		int i=1;
		
		if(this.getTelBeanList().size()<1)
		{
			sb.append("Telephone Segment should have atleast one type of telephone number \n");
		}else{
		for(TelephoneBean telBean:this.getTelBeanList())
		{
			if(telBean.getTelephoneType()==null || telBean.getTelephoneType().equals(""))
			{
				sb.append("TelephoneType "+i+"is  null or blank\n");
			}
			if(telBean.getTelephoneNumber()==null || telBean.getTelephoneNumber().equals(""))
			{
				sb.append("TelephoneNumber "+i+"is  null or blank\n");
			}else{
				
				if(telBean.getTelephoneNumber().length()<5 || telBean.getTelephoneNumber().length()>20)
				{
					sb.append("TelephoneNumber "+telBean.getTelephoneNumber()+" length is less than 5 or more than 20 characters\n");
				}
				
				if(telBean.getTelephoneNumber().startsWith("1"))
				{
					sb.append("TelephoneNumber "+telBean.getTelephoneNumber()+" must not start '1'.\n");
				}
				
				if(telBean.getTelephoneNumber().startsWith("6")||telBean.getTelephoneNumber().startsWith("7")||telBean.getTelephoneNumber().startsWith("8")||telBean.getTelephoneNumber().startsWith("9"))
				{
					if(telBean.getTelephoneNumber().length()<10)
					{
						sb.append("TelephoneNumber "+telBean.getTelephoneNumber()+" start with '6' or '7' or '8' or '9' should have  minimum length of 10.\n");
					}
				}
				
				if(telBean.getTelephoneNumber().trim().contains(" "))
				{
					sb.append("TelephoneNumber "+telBean.getTelephoneNumber()+" should not have space in between.\n");
				}
			}
			
			i++;
		}
		
		}
		return sb.toString();
		
		
		
	}
	
	public StringBuilder generateTelephoneSegment(){
		StringBuilder telSegment=new StringBuilder();
	
		int i=1;
		
		for(TelephoneBean bean:telBeanList)
		{
			telSegment.append("PT03T"+String.format("%02d", i));
			telSegment.append("01"+String.format("%02d",bean.getTelephoneNumber().length())+bean.getTelephoneNumber());
			
			if(bean.getTelephoneExt()!=null && bean.getTelephoneExt()!="")
			{
				telSegment.append("02"+String.format("%02d",bean.getTelephoneExt().length())+bean.getTelephoneExt());
			}
			
			telSegment.append("0302"+bean.getTelephoneType());
			i++;	
		}
		return telSegment;
	}
}
