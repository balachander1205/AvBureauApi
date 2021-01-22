package com.avanse.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ConnectException;
import java.net.NoRouteToHostException;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * <h1>Avanse Bureau Api's </h1>
 * The Cibil Connectivity util for AvBureauApi api's.
 * <p> This program hit cibil server and get cibil response for bureau api's</p>
 * 
 * @author Swapnil Sawant
 * @version 1.0
 * @since 2019-07-22
 */
public class SocketRequest {

	private static String sendRequest(String requestMessage) {

		StringBuilder builder = new StringBuilder();

		try {

			Socket socket = new Socket("103.225.112.27", 17507);
			System.out.println("--Connected to server.--");

			OutputStream out = socket.getOutputStream();

			InputStream in = socket.getInputStream();

			int i = 19;

			char c = (char) i;

			requestMessage = requestMessage + c;

			out.write(requestMessage.getBytes(), 0,
					requestMessage.getBytes().length); // Send the encoded
														// string to the server

			out.flush();

			// Receive the same string back from the serve

			BufferedReader reader = new BufferedReader(
					new InputStreamReader(in));

			String line;

			while ((line = reader.readLine()) != null) {

				builder.append(line);

			}

			socket.close(); // Close the socket and its streams

		} catch (Exception e) {

			e.printStackTrace();

		}

		finally {

		}

		return builder.toString();

	}
	/**
	 * 
	 * @param requestMessage Input cibil request string to hit cibil server
	 * @return This method hit cibil server with peer-to-peer socket connectivity and return cibil response.
	 */
	public static String sendRequestToCibil(String requestMessage) {

		StringBuilder builder = new StringBuilder();

		try {

			Socket socket = new Socket(PropertyReader.getProperty("cibilServerIP"), Integer.parseInt(PropertyReader.getProperty("cibilServerPort")));

			System.out.println("--Connected to Cibil server.--");

			OutputStream out = socket.getOutputStream();

			InputStream in = socket.getInputStream();

			int i = 19;

			char c = (char) i;

			requestMessage = requestMessage + c;

			out.write(requestMessage.getBytes(), 0,
					requestMessage.getBytes().length); // Send the encoded
														// string to the server

			out.flush();

			// Receive the same string back from the serve

			BufferedReader reader = new BufferedReader(
					new InputStreamReader(in));

			String line;

			while ((line = reader.readLine()) != null) {

				builder.append(line);

			}

			socket.close(); // Close the socket and its streams

		}
		
		catch(UnknownHostException e)
		{
			return "UnknownHostException : Unknown Host "+PropertyReader.getProperty("cibilServerIP")+":"+Integer.parseInt(PropertyReader.getProperty("cibilServerPort"));
		}
		catch(ConnectException e)
		{
			return "ConnectException: Could not connect to "+PropertyReader.getProperty("cibilServerIP")+":"+Integer.parseInt(PropertyReader.getProperty("cibilServerPort"));
		}
		
		catch(NoRouteToHostException e)
		{
			return "NoRouteToHostException Exception: No route to host "+PropertyReader.getProperty("cibilServerIP")+":"+Integer.parseInt(PropertyReader.getProperty("cibilServerPort"));
		}
		catch(IOException e)
		{
			return "IOException Exception: IOException to "+PropertyReader.getProperty("cibilServerIP")+":"+Integer.parseInt(PropertyReader.getProperty("cibilServerPort"));
		}
		return builder.toString();

	}
}
