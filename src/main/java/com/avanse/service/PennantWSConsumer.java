package com.avanse.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.springframework.stereotype.Service;

import com.avanse.model.PennantRequest_bkp;
import com.avanse.model.PennantResponse;
import com.avanse.util.PropertyReader;
import com.google.gson.Gson;

@Service
public class PennantWSConsumer {
	
	public PennantResponse consumeWebService(PennantRequest_bkp request) {
		
		String input=getInputJson(request);
		
		PennantResponse response=this.getResponseObject(input);
		
		return response;
	}
	
public String getInputJson(Object requestObject){
		
		Gson gson =new Gson();
		String json=gson.toJson(requestObject);
		return json;
	}

public PennantResponse getResponseObject(String inputString)
{

	DefaultHttpClient httpClient = new DefaultHttpClient();
	HttpResponse response=null;
	PennantResponse pennantResponse=null;
	
	try {
	HttpPost httpPost = new HttpPost(PropertyReader.getProperty("pennantApiURI"));

	HttpEntity entity = new StringEntity(inputString,"UTF-8");
	
	httpPost.setEntity(entity);

	httpPost.setHeader("Accept-Encoding", "UTF-8");
	httpPost.setHeader("Content-Type", "application/json");

	
	try {
		response = httpClient.execute(httpPost);
	} catch (Exception e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	
	pennantResponse=getPennantResponse(response);
	httpClient.getConnectionManager().shutdown();
	
	}
catch (IllegalStateException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	return pennantResponse;	

}

public PennantResponse getPennantResponse(HttpResponse response)
{
	PennantResponse pennantResponse=null;
	try{
		BufferedReader br = new BufferedReader(
                new InputStreamReader((response.getEntity().getContent())));
		
		StringBuilder sb=new StringBuilder();
		String output;
		while ((output = br.readLine()) != null) {
			sb.append(output);
		}
		
		pennantResponse=setOutputJson(sb.toString());
		}
	catch (UnsupportedEncodingException e) {
		e.printStackTrace();
	}
catch (MalformedURLException e) {

	e.printStackTrace();
}
	catch (IllegalStateException e) {
		e.printStackTrace();
	}
	
	catch (IOException e) {

	e.printStackTrace();

  }
	return pennantResponse;
}

private PennantResponse setOutputJson(String outputString)
{
	Gson gson = new Gson();
	PennantResponse pennantResponse= gson.fromJson(outputString, PennantResponse.class);
	return pennantResponse;
}

}
