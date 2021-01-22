package com.avanse.util;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

 
/**
 * This program demonstrates how to establish database connection to Microsoft
 * SQL Server.
 * @author www.codejava.net
 *
 */
public class JdbcSQLServerConnection {
    
    public static Connection getConnection() throws ClassNotFoundException{

    	Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
    	Connection conn = null;
 
        try {
 
            String dbURL =PropertyReader.getProperty("pennDbURL");
            String user=PropertyReader.getProperty("pennDbUser");
            String pass=PropertyReader.getProperty("pennDbPass");
            
            conn = DriverManager.getConnection(dbURL, user, pass);
 
        } catch (SQLException ex) {
            ex.printStackTrace();
        } 
        
        return conn;
    
    }
}

