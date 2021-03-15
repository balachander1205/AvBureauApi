package com.avanse.service;

import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.sql.CallableStatement;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.avanse.model.AccountNumberBean;
import com.avanse.model.AddressBean;
import com.avanse.model.AddressSegment;
import com.avanse.model.CibilResponse;
import com.avanse.model.ConsumerDisputeRemarksSegment;
import com.avanse.model.EmploymentSegment;
import com.avanse.model.EndSegment;
import com.avanse.model.EnquiryBean;
import com.avanse.model.Error;
import com.avanse.model.ErrorSegment;
import com.avanse.model.FFRHeader;
import com.avanse.model.FFRRequest;
import com.avanse.model.FFRResponse;
import com.avanse.model.HeaderSegment;
import com.avanse.model.IDBean;
import com.avanse.model.IdentificationSegment;
import com.avanse.model.NameSegment;
import com.avanse.model.OthersAccountBean;
import com.avanse.model.OwnAccountBean;
import com.avanse.model.Request;
import com.avanse.model.ScoreBean;
import com.avanse.model.TelephoneBean;
import com.avanse.model.TelephoneSegment;
import com.avanse.util.ConnectionFactory;
import com.avanse.util.PropertyReader;

import sun.misc.BASE64Decoder;

/**
 * <h1>Avanse Bureau Api's</h1> The DAO class for AvBureauApi api's.
 * <p>
 * This program perform database operation for bureau api's
 * </p>
 * 
 * @author Swapnil Sawant
 * @version 1.0
 * @since 2019-07-22
 */
public class DAOBASE {

	/**
	 * 
	 * 
	 * @param request User original input request attributes
	 * @return CibilRequest This code converts user inputs into cibil input request
	 *         to be send to cibil server for processing.
	 */
	public NewCibilRequest getSegmentDataModel(Request request) {
		NewCibilRequest ncr = new NewCibilRequest();

		try {

			HeaderSegment hb = new HeaderSegment();
			hb.setSegmentTag("TUEF");
			hb.setVersion("12");
			hb.setMobileRefNo(request.getMobileRefNo() != null ? request.getMobileRefNo().trim() : null);
			hb.setLeadId(request.getLeadId() != null ? request.getLeadId().trim() : null);
			hb.setMemberRefNumber(fixedLengthString(PropertyReader.getProperty("memberRefNo"), 25));
			hb.setFutureUse1("  ");
			hb.setEnqMemberUserId(fixedLengthString(this.getCibilUserID(), 30));
			hb.setEnqPassword(fixedLengthString(this.getCibilUserPwd(), 30));
			hb.setEnqPurpose("08");// 08 = Education Loan By default

			String enqAmt = request.getEnqAmount() != null ? request.getEnqAmount() : "0";
			hb.setEnqAmount(String.format("%09d", Integer.parseInt(enqAmt)));
			hb.setFutureUse2("   ");
			hb.setScoreType(this.getCibilVersion());
			// hb.setScoreType("04");// 04 = Requesting the CIBIL TransUnion
			// Score Version 2.0 (CIBILTUSCR2) only.
			hb.setOutputFormat("01"); // 01 = Machine-Readable Formatted
										// Response Record
			hb.setResponseSize("1");
			hb.setInoutMedia("CC");// CC = CPU-to-CPU
			hb.setAuthMethod("L"); // L = Legacy
			hb.setHeaderBeanString();

			ncr.setHeaderSegment(hb);

			NameSegment ns = new NameSegment();
			ns.setfName(request.getfName());
			ns.setmName(request.getmName());
			ns.setlName(request.getlName());

			ns.setDob(request.getDob());

			ns.setGender(request.getGender());
			ncr.setNameSegment(ns);

			List<IDBean> idBeanList = new ArrayList<IDBean>();

			String PAN = request.getPanNo();
			String PASSPORT = request.getPassport();
			String VOTER_ID = request.getVoterID();
			String DRIV_LIC_NO = request.getDrivLicNo();
			String ADHAR_ID = request.getAdharID();

			if (PAN != null && !PAN.equals("")) {
				idBeanList.add(new IDBean("1", PAN));
			}

			if (PASSPORT != null && !PASSPORT.equals("")) {
				idBeanList.add(new IDBean("2", PASSPORT));
			}

			if (VOTER_ID != null && !VOTER_ID.equals("")) {
				idBeanList.add(new IDBean("3", VOTER_ID));
			}

			if (DRIV_LIC_NO != null && !DRIV_LIC_NO.equals("")) {
				idBeanList.add(new IDBean("4", DRIV_LIC_NO));
			}

			if (ADHAR_ID != null && !ADHAR_ID.equals("")) {
				idBeanList.add(new IDBean("6", ADHAR_ID));
			}

			IdentificationSegment is = new IdentificationSegment();
			is.setIdBeanList(idBeanList);

			ncr.setIdentificationSegment(is);

			List<TelephoneBean> telBeanList = new ArrayList<TelephoneBean>();

			String MOBILE = request.getMobile();
			String HOME_PHONE = request.getHomePhone();
			String OFFICE_PHONE = request.getOfficePhone();

			if (telBeanList.size() < 3 && MOBILE != null && !MOBILE.equals("")) {
				telBeanList.add(new TelephoneBean(MOBILE, null, "01"));
			}

			if (telBeanList.size() < 3 && HOME_PHONE != null && !HOME_PHONE.equals("")) {
				telBeanList.add(new TelephoneBean(HOME_PHONE, null, "02"));
			}

			if (telBeanList.size() < 3 && OFFICE_PHONE != null && !OFFICE_PHONE.equals("")) {
				telBeanList.add(new TelephoneBean(OFFICE_PHONE, null, "03"));
			}

			TelephoneSegment pt = new TelephoneSegment();
			pt.setTelBeanList(telBeanList);

			ncr.setTelephoneSegment(pt);

			AddressBean addBean1 = new AddressBean();

			String RESI_ADD = request.getResiAddress() != null ? request.getResiAddress().trim() : "";
			String RESI_LANDMARK = request.getLandmark() != null ? request.getLandmark().trim() : "";
			String RESI_CITY = request.getCity() != null ? request.getCity().trim() : "";
			String RESI_STATE = request.getState() != null ? request.getState().trim() : "";
			String STATE_CODE = request.getStateCode() != null ? request.getStateCode().trim() : null;
			String RESI_PIN = request.getPin() != null ? request.getPin().trim() : "";
			String RESI_COUNTRY = request.getCountry() != null ? request.getCountry().trim() : "";
			String RESI_CODE = request.getResiCode() != null ? request.getResiCode().trim() : "";

			String fullAddress = "";

			if (fullAddress.trim().length() < 200 && !RESI_ADD.equals("")) {
				fullAddress += RESI_ADD;
			}

			if (fullAddress.trim().length() < 200 && !RESI_LANDMARK.equals("")) {
				fullAddress += " " + RESI_LANDMARK;
			}

			if (fullAddress.trim().length() < 200 && !RESI_CITY.equals("")) {
				fullAddress += " " + RESI_CITY;
			}

			if (fullAddress.trim().length() < 200 && !RESI_STATE.equals("")) {
				fullAddress += " " + RESI_STATE;
			}

			if (fullAddress.trim().length() < 200 && !RESI_PIN.equals("")) {
				fullAddress += " " + RESI_PIN;
			}

			if (fullAddress.trim().length() < 200 && !RESI_COUNTRY.equals("")) {
				fullAddress += " " + RESI_COUNTRY;
			}

			addBean1.setFullAddress(fullAddress);
			addBean1.setStateCode(STATE_CODE);
			addBean1.setPinCode(RESI_PIN);
			addBean1.setAddCategory("01");// 01 = Permanent Address

			if (RESI_CODE.toUpperCase().equals("OWNED")) {
				addBean1.setResidenceCode("01");
			} else if (RESI_CODE.toUpperCase().equals("RENTED")) {
				addBean1.setResidenceCode("02");
			}

			List<AddressBean> addBeanList = new ArrayList<AddressBean>();
			addBeanList.add(addBean1);
			AddressSegment pa = new AddressSegment();
			pa.setAddBeanList(addBeanList);

			ncr.setAddressSegment(pa);

			EndSegment es = new EndSegment();

			ncr.setEndSegment(es);

		} catch (Exception e) {
			e.printStackTrace();
		}
		return ncr;
	}

