package com.avanse.util;

import java.io.StringWriter;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import com.google.gson.Gson;

public class ObjectConverter {

	public static String jaxbObjectToXML(Object object)
	{
		String xmlContent=null;
		
		try
	    {
	        JAXBContext jaxbContext = JAXBContext.newInstance(object.getClass());
	        Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
	        jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
	        StringWriter sw = new StringWriter();
	        jaxbMarshaller.marshal(object, sw);
	        xmlContent = sw.toString();

	    } catch (JAXBException e) {
	        e.printStackTrace();
	    }
		return xmlContent;
	}
	
public static String getInputJson(Object requestObject){
		
		Gson gson =new Gson();
		String json=gson.toJson(requestObject);
		return json;
	}
}
