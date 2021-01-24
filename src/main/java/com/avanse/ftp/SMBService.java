package com.avanse.ftp;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.avanse.util.PropertyReader;

import jcifs.smb.NtlmPasswordAuthentication;
import jcifs.smb.SmbFile;
import jcifs.smb.SmbFileOutputStream;

public class SMBService {
	// Connect to remote location and create CIBIL report
	public void saveCIBILReportToFolder(String fileName, ByteArrayOutputStream byteOutStream, String userId) {
		try {
			// Current date
			long currTimeMiles = System.currentTimeMillis();
			Date currDate = new Date(currTimeMiles);
			// Date formattar "dd-MM-yyyy"
			SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
			String curDateFolder = dateFormat.format(currDate);
			System.out.println("SMB:current date folder=" + curDateFolder);
			// String url = "smb://10.250.6.23/elms-upload/";
			// Windows domain name
			String domain = PropertyReader.getProperty("ftpDomain");
			// Windows user name
			String userName = PropertyReader.getProperty("ftpUsername");
			// Windows password
			String password = PropertyReader.getProperty("ftpPassword");
			// Windows shared location path
			String url = PropertyReader.getProperty("ftpFilePathProd");
			System.out.println("ftpDomain=" + domain + " ftpUsername=" + userName + " ftpPassword=" + password
					+ " ftpFilePathProd=" + url+ " Partner="+userId);
			// Do windows authentication
			NtlmPasswordAuthentication auth = new NtlmPasswordAuthentication(domain, userName, password);
			// Check partner directory exists or not
			SmbFile dir = new SmbFile(url + userId, auth);
			System.out.println(dir.getPath());
			if (!dir.exists()) {
				// create current date folder under partner folder
				dir.mkdir();
			}
			SmbFile dir2 = new SmbFile(url + userId + "/" + curDateFolder + "/", auth);
			if (!dir2.exists()) {
				// create current date folder under partner folder
				dir2.mkdir();
			}
			System.out.println(dir2.getPath());
			// CIBIL report file
			SmbFile cibilFile = new SmbFile(dir2 + "/"+ fileName, auth);
			System.out.println(cibilFile.getPath()+"/"+ fileName);
			// if(!cibilFile.exists()) {
			// Creation of CIBIL report in remote location
			System.out.println("SMB:cibil report creation started");
			SmbFileOutputStream outputStream = new SmbFileOutputStream(cibilFile);			
			byte[] cibilReportByteArr = byteOutStream.toByteArray();
			outputStream.write(cibilReportByteArr);
			outputStream.close();
			System.out.println("SMB:cibil report creation ended");
			// }
		} catch (Exception e) {
			System.out.println("Xception:saveCIBILReportToFolder()=" + e);
		}
	}
}
