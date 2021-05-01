package com.avanse.service;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import javax.servlet.http.HttpServletRequest;

import com.avanse.common.util.CommonUtil;
import com.avanse.ftp.SMBService;
import com.avanse.model.AddressBean;
import com.avanse.model.AddressDetails;
import com.avanse.model.CibilResponse;
import com.avanse.model.EmploymentDetails;
import com.avanse.model.EnquiryBean;
import com.avanse.model.EnquiryDetails;
import com.avanse.model.Error;
import com.avanse.model.IDBean;
import com.avanse.model.NameDetails;
import com.avanse.model.OthersAccountBean;
import com.avanse.model.OwnAccountBean;
import com.avanse.model.Request;
import com.avanse.model.Response;
import com.avanse.model.ScoreBean;
import com.avanse.model.ScoreDetails;
import com.avanse.model.TelephoneBean;
import com.avanse.model.TradeLineDetails;
import com.avanse.model.UserReferenceErrorSegment;
import com.avanse.util.EncryptUtil;
import com.avanse.util.PropertyReader;
import com.avanse.util.SocketRequest;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfPCell;

import jcifs.smb.NtlmPasswordAuthentication;
import jcifs.smb.SmbFile;
import jcifs.smb.SmbFileOutputStream;

public class CustomerDetail {

	private DAOBASE dao = new DAOBASE();
	Properties prop = null;

	/*
	 * @Autowired PennantWSConsumer pennantWSConsumer;
	 */

	private NewCibilRequest newCibilRequest = null;

	public NewCibilRequest getNewCibilRequest() {
		return newCibilRequest;
	}

	public void setNewCibilRequest(NewCibilRequest newCibilRequest) {
		this.newCibilRequest = newCibilRequest;
	}

	public int storeOutputResponse(StringBuilder inputCSV, NewCibilRequest ncr, String output, String inputRequest,
			String headerInfo, String ip, Map<String, Object> existMapData, String user_id) {
		return dao.insertCibilResponse(inputCSV, ncr, output, inputRequest, headerInfo, ip, existMapData, user_id);
	}

	public int storeErrorResponse(String request, String error, String headerInfo, String ip, String user_id) {
		return dao.insertErrorResponse(request, error, headerInfo, ip, user_id);
	}

	public Response getCibilErrorResponse(CibilResponse cibilResponse) {
		Response ecr = new Response();

		List<Error> errorDetailsList = new ArrayList<Error>();

		UserReferenceErrorSegment ures = cibilResponse.getErrorSegment().getUserReferenceErrorSegment();

		if (ures.getInvalidVersion() != null) {
			Error error = new Error();
			error.setErrorCode("03");
			error.setErrorMessage("Invalid Cibil Version:" + ures.getInvalidVersion());

			errorDetailsList.add(error);
		}

		if (ures.getInvalidFieldLength() != null) {
			Error error = new Error();
			error.setErrorCode("04");
			error.setErrorMessage("Invalid Field Length:" + ures.getInvalidFieldLength());

			errorDetailsList.add(error);
		}

		if (ures.getInvalidTotalLength() != null) {
			Error error = new Error();
			error.setErrorCode("05");
			error.setErrorMessage("Invalid Total Length:" + ures.getInvalidTotalLength());

			errorDetailsList.add(error);
		}

		if (ures.getInvalidEnquiryPurpose() != null) {
			Error error = new Error();
			error.setErrorCode("06");
			error.setErrorMessage("Invalid Enquiry Purpose:" + ures.getInvalidEnquiryPurpose());

			errorDetailsList.add(error);
		}

		if (ures.getInvalidEnquiryAmount() != null) {
			Error error = new Error();
			error.setErrorCode("07");
			error.setErrorMessage("Invalid Enquiry Amount:" + ures.getInvalidEnquiryAmount());

			errorDetailsList.add(error);
		}

		if (ures.getInvalidEnquiryMemberUserIDOrPassword() != null) {
			Error error = new Error();
			error.setErrorCode("08");
			error.setErrorMessage(
					"Invalid Enquiry Member UserID or Password:" + ures.getInvalidEnquiryMemberUserIDOrPassword());

			errorDetailsList.add(error);
		}

		if (ures.getRequiredEnquirySegmentMissing() != null) {
			Error error = new Error();
			error.setErrorCode("09");
			error.setErrorMessage("Required Enquiry Segment Missing:" + ures.getRequiredEnquirySegmentMissing());

			errorDetailsList.add(error);
		}

		for (int i = 0; i < ures.getInvalidEnquiryData().size(); i++) {
			Error error = new Error();
			error.setErrorCode("10-" + i);
			error.setErrorMessage("Invalid Enquiry Data:" + ures.getInvalidEnquiryData().get(i));

			errorDetailsList.add(error);
		}

		if (ures.getCibilSystemError() != null) {
			Error error = new Error();
			error.setErrorCode("11");
			error.setErrorMessage("Cibil Syster Error. Please contact Cibil System as soon as possible");

			errorDetailsList.add(error);
		}

		if (ures.getInvalidSegmentTag() != null) {
			Error error = new Error();
			error.setErrorCode("12");
			error.setErrorMessage("Invalid Segment Tag:" + ures.getInvalidSegmentTag());

			errorDetailsList.add(error);
		}

		if (ures.getInvalidSegmentOrder() != null) {
			Error error = new Error();
			error.setErrorCode("13");
			error.setErrorMessage("Invalid Segment Order:" + ures.getInvalidSegmentOrder());

			errorDetailsList.add(error);
		}

		if (ures.getInvalidFieldTagOrder() != null) {
			Error error = new Error();
			error.setErrorCode("14");
			error.setErrorMessage("Invalid Field Tag Order:" + ures.getInvalidFieldTagOrder());

			errorDetailsList.add(error);
		}

		for (int i = 0; i < ures.getMissingRequiredField().size(); i++) {

			Error error = new Error();
			error.setErrorCode("15-" + i);
			error.setErrorMessage("Missing Required Field:" + ures.getMissingRequiredField().get(i));

			errorDetailsList.add(error);

		}

		if (ures.getRequestedResponseSizeExceeded() != null) {
			Error error = new Error();
			error.setErrorCode("16");
			error.setErrorMessage("Requested Response Size Exceeded");

			errorDetailsList.add(error);
		}

		if (ures.getInvalidInputOrOutputMedia() != null) {
			Error error = new Error();
			error.setErrorCode("17");
			error.setErrorMessage("Invalid Input Or Output Media:" + ures.getInvalidInputOrOutputMedia());

			errorDetailsList.add(error);
		}

		ecr.setErrorDetails(errorDetailsList);

		return ecr;
	}