	public String getCibilVersion() {

		ResultSet rs = null;
		String version = null;

		String query = "select const_value from ilm_gl_const_params where const_code = 'CIBIL_VERSION'";
		try {
			/*
			 * Connection con = ConnectionFactory.getConnection(); Statement st =
			 * con.createStatement(); rs = st.executeQuery(query); while(rs.next()) {
			 * version=rs.getString(1);
			 * 
			 * }
			 */
			// Hard coded version number on 27/1/2021
			version = "08";
		} catch (Exception e) {
			e.printStackTrace();
		}

		return version;

	}

	public String getCibilUserID() {
		ResultSet rs = null;
		String userId = null;

		// String query = "select const_value from ilm_gl_const_params where const_code
		// = 'CIBIL_USER_ID'";
		// UAT Query
		String query = "SELECT Member_Id FROM PLF_13_01_2021..Cibil_Member_Details WHERE Segment_Type='RETAIL'";
		// Production Query
		//String query = "SELECT Member_Id FROM PLF..Cibil_Member_Details WHERE Segment_Type='RETAIL'";
		try {
			Connection con = ConnectionFactory.getConnection();
			Statement st = con.createStatement();
			rs = st.executeQuery(query);
			while (rs.next()) {
				userId = rs.getString(1);

			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return userId;

	}

	public String getCibilUserPwd() {

		ResultSet rs = null;
		String userPwd = null;

		// String query = "select const_value from ilm_gl_const_params where const_code
		// = 'CIBIL_USER_PWD'";
		// UAT Query
		String query = "SELECT Member_Password FROM PLF_13_01_2021..Cibil_Member_Details WHERE Segment_Type='RETAIL'";
		// Production Query
		//String query = "SELECT Member_Password FROM PLF..Cibil_Member_Details WHERE Segment_Type='RETAIL'";
		try {
			Connection con = ConnectionFactory.getConnection();
			Statement st = con.createStatement();
			rs = st.executeQuery(query);
			while (rs.next()) {
				userPwd = rs.getString(1);

			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return userPwd;

	}

	public void validateCibilRequest(NewCibilRequest ncr) {
		StringBuilder sb = new StringBuilder();

		sb.append(ncr.getHeaderSegment().validateHeaderSegment());
		sb.append(ncr.getNameSegment().validateNameSegment());
		sb.append(ncr.getIdentificationSegment().validateIdentificationSegment());
		sb.append(ncr.getTelephoneSegment().validateTelephoneSegment());
		sb.append(ncr.getAddressSegment().validateAddressSegment());
		sb.append(ncr.getAccNumSegment().validateAccountNumberSegment());
		if ("".equals(sb.toString())) {
		} else {
		}
	}

	public List<Error> validateRequest(Request ncr) {
		StringBuilder sb = new StringBuilder();

		List<Error> errorList = new ArrayList<Error>();
		errorList.add(new Error("112", "Input fields errors:" + sb.toString()));

		return errorList;
	}

	public boolean validateFFRRequest(FFRHeader header, FFRRequest request) {

		StringBuilder sb = new StringBuilder();
		// boolean isCorrect=false;

		String custId = header.getCustID();
		String fileNo = header.getFileNo();
		String srNo = header.getSrNo();
		String cibilString = request.getCibilString();
		String emailId = request.getEmailId();
		String name = request.getName();

		if (cibilString == null || cibilString.equals("")) {
			sb.append("SrNo:" + srNo + "/FileNo:" + fileNo + "/CustID:" + custId + ":"
					+ "InputCibilString is null or blank");
		}
		if (emailId == null || emailId.equals("")) {
			sb.append("SrNo:" + srNo + "/FileNo:" + fileNo + "/CustID:" + custId + ":"
					+ "Customer EmailID is null or blank");
		}
		if (name == null || name.equals("")) {
			sb.append("SrNo:" + srNo + "/FileNo:" + fileNo + "/CustID:" + custId + ":"
					+ "Customer Name is null or blank");
		}

		System.out.println(sb);
		if (sb.toString().equals("")) {
			return true;
		} else {
			logFileErrors(PropertyReader.getProperty("inputFFRErrorsPath"), sb);
		}

		return false;

	}

	public void logFileErrors(String filePath, StringBuilder sb) {

		try {

			String currentDate = new SimpleDateFormat("ddMMyyyy").format(new java.util.Date());
			FileWriter fw = new FileWriter(filePath + currentDate + ".txt", true);
			System.out.println("fw" + fw.toString());
			BufferedWriter bw = new BufferedWriter(fw);

			bw.append(sb.toString());

			bw.newLine();
			bw.close();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (MalformedURLException e) {

			e.printStackTrace();

		} catch (IllegalStateException e) {
			e.printStackTrace();
		}

		catch (IOException e) {

			e.printStackTrace();

		}

	}

	public Map<FFRHeader, FFRRequest> validateFFRDataModel(Map<FFRHeader, FFRRequest> ffrRequestMap) {
		Map<FFRHeader, FFRRequest> newffrRequestMap = new HashMap<FFRHeader, FFRRequest>();

		for (Map.Entry<FFRHeader, FFRRequest> entry : ffrRequestMap.entrySet()) {

			FFRHeader header = entry.getKey();
			FFRRequest request = entry.getValue();

			if (this.validateFFRRequest(header, request)) {
				newffrRequestMap.put(header, request);
			}
		}

		return newffrRequestMap;
	}

	public NewCibilRequest getNewCibilRequest(Request request) {

		NewCibilRequest newCibilRequest = null;

		try {

			newCibilRequest = getSegmentDataModel(request);

		} catch (Exception e) {
			e.printStackTrace();
		}

		return newCibilRequest;
	}

	public Map<FFRHeader, FFRRequest> getFFRDataModel(ResultSet rs) {

		Map<FFRHeader, FFRRequest> ffrRequestMap = new HashMap<FFRHeader, FFRRequest>();
		try {
			while (rs.next()) {

				FFRHeader ffrHeader = new FFRHeader();
				ffrHeader.setSrNo(rs.getString("SR_NO"));
				ffrHeader.setCustID(rs.getString("CUST_ID"));
				ffrHeader.setFileNo(rs.getString("FILE_NO"));

				FFRRequest ffrRequest = new FFRRequest();
				ffrRequest.setPartnerCode(PropertyReader.getProperty("partnerCode"));
				ffrRequest.setApiAccessKey(PropertyReader.getProperty("apiAccessKey"));
				ffrRequest.setApplicationId(rs.getString("FILE_NO") + "_" + rs.getString("SR_NO"));
				ffrRequest.setCustomerSource(PropertyReader.getProperty("customerSource"));
				ffrRequest.setSendEmail(PropertyReader.getProperty("sendEmail"));

				String fullName = (rs.getString("FNAME") != null ? rs.getString("FNAME")
						: "" + " " + rs.getString("MNAME") != null ? rs.getString("MNAME")
								: "" + " " + rs.getString("LNAME") != null ? rs.getString("LNAME") : "").trim();
				ffrRequest.setName(fullName);
				ffrRequest.setEmailId(rs.getString("EMAIL"));
				ffrRequest.setCibilString(rs.getString("INPUT_TUEF"));

				ffrRequestMap.put(ffrHeader, ffrRequest);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return ffrRequestMap;

	}

	public Map<FFRHeader, FFRRequest> getFFRRequestMap() {

		ResultSet rs = null;
		Map<FFRHeader, FFRRequest> ffrRequestMap = null;

		String query = "select apc.APP_PERS_SR_NO AS SR_NO,apd.APP_PERS_FILE_NO AS FILE_NO,apd.APP_PERS_ID AS CUST_ID,apd.APP_PERS_FNAME as FNAME,apd.APP_PERS_MNAME as MNAME,apd.APP_PERS_LNAME as LNAME,apd.APP_PERS_EMAIL as EMAIL,apc.APP_PERS_CIBIL_RESP as INPUT_TUEF "
				+ "from ilt_iq_app_pers_det apd,ilt_iq_app_pers_cibil apc "
				+ "where apd.APP_PERS_FILE_NO=apc.APP_PERS_FILE_NO and apd.APP_PERS_ID=apc.APP_PERS_ID and apc.APP_PERS_FHR_FLAG='N' and apc.APP_PERS_CIBIL_FLAG='Y' and apc.APP_PERS_CIBIL_REMARKS='SUCCESS' and apc.APP_PERS_CIBIL_RESP is not null and rownum<=1";
		try {
			Connection con = ConnectionFactory.getConnection();
			Statement st = con.createStatement();
			rs = st.executeQuery(query);
			ffrRequestMap = validateFFRDataModel(getFFRDataModel(rs));
		} catch (Exception e) {
			e.printStackTrace();
		}

		return ffrRequestMap;
	}

	public int updateFFRResponse(FFRHeader header, FFRResponse response, String jsonResponse) {

		int status = 0;

		String query = "update ilt_iq_app_pers_cibil set APP_PERS_FHR_RESP=?,APP_PERS_FHR_REMARKS=?,APP_PERS_FHR_FILE=?,APP_PERS_FHR_FLAG=? where APP_PERS_FILE_NO=? and APP_PERS_ID=? and APP_PERS_SR_NO=?";

		try {
			System.out.println("query:" + query + " fileNo: " + header.getFileNo() + " custID:" + header.getCustID()
					+ " srNo:" + header.getSrNo());
			Connection con = ConnectionFactory.getConnection();
			PreparedStatement st = con.prepareStatement(query);
			st.setString(1, jsonResponse);

			if (response.getErrorList() != null && response.getErrorList().size() > 0) {
				st.setString(2, "ERROR");
			} else {
				st.setString(2, "SUCCESS");
			}

			byte[] decodedBytes = null;
			if (response.getPdfContent() != null) {
				BASE64Decoder decoder = new BASE64Decoder();
				try {
					decodedBytes = decoder.decodeBuffer(response.getPdfContent());
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			st.setBytes(3, decodedBytes != null ? decodedBytes : null);

			st.setString(4, "Y");
			st.setString(5, header.getFileNo());
			st.setInt(6, Integer.parseInt(header.getCustID()));
			st.setInt(7, Integer.parseInt(header.getSrNo()));

			status = st.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return status;

	}

	public void storeParsedCibilResponse(CibilResponse cibilResponse, String mobRefNo, String leadID, long srNo) {
		System.out.println("storeParsedCibilResponse started");

		if (cibilResponse != null) {
			if (cibilResponse.getHeaderSegment() != null) {
				this.storeHeaderSegment(cibilResponse.getHeaderSegment(), mobRefNo, leadID, srNo);
			}

			if (cibilResponse.getNameSegment() != null) {
				this.storeNameSegment(cibilResponse.getNameSegment(), mobRefNo, leadID, srNo);
			}

			if (cibilResponse.getIdentificationSegment() != null
					&& cibilResponse.getIdentificationSegment().getIdBeanList() != null) {
				this.storeIdentificationSegment(cibilResponse.getIdentificationSegment().getIdBeanList(), mobRefNo,
						leadID, srNo);
			}

			if (cibilResponse.getTelephoneSegment() != null
					&& cibilResponse.getTelephoneSegment().getTelBeanList() != null) {
				this.storeTelephoneSegment(cibilResponse.getTelephoneSegment().getTelBeanList(), mobRefNo, leadID,
						srNo);
			}

			if (cibilResponse.getEmailContactSegment() != null
					&& cibilResponse.getEmailContactSegment().getEmailList() != null) {
				this.storeEmailContactSegment(cibilResponse.getEmailContactSegment().getEmailList(), mobRefNo, leadID,
						srNo);
			}

			if (cibilResponse.getEmploymentSegment() != null) {
				this.storeEmploymentSegment(cibilResponse.getEmploymentSegment(), mobRefNo, leadID, srNo);
			}

			if (cibilResponse.getAccNumSegment() != null
					&& cibilResponse.getAccNumSegment().getAccNumBeanList() != null) {
				this.storeEnquiryAccountNumberSegment(cibilResponse.getAccNumSegment().getAccNumBeanList(), mobRefNo,
						leadID, srNo);
			}

			if (cibilResponse.getScoreSegment() != null && cibilResponse.getScoreSegment().getScoreBeanList() != null) {
				this.storeScoreSegment(cibilResponse.getScoreSegment().getScoreBeanList(), mobRefNo, leadID, srNo);
			}

			if (cibilResponse.getAddressSegment() != null
					&& cibilResponse.getAddressSegment().getAddBeanList() != null) {
				this.storeAddressSegment(cibilResponse.getAddressSegment().getAddBeanList(), mobRefNo, leadID, srNo);
			}

			if (cibilResponse.getAccountSegment() != null
					&& cibilResponse.getAccountSegment().getOwnAccountBeanList() != null) {
				this.storeAccountSegmentOwnAccounts(cibilResponse.getAccountSegment().getOwnAccountBeanList(), mobRefNo,
						leadID, srNo);
			}

			if (cibilResponse.getAccountSegment() != null
					&& cibilResponse.getAccountSegment().getOthersAccountBeanList() != null) {
				this.storeAccountSegmentOthersAccounts(cibilResponse.getAccountSegment().getOthersAccountBeanList(),
						mobRefNo, leadID, srNo);
			}

			if (cibilResponse.getEnquirySegment() != null
					&& cibilResponse.getEnquirySegment().getEnquiryBeanList() != null) {
				this.storeEnquirySegment(cibilResponse.getEnquirySegment().getEnquiryBeanList(), mobRefNo, leadID,
						srNo);
			}

			if (cibilResponse.getConsumerDisputeRemarksSegment() != null) {
				this.storeConsumerDisputeRemarksSegment(cibilResponse.getConsumerDisputeRemarksSegment(), mobRefNo,
						leadID, srNo);
			}

			if (cibilResponse.getEndSegment() != null) {
				this.storeEndSegment(cibilResponse.getEndSegment(), mobRefNo, leadID, srNo);
			}

			if (cibilResponse.getErrorSegment() != null) {
				this.storeErrorSegment(cibilResponse.getErrorSegment(), mobRefNo, leadID, srNo);
			}

			System.out.println("storeParsedCibilResponse completed");
		}
	}

	public void storeErrorSegment(ErrorSegment errorSegment, String mobRefNo, String leadID, long srNo) {

		String query = "insert into ILT_IQ_CIBIL_ERROR_SGMT(APP_PERS_SR_NO,CIBIL_MOB_REF_NO,CIBIL_LEAD_ID,CIBIL_DATE_PROCESSED,CIBIL_TIME_PROCESSED,CIBIL_MEM_REF_NUM,CIBIL_INVALID_VERSION,CIBIL_INVALID_FIELD_LENGTH,CIBIL_INVALID_TOT_LENGTH,CIBIL_INVALID_ENQ_PURPOSE,CIBIL_INVALID_ENQ_AMT,INV_ENQ_MEM_USER_ID_OR_PSS,REQ_ENQ_SGMT_MISSING,CIBIL_INV_ENQ_DATA,CIBIL_SYSTEM_ERROR,CIBIL_INV_SGMT_TAG,CIBIL_INV_SGMT_ORDER,CIBIL_INV_FIELD_TAG_ORDER,CIBIL_MISSING_REQ_FIELD,REQ_RES_SIZE_EXCEEDED,INV_INPUT_OR_OUTPUT_MEDIA) values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";

		try {
			Connection con = ConnectionFactory.getConnection();
			PreparedStatement st = con.prepareStatement(query);
			st.setLong(1, srNo);
			st.setString(2, mobRefNo);
			st.setString(3, leadID);
			st.setString(4, errorSegment.getDateProcessed());
			st.setString(5, errorSegment.getTimeProcessed());
			st.setString(6, errorSegment.getUserReferenceErrorSegment().getMemberReferenceNumber());
			st.setString(7, errorSegment.getUserReferenceErrorSegment().getInvalidVersion());
			st.setString(8, errorSegment.getUserReferenceErrorSegment().getInvalidFieldLength());
			st.setString(9, errorSegment.getUserReferenceErrorSegment().getInvalidTotalLength());
			st.setString(10, errorSegment.getUserReferenceErrorSegment().getInvalidEnquiryPurpose());
			st.setString(11, errorSegment.getUserReferenceErrorSegment().getInvalidEnquiryAmount());
			st.setString(12, errorSegment.getUserReferenceErrorSegment().getInvalidEnquiryMemberUserIDOrPassword());
			st.setString(13, errorSegment.getUserReferenceErrorSegment().getRequiredEnquirySegmentMissing());

			StringBuilder invalidEnquiryDataSb = new StringBuilder();
			for (String invalidEnquiryData : errorSegment.getUserReferenceErrorSegment().getInvalidEnquiryData()) {
				invalidEnquiryDataSb.append(invalidEnquiryData + "|");
			}
			st.setString(14, invalidEnquiryDataSb.toString());
			st.setString(15, errorSegment.getUserReferenceErrorSegment().getCibilSystemError());
			st.setString(16, errorSegment.getUserReferenceErrorSegment().getInvalidSegmentTag());
			st.setString(17, errorSegment.getUserReferenceErrorSegment().getInvalidSegmentOrder());
			st.setString(18, errorSegment.getUserReferenceErrorSegment().getInvalidFieldTagOrder());

			StringBuilder missingRequiredFieldSb = new StringBuilder();
			for (String missingRequiredField : errorSegment.getUserReferenceErrorSegment().getMissingRequiredField()) {
				missingRequiredFieldSb.append(missingRequiredField + "|");
			}

			st.setString(19, missingRequiredFieldSb.toString());
			st.setString(20, errorSegment.getUserReferenceErrorSegment().getRequestedResponseSizeExceeded());
			st.setString(21, errorSegment.getUserReferenceErrorSegment().getInvalidInputOrOutputMedia());

			st.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public void storeEndSegment(EndSegment endSegment, String mobRefNo, String leadID, long srNo) {

		String query = "insert into ILT_IQ_CIBIL_END_SGMT(APP_PERS_SR_NO,CIBIL_MOB_REF_NO,CIBIL_LEAD_ID,CIBIL_LENGTH_OF_TRANSMISSION) values(?,?,?,?)";

		try {
			Connection con = ConnectionFactory.getConnection();
			PreparedStatement st = con.prepareStatement(query);
			st.setLong(1, srNo);
			st.setString(2, mobRefNo);
			st.setString(3, leadID);
			st.setInt(4, endSegment.getLenOfRecord());

			st.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public void storeConsumerDisputeRemarksSegment(ConsumerDisputeRemarksSegment consumerDisputeRemarksSegment,
			String mobRefNo, String leadID, long srNo) {

		String query = "insert into ILT_IQ_CIBIL_CON_DISP_REM_SGMT(APP_PERS_SR_NO,CIBIL_MOB_REF_NO,CIBIL_LEAD_ID,CIBIL_DATE_OF_ENTRY,CIBIL_DISPUTE_REM_LINE_1,CIBIL_DISPUTE_REM_LINE_2,CIBIL_DISPUTE_REM_LINE_3,CIBIL_DISPUTE_REM_LINE_4,CIBIL_DISPUTE_REM_LINE_5,CIBIL_DISPUTE_REM_LINE_6) values(?,?,?,?,?,?,?,?,?,?)";

		try {
			Connection con = ConnectionFactory.getConnection();
			PreparedStatement st = con.prepareStatement(query);
			st.setLong(1, srNo);
			st.setString(2, mobRefNo);
			st.setString(3, leadID);
			st.setString(4, consumerDisputeRemarksSegment.getDateOfEntry());
			st.setString(5, consumerDisputeRemarksSegment.getDisputeRemarksLine1());
			st.setString(6, consumerDisputeRemarksSegment.getDisputeRemarksLine2());
			st.setString(7, consumerDisputeRemarksSegment.getDisputeRemarksLine3());
			st.setString(8, consumerDisputeRemarksSegment.getDisputeRemarksLine4());
			st.setString(9, consumerDisputeRemarksSegment.getDisputeRemarksLine5());
			st.setString(10, consumerDisputeRemarksSegment.getDisputeRemarksLine6());

			st.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public void storeEnquirySegment(List<EnquiryBean> enquiryBeanList, String mobRefNo, String leadID, long srNo) {

		String query = "insert into ILT_IQ_CIBIL_ENQ_SGMT(APP_PERS_SR_NO,CIBIL_MOB_REF_NO,CIBIL_LEAD_ID,CIBIL_DATE_OF_ENQ,CIBIL_ENQ_MEM_SHORT_NAME,CIBIL_ENQ_PURPOSE,CIBIL_ENQ_AMT) values(?,?,?,?,?,?,?)";

		try {
			Connection con = ConnectionFactory.getConnection();
			PreparedStatement st = con.prepareStatement(query);

			for (EnquiryBean enquiryBean : enquiryBeanList) {
				st.setLong(1, srNo);
				st.setString(2, mobRefNo);
				st.setString(3, leadID);
				st.setString(4, enquiryBean.getDateOfEnquiry());
				st.setString(5, enquiryBean.getEnquiringMemberShortName());
				st.setString(6, enquiryBean.getEnquiryPurpose());
				st.setString(7, enquiryBean.getEnquiryAmount());

				st.addBatch();
			}
			st.executeBatch();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public void storeAccountSegmentOthersAccounts(List<OthersAccountBean> othersAccountBeanList, String mobRefNo,
			String leadID, long srNo) {

		String query = "insert into ILT_IQ_CIBIL_ACC_SGMT_OTH(APP_PERS_SR_NO,CIBIL_MOB_REF_NO,CIBIL_LEAD_ID,CIBIL_REP_MEM_SHORT_NAME,CIBIL_NUM_OF_ACC,CIBIL_ACC_GROUP,CIBIL_LIVE_OR_CLOSE_INDI,CRT_LMT_OR_HI_CRT_OR_SAN_AMT,CIBIL_CURRENT_BAL,CIBIL_DATE_OPEN_OR_DISBURSED,CIBIL_DATE_OF_LAST_PAYMENT,CIBIL_DATE_CLOSED,CIBIL_DATE_REP_AND_CERTIFIED,CIBIL_AMT_OVERDUE,CIBIL_PAYMENT_HIS_1,CIBIL_PAYMENT_HIS_2,CIBIL_PAYMENT_HIS_START_DATE,CIBIL_PAYMENT_HIS_END_DATE,SUIT_FILED_OR_WILFUL_DEFAULT,WRITTEN_OFF_AND_SETTLED_STATUS,DATE_OF_ENTY_ERR_CODE,CIBIL_ERROR_CODE,DATE_OF_ENTY_CIBIL_REMK_CODE,CIBIL_REMARKS_CODE,DATE_ENTY_ERR_OR_DIS_REM_CODE,ERR_OR_DIS_REM_CODE_1,ERR_OR_DIS_REM_CODE_2) values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";

		try {
			Connection con = ConnectionFactory.getConnection();
			PreparedStatement st = con.prepareStatement(query);
			for (OthersAccountBean othersAccountBean : othersAccountBeanList) {
				st.setLong(1, srNo);
				st.setString(2, mobRefNo);
				st.setString(3, leadID);
				st.setString(4, othersAccountBean.getReportingMemberShortName());
				st.setString(5, othersAccountBean.getNumberOfAccounts());
				st.setString(6, othersAccountBean.getAccountGroup());
				st.setString(7, othersAccountBean.getLiveOrClosedIndicator());
				st.setString(8, othersAccountBean.getCreditLimitOrHighCreditOrSanctionedAmount());
				st.setString(9, othersAccountBean.getCurrentBalance());
				st.setString(10, othersAccountBean.getDateOpenedOrDisbursed());
				st.setString(11, othersAccountBean.getDateOfLastPayment());
				st.setString(12, othersAccountBean.getDateClosed());
				st.setString(13, othersAccountBean.getDateReportedAndCertified());
				st.setString(14, othersAccountBean.getAmountOverdue());
				st.setString(15, othersAccountBean.getPaymentHistory1());
				st.setString(16, othersAccountBean.getPaymentHistory2());
				st.setString(17, othersAccountBean.getPaymentHistoryStartDate());
				st.setString(18, othersAccountBean.getPaymentHistoryEndDate());
				st.setString(19, othersAccountBean.getSuitFiledOrWilfulDefault());
				st.setString(20, othersAccountBean.getWrittenOffAndSettledStatus());
				st.setString(21, othersAccountBean.getDateOfEntryForErrorCode());
				st.setString(22, othersAccountBean.getErrorCode());
				st.setString(23, othersAccountBean.getDateOfEntryForCibilRemarksCode());
				st.setString(24, othersAccountBean.getCibilRemarksCode());
				st.setString(25, othersAccountBean.getDateOfEntryForErrorOrDisputeRemarksCode());
				st.setString(26, othersAccountBean.getErrorOrDisputeRemarksCode1());
				st.setString(27, othersAccountBean.getErrorOrDisputeRemarksCode2());

				st.addBatch();
			}
			st.executeBatch();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public void storeAccountSegmentOwnAccounts(List<OwnAccountBean> ownAccountBeanList, String mobRefNo, String leadID,
			long srNo) {

		String query = "insert into ILT_IQ_CIBIL_ACC_SGMT_OWN(APP_PERS_SR_NO,CIBIL_MOB_REF_NO,CIBIL_LEAD_ID,CIBIL_REP_MEM_SHORT_NAME,CIBIL_ACC_NUMBER,CIBIL_ACC_TYPE,CIBIL_OWNERSHIP_INDI,CIBIL_DATE_OPEN_OR_DISBURSED,CIBIL_DATE_OF_LST_PAYMENT,CIBIL_DATE_CLOSED,CIBIL_DATE_REP_AND_CERTIFIED,CIBIL_HI_CRE_OR_SANCTIONED_AMT,CIBIL_CURRENT_BAL,CIBIL_AMT_OVERDUE,CIBIL_PAYMENT_HIS_1,CIBIL_PAYMENT_HIS_2,CIBIL_PAYMENT_HIS_START_DATE,CIBIL_PAYMENT_HIS_END_DATE,SUIT_FILED_OR_WILFUL_DEFAULT,WRITTEN_OFF_AND_SETTLED_STATUS,CIBIL_VALUE_OF_COLLATERAL,CIBIL_TYPE_OF_COLLATERAL,CIBIL_CREDIT_LIMIT,CIBIL_CASH_LIMIT,CIBIL_RATE_OF_INT,CIBIL_REPAYMENT_TENURE,CIBIL_EMI_AMOUNT,CIBIL_WRITTEN_OF_AMT_TOT,CIBIL_WRITTEN_OF_AMT_PRINC,CIBIL_SETTLEMENT_AMT,CIBIL_PAYMENT_FREQ,CIBIL_ACCTUAL_PAY_AMT,DATE_OF_ENTY_ERR_CODE,DATA_ERROR_CODE,DATE_OF_ENTY_CIBIL_REMK_CODE,CIBIL_REMARKS_CODE,DATE_ENTY_ERR_OR_DIS_REM_CODE,ERR_OR_DIS_REM_CODE_1,ERR_OR_DIS_REM_CODE_2) values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";

		try {
			Connection con = ConnectionFactory.getConnection();
			PreparedStatement st = con.prepareStatement(query);
			for (OwnAccountBean ownAccountBean : ownAccountBeanList) {
				st.setLong(1, srNo);
				st.setString(2, mobRefNo);
				st.setString(3, leadID);
				st.setString(4, ownAccountBean.getReportingMemberShortName());
				st.setString(5, ownAccountBean.getAccountNumber());
				st.setString(6, ownAccountBean.getAccountType());
				st.setString(7, ownAccountBean.getOwnershipIndicator());
				st.setString(8, ownAccountBean.getDateOpenedOrDisbursed());
				st.setString(9, ownAccountBean.getDateOfLastPayment());
				st.setString(10, ownAccountBean.getDateClosed());
				st.setString(11, ownAccountBean.getDateReportedAndCertified());
				st.setString(12, ownAccountBean.getHighCreditOrSanctionedAmount());
				st.setString(13, ownAccountBean.getCurrentBalance());
				st.setString(14, ownAccountBean.getAmountOverdue());
				st.setString(15, ownAccountBean.getPaymentHistory1());
				st.setString(16, ownAccountBean.getPaymentHistory2());
				st.setString(17, ownAccountBean.getPaymentHistoryStartDate());
				st.setString(18, ownAccountBean.getPaymentHistoryEndDate());
				st.setString(19, ownAccountBean.getSuitFiledOrWilfulDefault());
				st.setString(20, ownAccountBean.getWrittenOffAndSettledStatus());
				st.setString(21, ownAccountBean.getValueOfCollateral());
				st.setString(22, ownAccountBean.getTypeOfCollateral());
				st.setString(23, ownAccountBean.getCreditLimit());
				st.setString(24, ownAccountBean.getCashLimit());
				st.setString(25, ownAccountBean.getRateOfInterest());
				st.setString(26, ownAccountBean.getRepaymentTenure());
				st.setString(27, ownAccountBean.getEMIAmount());
				st.setString(28, ownAccountBean.getWrittenOffAmountTotal());
				st.setString(29, ownAccountBean.getWrittenOffAmountPrincipal());
				st.setString(30, ownAccountBean.getSettlementAmount());
				st.setString(31, ownAccountBean.getPaymentFrequency());
				st.setString(32, ownAccountBean.getActualPaymentAmount());
				st.setString(33, ownAccountBean.getDateOfEntryForErrorCode());
				st.setString(34, ownAccountBean.getErrorCode());
				st.setString(35, ownAccountBean.getDateOfEntryForCibilRemarksCode());
				st.setString(36, ownAccountBean.getCibilRemarksCode());
				st.setString(37, ownAccountBean.getDateOfEntryForErrorOrDisputeRemarksCode());
				st.setString(38, ownAccountBean.getErrorOrDisputeRemarksCode1());
				st.setString(39, ownAccountBean.getErrorOrDisputeRemarksCode2());

				st.addBatch();
			}
			st.executeBatch();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public void storeAddressSegment(List<AddressBean> addressBeanList, String mobRefNo, String leadID, long srNo) {
		String query = "insert into ILT_IQ_CIBIL_ADDRESS_SGMT(APP_PERS_SR_NO,CIBIL_MOB_REF_NO,CIBIL_LEAD_ID,CIBIL_ADDRESS_LINE_1,CIBIL_ADDRESS_LINE_2,CIBIL_ADDRESS_LINE_3,CIBIL_ADDRESS_LINE_4,CIBIL_ADDRESS_LINE_5,CIBIL_STATE_CODE,CIBIL_PIN_CODE,CIBIL_ADDRESS_CATEGORY,CIBIL_RES_CODE,CIBIL_DATE_REPORTED,CIBIL_MEM_SHORT_NAME,CIBIL_ENRICHED_THRO_ENQ) values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";

		try {
			Connection con = ConnectionFactory.getConnection();
			PreparedStatement st = con.prepareStatement(query);
			for (AddressBean addressBean : addressBeanList) {
				st.setLong(1, srNo);
				st.setString(2, mobRefNo);
				st.setString(3, leadID);
				st.setString(4, addressBean.getAddLine1());
				st.setString(5, addressBean.getAddLine2());
				st.setString(6, addressBean.getAddLine3());
				st.setString(7, addressBean.getAddLine4());
				st.setString(8, addressBean.getAddLine5());
				st.setString(9, addressBean.getStateCode());
				st.setString(10, addressBean.getPinCode());
				st.setString(11, addressBean.getAddCategory());
				st.setString(12, addressBean.getResidenceCode());
				st.setString(13, addressBean.getDateReported());
				st.setString(14, addressBean.getMemberShortName());
				st.setString(15, addressBean.getEnrichedThroughEnquiry());

				st.addBatch();
			}
			st.executeBatch();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public void storeScoreSegment(List<ScoreBean> scoreBeanList, String mobRefNo, String leadID, long srNo) {

		String query = "insert into ILT_IQ_CIBIL_SCORE_SGMT(APP_PERS_SR_NO,CIBIL_MOB_REF_NO,CIBIL_LEAD_ID,CIBIL_SCORE_NAME,CIBIL_SCORE_CARD_NAME,CIBIL_SCORE_CARD_VERSION,CIBIL_SCORE_DATE,CIBIL_SCORE,CIBIL_EXC_CODE_1,CIBIL_EXC_CODE_2,CIBIL_EXC_CODE_3,CIBIL_EXC_CODE_4,CIBIL_EXC_CODE_5,CIBIL_EXC_CODE_6,CIBIL_EXC_CODE_7,CIBIL_EXC_CODE_8,CIBIL_EXC_CODE_9,CIBIL_EXC_CODE_10,CIBIL_REASON_CODE_1,CIBIL_REASON_CODE_2,CIBIL_REASON_CODE_3,CIBIL_REASON_CODE_4,CIBIL_REASON_CODE_5,CIBIL_ERR_CODE) values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";

		try {
			Connection con = ConnectionFactory.getConnection();
			PreparedStatement st = con.prepareStatement(query);
			for (ScoreBean scoreBean : scoreBeanList) {
				st.setLong(1, srNo);
				st.setString(2, mobRefNo);
				st.setString(3, leadID);
				st.setString(4, scoreBean.getScoreName());
				st.setString(5, scoreBean.getScoreCardName());
				st.setString(6, scoreBean.getScoreCardVersion());
				st.setString(7, scoreBean.getScoreDate());
				st.setString(8, scoreBean.getScore());
				st.setString(9, scoreBean.getExclusionCode1());
				st.setString(10, scoreBean.getExclusionCode2());
				st.setString(11, scoreBean.getExclusionCode3());
				st.setString(12, scoreBean.getExclusionCode4());
				st.setString(13, scoreBean.getExclusionCode5());
				st.setString(14, scoreBean.getExclusionCode6());
				st.setString(15, scoreBean.getExclusionCode7());
				st.setString(16, scoreBean.getExclusionCode8());
				st.setString(17, scoreBean.getExclusionCode9());
				st.setString(18, scoreBean.getExclusionCode10());
				st.setString(19, scoreBean.getReasonCode1());
				st.setString(20, scoreBean.getReasonCode2());
				st.setString(21, scoreBean.getReasonCode3());
				st.setString(22, scoreBean.getReasonCode4());
				st.setString(23, scoreBean.getReasonCode5());
				st.setString(24, scoreBean.getErrorCode());

				st.addBatch();
			}

			st.executeBatch();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public void storeEnquiryAccountNumberSegment(List<AccountNumberBean> accNumBeanList, String mobRefNo, String leadID,
			long srNo) {

		String query = "insert into ILT_IQ_CIBIL_ENQ_ACCNO_SGMT(APP_PERS_SR_NO,CIBIL_MOB_REF_NO,CIBIL_LEAD_ID,CIBIL_ACC_NO) values(?,?,?,?)";

		try {
			Connection con = ConnectionFactory.getConnection();
			PreparedStatement st = con.prepareStatement(query);
			for (AccountNumberBean accNumBean : accNumBeanList) {
				st.setLong(1, srNo);
				st.setString(2, mobRefNo);
				st.setString(3, leadID);
				st.setString(4, accNumBean.getAccountNumber());

				st.addBatch();
			}
			st.executeBatch();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public void storeEmploymentSegment(EmploymentSegment employmentSegment, String mobRefNo, String leadID, long srNo) {
		String query = "insert into ILT_IQ_CIBIL_EMP_SGMT(APP_PERS_SR_NO,CIBIL_MOB_REF_NO,CIBIL_LEAD_ID,CIBIL_ACCO_TYPE,CIBIL_DATE_REP_AND_CERTIFIED,CIBIL_OCCUPATION_CODE,CIBIL_INCOME,CIBIL_NET_OR_INC_INDI,CIBIL_MON_OR_ANN_INC_INDI,CIBIL_DATE_OF_ENTRY_ERR_CODE,CIBIL_ERROR_CODE,DATE_OF_ENTRY_CIBIL_REM_CODE,CIBIL_REMARKS_CODE,DATE_ENTRY_ERR_OR_DIS_REM_CODE,ERROR_OR_DIS_REM_CODE_1,ERROR_OR_DIS_REM_CODE_2) values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";

		try {
			Connection con = ConnectionFactory.getConnection();
			PreparedStatement st = con.prepareStatement(query);
			st.setLong(1, srNo);
			st.setString(2, mobRefNo);
			st.setString(3, leadID);
			st.setString(4, employmentSegment.getAccountType());
			st.setString(5, employmentSegment.getDateReportedAndCertified());
			st.setString(6, employmentSegment.getOccupationCode());
			st.setString(7, employmentSegment.getIncome());
			st.setString(8, employmentSegment.getNetOrGrossIncomeIndicator());
			st.setString(9, employmentSegment.getMonthlyOrAnnualIncomeIndicator());
			st.setString(10, employmentSegment.getDateOfEntryForErrorCode());
			st.setString(11, employmentSegment.getErrorCode());
			st.setString(12, employmentSegment.getDateOfEntryForCibilRemarksCode());
			st.setString(13, employmentSegment.getCibilRemarksCode());
			st.setString(14, employmentSegment.getDateOfEntryForErrorOrDisputeRemarksCode());
			st.setString(15, employmentSegment.getErrorOrDisputeRemarksCode1());
			st.setString(16, employmentSegment.getErrorOrDisputeRemarksCode2());

			st.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void storeEmailContactSegment(List<String> emailAddressList, String mobRefNo, String leadID, long srNo) {

		String query = "insert into ILT_IQ_CIBIL_EMIL_SGMT(APP_PERS_SR_NO,CIBIL_MOB_REF_NO,CIBIL_LEAD_ID,CIBIL_EMIL_ID) values(?,?,?,?)";

		try {
			Connection con = ConnectionFactory.getConnection();
			PreparedStatement st = con.prepareStatement(query);
			for (String emailAddress : emailAddressList) {
				st.setLong(1, srNo);
				st.setString(2, mobRefNo);
				st.setString(3, leadID);
				st.setString(4, emailAddress);

				st.addBatch();
			}

			st.executeBatch();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void storeTelephoneSegment(List<TelephoneBean> telBeanList, String mobRefNo, String leadID, long srNo) {

		String query = "insert into ILT_IQ_CIBIL_TEL_SGMT(APP_PERS_SR_NO,CIBIL_MOB_REF_NO,CIBIL_LEAD_ID,CIBIL_TEL_NUM,CIBIL_TEL_EXTENSION,CIBIL_TEL_TYPE,CIBIL_ENRICHED_THRO_ENQY) values(?,?,?,?,?,?,?)";

		try {
			Connection con = ConnectionFactory.getConnection();
			PreparedStatement st = con.prepareStatement(query);

			for (TelephoneBean telBean : telBeanList) {
				st.setLong(1, srNo);
				st.setString(2, mobRefNo);
				st.setString(3, leadID);
				st.setString(4, telBean.getTelephoneNumber());
				st.setString(5, telBean.getTelephoneExt());
				st.setString(6, telBean.getTelephoneType());
				st.setString(7, telBean.getEnrichedThroughEnquiry());

				st.addBatch();
			}
			st.executeBatch();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void storeIdentificationSegment(List<IDBean> idBeanList, String mobRefNo, String leadID, long srNo) {
		String query = "insert into ILT_IQ_CIBIL_IDENTIFI_SGMT(APP_PERS_SR_NO,CIBIL_MOB_REF_NO,CIBIL_LEAD_ID,CIBIL_ID_TYPE,"
				+ "CIBIL_ID_NUMBER,CIBIL_ISSUE_DATE,CIBIL_EXPIRATION_DATE,CIBIL_ENRICHED_THRO_ENQY) "
				+ "values(?,?,?,?,?,?,?,?)";

		try {
			Connection con = ConnectionFactory.getConnection();
			PreparedStatement st = con.prepareStatement(query);

			for (IDBean idBean : idBeanList) {
				st.setLong(1, srNo);
				st.setString(2, mobRefNo);
				st.setString(3, leadID);
				st.setString(4, idBean.getIdType());
				st.setString(5, idBean.getIdNumber());
				st.setString(6, idBean.getIssueDate());
				st.setString(7, idBean.getExpirationDate());
				st.setString(8, idBean.getEnrichedThroughEnquiry());

				st.addBatch();

			}

			st.executeBatch();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public void storeNameSegment(NameSegment nameSegment, String mobRefNo, String leadID, long srNo) {

		String query = "insert into ILT_IQ_CIBIL_NAME_SGMT(APP_PERS_SR_NO,CIBIL_MOB_REF_NO,CIBIL_LEAD_ID,CIBIL_CONSU_NAME_FIELD_1,CIBIL_CONSU_NAME_FIELD_2,CIBIL_CONSU_NAME_FIELD_3,CIBIL_CONSU_NAME_FIELD_4,CIBIL_CONSU_NAME_FIELD_5,CIBIL_DATE_OF_BIRTH,CIBIL_GENDER,CIBIL_DATE_OF_ENY_FOR_ERR_CODE,CIBIL_ERROR_SEG_TAG,CIBIL_ERROR_CODE,CIBIL_DATE_ENTY_CIBIL_REM_CODE,CIBIL_REMARK_CODE,DATE_ERROR_OR_DISPUTE_CODE,ERROR_OR_DISPUTE_REMARK_CODE_1,ERROR_OR_DISPUTE_REMARK_CODE_2) values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";

		try {
			Connection con = ConnectionFactory.getConnection();
			PreparedStatement st = con.prepareStatement(query);
			st.setLong(1, srNo);
			st.setString(2, mobRefNo);
			st.setString(3, leadID);
			st.setString(4, nameSegment.getfName());
			st.setString(5, nameSegment.getmName());
			st.setString(6, nameSegment.getlName());
			st.setString(7, nameSegment.getNameField4());
			st.setString(8, nameSegment.getNameField5());
			st.setString(9, nameSegment.getDob());
			st.setString(10, nameSegment.getGender());
			st.setString(11, nameSegment.getDateOfEntryForErrorCode());
			st.setString(12, nameSegment.getErrorSegmentTag());
			st.setString(13, nameSegment.getErrorCode());
			st.setString(14, nameSegment.getDateOfEntryForCibilRemarksCode());
			st.setString(15, nameSegment.getCibilRemarksCode());
			st.setString(16, nameSegment.getDateOfEntryForErrorOrDisputeRemarksCode());
			st.setString(17, nameSegment.getErrorOrDisputeRemarksCode1());
			st.setString(18, nameSegment.getErrorOrDisputeRemarksCode2());

			st.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void storeHeaderSegment(HeaderSegment headerSegment, String mobRefNo, String leadID, long srNo) {

		String query = "insert into ILT_IQ_CIBIL_HEAD_SGMT(APP_PERS_SR_NO,CIBIL_MOB_REF_NO,CIBIL_LEAD_ID,CIBIL_MEM_REF_NUM,CIBIL_ENQ_MEM_USER_ID,CIBIL_SUB_REASON_CODE,CIBIL_ENQ_CONTROL_NUM,CIBIL_DATE_PROCESSED,CIBIL_TIME_PROCESSED) values(?,?,?,?,?,?,?,?,?)";

		try {
			Connection con = ConnectionFactory.getConnection();
			PreparedStatement st = con.prepareStatement(query);

			st.setLong(1, srNo);
			st.setString(2, mobRefNo);
			st.setString(3, leadID);
			st.setString(4, headerSegment.getMemberRefNumber());
			st.setString(5, headerSegment.getEnqMemberUserId());
			st.setString(6, headerSegment.getSubReasonCode());
			st.setString(7, headerSegment.getEnqControlNumber());
			st.setString(8, headerSegment.getDateProcessed());
			st.setString(9, headerSegment.getTimeProcessed());

			st.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/*
	 * public int insertPennantCibilResponse(PennantRequest req) { int status = 0;
	 * //int id=0;
	 * 
	 * String query =
	 * "insert into cust_api_cibil_data(CIBILSRNO,CUSTOMERNAME,PANNUMBER,DATEOFBIRTH,DATEPROCESSED,MEMBER_REF_NUM,"
	 * +
	 * "TIMEPROCESSED,SCORENAME,SCORECARDNAME,SCORECARDVERSION,SCORE,CIBILRESPONSE) values(?,?,?,?,?,?,?,?,?,?,?,?)"
	 * ;
	 * 
	 * try { Connection con = JdbcSQLServerConnection.getConnection();
	 * //PreparedStatement st = con.prepareStatement(query); //PreparedStatement
	 * st=con.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
	 * CallableStatement st=con.prepareCall(query); st.setString(1,
	 * req.getCibilSrNo()); st.setString(2, req.getCustomerName()); st.setString(3,
	 * req.getPan()); st.setDate(4, new java.sql.Date(req.getDob().getTime()));
	 * st.setString(5, req.getDateProcessed()); st.setString(6, req.getMemRefNo());
	 * st.setString(7, req.getTimeProcessed()); st.setString(8, req.getScoreName());
	 * st.setString(9, req.getScoreCardName()); st.setString(10,
	 * req.getScoreCardVersion()); st.setString(11, req.getScore());
	 * st.setString(12, req.getCibilResponse());
	 * 
	 * status = st.executeUpdate();
	 * 
	 * } catch (Exception e) { e.printStackTrace(); }
	 * 
	 * return status; }
	 */

	public int insertCibilResponse(StringBuilder inputCSV, NewCibilRequest ncr, String output, String inputRequest,
			String headerInfo, String ip, Map<String, Object> existMapData, String user_id) {
		int status = 0;
		int id = 0;

		/*
		 * String query =
		 * "begin insert into ilt_iq_app_pers_cibil(APP_PERS_MOB_REF_NO,APP_PERS_LEAD_ID,APP_PERS_INPUT_CSV,"
		 * +
		 * "APP_PERS_CIBIL_RESP,APP_PERS_CIBIL_REMARKS,APP_PERS_CIBIL_FLAG,APP_PERS_CIBIL_DATE,APP_PERS_CLIENT_REQ,"
		 * +
		 * "APP_PERS_REQ_HEADER,APP_PERS_INITIATED_DATE,APP_PERS_IP_ADDRESS,APP_PERS_INITIATED_BY) "
		 * + "values(?,?,?,?,?,?,?,?,?,SYSDATE,?,?) " +
		 * "RETURNING APP_PERS_SR_NO INTO ?;end;";
		 */
		String query = "insert into ilt_iq_app_pers_cibil(APP_PERS_MOB_REF_NO,APP_PERS_LEAD_ID,APP_PERS_INPUT_CSV,"
				+ "APP_PERS_CIBIL_RESP,APP_PERS_CIBIL_REMARKS,APP_PERS_CIBIL_FLAG,APP_PERS_CIBIL_DATE,APP_PERS_CLIENT_REQ,"
				+ "APP_PERS_REQ_HEADER,APP_PERS_INITIATED_DATE,APP_PERS_IP_ADDRESS,APP_PERS_INITIATED_BY) "
				+ " values(?,?,?,?,?,?,?,?,?,GETDATE(),?,?)";

		try {
			String generatedColumns[] = { "APP_PERS_SR_NO" };
			Connection con = ConnectionFactory.getConnection();
			// CallableStatement st=con.prepareCall(query);
			PreparedStatement st = con.prepareStatement(query, generatedColumns);
			st.setString(1, ncr.getHeaderSegment().getMobileRefNo());
			st.setString(2, ncr.getHeaderSegment().getLeadId());
			st.setString(3, inputCSV != null ? inputCSV.toString() : null);
			st.setString(4, output);
			if (output.startsWith("TUEF")) {
				st.setString(5, "SUCCESS");
			} else {
				st.setString(5, "ERROR");
			}

			if (!existMapData.isEmpty() && existMapData.get("output") != null) {
				st.setString(6, "N");
			} else {
				st.setString(6, "Y");
			}

			java.sql.Date date = (Date) existMapData.get("date");
			java.util.Date current_date = new java.util.Date();

			st.setDate(7, date != null ? date : new java.sql.Date(current_date.getTime()));
			st.setString(8, inputRequest);
			st.setString(9, headerInfo);
			st.setString(10, ip);
			st.setString(11, user_id);
			// st.registerOutParameter(12, Types.INTEGER);
			status = st.executeUpdate();
			// status = (st.executeUpdate()<=0)?0:1;
			ResultSet rs = st.getGeneratedKeys();
			if (rs.next()) {
				id = rs.getInt(1);
			}
			// id=st.getInt(1);
			if (id == 0) {
				id = 123456789;
			}
			System.out.println("insertCibilResponse:APP_PERS_SR_NO:id=" + id);

		} catch (Exception e) {
			e.printStackTrace();
		}

		return id;
	}

	/**
	 * 
	 * @param inputRequest This is input request string
	 * @param error        This is error string
	 * @param headerInfo   This is input headers string
	 * @param ip           This is ip address of request machine.
	 * @param user_id      This is user id of request
	 * 
	 *                     These lines of code store inputs and error output in db
	 *                     for further reference.
	 * 
	 * @return int This returns unique serial number of db insert operation.
	 */
	public int insertErrorResponse(String inputRequest, String error, String headerInfo, String ip, String user_id) {
		int status = 0;
		int id = 0;

		String query = "begin insert into ilt_iq_app_pers_cibil(APP_PERS_API_REMARK,APP_PERS_CLIENT_REQ,APP_PERS_CLIENT_RESP,APP_PERS_REQ_HEADER,APP_PERS_IP_ADDRESS,APP_PERS_INITIATED_BY,APP_PERS_INITIATED_DATE) values('ERROR',?,?,?,?,?,SYSDATE) RETURNING APP_PERS_SR_NO INTO ?;end;";

		try {
			Connection con = ConnectionFactory.getConnection();
			CallableStatement st = con.prepareCall(query);
			st.setString(1, inputRequest);
			st.setString(2, error);
			st.setString(3, headerInfo);
			st.setString(4, ip);
			st.setString(5, user_id);
			st.registerOutParameter(6, Types.INTEGER);
			status = st.executeUpdate();

			id = st.getInt(6);

		} catch (Exception e) {
			e.printStackTrace();
		}

		return id;
	}

	public void storeCibilReportToDB(String srNo, String fileNo, int custID, ByteArrayOutputStream out) {

		String query = "update ilt_iq_app_pers_cibil set APP_PERS_CIBIL_FILE=? where APP_PERS_FILE_NO=? and APP_PERS_ID=? and APP_PERS_SR_NO=?";

		try {
			Connection con = ConnectionFactory.getConnection();
			PreparedStatement st = con.prepareStatement(query);
			st.setBytes(1, out != null ? out.toByteArray() : null);

			st.setString(2, fileNo);
			st.setInt(3, custID);
			st.setString(4, srNo);

			st.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 
	 * @param out This is cibil report Base64 string
	 * @param id  This is id against which cibil report will be saved in db
	 * 
	 *            This method stores cibil report in Base64 String in db clob column
	 *            against given sr no id.
	 */
	public void storeCibilReportToDB(ByteArrayOutputStream out, long id) {
		String query = "update ilt_iq_app_pers_cibil set APP_PERS_CIBIL_FILE=? where APP_PERS_SR_NO=?";

		try {
			Connection con = ConnectionFactory.getConnection();
			PreparedStatement st = con.prepareStatement(query);
			st.setBytes(1, out != null ? out.toByteArray() : null);
			st.setLong(2, id);

			st.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public Map<String, String> getAccountTypeAndEnquiryPurposeData() {
		ResultSet rs = null;
		Map<String, String> mapData = new HashMap<String, String>();

		String query = "select CIBIL_VALUE,CIBIL_ACCOUNT_TYPE from ILM_GL_CIBIL_ACC_MST WHERE CIBIL_ACC_STATUS='ACT'";
		try {
			Connection con = ConnectionFactory.getConnection();
			Statement st = con.createStatement();
			rs = st.executeQuery(query);
			while (rs.next()) {
				mapData.put(rs.getString(1), rs.getString(2));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return mapData;

	}

	public String getInquiryId(Connection con) {
		String tmpInqId = "";
		try {

			String inquiryid = "select ISEQ_INQ_ID.nextVal from dual";
			Statement st = con.createStatement();
			ResultSet rs = st.executeQuery(inquiryid);

			while (rs.next()) {
				tmpInqId = "ES" + rs.getInt("NEXTVAL");
			}
			System.out.println("InquiryId:" + tmpInqId);
		} catch (Exception e) {

		}

		return tmpInqId;
	}

	public static String fixedLengthString(String string, int length) {
		return String.format("%0$-" + length + "s", string);
	}

	public void updateClientResponse(long sr_no, String clientResponse) {
		String query = "update ilt_iq_app_pers_cibil set APP_PERS_CLIENT_RESP=? where APP_PERS_SR_NO=?";

		try {
			Connection con = ConnectionFactory.getConnection();
			PreparedStatement st = con.prepareStatement(query);
			st.setString(1, clientResponse);
			st.setLong(2, sr_no);
			st.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 
	 * @param score
	 * @param userId
	 * @return This method return cibil remarks and band values against score and
	 *         userId.
	 */
	public Map<String, String> getCibilRemarksForScore(int score, String userId) {

		Map<String, String> mapData = new HashMap<>();

		ResultSet rs = null;

		// cast(getdate() As Date)
		// String query = "select remarks,band_value from ilt_ap_cibil_remark where ?
		// between from_score and to_score and trunc(sysdate) between effective_date and
		// to_date and API_USER_ID=?";
		// Update query on 27/1/2021 for SQL Server
		String query = "select remarks,band_value from ilt_ap_cibil_remark where ? between "
				+ "from_score and to_score and cast(getdate() As Date) between effective_date and to_date and API_USER_ID=?";
		try {
			Connection con = ConnectionFactory.getConnection();
			PreparedStatement st = con.prepareStatement(query);
			st.setInt(1, score);
			st.setString(2, userId);

			rs = st.executeQuery();
			while (rs.next()) {
				mapData.put("REMARKS", rs.getString("remarks"));
				mapData.put("BAND", rs.getString("band_value"));
				return mapData;

			}
		} catch (Exception e) {
			e.printStackTrace();
			mapData.put("ERROR", "DBConnectException");
			return mapData;
		}

		return null;
	}

	/**
	 * 
	 * @param userId        Input userId of request headers.
	 * @param authKey       Input authKey of request headers.
	 * @param inputRespType This is response type requested by user.
	 * 
	 *                      <p>
	 *                      This method validates users auth key, userId and
	 *                      resp_type against db and returns restriction
	 *                      message,DBException message or api user status such as
	 *                      'active' or 'deactive'.
	 *                      </p>
	 * 
	 * @return Api user status against auth key and userId.
	 */
	public String validateUserIdAuthKey(String userId, String authKey, String inputRespType) {
		// TODO Auto-generated method stub

		ResultSet rs = null;

		String query = "select api_user_status,api_resp_type from ilm_gl_api_auth_mst where api_user_id = ? and api_auth_key=?";
		try {
			Connection con = ConnectionFactory.getConnection();
			PreparedStatement st = con.prepareStatement(query);
			st.setString(1, userId);
			st.setString(2, authKey);
			rs = st.executeQuery();
			while (rs.next()) {
				String respType = rs.getString("api_resp_type");

				if (respType == null) {
					return "RestrictedRespType";
				} else if (!respType.contains(inputRespType)) {
					return "RestrictedRespType";
				}

				return rs.getString("api_user_status");

			}
		} catch (Exception e) {
			e.printStackTrace();
			return "DBConnectException";
		}

		return null;

	}

	/**
	 * 
	 * @param inputCSV Input CSV string to be checked in db.
	 * @return This method checks compare given input csv with db table.So that
	 *         there will not multiple hits to cibil server for same request in day.
	 */
	public Map<String, Object> findCibilRespByInputCSVForCurrentDay(StringBuilder inputCSV) {

		ResultSet rs = null;
		String cibil_resp = null;
		Date cibil_date = null;
		Map<String, Object> mapData = new HashMap<String, Object>();

		// String query = "select app_pers_cibil_resp,app_pers_cibil_date from
		// ilt_iq_app_pers_cibil where app_pers_input_csv = ? and
		// trunc(app_pers_initiated_date)= trunc(sysdate) and
		// app_pers_cibil_remarks='SUCCESS' and rownum = 1 order by app_pers_sr_no
		// desc";
		String query = "select TOP 1 app_pers_cibil_resp,app_pers_cibil_date from ilt_iq_app_pers_cibil "
				+ "where app_pers_input_csv = ? and cast(app_pers_initiated_date As Date)= cast(getdate() As Date) and "
				+ "app_pers_cibil_remarks='SUCCESS' order by app_pers_sr_no desc";
		try {
			Connection con = ConnectionFactory.getConnection();
			PreparedStatement st = con.prepareStatement(query);
			st.setString(1, inputCSV.toString());
			// st.setString(2, authKey);
			rs = st.executeQuery();
			while (rs.next()) {
				cibil_resp = rs.getString("app_pers_cibil_resp") != null ? rs.getString("app_pers_cibil_resp").trim()
						: null;
				System.out.println("Cibil Response collected from database");
				cibil_date = rs.getDate("app_pers_cibil_date");
			}

			mapData.put("output", cibil_resp);
			mapData.put("date", cibil_date);

		} catch (Exception e) {
			e.printStackTrace();
			mapData.put("exception", "DBConnectException");
			return mapData;
		}
		return mapData;
	}

	public boolean isExistingCibilResponse(String input) {

		ResultSet rs = null;

		String query = "select count(app_pers_sr_no) as srCount from ilt_iq_app_pers_cibil where dbms_lob.compare(app_pers_cibil_resp,?)=0";
		try {
			Connection con = ConnectionFactory.getConnection();
			Clob clob = con.createClob();
			clob.setString(1, input);
			PreparedStatement st = con.prepareStatement(query);
			st.setClob(1, clob);
			rs = st.executeQuery();
			while (rs.next()) {
				int count = rs.getInt("srCount");
				return true;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}

		return false;
	}

	/**
	 * 
	 * @param srNo Input serial number provided in input request
	 * @return It returns data map against serial number from db.
	 */
	public Map<String, Object> getExistingCibilResponse(String srNo) {

		ResultSet rs = null;

		Map<String, Object> mapData = new HashMap<String, Object>();

		String query = "select app_pers_cibil_resp,app_pers_cibil_date,app_pers_cibil_remarks,app_pers_input_csv from ilt_iq_app_pers_cibil where APP_PERS_SR_NO=?";
		try {
			Connection con = ConnectionFactory.getConnection();
			PreparedStatement st = con.prepareStatement(query);
			st.setString(1, srNo);
			rs = st.executeQuery();
			while (rs.next()) {
				mapData.put("output",
						rs.getString("app_pers_cibil_resp") != null ? rs.getString("app_pers_cibil_resp").trim()
								: null);
				mapData.put("date", rs.getDate("app_pers_cibil_date"));
				mapData.put("remarks", rs.getString("app_pers_cibil_remarks"));
				mapData.put("inputCSV", rs.getString("app_pers_input_csv"));
			}

		} catch (Exception e) {
			e.printStackTrace();
			mapData.put("exception", "DBConnectException");
			return mapData;
		}

		return mapData;
	}
}
