package com.avanse.util;

import jcifs.smb.NtlmPasswordAuthentication;
import jcifs.smb.SmbFile;

public class SMBServiceTest {
	public static void main(String[] args) throws Exception{
		String url = "smb://10.250.6.23/elms-upload/";
		NtlmPasswordAuthentication auth = new NtlmPasswordAuthentication(null, "aitdev", "Tea@1234");
		SmbFile dir = new SmbFile(url, auth);
		for (SmbFile f : dir.listFiles()) {
			System.out.println(f.getName());
		}
	}

}