	public Response getExperianCibilResponse(Request clientRequest, CibilResponse cibilResponse) {
		Response ecr = new Response();
		ecr.setRequest(clientRequest);

		if (cibilResponse.getScoreSegment() != null) {
			List<ScoreDetails> scoreDetailsList = new ArrayList<ScoreDetails>();
			for (ScoreBean scoreBean : cibilResponse.getScoreSegment().getScoreBeanList()) {
				ScoreDetails scoreDetails = new ScoreDetails();
				scoreDetails.setScore(scoreBean.getScore());
				scoreDetails.setScoreDate(scoreBean.getScoreDate());
				scoreDetailsList.add(scoreDetails);
			}
			ecr.setScoreDetails(scoreDetailsList);
		}

		if (cibilResponse.getAddressSegment() != null) {
			List<AddressDetails> addressDetailsList = new ArrayList<AddressDetails>();
			for (AddressBean addressBean : cibilResponse.getAddressSegment().getAddBeanList()) {
				AddressDetails addressDetails = new AddressDetails();
				addressDetails.setPinCode(addressBean.getPinCode());
				addressDetails.setAddCategory(addressBean.getAddCategory());
				addressDetails.setAddLine1(addressBean.getAddLine1());
				addressDetails.setAddLine2(addressBean.getAddLine2());
				addressDetails.setAddLine3(addressBean.getAddLine3());
				addressDetails.setAddLine4(addressBean.getAddLine4());
				addressDetails.setAddLine5(addressBean.getAddLine5());
				addressDetails.setStateCode(addressBean.getStateCode());
				addressDetails.setDateReported(addressBean.getDateReported());
				addressDetailsList.add(addressDetails);
			}
			ecr.setAddressDetails(addressDetailsList);
		}

		if (cibilResponse.getNameSegment() != null) {
			NameDetails nameDetails = new NameDetails();
			nameDetails.setNameField1(cibilResponse.getNameSegment().getfName());
			nameDetails.setNameField2(cibilResponse.getNameSegment().getmName());
			nameDetails.setNameField3(cibilResponse.getNameSegment().getlName());
			nameDetails.setNameField4(cibilResponse.getNameSegment().getNameField4());
			nameDetails.setNameField5(cibilResponse.getNameSegment().getNameField5());

			ecr.setNameDetails(nameDetails);
		}

		if (cibilResponse.getEmploymentSegment() != null) {
			EmploymentDetails empDetails = new EmploymentDetails();
			empDetails.setIncome(cibilResponse.getEmploymentSegment().getIncome());
			empDetails.setOccupationCode(cibilResponse.getEmploymentSegment().getOccupationCode());
			empDetails
					.setNetOrGrossIncomeIndicator(cibilResponse.getEmploymentSegment().getNetOrGrossIncomeIndicator());
			empDetails.setMonthlyOrAnnualIncomeIndicator(
					cibilResponse.getEmploymentSegment().getMonthlyOrAnnualIncomeIndicator());

			ecr.setEmploymentDetails(empDetails);
		}

		if (cibilResponse.getEnquirySegment() != null) {
			List<EnquiryDetails> enquiryDetailsList = new ArrayList<EnquiryDetails>();
			for (EnquiryBean enquiryBean : cibilResponse.getEnquirySegment().getEnquiryBeanList()) {
				EnquiryDetails enquiryDetails = new EnquiryDetails();
				enquiryDetails.setEnquiryDate(enquiryBean.getDateOfEnquiry());
				enquiryDetails.setPurpose(enquiryBean.getEnquiryPurpose());
				enquiryDetails.setAmount(enquiryBean.getEnquiryAmount());

				enquiryDetailsList.add(enquiryDetails);
			}

			ecr.setEnquiryDetails(enquiryDetailsList);
		}

		if (cibilResponse.getAccountSegment() != null) {
			List<TradeLineDetails> tradeLineDetailsList = new ArrayList<TradeLineDetails>();
			for (OwnAccountBean ownAccBean : cibilResponse.getAccountSegment().getOwnAccountBeanList()) {
				TradeLineDetails tradeLineDetails = new TradeLineDetails();
				tradeLineDetails.setAccountNumber(ownAccBean.getAccountNumber());
				tradeLineDetails.setOwnershipIndicator(ownAccBean.getOwnershipIndicator());
				tradeLineDetails.setAccountType(ownAccBean.getAccountType());
				tradeLineDetails.setDateOpenedOrDisbursed(ownAccBean.getDateOpenedOrDisbursed());
				tradeLineDetails.setCreditLimit(ownAccBean.getCreditLimit());
				tradeLineDetails.setCashLimit(ownAccBean.getCashLimit());
				tradeLineDetails.setHighCreditOrSanctionedAmount(ownAccBean.getHighCreditOrSanctionedAmount());
				tradeLineDetails.setPaymentHistory1(ownAccBean.getPaymentHistory1());
				tradeLineDetails.setPaymentHistory2(ownAccBean.getPaymentHistory2());
				tradeLineDetails.setPaymentHistoryStartDate(ownAccBean.getPaymentHistoryStartDate());
				tradeLineDetails.setPaymentHistoryEndDate(ownAccBean.getPaymentHistoryEndDate());
				tradeLineDetails.setCurrentBalance(ownAccBean.getCurrentBalance());
				tradeLineDetails.setAmountOverdue(ownAccBean.getAmountOverdue());
				tradeLineDetails.setDateReportedAndCertified(ownAccBean.getDateReportedAndCertified());
				tradeLineDetails.setDateClosed(ownAccBean.getDateClosed());
				tradeLineDetails.setDateOfLastPayment(ownAccBean.getDateOfLastPayment());
				tradeLineDetails.setSuitFiledOrWilfulDefault(ownAccBean.getSuitFiledOrWilfulDefault());
				tradeLineDetails.setWrittenOffAndSettledStatus(ownAccBean.getWrittenOffAndSettledStatus());
				tradeLineDetails.setSettlementAmount(ownAccBean.getSettlementAmount());
				tradeLineDetails.setValueOfCollateral(ownAccBean.getValueOfCollateral());
				tradeLineDetails.setTypeOfCollateral(ownAccBean.getTypeOfCollateral());
				tradeLineDetails.setWrittenOfAmountTotal(ownAccBean.getWrittenOffAmountTotal());
				tradeLineDetails.setWrittenOfAmountPrincipal(ownAccBean.getWrittenOffAmountPrincipal());
				tradeLineDetails.setRateOfInterest(ownAccBean.getRateOfInterest());
				tradeLineDetails.setRepaymentTenure(ownAccBean.getRepaymentTenure());
				tradeLineDetails.setPaymentFrequency(ownAccBean.getPaymentFrequency());
				tradeLineDetails.setEmiAmount(ownAccBean.getEMIAmount());
				tradeLineDetails.setActualPaymentAmount(ownAccBean.getActualPaymentAmount());

				tradeLineDetailsList.add(tradeLineDetails);
			}
			ecr.setTradeLineDetails(tradeLineDetailsList);
		}

		if (cibilResponse.getErrors() != null) {
			ecr.setErrorDetails(cibilResponse.getErrors());
		}

		if (cibilResponse.getErrorSegment() != null) {
			List<Error> errorDetailsList = new ArrayList<Error>();

			UserReferenceErrorSegment ures = cibilResponse.getErrorSegment().getUserReferenceErrorSegment();

			if (ures.getInvalidVersion() != null) {
				Error error = new Error();
				error.setErrorCode("03");
				error.setErrorMessage("Invalid Cibil Version:" + ures.getInvalidVersion());

				errorDetailsList.add(error);
			}

			if (ures.getInvalidFieldLength() != null) {
				Error error = new Error();
				error.setErrorCode("04");
				error.setErrorMessage("Invalid Field Length:" + ures.getInvalidFieldLength());

				errorDetailsList.add(error);
			}

			if (ures.getInvalidTotalLength() != null) {
				Error error = new Error();
				error.setErrorCode("05");
				error.setErrorMessage("Invalid Total Length:" + ures.getInvalidTotalLength());

				errorDetailsList.add(error);
			}

			if (ures.getInvalidEnquiryPurpose() != null) {
				Error error = new Error();
				error.setErrorCode("06");
				error.setErrorMessage("Invalid Enquiry Purpose:" + ures.getInvalidEnquiryPurpose());

				errorDetailsList.add(error);
			}

			if (ures.getInvalidEnquiryAmount() != null) {
				Error error = new Error();
				error.setErrorCode("07");
				error.setErrorMessage("Invalid Enquiry Amount:" + ures.getInvalidEnquiryAmount());

				errorDetailsList.add(error);
			}

			if (ures.getInvalidEnquiryMemberUserIDOrPassword() != null) {
				Error error = new Error();
				error.setErrorCode("08");
				error.setErrorMessage(
						"Invalid Enquiry Member UserID or Password:" + ures.getInvalidEnquiryMemberUserIDOrPassword());

				errorDetailsList.add(error);
			}

			if (ures.getRequiredEnquirySegmentMissing() != null) {
				Error error = new Error();
				error.setErrorCode("09");
				error.setErrorMessage("Required Enquiry Segment Missing:" + ures.getRequiredEnquirySegmentMissing());

				errorDetailsList.add(error);
			}

			for (int i = 0; i < ures.getInvalidEnquiryData().size(); i++) {
				Error error = new Error();
				error.setErrorCode("10-" + i);
				error.setErrorMessage("Invalid Enquiry Data:" + ures.getInvalidEnquiryData().get(i));

				errorDetailsList.add(error);
			}

			if (ures.getCibilSystemError() != null) {
				Error error = new Error();
				error.setErrorCode("11");
				error.setErrorMessage("Cibil Syster Error. Please contact Cibil System as soon as possible");

				errorDetailsList.add(error);
			}

			if (ures.getInvalidSegmentTag() != null) {
				Error error = new Error();
				error.setErrorCode("12");
				error.setErrorMessage("Invalid Segment Tag:" + ures.getInvalidSegmentTag());

				errorDetailsList.add(error);
			}

			if (ures.getInvalidSegmentOrder() != null) {
				Error error = new Error();
				error.setErrorCode("13");
				error.setErrorMessage("Invalid Segment Order:" + ures.getInvalidSegmentOrder());

				errorDetailsList.add(error);
			}

			if (ures.getInvalidFieldTagOrder() != null) {
				Error error = new Error();
				error.setErrorCode("14");
				error.setErrorMessage("Invalid Field Tag Order:" + ures.getInvalidFieldTagOrder());

				errorDetailsList.add(error);
			}

			for (int i = 0; i < ures.getMissingRequiredField().size(); i++) {

				Error error = new Error();
				error.setErrorCode("15-" + i);
				error.setErrorMessage("Missing Required Field:" + ures.getMissingRequiredField().get(i));

				errorDetailsList.add(error);

			}

			if (ures.getRequestedResponseSizeExceeded() != null) {
				Error error = new Error();
				error.setErrorCode("16");
				error.setErrorMessage("Requested Response Size Exceeded");

				errorDetailsList.add(error);
			}

			if (ures.getInvalidInputOrOutputMedia() != null) {
				Error error = new Error();
				error.setErrorCode("17");
				error.setErrorMessage("Invalid Input Or Output Media:" + ures.getInvalidInputOrOutputMedia());

				errorDetailsList.add(error);
			}

			ecr.setErrorDetails(errorDetailsList);
		}

		// if(cibilResponse.getPdfContent()!=null)
		// {
		ecr.setPdfContent(cibilResponse.getPdfContent());
		// }

		// if(cibilResponse.getOutputTuef()!=null)
		// {
		ecr.setOutputTuef(cibilResponse.getOutputTuef());
		// }

		// if(cibilResponse.getRemarks()!=null) {
		ecr.setRemarks(cibilResponse.getRemarks());
		// }

		ecr.setIs60AboveDpd(cibilResponse.getIs60AboveDpd());
		ecr.setIsAccWillfulDefault(cibilResponse.getIsAccWillfulDefault());
		ecr.setIsAccWrittOffOrSettled(cibilResponse.getIsAccWrittOffOrSettled());
		ecr.setIsMinCibilScore(cibilResponse.getIsMinCibilScore());

		return ecr;
	}

	/**
	 * @param input This is cibil response string.
	 * @return This method return error code against input cibil response
	 */
	public Error getCibilConnectionErrorList(String input) {
		Error error = new Error();
		if (input == null || input.equals("")) {

			error.setErrorCode("100");
			error.setErrorMessage("Null or Blank Output from Cibil Server");
		} else {
			if (input.startsWith("UnknownHostException")) {
				error.setErrorCode("101");
				error.setErrorMessage(input);
			} else if (input.startsWith("ConnectException")) {
				error.setErrorCode("102");
				error.setErrorMessage(input);
			} else if (input.startsWith("NoRouteToHostException")) {
				error.setErrorCode("103");
				error.setErrorMessage(input);
			} else if (input.startsWith("IOException")) {
				error.setErrorCode("104");
				error.setErrorMessage(input);
			} else if (!(input.startsWith("TUEF") || input.startsWith("ERRR")) || !(input.endsWith("0102**"))) {
				error.setErrorCode("113");
				error.setErrorMessage("Input tuef does not start with TUEF or ERRR or ends with 0102**");
			}
		}

		return error;
	}

	public void parseResponseFieldsAndPdf(String output, NewCibilRequest ncr, long id) {

		StringBuilder input2 = new StringBuilder(output);

		List<String> inputList = new ArrayList<>();
		List<CibilResponse> responseList = new ArrayList<CibilResponse>();

		if (output.startsWith("TUEF12")) {
			while (input2.length() > 0) {

				String in = input2.substring(input2.indexOf("TUEF12"), input2.indexOf("0102**") + 6);
				inputList.add(in);
				input2.replace(0, in.length(), "");
			}
		} else {
			inputList.add(output);
		}

		for (String input3 : inputList) {

			if (input3 != null && (input3.startsWith("TUEF") || input3.startsWith("ERRR"))) {

				CibilResponse cibilResponse = parseOutputResponse(false, input3, ncr, id);
				cibilResponse.setSrNo(id);
				responseList.add(cibilResponse);
			}
		}

		getCibilPdf(responseList, ncr, id);
	}

