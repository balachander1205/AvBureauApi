package com.avanse.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(value= {"segmentTag"})
public class AddressSegment {
	private String segmentTag;
	private List<AddressBean> addBeanList;
	
	public String getSegmentTag() {
		return segmentTag;
	}
	public void setSegmentTag(String segmentTag) {
		this.segmentTag = segmentTag;
	}
	
	public List<AddressBean> getAddBeanList() {
		return addBeanList;
	}
	public void setAddBeanList(List<AddressBean> addBeanList) {
		this.addBeanList = addBeanList;
	}
	
	public StringBuilder generateAddressSegment(){
		StringBuilder addSegment=new StringBuilder();
	
		int i=1;
		
		for(AddressBean bean:addBeanList)
		{
			addSegment.append("PA03A"+String.format("%02d", i));
			
			if(bean.getAddLine1()!=null && bean.getAddLine1()!=""){
				addSegment.append("01"+String.format("%02d",bean.getAddLine1().length())+bean.getAddLine1());
			}
			
			if(bean.getAddLine2()!=null && bean.getAddLine2()!=""){
				addSegment.append("02"+String.format("%02d",bean.getAddLine2().length())+bean.getAddLine2());
			}
			
			if(bean.getAddLine3()!=null && bean.getAddLine3()!=""){
				addSegment.append("03"+String.format("%02d",bean.getAddLine3().length())+bean.getAddLine3());
			}
			
			if(bean.getAddLine4()!=null && bean.getAddLine4()!=""){
				addSegment.append("04"+String.format("%02d",bean.getAddLine4().length())+bean.getAddLine4());
			}
			
			if(bean.getAddLine5()!=null && bean.getAddLine4()!=""){
				addSegment.append("05"+String.format("%02d",bean.getAddLine5().length())+bean.getAddLine5());
			}
			
			if(bean.getStateCode()!=null && bean.getStateCode()!=""){
				addSegment.append("0602"+String.format("%02d",Integer.parseInt(bean.getStateCode())));
			}
			
			if(bean.getPinCode()!=null && bean.getPinCode()!=""){
				addSegment.append("07"+String.format("%02d",bean.getPinCode().length())+bean.getPinCode());
			}
			
			if(bean.getAddCategory()!=null && bean.getAddCategory()!=""){
				addSegment.append("0802"+bean.getAddCategory());
			}
			
			if(bean.getResidenceCode()!=null && bean.getResidenceCode()!=""){
				addSegment.append("0902"+bean.getResidenceCode());
			}
			
			i++;	
		}
		return addSegment;
	}
	
	public String validateAddressSegment()
	{
		StringBuilder sb=new StringBuilder("");
		
		int i=1;
		
		if(this.getAddBeanList().size()<1)
		{
			sb.append("Address Segment should have atleast one type of address \n");
		}
		else{
		for(AddressBean addBean:this.getAddBeanList())
		{
			if(addBean.getAddLine1()==null || addBean.getAddLine1().equals(""))
			{
				sb.append("Address LineField "+i+"is  null or blank\n");
			}else{
				if(addBean.getAddLine1().length()<3 || addBean.getAddLine1().length()>40)
				{
					sb.append("Address LineField "+i+"length is lesss than 3 or more than 40 characters\n");
				}
			}
			
			if(addBean.getStateCode()==null || addBean.getStateCode().equals(""))
			{
				sb.append("StateCode"+i+"is  null or blank\n");
			}
			
			if(addBean.getPinCode()==null || addBean.getPinCode().equals(""))
			{
				sb.append("PinCode"+i+"is  null or blank\n");
			}else{
				if(addBean.getPinCode().length()<6 || addBean.getPinCode().length()>10)
				{
					sb.append(""+addBean.getPinCode()+" length is lesss than 3 or more than 10 characters\n");
				}
				
				if(addBean.getPinCode().endsWith("000"))
				{
					sb.append(""+addBean.getPinCode()+" should not end with '000'\n");
				}
				
				if(addBean.getPinCode().startsWith("0"))
				{
					sb.append(""+addBean.getPinCode()+" should not start with '0'\n");
				}
			}
			
			if(addBean.getAddCategory()==null || addBean.getAddCategory().equals(""))
			{
				sb.append("Address Category"+i+"is  null or blank\n");
			}
				
			
			i++;
		}
		
		}
		return sb.toString();
		
		
		
	}

}