package com.avanse.controller;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import com.avanse.model.CibilResponse;
import com.avanse.model.Error;
import com.avanse.model.Request;
import com.avanse.model.Response;
import com.avanse.service.CustomerDetail;
import com.avanse.util.ObjectConverter;

/**
 * <h1>Avanse Bureau Api's </h1>
 * The controller class for AvBureauApi api's.
 * <p> This program takes cibil request inputs from user in the form of application/xml or application/json and returns cibil responses.</p>
 * 
 * @author Swapnil Sawant
 * @version 1.0
 * @since 2019-07-22
 */

@RestController
public class RequestController {

	/**
	 * 
	 * @param headers These are input headers for request
	 * @param request These are user inputs for request.
	 * 
	 * @return Object Return object will be as per input headers Content-Type. It
	 *         will either Response.class for XML or CibilResponse.class for json.
	 */
	@PostMapping("response")
	public Object getResponse(@RequestHeader Map<String, String> headers, @RequestBody Request request,
			HttpServletRequest httpRequest) {
		Response response = null;

		// These lines of code get user headers in StringBuffer for storing input
		// headers values
		// in db table for further reference.

		StringBuffer headerInfo = new StringBuffer();

		for (String key : headers.keySet()) {
			headerInfo.append(key + "=" + headers.get(key) + "\n");
		}

		String accept = headers.get("accept");
		String contentType = headers.get("content-type");
		String inputRequest = null;

		// These lines of code get input request String as per content-type for storing
		// input request in db table for further reference.
		if ("application/xml".equals(contentType)) {
			inputRequest = ObjectConverter.jaxbObjectToXML(request);

		} else {
			inputRequest = ObjectConverter.getInputJson(request);
		}

		CustomerDetail customerDetail = new CustomerDetail();
		System.out.println("HeaderInfo validation started:" + new java.util.Date().getTime());
		// This line of validate input headers and return error codes list if any.
		List<Error> errorList = customerDetail.validateHeaderInfo(headers, httpRequest);
		System.out.println("HeaderInfo validation end:" + new java.util.Date().getTime());

		System.out.println("ReqquestBody validation started:" + new java.util.Date().getTime());
		errorList.addAll(customerDetail.validateRequestBody(request));
		// errorList =customerDetail.validateRequestBody(request);
		System.out.println("ReqquestBody validation started:" + new java.util.Date().getTime());

		// This lines of code returns errror list as response if list is not empty
		// and stores input output information for errors in db.
		if (!errorList.isEmpty()) {
			response = new Response();
			response.setErrorDetails(errorList);

			String clientResponse = null;

			// This line of code convert reponse object to String object to either xml
			// String or json string
			// for storing response string in db for further reference.

			if ("application/xml".equals(accept)) {
				clientResponse = ObjectConverter.jaxbObjectToXML(response);

			} else {
				clientResponse = ObjectConverter.getInputJson(response);
			}

			// This line of code stores input request string,response string string,
			// headerinfo string, remote address of request machine in db table for further
			// reference.
			long srNo = customerDetail.storeErrorResponse(inputRequest, clientResponse, headerInfo.toString(),
					httpRequest.getRemoteAddr(), headers.get("user_id"));
			response.setSrNo(srNo);

			return response;
		} else {

			// This line of code create new cibil request data model.It cibil request
			// contains data segements such as HeaderSegment, TelephoneSegment etc.
			customerDetail.newCibilRequest(request);

			// This line of code perform cibil operation of cibil request and returns cibil
			// response.
			List<CibilResponse> cibilResponse = customerDetail.storeResponseStringInDB(headerInfo, request, httpRequest,
					customerDetail.getNewCibilRequest(), inputRequest, headers);

			if (cibilResponse != null) {
				System.out.println("Generating response for user start:" + new java.util.Date().getTime());
				String clientResponse = null;

				if (Integer.parseInt(headers.get("resp_type")) == 0) {
					customerDetail.parseResponseFieldsAndPdf(cibilResponse.get(0).getOutputTuef(),
							customerDetail.getNewCibilRequest(), cibilResponse.get(0).getSrNo());
				}

				if ("application/xml".equals(accept)) {
					response = customerDetail.getExperianCibilResponse(request, cibilResponse.get(0));
					response.setSrNo(cibilResponse.get(0).getSrNo());

					clientResponse = ObjectConverter.jaxbObjectToXML(response);

				} else {
					if (cibilResponse.get(0).getErrorSegment() != null) {
						response = customerDetail.getCibilErrorResponse(cibilResponse.get(0));
						response.setSrNo(cibilResponse.get(0).getSrNo());
						clientResponse = ObjectConverter.getInputJson(response);
						System.out.println("Generating response for user end:" + new java.util.Date().getTime());
						System.out.println("Storing user response in db start:" + new java.util.Date().getTime());
						customerDetail.storeClientResponse(cibilResponse.get(0).getSrNo(), clientResponse);
						System.out.println("Storing user response in db end:" + new java.util.Date().getTime());

						return response;
					} else {
						clientResponse = ObjectConverter.getInputJson(cibilResponse);
					}
				}

				System.out.println("Generating response for user end:" + new java.util.Date().getTime());
				System.out.println("Storing user response in db start:" + new java.util.Date().getTime());
				customerDetail.storeClientResponse(cibilResponse.get(0).getSrNo(), clientResponse);
				System.out.println("Storing user response in db end:" + new java.util.Date().getTime());
			}

			if ("application/xml".equals(accept)) {
				return response;
			} else {
				return cibilResponse;
			}
		}
	}
}
