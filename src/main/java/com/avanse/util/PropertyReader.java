package com.avanse.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class PropertyReader {

	
	public static String getProperty(String key){
		Properties prop = new Properties();
		InputStream input = null;
		String filename = "application.properties";
		input = PropertyReader.class.getClassLoader().getResourceAsStream(filename);	
		String res=null;
		if(key==null){
			System.out.println("Sorry, unable to read. Key is null"+key);
			return "";
		}
		try {
			prop.load(input);
			res=prop.getProperty(key);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return res;
	}
	
	public static void main(String[] args) {
		System.out.println("PropertyReader.main()"+PropertyReader.getProperty("driverName"));
	}
}
