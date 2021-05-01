package com.avanse.service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import com.avanse.common.util.CommonUtil;
import com.avanse.model.CibilResponse;
import com.avanse.model.OthersAccountBean;
import com.avanse.model.OwnAccountBean;

// Added on - 24/3/2021
public class ResponseType9 {
	private DAOBASE dao = new DAOBASE();
	Properties prop = null;

	// Custom response code for client resp_type=09, `DIGAKBJ`
	/*
	 * getResponseType09
	 * @Param id
	 * @Param headers
	 * @Param score
	 * return
	 * -- Method to get response for type==09 partner=DIGAKBJ
	 * */
	public List<CibilResponse> getResponseType09(long id, Map<String, String> headers, int score, List<CibilResponse> responseList) {
		CibilResponse cibilResponse = null;
		//List<CibilResponse> responseList = new ArrayList<>();
		List<CibilResponse> tempResponseList = new ArrayList<>();
		CommonUtil commonUtil = new CommonUtil();
		tempResponseList = responseList;
		System.out.println("---- Started procesing getResponseType09 ---------");
		try {
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
					System.out.println("getResponseType09:accType=" + accType + " writtOffPrincAmt=" + writtOffPrincAmt
							+ " willfulDefaultCount=" + willfulDefaultCount);
					try {
						// These line of code compare account closed date with current date and checks
						// if difference is greater than 180 days or not.
						String dateClosed = own.getDateClosed();
						long diffInMillies, diff = 0;
						if (dateClosed != null) {
							diffInMillies = Math.abs(sdf.parse(dateClosed).getTime() - new java.util.Date().getTime());
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
								totalObligation += commonUtil.getEMI(princAmt, 12, 5);
								break;
							// acc type housing loan 02
							case "02":
								totalObligation += commonUtil.getEMI(princAmt, 9, 15);
								break;
							// acc type personal loan 05
							case "05":
								totalObligation += commonUtil.getEMI(princAmt, 14, 3);
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
								totalObligation += commonUtil.getEMI(princAmt, 18, 3);
								break;
							}
						}
					}catch (Exception e) {
						System.out.println("Xception:getResponseType09-1="+e);
						e.printStackTrace(System.out);
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

			//cibilResponse.setTotalObligation(Math.round(totalObligation));

			/*if (score >= 710) {
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
			}*/

			responseList = new ArrayList<CibilResponse>();
			responseList.add(cibilResponse);
			// Code added on 20/1/2021 to save pdf report to folder for easy access.
			responseList = commonUtil.getCibilReportContent(tempResponseList, cibilResponse, userId, responseList);

		} catch (Exception e) {
			System.out.println("Xception:getResponseType09-2="+e);
			e.printStackTrace(System.out);
		}
		return responseList;
	}

	public Map<String, String> getCibilRemarksForScore(int score, String userId) {
		return dao.getCibilRemarksForScore(score, userId);
	}
}