	public List<CibilResponse> storeResponseStringInDB(StringBuffer headerInfo, Request request,
			HttpServletRequest httpRequest, NewCibilRequest ncr, String inputRequest, Map<String, String> headers) {

		List<Error> errorList = new ArrayList<Error>();
		CibilResponse cibilResponse = null;
		List<CibilResponse> responseList = new ArrayList<>();
		List<CibilResponse> tempResponseList = new ArrayList<>();
		CommonUtil commonUtil = new CommonUtil();

		StringBuilder inputCSV = null;

		StringBuilder input2 = null;

		String output = null;
		boolean isExistCibil = false;
		Map<String, Object> existMapData = new HashMap<String, Object>();

		// These lines of code checks whether input has an attribute srNo or not. If
		// input has srNo attribute,
		// i.e. it doesn't want to hit cibil server again and wants to get stored cibil
		// response from db
		if (request.getSrNo() != null) {
			existMapData = dao.getExistingCibilResponse(request.getSrNo());
			output = existMapData.get("output") == null ? null : (String) existMapData.get("output");
			isExistCibil = output != null ? true : false;
			inputCSV = existMapData.get("inputCSV") == null ? null
					: new StringBuilder((String) existMapData.get("inputCSV"));

			if (existMapData.isEmpty()) {
				Error error = new Error("114", "Invalid Sr No.");
				errorList.add(error);
			}

		}
		// This line of code checks if user has already provided input cibil tuef
		// response in request
		else if (request.getInputTuef() != null) {

			output = request.getInputTuef().trim();
			existMapData.put("output", output);

		} else {

			// This line generate cibil input request string to hit cibil server.
			StringBuilder input = this.generateInputString(ncr);
			// This line of code create CSV string of input user request for further
			// reference in db.
			inputCSV = this.generateInputCSV(ncr);

			if (!inputCSV.toString().trim().equals("")) {
				// This line of checks if given input csv is matching with db records to
				// identify whether there is already same hit to cibil server in current day.
				// output=dao.findCibilRespByInputCSVForCurrentDay(inputCSV);
				existMapData = dao.findCibilRespByInputCSVForCurrentDay(inputCSV);
				output = existMapData.get("output") == null ? null : (String) existMapData.get("output");
			}

			if ((output == null || output.equals("")) && (input.toString() != null && !input.toString().equals(""))) {
				System.out.println("Getting response from cibil started:" + new java.util.Date().getTime());

				// This code is used to hit cibil request with input
				output = SocketRequest.sendRequestToCibil(input.toString());
				System.out.println("Getting response from cibil end:" + new java.util.Date().getTime());
			}
		}

		// This line of code checks if we are getting null or blank cibil respone from
		// cibil server and generate error data codes for further processing.
		if (output == null || output.equals("") || !(output.startsWith("TUEF") || output.startsWith("ERRR"))) {
			Error error = getCibilConnectionErrorList(output);
			errorList.add(error);
		}

		Integer resp_type = Integer.parseInt(headers.get("resp_type"));
		String loan_type = headers.get("loan_type");
		String product_type = headers.get("product_type");

		if (output != null && !output.equals("") && (output.startsWith("TUEF") || output.startsWith("ERRR"))) {

			// if srNo is not null then cibil date,cibil remarks and cibil tuef should be of
			// srNo from existing db row
			System.out.println("Storing cibil response to db start:" + new java.util.Date().getTime());
			// This line stores cibil request,response in db table.So that in case if any
			// error occurs after this line of code
			// ,same new request can use existing cibil response and no need to hit cibil
			// server again.
			long id = this.storeOutputResponse(inputCSV, ncr, output, inputRequest, headerInfo.toString(),
					httpRequest.getRemoteAddr(), existMapData, headers.get("user_id"));
			System.out.println("Storing cibil response to db end:" + new java.util.Date().getTime());

			// If resp_type is 0, it returns raw cibil response string as api response.
			if (resp_type == 0) {
				cibilResponse = new CibilResponse();
				cibilResponse.setSrNo(id);
				output = output.replaceAll("\\p{Cc}", "");
				cibilResponse.setOutputTuef(output);
				responseList = new ArrayList<CibilResponse>();
				responseList.add(cibilResponse);
				return responseList;
			}

			System.out.println("Clean cibil response start:" + new java.util.Date().getTime());

			// This line of code cleans cibil response having any other invalid ASCII
			// characters.
			input2 = new StringBuilder(output.replaceAll("\\p{Cc}", ""));

			List<String> inputList = new ArrayList<>();

			// These lines of code checks whether cibil response has additional info TUEF
			// response or not.
			// If yes then it substring it in 2 inputs, one which is main data TUEF and 2nd
			// which is additional info TUEF
			if (output.startsWith("TUEF12")) {
				while (input2.length() > 0) {

					String in = input2.substring(input2.indexOf("TUEF12"), input2.indexOf("0102**") + 6);
					inputList.add(in);
					input2.replace(0, in.length(), "");
				}
			} else {
				inputList.add(output);
			}

			System.out.println("Clean cibil response end:" + new java.util.Date().getTime());

			for (String input3 : inputList) {

				if (input3 != null && (input3.startsWith("TUEF") || input3.startsWith("ERRR"))) {

					// put id in parseOutputResponse
					// This line of code parse cibil response.
					cibilResponse = parseOutputResponse(isExistCibil, input3, ncr, id);
					cibilResponse.setSrNo(id);
					responseList.add(cibilResponse);
				}
			}

			System.out.println("Generating pdf start:" + new java.util.Date().getTime());

			// This line of code generate cibil report pdf for response data model.
			cibilResponse = getCibilPdf(responseList, ncr, id);
			// Code added on 21/1/2021 to save actual response in tempResponseList
			tempResponseList = responseList;
			
			// This line of return response type 01 (Cibil response fields)
			if (resp_type == 1) {
				return responseList;
			}

			// This line of code returns cibil report pdf as base 64 string in response.
			if (resp_type == 2) {
				// cibilResponse = getCibilPdf(responseList, ncr,id);
				cibilResponse.setSrNo(id);
				responseList = new ArrayList<CibilResponse>();
				responseList.add(cibilResponse);
				// Code added on 20/1/2021 to save pdf report to folder for easy access.
				responseList = getCibilReportContent(tempResponseList, cibilResponse, headers.get("user_id"), responseList);
				return responseList;
			}

			if (responseList.get(0).getErrorSegment() != null) {
				return responseList;
			}

			CibilResponse resp = responseList.get(0);

			String scoreVal = resp.getScoreSegment().getScoreBeanList().get(0).getScore().trim();

			int score = 0;

			if (scoreVal.equals("000-1")) {
				score = -1;
			} else {
				score = Integer.parseInt(scoreVal);
			}

			// This line of code is used to return response as 03 (Remarks-Decision)
			if (resp_type == 3) {

				// Code commented for UAT and uncommented for Prod
				// This code of block stores cibil fields in Pennant db table for further
				// reference by Pennant
				/*
				 * if(output.startsWith("TUEF")) { String
				 * customerName=resp.getNameSegment().getfName()!=null?resp.getNameSegment().
				 * getfName():"";
				 * customerName+=resp.getNameSegment().getmName()!=null?resp.getNameSegment().
				 * getmName():"";
				 * customerName+=resp.getNameSegment().getlName()!=null?resp.getNameSegment().
				 * getlName():"";
				 * 
				 * String
				 * pan=resp.getIdentificationSegment().getIdBeanList().parallelStream().filter(p
				 * ->p.getIdType().equals("01")).collect(Collectors.toList()).get(0).getIdNumber
				 * (); String dateProcessed=resp.getHeaderSegment().getDateProcessed(); Date dob
				 * = null; try { dob = new
				 * SimpleDateFormat("ddMMyyyy").parse(resp.getNameSegment().getDob()); } catch
				 * (ParseException e1) { // TODO Auto-generated catch block
				 * e1.printStackTrace(); } String
				 * memRefNo=resp.getHeaderSegment().getMemberRefNumber(); String
				 * timeProcessed=resp.getHeaderSegment().getTimeProcessed(); String
				 * scoreName=resp.getScoreSegment().getScoreBeanList().get(0).getScoreName();
				 * String scoreCardName=resp.getScoreSegment().getScoreBeanList().get(0).
				 * getScoreCardName(); String
				 * scoreCardVersion=resp.getScoreSegment().getScoreBeanList().get(0).
				 * getScoreCardVersion();
				 * 
				 * PennantRequest req=new PennantRequest(customerName, pan, dob, dateProcessed,
				 * memRefNo, timeProcessed, scoreName, scoreCardName, scoreCardVersion,
				 * scoreVal, Long.toString(id), output);
				 * 
				 * System.out.println("Pennant cibil resp insertion start:"+new
				 * java.util.Date().getTime()); dao.insertPennantCibilResponse(req);
				 * System.out.println("Pennant cibil resp insertion end:"+new
				 * java.util.Date().getTime()); }
				 */

				String userId = headers.get("user_id");

				// This line get cibil remarks against score from db
				Map<String, String> mapData = getCibilRemarksForScore(score, userId);

				// String remark=getCibilRemarksForScore(score,userId);

				String remark = mapData != null ? mapData.get("REMARKS") : null;
				String band = mapData != null ? mapData.get("BAND") : null;

				cibilResponse.setPdfContent(null);
				cibilResponse.setRemarks(remark);
				cibilResponse.setBand(band);

				cibilResponse.setSrNo(id);

				responseList = new ArrayList<CibilResponse>();
				responseList.add(cibilResponse);
				// Code added on 20/1/2021 to save pdf report to folder for easy access.
				responseList = getCibilReportContent(tempResponseList, cibilResponse, userId, responseList);
				return responseList;
			}

			// This line of code return response as 04 (Client Propelled custom logic)
			if (resp_type == 4) {

				int dpdCount = 0;
				int writtOffCount = 0;
				int willfulDefaultCount = 0;
				double totalObligation = 0;
				SimpleDateFormat sdf = new SimpleDateFormat("ddMMyyyy");

				if (responseList.get(0).getAccountSegment() != null) {

					for (OwnAccountBean own : responseList.get(0).getAccountSegment().getOwnAccountBeanList()) {

						String payHist = own.getPaymentHistory1();

						if (payHist != null) {
							// This line of code get last 12 months of payment history of user loan account
							if (payHist.length() >= 36) {
								payHist = payHist.substring(0, 36);
							}
						}

						StringBuffer payHistory = new StringBuffer(payHist);
						Set<String> setData = new HashSet<String>();

						while (payHistory.length() > 0) {
							setData.add(payHistory.substring(0, 3));
							payHistory.replace(0, 3, "");

						}

						for (String hist : setData) {
							// This line of code checks if any payment history has not following value and
							// integer value of payHistory is greater than 60
							if (!(hist.equals("XXX") || hist.equals("000") || hist.equals("STD") || hist.equals("SMA")
									|| hist.equals("SUB") || hist.equals("DBT") || hist.equals("LSS"))) {
								if (Integer.parseInt(hist) > 60) {
									dpdCount++;
									break;
								}
							}
						}

						String writtOffSettStatus = own.getWrittenOffAndSettledStatus();
						String accType = own.getAccountType();
						String writtOffPrincAmt = own.getWrittenOffAmountPrincipal();
						String wilfulDefault = own.getSuitFiledOrWilfulDefault();

						// This line of code is for accType 10(Credit card)
						if (accType != null && accType.equals("10")) {

							// It checks if account type is credit card and written off principle amount is
							// less than 7500
							if (writtOffPrincAmt != null && Integer.parseInt(writtOffPrincAmt) < 7500)
								continue;

						} else if (writtOffSettStatus != null
								&& (writtOffSettStatus.equals("02") || writtOffSettStatus.equals("03"))) {
							writtOffCount++;
						}

						if (wilfulDefault != null && wilfulDefault.equals("02")) {
							willfulDefaultCount++;
						}

						try {
							// These line of code compare account closed date with current date and checks
							// if difference is greater than 180 days or not.
							String dateClosed = own.getDateClosed();
							long diffInMillies, diff = 0;

							if (dateClosed != null) {
								diffInMillies = Math
										.abs(sdf.parse(dateClosed).getTime() - new java.util.Date().getTime());
								diff = TimeUnit.DAYS.convert(diffInMillies, TimeUnit.MILLISECONDS);

							}
							if (dateClosed != null && diff <= 180)
								continue;
							String sanctionedAmt = own.getHighCreditOrSanctionedAmount();
							// These lines of code get EMI against sanctioned amount to make it part of
							// total obligation amount based on account type.
							if (sanctionedAmt != null) {
								double princAmt = Double.parseDouble(sanctionedAmt);
								switch (accType) {
								// acc type auto loan 01
								case "01":
									totalObligation += getEMI(princAmt, 12, 5);
									break;

								// acc type housing loan 02
								case "02":
									totalObligation += getEMI(princAmt, 9, 15);
									break;

								// acc type personal loan 05
								case "05":
									totalObligation += getEMI(princAmt, 14, 3);
									break;

								// acc type business loan 51 to 60
								case "51":
								case "52":
								case "53":
								case "54":
								case "55":
								case "56":
								case "57":
								case "58":
								case "59":
								case "60":
									totalObligation += getEMI(princAmt, 18, 3);
									break;
								}

							}
						} catch (NumberFormatException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (ParseException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}

					}

					// Same above operation of own account w.r.t Others account. Total obligation
					// can't be calculated in OthersAccounts as no account type value present in
					// OthersAccounts.
					for (OthersAccountBean other : responseList.get(0).getAccountSegment().getOthersAccountBeanList()) {
						String payHist = other.getPaymentHistory1();

						if (payHist != null) {

							if (payHist.length() >= 36) {
								payHist = payHist.substring(0, 36);
							}
						}

						StringBuffer payHistory = new StringBuffer(payHist);
						Set<String> setData = new HashSet<String>();

						while (payHistory.length() > 0) {
							setData.add(payHistory.substring(0, 3));
							payHistory.replace(0, 3, "");
						}

						for (String hist : setData) {

							if (!(hist.equals("XXX") || hist.equals("000") || hist.equals("STD") || hist.equals("SMA")
									|| hist.equals("SUB") || hist.equals("DBT") || hist.equals("LSS"))) {
								if (Integer.parseInt(hist) > 60) {
									dpdCount++;
									break;
								}
							}
						}

						String writtOffSettStatus = other.getWrittenOffAndSettledStatus();
						String wilfulDefault = other.getSuitFiledOrWilfulDefault();

						if (writtOffSettStatus != null
								&& (writtOffSettStatus.equals("02") || writtOffSettStatus.equals("03"))) {
							writtOffCount++;
						}

						if (wilfulDefault != null && wilfulDefault.equals("02")) {
							willfulDefaultCount++;
						}
					}
				}

				cibilResponse = new CibilResponse();
				cibilResponse.setSrNo(id);
				String userId = headers.get("user_id");

				// This line get cibil remarks against score from db
				Map<String, String> mapData = getCibilRemarksForScore(score, userId);

				String band = mapData != null ? mapData.get("BAND") : null;

				cibilResponse.setBand(band);

				cibilResponse.setTotalObligation(Math.round(totalObligation));

				if (score >= 710) {
					cibilResponse.setIsMinCibilScore(true);
				} else {
					cibilResponse.setIsMinCibilScore(false);
				}

				if (dpdCount > 0) {
					cibilResponse.setIs60AboveDpd(true);
				} else {
					cibilResponse.setIs60AboveDpd(false);
				}

				if (writtOffCount > 0) {
					cibilResponse.setIsAccWrittOffOrSettled(true);
				} else {
					cibilResponse.setIsAccWrittOffOrSettled(false);
				}

				if (willfulDefaultCount > 0) {
					cibilResponse.setIsAccWillfulDefault(true);
				} else {
					cibilResponse.setIsAccWillfulDefault(false);
				}

				responseList = new ArrayList<CibilResponse>();
				responseList.add(cibilResponse);
				// Code added on 20/1/2021 to save pdf report to folder for easy access.
				responseList = getCibilReportContent(tempResponseList, cibilResponse, userId, responseList);
				return responseList;

			}

			// This line of code return response as 05 (Client Credence custom logic)
			if (resp_type == 5) {

				int dpdCount = 0;
				int totalDpdAmount = 0;
				// String prevHist=null;
				int counter = 0;

				if (responseList.get(0).getAccountSegment() != null) {

					for (OwnAccountBean own : responseList.get(0).getAccountSegment().getOwnAccountBeanList()) {

						String payHist = own.getPaymentHistory1()
								+ (own.getPaymentHistory2() != null ? own.getPaymentHistory2() : "");
						totalDpdAmount += own.getAmountOverdue() != null ? Integer.parseInt(own.getAmountOverdue()) : 0;

						if (payHist != null) {
							// This line of code get last 24 months of payment history of user loan account
							if (payHist.length() >= 72) {
								payHist = payHist.substring(0, 72);
							}
						}

						StringBuffer payHistory = new StringBuffer(payHist);
						Set<String> setData = new HashSet<String>();

						while (payHistory.length() > 0) {
							setData.add(payHistory.substring(0, 3));
							payHistory.replace(0, 3, "");

						}

						for (String hist : setData) {

							// This line of code checks if any payment history has not following value and
							// integer value of payHistory is greater than 60
							if (!(hist.equals("XXX") || hist.equals("000") || hist.equals("STD") || hist.equals("SMA")
									|| hist.equals("SUB") || hist.equals("DBT") || hist.equals("LSS"))) {
								if (Integer.parseInt(hist) > 30) {
									++counter;
								} else {
									counter = 0;
								}

								if (counter > 6) {
									dpdCount++;
									break;
									// counter=1;
								}
							} else {
								counter = 0;
							}
						}
					}

					// Same above operation of own account w.r.t Others account. Total obligation
					// can't be calculated in OthersAccounts as no account type value present in
					// OthersAccounts.
					for (OthersAccountBean other : responseList.get(0).getAccountSegment().getOthersAccountBeanList()) {
						String payHist = other.getPaymentHistory1()
								+ (other.getPaymentHistory2() != null ? other.getPaymentHistory2() : "");
						totalDpdAmount += other.getAmountOverdue() != null ? Integer.parseInt(other.getAmountOverdue())
								: 0;
						if (payHist != null) {

							if (payHist.length() >= 72) {
								payHist = payHist.substring(0, 72);
							}
						}

						StringBuffer payHistory = new StringBuffer(payHist);
						Set<String> setData = new HashSet<String>();

						while (payHistory.length() > 0) {
							setData.add(payHistory.substring(0, 3));
							payHistory.replace(0, 3, "");
						}

						for (String hist : setData) {

							if (!(hist.equals("XXX") || hist.equals("000") || hist.equals("STD") || hist.equals("SMA")
									|| hist.equals("SUB") || hist.equals("DBT") || hist.equals("LSS"))) {
								if (Integer.parseInt(hist) > 30) {
									++counter;
								} else {
									counter = 0;
								}

								if (counter > 6) {
									dpdCount++;
									break;
									// counter=1;
								}
							} else {
								counter = 0;
							}
						}
					}
				}

				cibilResponse = new CibilResponse();
				cibilResponse.setSrNo(id);
				String userId = headers.get("user_id");

				// This line get cibil remarks against score from db
				Map<String, String> mapData = getCibilRemarksForScore(score, userId);

				String band = mapData != null ? mapData.get("BAND") : null;

				switch (loan_type.toUpperCase()) {
				case "COLLEGE":
					switch (product_type.toUpperCase()) {
					case "PI/SI/EMI":
					case "PI":
					case "SI":
					case "EMI":
						if (score == -1 || score > 680) {
							cibilResponse.setRemarks("Approve");
							cibilResponse.setBand(band);
						} else if (score < 620) {
							cibilResponse.setRemarks("Reject");
							cibilResponse.setBand(band);
						} else if (score >= 620 && score <= 680) {
							if (totalDpdAmount < 10000 && dpdCount == 0) {
								cibilResponse.setRemarks("Approve");
								cibilResponse.setBand(band);
							}
						}

						break;
					case "ISA":
						if (score == -1 || score > 620) {
							cibilResponse.setRemarks("Approve");
							cibilResponse.setBand(band);
						}
						break;
					default:
						cibilResponse.setRemarks("Reject");
						cibilResponse.setBand(band);
						break;
					}

					break;
				case "SHORT TICKET":
					switch (product_type.toUpperCase()) {
					case "EMI":
						if (score == -1 || score > 680) {
							cibilResponse.setRemarks("Approve");
							cibilResponse.setBand(band);
						}
						break;
					case "ISA":
						if (score == -1 || score > 650) {
							cibilResponse.setRemarks("Approve");
							cibilResponse.setBand(band);
						}
						break;
					default:
						cibilResponse.setRemarks("Reject");
						cibilResponse.setBand(band);
						break;
					}
					break;
				default:
					cibilResponse.setRemarks("Reject");
					cibilResponse.setBand(band);
					break;
				}
				responseList = new ArrayList<CibilResponse>();
				responseList.add(cibilResponse);
				// Code added on 20/1/2021 to save pdf report to folder for easy access.
				responseList = getCibilReportContent(tempResponseList, cibilResponse, userId, responseList);
				return responseList;
			}
			if (resp_type == 6) {

				int dpdCount = 0;
				int writtOffCount = 0;
				int willfulDefaultCount = 0;
				double totalObligation = 0;
				SimpleDateFormat sdf = new SimpleDateFormat("ddMMyyyy");

				if (responseList.get(0).getAccountSegment() != null) {

					for (OwnAccountBean own : responseList.get(0).getAccountSegment().getOwnAccountBeanList()) {

						String payHist = own.getPaymentHistory1();

						if (payHist != null) {
							// This line of code get last 12 months of payment history of user loan account
							if (payHist.length() >= 36) {
								payHist = payHist.substring(0, 36);
							}
						}

						StringBuffer payHistory = new StringBuffer(payHist);
						Set<String> setData = new HashSet<String>();

						while (payHistory.length() > 0) {
							setData.add(payHistory.substring(0, 3));
							payHistory.replace(0, 3, "");

						}
						String accType = own.getAccountType();

						for (String hist : setData) {
							// This line of code checks if any payment history has not following value and
							// integer value of payHistory is greater than 60
							if (!(hist.equals("XXX") || hist.equals("000") || hist.equals("STD") || hist.equals("SMA")
									|| hist.equals("SUB") || hist.equals("DBT") || hist.equals("LSS"))
									&& !(accType.equals("36") || accType.equals("07") || accType.equals("10"))) {
								if (Integer.parseInt(hist) >= 60) {
									dpdCount++;
									break;
								}
							}
						}

						String writtOffSettStatus = own.getWrittenOffAndSettledStatus();
						String writtOffPrincAmt = own.getWrittenOffAmountPrincipal();
						String wilfulDefault = own.getSuitFiledOrWilfulDefault();

						if (wilfulDefault != null && wilfulDefault.equals("02")) {
							willfulDefaultCount++;
						}

						// This line of code is for accType 10(Credit card)
						if (accType != null && accType.equals("10")) {

							// It checks if account type is credit card and written off principle amount is
							// less than 7500
							if (writtOffPrincAmt != null && Integer.parseInt(writtOffPrincAmt) < 7500)
								continue;

						} else if (writtOffSettStatus != null
								&& (writtOffSettStatus.equals("02") || writtOffSettStatus.equals("03"))) {
							writtOffCount++;
						}
						try {
							// These line of code compare account closed date with current date and checks
							// if difference is greater than 180 days or not.
							String dateClosed = own.getDateClosed();
							long diffInMillies, diff = 0;

							if (dateClosed != null) {
								diffInMillies = Math
										.abs(sdf.parse(dateClosed).getTime() - new java.util.Date().getTime());
								diff = TimeUnit.DAYS.convert(diffInMillies, TimeUnit.MILLISECONDS);

							}
							if (dateClosed != null && diff <= 180)
								continue;
							String sanctionedAmt = own.getHighCreditOrSanctionedAmount();
							// These lines of code get EMI against sanctioned amount to make it part of
							// total obligation amount based on account type.
							if (sanctionedAmt != null) {
								double princAmt = Double.parseDouble(sanctionedAmt);
								switch (accType) {
								// acc type auto loan 01
								case "01":
									totalObligation += getEMI(princAmt, 12, 5);
									break;

								// acc type housing loan 02
								case "02":
									totalObligation += getEMI(princAmt, 9, 15);
									break;

								// acc type personal loan 05
								case "05":
									totalObligation += getEMI(princAmt, 14, 3);
									break;

								// acc type business loan 51 to 60
								case "51":
								case "52":
								case "53":
								case "54":
								case "55":
								case "56":
								case "57":
								case "58":
								case "59":
								case "60":
									totalObligation += getEMI(princAmt, 18, 3);
									break;
								}

							}
						} catch (NumberFormatException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (ParseException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}

					}

					// Same above operation of own account w.r.t Others account. Total obligation
					// can't be calculated in OthersAccounts as no account type value present in
					// OthersAccounts.
					for (OthersAccountBean other : responseList.get(0).getAccountSegment().getOthersAccountBeanList()) {
						String payHist = other.getPaymentHistory1();

						if (payHist != null) {

							if (payHist.length() >= 36) {
								payHist = payHist.substring(0, 36);
							}
						}

						StringBuffer payHistory = new StringBuffer(payHist);
						Set<String> setData = new HashSet<String>();

						while (payHistory.length() > 0) {
							setData.add(payHistory.substring(0, 3));
							payHistory.replace(0, 3, "");
						}

						for (String hist : setData) {

							if (!(hist.equals("XXX") || hist.equals("000") || hist.equals("STD") || hist.equals("SMA")
									|| hist.equals("SUB") || hist.equals("DBT") || hist.equals("LSS"))) {
								if (Integer.parseInt(hist) >= 60) {
									dpdCount++;
									break;
								}
							}
						}

						String writtOffSettStatus = other.getWrittenOffAndSettledStatus();
						String wilfulDefault = other.getSuitFiledOrWilfulDefault();

						if (writtOffSettStatus != null
								&& (writtOffSettStatus.equals("02") || writtOffSettStatus.equals("03"))) {
							writtOffCount++;
						}

						if (wilfulDefault != null && wilfulDefault.equals("02")) {
							willfulDefaultCount++;
						}
					}
				}
				String userId = headers.get("user_id");
				Map<String, String> mapData = getCibilRemarksForScore(score, userId);
				String remark = mapData != null ? mapData.get("REMARKS") : null;
				cibilResponse = new CibilResponse();
				cibilResponse.setSrNo(id);
				cibilResponse.setTotalObligation(Math.round(totalObligation));
				cibilResponse.setRemarks(remark);
				if (dpdCount > 0) {
					cibilResponse.setIs60AboveDpd(true);
				} else {
					cibilResponse.setIs60AboveDpd(false);
				}

				if (writtOffCount > 0) {
					cibilResponse.setIsAccWrittOffOrSettled(true);
				} else {
					cibilResponse.setIsAccWrittOffOrSettled(false);
				}

				if (willfulDefaultCount > 0) {
					cibilResponse.setIsAccWillfulDefault(true);
				} else {
					cibilResponse.setIsAccWillfulDefault(false);
				}
				responseList = new ArrayList<CibilResponse>();
				responseList.add(cibilResponse);
				// Code added on 20/1/2021 to save pdf report to folder for easy access.
				responseList = getCibilReportContent(tempResponseList, cibilResponse, userId, responseList);
				return responseList;

			}
			if (resp_type == 7) {

				int dpdCount = 0;
				int writtOffCount = 0;
				int willfulDefaultCount = 0;
				double totalObligation = 0;
				SimpleDateFormat sdf = new SimpleDateFormat("ddMMyyyy");

				if (responseList.get(0).getAccountSegment() != null) {

					for (OwnAccountBean own : responseList.get(0).getAccountSegment().getOwnAccountBeanList()) {

						String payHist = own.getPaymentHistory1();

						if (payHist != null) {
							// This line of code get last 12 months of payment history of user loan account
							if (payHist.length() >= 36) {
								payHist = payHist.substring(0, 36);
							}
						}

						StringBuffer payHistory = new StringBuffer(payHist);
						Set<String> setData = new HashSet<String>();

						while (payHistory.length() > 0) {
							setData.add(payHistory.substring(0, 3));
							payHistory.replace(0, 3, "");

						}
						String accType = own.getAccountType();

						for (String hist : setData) {
							// This line of code checks if any payment history has not following value and
							// integer value of payHistory is greater than 60
							if (!(hist.equals("XXX") || hist.equals("000") || hist.equals("STD") || hist.equals("SMA")
									|| hist.equals("SUB") || hist.equals("DBT") || hist.equals("LSS"))
									&& !(accType.equals("36") || accType.equals("07") || accType.equals("10"))) {
								if (Integer.parseInt(hist) >= 30) {
									dpdCount++;
									break;
								}
							}
						}

						String writtOffSettStatus = own.getWrittenOffAndSettledStatus();
						String writtOffPrincAmt = own.getWrittenOffAmountPrincipal();
						String wilfulDefault = own.getSuitFiledOrWilfulDefault();

						if (wilfulDefault != null && wilfulDefault.equals("02")) {
							willfulDefaultCount++;
						}

						// This line of code is for accType 10(Credit card)
						if (accType != null && accType.equals("10")) {

							// It checks if account type is credit card and written off principle amount is
							// less than 7500
							if (writtOffPrincAmt != null && Integer.parseInt(writtOffPrincAmt) < 7500)
								continue;

						} else if (writtOffSettStatus != null
								&& (writtOffSettStatus.equals("02") || writtOffSettStatus.equals("03"))) {
							writtOffCount++;
						}
						try {
							// These line of code compare account closed date with current date and checks
							// if difference is greater than 180 days or not.
							String dateClosed = own.getDateClosed();
							long diffInMillies, diff = 0;

							if (dateClosed != null) {
								diffInMillies = Math
										.abs(sdf.parse(dateClosed).getTime() - new java.util.Date().getTime());
								diff = TimeUnit.DAYS.convert(diffInMillies, TimeUnit.MILLISECONDS);

							}
							if (dateClosed != null && diff <= 180)
								continue;
							String sanctionedAmt = own.getHighCreditOrSanctionedAmount();
							// These lines of code get EMI against sanctioned amount to make it part of
							// total obligation amount based on account type.
							if (sanctionedAmt != null) {
								double princAmt = Double.parseDouble(sanctionedAmt);
								switch (accType) {
								// acc type auto loan 01
								case "01":
									totalObligation += getEMI(princAmt, 12, 5);
									break;

								// acc type housing loan 02
								case "02":
									totalObligation += getEMI(princAmt, 9, 15);
									break;

								// acc type personal loan 05
								case "05":
									totalObligation += getEMI(princAmt, 14, 3);
									break;

								// acc type business loan 51 to 60
								case "51":
								case "52":
								case "53":
								case "54":
								case "55":
								case "56":
								case "57":
								case "58":
								case "59":
								case "60":
									totalObligation += getEMI(princAmt, 18, 3);
									break;
								}

							}
						} catch (NumberFormatException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (ParseException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}

					}

					// Same above operation of own account w.r.t Others account. Total obligation
					// can't be calculated in OthersAccounts as no account type value present in
					// OthersAccounts.
					for (OthersAccountBean other : responseList.get(0).getAccountSegment().getOthersAccountBeanList()) {
						String payHist = other.getPaymentHistory1();

						if (payHist != null) {

							if (payHist.length() >= 36) {
								payHist = payHist.substring(0, 36);
							}
						}

						StringBuffer payHistory = new StringBuffer(payHist);
						Set<String> setData = new HashSet<String>();

						while (payHistory.length() > 0) {
							setData.add(payHistory.substring(0, 3));
							payHistory.replace(0, 3, "");
						}

						for (String hist : setData) {

							if (!(hist.equals("XXX") || hist.equals("000") || hist.equals("STD") || hist.equals("SMA")
									|| hist.equals("SUB") || hist.equals("DBT") || hist.equals("LSS"))) {
								if (Integer.parseInt(hist) >= 30) {
									dpdCount++;
									break;
								}
							}
						}

						String writtOffSettStatus = other.getWrittenOffAndSettledStatus();
						String wilfulDefault = other.getSuitFiledOrWilfulDefault();

						if (writtOffSettStatus != null
								&& (writtOffSettStatus.equals("02") || writtOffSettStatus.equals("03"))) {
							writtOffCount++;
						}

						if (wilfulDefault != null && wilfulDefault.equals("02")) {
							willfulDefaultCount++;
						}
					}
				}
				String userId = headers.get("user_id");
				Map<String, String> mapData = getCibilRemarksForScore(score, userId);
				String remark = mapData != null ? mapData.get("REMARKS") : null;
				cibilResponse = new CibilResponse();
				cibilResponse.setSrNo(id);
				cibilResponse.setTotalObligation(Math.round(totalObligation));
				cibilResponse.setRemarks(remark);
				if (writtOffCount > 0) {
					cibilResponse.setIsAccWrittOffOrSettled(true);
				} else {
					cibilResponse.setIsAccWrittOffOrSettled(false);
				}

				if (willfulDefaultCount > 0) {
					cibilResponse.setIsAccWillfulDefault(true);
				} else {
					cibilResponse.setIsAccWillfulDefault(false);
				}
				responseList = new ArrayList<CibilResponse>();
				responseList.add(cibilResponse);
				// Code added on 20/1/2021 to save pdf report to folder for easy access.
				responseList = getCibilReportContent(tempResponseList, cibilResponse, userId, responseList);
				return responseList;

			}
			if (resp_type == 8) {

				int dpdCount = 0;
				int writtOffCount = 0;
				int willfulDefaultCount = 0;
				int enqAmount = Integer.parseInt(request.getEnqAmount());
				double totalObligation = 0;
				boolean isDpd = false;
				boolean isWriteOff = false;
				boolean isWillfullDefault = false;
				SimpleDateFormat sdf = new SimpleDateFormat("ddMMyyyy");

				if (responseList.get(0).getAccountSegment() != null) {
					int count = 0;
					for (OwnAccountBean own : responseList.get(0).getAccountSegment().getOwnAccountBeanList()) {
						String payHist = own.getPaymentHistory1();
						if (payHist != null) {
							// This line of code get last 12 months of payment history of user loan account
							if (payHist.length() >= 36) {
								payHist = payHist.substring(0, 36);
							}
						}
						StringBuffer payHistory = new StringBuffer(payHist);
						Set<String> setData = new HashSet<String>();
						while (payHistory.length() > 0) {
							setData.add(payHistory.substring(0, 3));
							payHistory.replace(0, 3, "");

						}
						String accType = own.getAccountType();
						if (enqAmount <= 100000) {
							for (String hist : setData) {
								// This line of code checks if any payment history has not following value and
								// integer value of payHistory is greater than 60
								if (!(hist.equals("XXX") || hist.equals("000") || hist.equals("STD")
										|| hist.equals("SMA") || hist.equals("SUB") || hist.equals("DBT")
										|| hist.equals("LSS"))) {
									if (Integer.parseInt(hist) > 30) {
										dpdCount++;
										isDpd = true;
										break;
									}
								}
							}
						} else if (enqAmount >= 100000 && setData.size() > 0 && setData != null) {
							System.out.println("else1:if:enqAmount >= 100000 && setData.size() > 0 && setData != null");
							// Added code on 11/5/2020
							// Checking dpdCount for ownAccounts hist>=60 in last 12 months
							for (String hist : setData) {
								// This line of code checks if any payment history has not following value and
								// integer value of payHistory is greater than 60
								if (!(hist.equals("XXX") || hist.equals("000") || hist.equals("STD")
										|| hist.equals("SMA") || hist.equals("SUB") || hist.equals("DBT")
										|| hist.equals("LSS"))) {
									if (Integer.parseInt(hist) >= 60) {
										dpdCount++;
										isDpd = true;
										System.out.println("==========dpdCount1="+dpdCount);
										break;
									}
								}
							}
						}
						/*else {
							System.out.println("else1:enqAmount >= 100000 && setData.size() > 0 && setData != null");
							for (String hist : setData) {
								// This line of code checks if any payment history has not following value and
								// integer value of payHistory is greater than 60
								if (!(hist.equals("XXX") || hist.equals("000") || hist.equals("STD")
										|| hist.equals("SMA") || hist.equals("SUB") || hist.equals("DBT")
										|| hist.equals("LSS"))) {
									if (Integer.parseInt(hist) >= 60) {
										dpdCount++;
										System.out.println("==========dpdCount2="+dpdCount);
										break;
									}
								}
							}
						}*/
						String writtOffSettStatus = own.getWrittenOffAndSettledStatus();
						String writtOffPrincAmt = own.getWrittenOffAmountPrincipal();
						String wilfulDefault = own.getSuitFiledOrWilfulDefault();
						if (wilfulDefault != null && wilfulDefault.equals("02")) {
							willfulDefaultCount++;
							isWillfullDefault = true;
						}
						// This line of code is for accType 10(Credit card)
						System.out.println("accType1="+accType+" writtOffSettStatus1="+writtOffSettStatus+" writtOffCount1="+writtOffCount);
						if (accType != null && accType.equals("10")) {
							// It checks if account type is credit card and written off principle amount is
							// less than 7500
							if (writtOffPrincAmt != null && Integer.parseInt(writtOffPrincAmt) < 7500)
								continue;
						} else if (writtOffSettStatus != null
								&& (writtOffSettStatus.equals("02") || writtOffSettStatus.equals("03"))) {
							writtOffCount++;
							isWriteOff = true;
						}
						try {
							// These line of code compare account closed date with current date and checks
							// if difference is greater than 180 days or not.
							String dateClosed = own.getDateClosed();
							long diffInMillies, diff = 0;

							if (dateClosed != null) {
								diffInMillies = Math
										.abs(sdf.parse(dateClosed).getTime() - new java.util.Date().getTime());
								diff = TimeUnit.DAYS.convert(diffInMillies, TimeUnit.MILLISECONDS);
							}
							if (dateClosed != null && diff <= 180)
								continue;
							String sanctionedAmt = own.getHighCreditOrSanctionedAmount();
							String currentBalance = own.getCurrentBalance();
							String closeDate = own.getDateClosed();
							// EMI amount
							String emiAmount = own.getEMIAmount();
							// These lines of code get EMI against sanctioned amount to make it part of
							// total obligation amount based on account type.
							System.out.println("===================Started obligation calculation==================Record="+count++);
							double princAmt = Double.parseDouble((sanctionedAmt!=null && sanctionedAmt.trim()!="")?sanctionedAmt:"0");
							double princCurBal = Double.parseDouble((currentBalance!=null && currentBalance.trim()!="")?currentBalance:"0");
							// Setting EMI amount if not null
							System.out.println("Prinicipal Amount princAmt="+princAmt+" princCurBal="+princCurBal+" emiAmount="+emiAmount);
							// Calculation of obligation if EMI amount > 0 - added on 16/2/2021
							System.out.println("Logger: srNO="+cibilResponse.getSrNo()+"sanctionedAmt="+sanctionedAmt+ " princCurBal=" +princCurBal + " closeDate=" +closeDate+" EMI="+emiAmount);
							if((emiAmount!=null && emiAmount.trim()!="") && (closeDate == null || closeDate.trim()=="")) {
								System.out.println("Entered into if block:EMI>0");
								try {
									double emiAmt = Double.parseDouble((emiAmount!=null && emiAmount.trim()!="")?emiAmount:sanctionedAmt);							
									System.out.println("EMI Amount is emiAmount="+emiAmount);
									if(emiAmt>0) {
										System.out.println("EMI Amount is >0 emiAmount="+emiAmount);
										princAmt = emiAmt;
										totalObligation += princAmt;
									}
								}catch (Exception e) {
									System.out.println("Exception:emiAmount="+e);
									princAmt = princAmt;
								}								
							}else //{
								// Calculation of obligation if EMI amount < 0 and sanctioned amount >0 - added on 16/2/2021 
							if (sanctionedAmt != null && princCurBal > 0
									&& ((closeDate == null || closeDate.trim() == "")
											&& (emiAmount == null || emiAmount == ""))) {
									System.out.println("CloseDate=null & sanctionedAmount>0 & emiAmount=null");
									switch (accType) {
									// acc type auto loan 01
									case "01":
										// Old code to calcluate EMI for auto loan as per old ROI
										//totalObligation += getEMI(princAmt, 12, 5);
										// Code added on 8/1/2021 to calculate EMI for auto loan at ROI=2.20%
										totalObligation += getEMI(princAmt, 2.20, 5);
										System.out.println("Account Type="+accType+" princAmt="+princAmt+" totalObligation="+totalObligation);
										break;
									// acc type housing loan 02
									case "02":
										// Old code to calcluate EMI for housing loan as per old ROI
										//totalObligation += getEMI(princAmt, 9, 15);
										// Code added on 8/1/2021 to calculate EMI for Housing loan at ROI=1%
										totalObligation += getEMI(princAmt, 1, 5);
										System.out.println("Account Type="+accType+" princAmt="+princAmt+" totalObligation="+totalObligation);
										break;
									// acc type Property Loan 03
									case "03":
										// Code added on 8/2/2021 to calculate EMI for Property loan at ROI=1%
										totalObligation += getEMI(princAmt, 1, 5);
										System.out.println("Account Type="+accType+" princAmt="+princAmt+" totalObligation="+totalObligation);
										break;
									// acc type personal loan 05
									case "05":
										// Old code to calcluate EMI for personal loan as per old ROI
										//totalObligation += getEMI(princAmt, 14, 3);
										// Code added on 8/1/2021 to calculate EMI for personal loan at ROI=2.80%
										totalObligation += getEMI(princAmt, 2.80, 5);
										System.out.println("Account Type="+accType+" princAmt="+princAmt+" totalObligation="+totalObligation);
										break;
									// acc type consumer durable 06
									case "06":
										// Code added on 14/1/2021 to calculate EMI for consumer durable loan at ROI=9%
										totalObligation += getEMI(princAmt, 9, 5);
										System.out.println("Account Type="+accType+" princAmt="+princAmt+" totalObligation="+totalObligation);
										break;
									// acc type gold 07
									case "07":
										// Code added on 14/1/2021 to calculate EMI for gold loan at ROI=1.50%
										totalObligation += getEMI(princAmt, 1.5, 5);
										System.out.println("Account Type="+accType+" princAmt="+princAmt+" totalObligation="+totalObligation);
										break;
									// acc type education loan 8
									case "08":
										// Code added on 12/1/2021 to calculate EMI for education loan at ROI=2.80%
										totalObligation += getEMI(princAmt, 1.80, 5);
										System.out.println("Account Type="+accType+" princAmt="+princAmt+" totalObligation="+totalObligation);
										break;
									// acc type credit card 10
									case "10":
										// Code added on 12/1/2021 to calculate EMI for credit card at ROI=5%
										if(princCurBal>0) {
											princAmt = princCurBal;
										}
										totalObligation += getEMI(princAmt, 5, 5);
										System.out.println("Account Type="+accType+" princAmt="+princAmt+" totalObligation="+totalObligation);
										break;
									// acc type two wheeler 13
									case "13":
										// Code added on 14/1/2021 to calculate EMI for two wheeler loan at ROI=4.30%
										totalObligation += getEMI(princAmt, 4.30, 5);
										System.out.println("Account Type="+accType+" princAmt="+princAmt+" totalObligation="+totalObligation);
										break;
									// acc type commercial vehicle 17
									case "17":
										// Code added on 18/1/2021 to calculate EMI for commercial vehicle loan at ROI=2.4%
										totalObligation += getEMI(princAmt, 2.4, 5);
										System.out.println("Account Type="+accType+" princAmt="+princAmt+" totalObligation="+totalObligation);
										break;
									// acc type business loan 51 to 60
									case "51":
									case "52":
									case "53":
									case "54":
									case "55":
									case "56":
									case "57":
									case "58":
									case "59":
									case "60":
										// Old code to calcluate EMI for business loan as per old ROI
										//totalObligation += getEMI(princAmt, 18, 3);
										// Code added on 8/1/2021 to calculate EMI for business loan at ROI=3.50%
										totalObligation += getEMI(princAmt, 3.50, 5);
										System.out.println("Account Type="+accType+" princAmt="+princAmt+" totalObligation="+totalObligation);
										break;
									}
	
								}
							//}
						} catch (NumberFormatException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (ParseException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						System.out.println("===============End of Obligation calculation===================");
					}

					
					// Same above operation of own account w.r.t Others account. Total obligation
					// can't be calculated in OthersAccounts as no account type value present in
					// OthersAccounts.
					for (OthersAccountBean other : responseList.get(0).getAccountSegment().getOthersAccountBeanList()) {
						String payHist = other.getPaymentHistory1();

						if (payHist != null) {

							if (payHist.length() >= 36) {
								payHist = payHist.substring(0, 36);
							}
						}

						StringBuffer payHistory = new StringBuffer(payHist);
						Set<String> setData = new HashSet<String>();

						while (payHistory.length() > 0) {
							setData.add(payHistory.substring(0, 3));
							payHistory.replace(0, 3, "");
						}
						
						if (enqAmount >= 100000 && setData.size() > 0 && setData != null) {
							System.out.println("else2:if:enqAmount >= 100000 && setData.size() > 0 && setData != null");
							// Added code on 11/5/2020
							// Checking dpdCount for otherAccounts hist>=60 in last 12 months
							for (String hist : setData) {
								// This line of code checks if any payment history has not following value and
								// integer value of payHistory is greater than 60
								if (!(hist.equals("XXX") || hist.equals("000") || hist.equals("STD")
										|| hist.equals("SMA") || hist.equals("SUB") || hist.equals("DBT")
										|| hist.equals("LSS"))) {
									if (Integer.parseInt(hist) >= 60) {
										dpdCount++;
										isDpd = true;
										System.out.println("==========dpdCount3="+dpdCount);
										break;
									}
								}
							}
						} else {
							System.out.println("else2:enqAmount >= 100000 && setData.size() > 0 && setData != null");
							for (String hist : setData) {
								if (!(hist.equals("XXX") || hist.equals("000") || hist.equals("STD")
										|| hist.equals("SMA") || hist.equals("SUB") || hist.equals("DBT")
										|| hist.equals("LSS"))) {
									if (Integer.parseInt(hist) >= 60) {
										dpdCount++;
										isDpd = true;
										System.out.println("==========dpdCount4="+dpdCount);
										break;
									}
								}
							}
						}

						String writtOffSettStatus = other.getWrittenOffAndSettledStatus();
						String wilfulDefault = other.getSuitFiledOrWilfulDefault();

						if (writtOffSettStatus != null
								&& (writtOffSettStatus.equals("02") || writtOffSettStatus.equals("03"))) {
							writtOffCount++;
							isWriteOff = true;
						}
						System.out.println("writtOffSettStatus="+writtOffSettStatus+" writtOffCount="+writtOffCount);
						if (wilfulDefault != null && wilfulDefault.equals("02")) {
							willfulDefaultCount++;
							isWillfullDefault = true;
						}
						System.out.println("wilfulDefault="+wilfulDefault+" willfulDefaultCount="+willfulDefaultCount);
					}
				}
				cibilResponse = new CibilResponse();
				cibilResponse.setSrNo(id);
				
				if(dpdCount>0||writtOffCount > 0) {
					cibilResponse.setRemarks("Reject");
				}else {
					if (enqAmount <= 100000) {
						if(score==-1 || score==0) {
							cibilResponse.setRemarks("Refer");
						}
						else if(score>=1 && score<=299) {
							cibilResponse.setRemarks("Refer");
						}
						else if(score>=300 && score<=709) {
							cibilResponse.setRemarks("Reject");
						}
						else if(score>=710 && score<=766) {
							cibilResponse.setRemarks("Refer");
						}
						else if(score>=767 && score<=999) {
							cibilResponse.setRemarks("Approve");
						}
					}
					else {
						if (willfulDefaultCount > 0) {
							cibilResponse.setRemarks("Reject");
						} else {
							if (enqAmount > 100000 && enqAmount <= 200000) {
								if(score==-1 || score==0) {
									cibilResponse.setRemarks("Refer");
								}
								else if(score>=1 && score<=299) {
									cibilResponse.setRemarks("Refer");
								}
								else if(score>=300 && score<=709) {
									cibilResponse.setRemarks("Reject");
								}
								else if(score>=710 && score<=999) {
									cibilResponse.setRemarks("Approve");
								}
							}else if(enqAmount > 200000 && enqAmount <= 500000) {
								if(score==-1 || score==0) {
									cibilResponse.setRemarks("Refer");
								}
								else if(score>=1 && score<=299) {
									cibilResponse.setRemarks("Refer");
								}
								else if(score>=300 && score<=734) {
									cibilResponse.setRemarks("Reject");
								}
								else if(score>=735 && score<=999) {
									cibilResponse.setRemarks("Approve");
								}
							}
						}
					}
				}
				// Set band in cibil response 11/3/2021 ----
				String band = commonUtil.getCibilBand(score, cibilResponse.getSrNo());
				cibilResponse.setBand(band);
				// End of setting CIBIL band -----
				// Setting enquries in cibil response
				HashMap<String, String> enquries = commonUtil.getEnquries(cibilResponse);
				String past30daysEnq = enquries.get("past30daysEnq");
				String past12MonthsEnq = enquries.get("past12MonthsEnq");
				String past24MonthsEnq = enquries.get("past24MonthsEnq");
				System.out.println("Enquries:past30daysEnq=" + past30daysEnq + " past12MonthsEnq=" + past12MonthsEnq
						+ " past24MonthsEnq=" + past24MonthsEnq);
				cibilResponse.setPast12MonthsEnq(past12MonthsEnq);
				cibilResponse.setPast24MonthsEnq(past24MonthsEnq);
				cibilResponse.setPast30daysEnq(past30daysEnq);
				cibilResponse.setDescription(commonUtil.getRejectRemarks(isWriteOff, isDpd, cibilResponse.getSrNo(), isWillfullDefault));
				// End of setting enquries count in response ---
				// Setting additionalMatch param in cibil response. - 19/3/2021
				System.out.println("------isAdditionalMatch="+cibilResponse.getAdditionalMatch());
				String isAdditionalMatch = commonUtil.isAdditionalMatch(tempResponseList, cibilResponse.getSrNo());
				cibilResponse.setAdditionalMatch(isAdditionalMatch);
				

				System.out.println("SrNO="+cibilResponse.getSrNo()+ " Final dpdCount="+dpdCount+" writtOffCount="+writtOffCount+" Score="+score+" band="+band);
				cibilResponse.setTotalObligation(Math.round(totalObligation));
				responseList = new ArrayList<CibilResponse>();
				responseList.add(cibilResponse);
				System.out.println("Final cibilResponse Response= "+cibilResponse);
				// Code added on 20/1/2021 to save pdf report to folder for easy access.
				responseList = getCibilReportContent(tempResponseList, cibilResponse, headers.get("user_id"), responseList);
				return responseList;
			}
			// Getting response for DIGAKJB partner
			// Code added on 24/3/2021
			if(resp_type == 9) {
				System.out.println("--- entered resp_type=9 ------");
				ResponseType9 responseType9 = new ResponseType9();
				responseList = responseType9.getResponseType09(id, headers, score, responseList);
				System.out.println("--- end of processing resp_type=9 ------");
				return responseList;
			}

		} else {
			// This else block return errors of cibil reponse in db table and returns as api
			// response.
			cibilResponse = new CibilResponse();
			cibilResponse.setErrors(errorList);

			long id = this.storeErrorResponse(inputRequest, "", headerInfo.toString(), httpRequest.getRemoteAddr(),
					headers.get("user_id"));
			cibilResponse.setSrNo(id);
			responseList = new ArrayList<CibilResponse>();
			responseList.add(cibilResponse);
			// Code added on 20/1/2021 to save pdf report to folder for easy access.
			responseList = getCibilReportContent(tempResponseList, cibilResponse, headers.get("user_id"), responseList);
			return responseList;
		}
		return null;
	}
	
	// Code commented on 03/02/2021
	/*public double getEMI(double principal, double rate, double time) {
		rate = rate / (12 * 100);
		time = time * 12;
		double emi = (principal * rate * Math.pow(1 + rate, time)) / (Math.pow(1 + rate, time) - 1);
		return emi;
	}*/
	// Code added on 03/02/2021 with new EMI 
	public double getEMI(double principal, double rate, double time) {
		double emi = (principal * rate)/100;
		return emi;
	}

	public CibilResponse parseOutputResponse(boolean isExistCibil, String input, NewCibilRequest ncr, long id) {
		ParseCibilResponse pcr = new ParseCibilResponse();
		// This line of code parse cibil response to data model.
		CibilResponse cibilResponse = pcr.getParsedCibilResponse(new StringBuilder(input));

		if (cibilResponse != null && isExistCibil == false) {
			// if (cibilResponse != null && (existMapData==null ||
			// existMapData.get("output")==null)) {
			System.out.println("Storing parsed response to db start:" + new java.util.Date().getTime());
			// This line store parsed response object in db table.
			pcr.storeParsedCibilResponse(cibilResponse, ncr.getHeaderSegment().getMobileRefNo(),
					ncr.getHeaderSegment().getLeadId(), id);

			System.out.println("Storing parsed response to db end:" + new java.util.Date().getTime());
		}

		return cibilResponse;
	}

	public CibilResponse getCibilPdf(List<CibilResponse> cibilRespList, NewCibilRequest ncr, long id) {
		CibilResponse cibilResponse = new CibilResponse();
		String pdfContent = null;
		ParseCibilResponse pcr = new ParseCibilResponse();
		// This line of code generate and store cibil report in db.
		pdfContent = pcr.storeCibilReport(cibilRespList, id);
		cibilResponse.setPdfContent(pdfContent);

		return cibilResponse;
	}

	public List<Error> validateRequestBody(Request request) {

		List<Error> errorList = new ArrayList<Error>();

		if (request.getSrNo() != null && request.getSrNo().trim().equals("")) {
			Error error = new Error("114", "Invalid Sr No.");
			errorList.add(error);
		}

		if (request.getInputTuef() != null) {

			if (request.getInputTuef().trim().equals("")) {
				Error error = new Error("115", "Blank Input tuef");
				errorList.add(error);
			}

			if (!(request.getInputTuef().trim().startsWith("TUEF12")
					|| request.getInputTuef().trim().startsWith("ERRR"))) {
				Error error = new Error("113", "Input tuef does not starts with TUEF or ERRR");
				errorList.add(error);
			}
		}

		/*
		 * if(request.getLoanType()!=null) { String
		 * loanType=request.getLoanType().toUpperCase();
		 * 
		 * if(!(loanType.equals("COLLEGE")||loanType.equals("SHORT TICKET"))) { Error
		 * error=new Error("116","Invalid Loan Type"); errorList.add(error); } }
		 * 
		 * if(request.getProductType()!=null) { String
		 * productType=request.getProductType().toUpperCase();
		 * 
		 * if(!(productType.equals("PI/SI/EMI")||productType.equals("PI")||
		 * productType.equals("SI")||productType.equals("EMI")||productType.equals("ISA"
		 * ))) { Error error=new Error("117","Invalid Product Type");
		 * errorList.add(error); } }
		 */

		return errorList;

	}

	/**
	 * 
	 * @param headers     This is user input request headers.
	 * @param httpRequest
	 * @return List<Error> It returns list of errors in headers or empty list.
	 */

	public List<Error> validateHeaderInfo(Map<String, String> headers, HttpServletRequest httpRequest) {
		List<Error> errorList = new ArrayList<Error>();
		CustomerDetail customerDetail = null;

		String user_id = headers.get("user_id");
		String auth_key = headers.get("auth_key");
		String resp_type = headers.get("resp_type");
		String loan_type = headers.get("loan_type") != null ? headers.get("loan_type").toUpperCase() : null;
		String product_type = headers.get("product_type") != null ? headers.get("product_type").toUpperCase() : null;

		if (headers.get("user_id") != null && headers.get("auth_key") != null) {

			if (!("".equals(auth_key) || "".equals(user_id))) {

				// This line of code decrypt auth_key header.
				auth_key = EncryptUtil.decryptString(auth_key, "");

				customerDetail = new CustomerDetail();

				// This line of code verify auth_key and userId aginst database.
				String check = customerDetail.verifyUserIdAuthKey(user_id, auth_key, resp_type);

				// These lines of code create error data model based on verification check code.
				if (check == null) {
					Error error = new Error("105", "Invalid User Id/Auth_Key");
					errorList.add(error);
				} else if ("INACTIVE".equals(check)) {
					Error error = new Error("112", "Inactive User Id/Auth_Key");
					errorList.add(error);

				} else if ("DBConnectException".equals(check)) {
					Error error = new Error("108", "Error in connecting database server");
					errorList.add(error);
				} else if ("RestrictedRespType".equals(check)) {
					Error error = new Error("111", "Restricted response type against UserId/Auth_Key");
					errorList.add(error);
				}

			} else {
				Error error = new Error("106", "Blank User Id/Auth_Key");
				errorList.add(error);
			}

		} else {
			Error error = new Error("106", "Blank User Id/Auth_Key");
			errorList.add(error);

		}

		String accept = headers.get("accept");
		String contentType = headers.get("content-type");

		if (accept == null || "".equals(accept)
				|| !("application/xml".equals(accept) || "application/json".equals(accept))) {
			Error error = new Error("107", "Invalid Header Accept type");
			errorList.add(error);
		}

		if (contentType == null || "".equals(contentType)
				|| !("application/xml".equals(contentType) || "application/json".equals(contentType))) {
			Error error = new Error("109", "Invalid Header Content type");
			errorList.add(error);
		}

		if (resp_type == null || "".equals(resp_type)
				|| !("00".equals(resp_type) || "01".equals(resp_type) || "02".equals(resp_type)
						|| "03".equals(resp_type) || "04".equals(resp_type) || "05".equals(resp_type)
						|| "06".equals(resp_type) || "07".equals(resp_type)|| "08".equals(resp_type)
						|| "09".equals(resp_type))) {
			Error error = new Error("110", "Invalid Header Response Type value");
			errorList.add(error);
		} else if ("05".equals(resp_type)) {
			if (loan_type != null) {
				// String loanType=request.getLoanType().toUpperCase();

				if (!(loan_type.equals("COLLEGE") || loan_type.equals("SHORT TICKET"))) {
					Error error = new Error("116", "Invalid Loan Type");
					errorList.add(error);
				}
			} else {
				Error error = new Error("116", "Invalid Loan Type");
				errorList.add(error);
			}

			if (product_type != null) {
				// String productType=request.getProductType().toUpperCase();

				if (!(product_type.equals("PI/SI/EMI") || product_type.equals("PI") || product_type.equals("SI")
						|| product_type.equals("EMI") || product_type.equals("ISA"))) {
					Error error = new Error("117", "Invalid Product Type");
					errorList.add(error);
				}
			} else {
				Error error = new Error("117", "Invalid Product Type");
				errorList.add(error);
			}
		}

		return errorList;
	}

	public void newCibilRequest(Request request) {

		this.setNewCibilRequest(dao.getNewCibilRequest(request));
	}

	public String verifyUserIdAuthKey(String userId, String authKey, String inputRespType) {
		return dao.validateUserIdAuthKey(userId, authKey, inputRespType);
	}

	public Map<String, String> getCibilRemarksForScore(int score, String userId) {
		return dao.getCibilRemarksForScore(score, userId);
	}

	public StringBuilder generateInputCSV(NewCibilRequest ncr) {
		StringBuilder sb = new StringBuilder();

		sb.append(
				"MOBILE_REF_NO|LEAD_ID|ENQ_AMT|FNAME|MNAME|LNAME|DOB|GENDER|PAN|PASSPORT|VOTER_ID|DRIV_LIC_NO|ADHAR_ID|MOBILE|HOME_PHONE|OFFICE_PHONE|ADDLINE1|ADDLINE2|ADDLINE3|ADDLINE4|"
						+ "ADDLINE5|STATE_CODE|RESI_PIN|RESI_CODE\n");
		sb.append(ncr.getHeaderSegment().getMobileRefNo() + "|" + ncr.getHeaderSegment().getLeadId() + "|"
				+ ncr.getHeaderSegment().getEnqAmount() + "|" + ncr.getNameSegment().getfName() + "|"
				+ ncr.getNameSegment().getlName() + "|" + ncr.getNameSegment().getlName() + "|"
				+ ncr.getNameSegment().getDob() + "|" + ncr.getNameSegment().getGender() + "|");

		String PAN = null;
		String PASSPORT = null;
		String VOTER_ID = null;
		String DRIV_LIC_NO = null;
		String ADHAR_ID = null;

		for (IDBean idBean : ncr.getIdentificationSegment().getIdBeanList()) {
			String idNumber = idBean.getIdNumber();
			switch (idBean.getIdType()) {
			case "1":
				PAN = idNumber;
				break;
			case "2":
				PASSPORT = idNumber;
				break;
			case "3":
				VOTER_ID = idNumber;
				break;
			case "4":
				DRIV_LIC_NO = idNumber;
				break;
			case "6":
				ADHAR_ID = idNumber;
				break;
			}
		}

		sb.append(PAN + "|" + PASSPORT + "|" + VOTER_ID + "|" + DRIV_LIC_NO + "|" + ADHAR_ID + "|");

		String MOBILE = null;
		String HOME_PHONE = null;
		String OFFICE_PHONE = null;

		for (TelephoneBean telBean : ncr.getTelephoneSegment().getTelBeanList()) {
			String telNumber = telBean.getTelephoneNumber();
			switch (telBean.getTelephoneType()) {
			case "01":
				MOBILE = telNumber;
				break;
			case "02":
				HOME_PHONE = telNumber;
				break;
			case "03":
				OFFICE_PHONE = telNumber;
				break;

			}
		}

		sb.append(MOBILE + "|" + HOME_PHONE + "|" + OFFICE_PHONE + "|");

		String AddLine1 = ncr.getAddressSegment().getAddBeanList().get(0).getAddLine1();
		String AddLine2 = ncr.getAddressSegment().getAddBeanList().get(0).getAddLine2();
		String AddLine3 = ncr.getAddressSegment().getAddBeanList().get(0).getAddLine3();
		String AddLine4 = ncr.getAddressSegment().getAddBeanList().get(0).getAddLine4();
		String AddLine5 = ncr.getAddressSegment().getAddBeanList().get(0).getAddLine5();
		String STATE_CODE = ncr.getAddressSegment().getAddBeanList().get(0).getStateCode();
		String RESI_PIN = ncr.getAddressSegment().getAddBeanList().get(0).getPinCode();
		String RESI_CODE = ncr.getAddressSegment().getAddBeanList().get(0).getResidenceCode();

		sb.append(AddLine1 + "|" + AddLine2 + "|" + AddLine3 + "|" + AddLine4 + "|" + AddLine5 + "|" + STATE_CODE + "|"
				+ RESI_PIN + "|" + RESI_CODE);

		return sb;
	}

	public StringBuilder generateInputString(NewCibilRequest ncr) {
		StringBuilder sb = new StringBuilder();
		sb.append(ncr.getHeaderSegment().getHeaderBeanString());
		// }
		sb.append(ncr.getNameSegment().generateNameSegment());
		sb.append(ncr.getIdentificationSegment().generateIdentificationSegment());
		sb.append(ncr.getTelephoneSegment().generateTelephoneSegment());
		sb.append(ncr.getAddressSegment().generateAddressSegment());
		// sb.append(ncr.getAccNumSegment().generateAccountNumberSegment());

		int length = ncr.getHeaderSegment().getHeaderBeanString().length()
				+ ncr.getNameSegment().generateNameSegment().length()
				+ ncr.getIdentificationSegment().generateIdentificationSegment().length()
				+ ncr.getTelephoneSegment().generateTelephoneSegment().length()
				+ ncr.getAddressSegment().generateAddressSegment().length();
		// + ncr.getAccNumSegment().generateAccountNumberSegment().length();

		ncr.getEndSegment().setEndSegmentString(length + 15);
		sb.append(ncr.getEndSegment().getEndSegmentString());

		return sb;
	}

	public void generateInputString(List<NewCibilRequest> custloanfinfolist) {

		for (NewCibilRequest ncr : custloanfinfolist) {
			StringBuilder sb = new StringBuilder();
			sb.append(ncr.getHeaderSegment().getHeaderBeanString());
			sb.append(ncr.getNameSegment().generateNameSegment());
			sb.append(ncr.getIdentificationSegment().generateIdentificationSegment());
			sb.append(ncr.getTelephoneSegment().generateTelephoneSegment());
			sb.append(ncr.getAddressSegment().generateAddressSegment());
			sb.append(ncr.getAccNumSegment().generateAccountNumberSegment());

			int length = ncr.getHeaderSegment().getHeaderBeanString().length()
					+ ncr.getNameSegment().generateNameSegment().length()
					+ ncr.getIdentificationSegment().generateIdentificationSegment().length()
					+ ncr.getTelephoneSegment().generateTelephoneSegment().length()
					+ ncr.getAddressSegment().generateAddressSegment().length()
					+ ncr.getAccNumSegment().generateAccountNumberSegment().length();

			ncr.getEndSegment().setEndSegmentString(length + 15);
			sb.append(ncr.getEndSegment().getEndSegmentString());

		}
	}

	public void initializePropValue() {
		InputStream in = this.getClass().getResourceAsStream("/application_props.properties");
		try {
			prop = new Properties();
			prop.load(in);
			dao = new DAOBASE();
		} catch (Exception ee) {
			System.out.println("Exception initializePropValue:" + ee.toString());
		} finally {
			try {
				in.close();
				in = null;
			} catch (Exception eee) {
				System.out.println("Properties file closing exception:" + eee.toString());
			}
		}
	}

	public static String fixedLengthString(String string, int length) {
		return String.format("%0$-" + length + "s", string);
	}

	public void storeClientResponse(long SrNo, String clientResponse) {
		dao.updateClientResponse(SrNo, clientResponse);
	}

	public List<Error> validateRequest(Request ncr) {
		return dao.validateRequest(ncr);
	}
	
	public List<CibilResponse> getCibilReportContent(List<CibilResponse> responseListTemp, CibilResponse cibilResponse, String userId, List<CibilResponse> responseList) {
		// Code added on 20/1/2021 to save pdf report to folder for easy access.
		try {
			ParseCibilResponse parseCibilResponse = new ParseCibilResponse();
			// getting PDF content as ByteArrayOutputStream
			ByteArrayOutputStream out = parseCibilResponse.getCibilReport(responseListTemp);
			System.out.println("Started saving PDF report to folder..Srno=" + cibilResponse.getSrNo());
			// saving PDF to folder
			SMBService smbService = new SMBService();
			//savePdfFileToFolder(cibilResponse.getPdfContent(), cibilResponse.getSrNo() + ".pdf", out);
			smbService.saveCIBILReportToFolder(cibilResponse.getSrNo() + ".pdf", out, userId);
			System.out.println("Completed saving PDF report to folder..Srno=" + cibilResponse.getSrNo());
		} catch (Exception e) {
			System.out.println("Exception:While saving PDF tp folder=" + e);
			return responseList;
		}
		return responseList;
	}
}
