package com.avanse.ftp;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;

import com.avanse.util.PropertyReader;

public class FTPService {
	private static void showServerReply(FTPClient ftpClient) {
		String[] replies = ftpClient.getReplyStrings();
		if (replies != null && replies.length > 0) {
			for (String aReply : replies) {
				System.out.println("SERVER: " + aReply);
			}
		}
	}

	public static FTPClient getFtpClient() {
		System.out.println("getFtpClient():start");
		try {
			String server = PropertyReader.getProperty("ftpHost");
			int port = Integer.parseInt(PropertyReader.getProperty("ftpPort").trim());
			String user = PropertyReader.getProperty("ftpUsername");
			String pass = PropertyReader.getProperty("ftpPassword");
			FTPClient ftpClient = new FTPClient();
			ftpClient.connect(server, port);
			ftpClient.login(user, pass);
			ftpClient.enterLocalPassiveMode();
			return ftpClient;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static void saveFileToFTP(String base64, int resp_code, String fileName, String folderName) {
		FTPClient ftpClient = getFtpClient();
		try {
			String partner = getPartner(resp_code);
			ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
			String destination = PropertyReader.getProperty("ftpFilePathProd")+partner+"/";
			// Check partner folder exists
			if (checkDirectoryExists(ftpClient, destination)) {
				ftpClient.changeWorkingDirectory(destination);
			}else {
				// Create partner folder if not exists
				ftpClient.makeDirectory(destination);
				ftpClient.changeWorkingDirectory(destination);
			}
			System.out.println("upload()::DIRECTORY::" + destination);
			String newString = base64.substring(base64.indexOf(",") + 1);
			byte decoded[] = new sun.misc.BASE64Decoder().decodeBuffer(newString);
			ftpClient.makeDirectory(destination);
			String firstRemoteFile = destination + "/" +fileName+".pdf";
			InputStream inputStream = new ByteArrayInputStream(decoded);
			System.out.println("Start uploading first file");
			boolean done = ftpClient.storeFile(firstRemoteFile, inputStream);
			inputStream.close();
			Date d = new Date();
			if (done) {
				System.out.println("The first file is uploaded successfully.");
			}
			// ftpClient.logout();
			ftpClient.disconnect();
		} catch (IOException ex) {
			System.out.println("Oops! Something wrong happened");
			ex.printStackTrace();
		}
	}

	/**
	 * Creates a nested directory structure on a FTP server
	 * 
	 * @param ftpClient an instance of org.apache.commons.net.ftp.FTPClient class.
	 * @param dirPath   Path of the directory, i.e /projects/java/ftp/demo
	 * @return true if the directory was created successfully, false otherwise
	 * @throws IOException if any error occurred during client-server communication
	 */
	public static boolean makeDirectories(FTPClient ftpClient, String dirPath) throws IOException {
		String[] pathElements = dirPath.split("/");
		if (pathElements != null && pathElements.length > 0) {
			for (String singleDir : pathElements) {
				boolean existed = ftpClient.changeWorkingDirectory(singleDir);
				if (!existed) {
					boolean created = ftpClient.makeDirectory(singleDir);
					if (created) {
						System.out.println("CREATED directory: " + singleDir);
						ftpClient.changeWorkingDirectory(singleDir);
					} else {
						System.out.println("COULD NOT create directory: " + singleDir);
						return false;
					}
				}
			}
		}
		return true;
	}

	public static boolean checkDirectoryExists(FTPClient ftpClient, String dirPath) {
		try {
			ftpClient.changeWorkingDirectory(dirPath);
			int returnCode = ftpClient.getReplyCode();
			if (returnCode == 550) {
				return false;
			}
			return true;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}
	
	public static String getPartner(int index) {
		String[] partners = PropertyReader.getProperty("ftpPartnerFolders").split(",");
		System.out.println("Partner:="+partners[index]);
		return (partners[index]!=null || partners[index]!="")?partners[index]:"";
	}
}
