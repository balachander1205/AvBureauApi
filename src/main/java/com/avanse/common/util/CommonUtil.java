package com.avanse.common.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.avanse.model.CibilResponse;
import com.avanse.model.EnquiryBean;

public class CommonUtil {
	/*
	 * getEnquries 
	 * @Param cibilResponse 
	 * @return
	 */
	public HashMap<String, String> getEnquries(CibilResponse cibilResponse) {
		int past30daysEnqCount = 0;
		int past12MonthsEnqCount = 0;
		int past24MonthsEnqCount = 0;

		HashMap<String, String> enquries = new HashMap();
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DATE, -30);
		Date last30DaysDate = cal.getTime();
		cal.add(Calendar.DATE, 30);

		cal.add(Calendar.MONTH, -12);
		Date last12MonthsDate = cal.getTime();
		cal.add(Calendar.MONTH, 12);

		cal.add(Calendar.MONTH, -24);
		Date last24MonthsDate = cal.getTime();
		cal.add(Calendar.MONTH, 24);
		try {
			if (cibilResponse.getEnquirySegment() != null) {
				for (EnquiryBean enquiryBean : cibilResponse.getEnquirySegment().getEnquiryBeanList()) {
					String memberName = enquiryBean.getEnquiringMemberShortName() != null
							? enquiryBean.getEnquiringMemberShortName()
							: "";
					String enquiryDate = enquiryBean.getDateOfEnquiry() != null
							? getFormattedDateOrTime(enquiryBean.getDateOfEnquiry(), "ddMMyyyy")
							: "";
					if (enquiryDate != null && !enquiryDate.equals("")) {
						Date enqDate = new SimpleDateFormat("dd-MM-yyyy").parse(enquiryDate);
						if (enqDate.compareTo(last30DaysDate) >= 0) {
							past30daysEnqCount++;
						}
						if (enqDate.compareTo(last12MonthsDate) >= 0) {
							past12MonthsEnqCount++;
						}
						if (enqDate.compareTo(last24MonthsDate) >= 0) {
							past24MonthsEnqCount++;
						}
					}
				}
				// Add enquries to map
				enquries.put("past30daysEnq", String.valueOf(past30daysEnqCount));
				enquries.put("past12MonthsEnq", String.valueOf(past12MonthsEnqCount));
				enquries.put("past24MonthsEnq", String.valueOf(past24MonthsEnqCount));
			} else {
				// Add '0' enquries to map
				enquries.put("past30daysEnq", String.valueOf(0));
				enquries.put("past12MonthsEnq", String.valueOf(0));
				enquries.put("past24MonthsEnq", String.valueOf(0));
			}
		} catch (Exception e) {
			// Add '0' enquries to map
			enquries.put("past30daysEnq", String.valueOf(0));
			enquries.put("past12MonthsEnq", String.valueOf(0));
			enquries.put("past24MonthsEnq", String.valueOf(0));
			System.out.println("[ CommonUtil:getEnquries:Xception ] =" + e);
			e.printStackTrace();
		}
		return enquries;
	}

	/*
	 * getFormattedDateOrTime 
	 * @Param input 
	 * @return
	 */
	public String getFormattedDateOrTime(String input, String format) {
		System.out.println("getFormattedDateOrTime:started:input=" + input + " format=" + format);
		String output = "";
		try {
			SimpleDateFormat format1 = new SimpleDateFormat("ddMMyyyy");
			SimpleDateFormat format2 = null;
			if (format.equals("ddMMyyyy")) {
				format2 = new SimpleDateFormat("dd-MM-yyyy");
			} else if (format.equals("hhmmss")) {
				format2 = new SimpleDateFormat("hh:mm:ss");
			}

			if (input != null && !input.trim().equals("")) {
				Date date = format1.parse(input);
				output = format2.format(date);
			}
		} catch (ParseException e) {
			System.out.println("[ Xception:getFormattedDateOrTime ] " + e);
			e.printStackTrace();
		}
		return output;
	}

	/*
	 * getCibilBand
	 * @Param score 
	 * @Param srNo return -- Method to get band value based on cibil score. -
	 * 11/3/2021
	 */
	public String getCibilBand(int score, long srNo) {
		String band = "";
		try {
			if (score == -1 || score == 0) {
				band = "1";
			} else if (score >= 1 && score <= 299) {
				band = "2";
			} else if (score >= 300 && score <= 709) {
				band = "3";
			} else if (score >= 710 && score <= 720) {
				band = "4";
			} else if (score >= 721 && score <= 730) {
				band = "5";
			} else if (score >= 731 && score <= 740) {
				band = "6";
			} else if (score >= 741 && score <= 750) {
				band = "7";
			} else if (score >= 751 && score <= 766) {
				band = "8";
			} else if (score >= 767) {
				band = "9";
			}
		} catch (Exception e) {
			System.out.println("Xception:getCibilBand:SrNo=" + String.valueOf(srNo) + " error=" + e);
		}
		return band;
	}
	
	/*
	 *  getRejectRemarks
	 *  @Param isWriteOff
	 *  @Param isDpd
	 *  @Param isDpd
	 *  @return
	 *  -- Method to get cibil reject reason description - 13/3/2021
	 * */
	public String getRejectRemarks(boolean isWriteOff, boolean isDpd, long srNo, boolean isWillfullDefault) {
		String desc = "";
		System.out.println("getRejectRemarks:isWriteOff=" + isWriteOff + " isDpd=" + isDpd + " isWillfullDefault="
				+ isWillfullDefault + " srNo=" + String.valueOf(srNo));
		try {
			if(isWriteOff) {
				desc = desc + "Account has writeOff. ";
			}if(isDpd) {
				desc = desc + "Account has DPD. ";
			}if(isWillfullDefault) {
				desc = desc + "Account has willfull default.";
			}
			System.out.println("Description="+desc);
		} catch (Exception e) {
			System.out.println("Xception:getRejectRemarks:srNo="+String.valueOf(srNo) + " error=" + e);
		}
		return desc;
	}
}
