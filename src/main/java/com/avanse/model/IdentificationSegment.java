package com.avanse.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(value= {"segmentTag"})
public class IdentificationSegment {
	private String segmentTag;
	
	private List<IDBean> idBeanList;
	
	public String getSegmentTag() {
		return segmentTag;
	}
	public void setSegmentTag(String segmentTag) {
		this.segmentTag = segmentTag;
	}
	public List<IDBean> getIdBeanList() {
		return idBeanList;
	}
	public void setIdBeanList(List<IDBean> idBeanList) {
		this.idBeanList = idBeanList;
	}

	public String validateIdentificationSegment()
	{
		StringBuilder sb=new StringBuilder("");
		
		int i=1;
		for(IDBean idBean:this.getIdBeanList())
		{
			if(idBean.getIdType()==null || idBean.getIdType().equals(""))
			{
				sb.append("IDType "+i+"is  null or blank\n");
			}
			
			i++;
		}
		
		return sb.toString();
		
		
		
	}
	
	public StringBuilder generateIdentificationSegment(){
		StringBuilder idSegment=new StringBuilder();
	
		int i=1;
		
		for(IDBean bean:idBeanList)
		{
			idSegment.append("ID03I"+String.format("%02d", i));
			idSegment.append("0102"+String.format("%02d",Integer.parseInt(bean.getIdType())));
			idSegment.append("02"+String.format("%02d",bean.getIdNumber().length())+bean.getIdNumber());
			i++;	
		}
		return idSegment;
	}

}
