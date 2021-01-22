package com.avanse.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;


public class ConnectionFactory {
	
	private static ConnectionFactory instance = new ConnectionFactory();
	DataSource dataSource = null;
	
	private  Connection con=null;
	private ConnectionFactory() {
		
	}
	
	public static void main(String... args){
		try {
			instance.getInquiryId(getConnection());
		} catch (ClassNotFoundException | NamingException | SQLException e) {
			e.printStackTrace();
		}
	}
	
	public String getInquiryId(Connection con){
 		String tmpInqId="";
 		try{
 		
 		String inquiryid="select ISEQ_INQ_ID.nextVal from dual";
 		Statement st=con.createStatement();
 		ResultSet rs=st.executeQuery(inquiryid);
 		
 		while(rs.next()){
 			tmpInqId="ES"+rs.getInt("NEXTVAL");
 		}
 		System.out.println("InquiryId:"+tmpInqId);
 		}catch(Exception e){
 			
 		}
 		
 		return tmpInqId;
 	}
	
	private Connection createConnection() throws ClassNotFoundException,NamingException,SQLException  {
		try{
			Class.forName(PropertyReader.getProperty("driverName"));
			con=DriverManager.getConnection(PropertyReader.getProperty("url"), PropertyReader.getProperty("username"),PropertyReader.getProperty("password"));
		}
		catch(Exception e){
			e.printStackTrace();
		}
		return con;
	}	
	
	public static Connection getConnection() throws ClassNotFoundException, NamingException, SQLException {
		return instance.createConnection();
	}
}
