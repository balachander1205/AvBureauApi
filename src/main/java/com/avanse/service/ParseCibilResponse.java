package com.avanse.service;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.avanse.model.AccountNumberBean;
import com.avanse.model.AccountNumberSegment;
import com.avanse.model.AccountSegment;
import com.avanse.model.AddressBean;
import com.avanse.model.AddressSegment;
import com.avanse.model.CibilResponse;
import com.avanse.model.ConsumerDisputeRemarksSegment;
import com.avanse.model.EmailContactSegment;
import com.avanse.model.EmploymentSegment;
import com.avanse.model.EndSegment;
import com.avanse.model.EnquiryBean;
import com.avanse.model.EnquirySegment;
import com.avanse.model.ErrorSegment;
import com.avanse.model.HeaderSegment;
import com.avanse.model.IDBean;
import com.avanse.model.IdentificationSegment;
import com.avanse.model.NameSegment;
import com.avanse.model.OthersAccountBean;
import com.avanse.model.OwnAccountBean;
import com.avanse.model.ScoreBean;
import com.avanse.model.ScoreSegment;
import com.avanse.model.TelephoneBean;
import com.avanse.model.TelephoneSegment;
import com.avanse.model.UserReferenceErrorSegment;
import com.avanse.util.PropertyReader;
import com.google.gson.Gson;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Font.FontFamily;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.html.WebColors;
import com.itextpdf.text.pdf.ColumnText;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfPageEventHelper;
import com.itextpdf.text.pdf.PdfTemplate;
import com.itextpdf.text.pdf.PdfWriter;

/**
 * <h1>Avanse Bureau Api's</h1>
 *
 * <p>
 * The class is used for parsing cibil response for AvBureauApi api's.
 * </p>
 * 
 * @author Swapnil Sawant
 * @version 1.0
 * @since 2019-07-22
 */
public class ParseCibilResponse {

	class MyFooter extends PdfPageEventHelper {
		Font ffont = new Font(FontFamily.UNDEFINED, 10);
		PdfTemplate t;
		PdfTemplate total;

		@Override
		public void onEndPage(PdfWriter writer, Document document) {
			PdfContentByte cb = writer.getDirectContent();
			Phrase footer = new Phrase(String.format("Page %d", writer.getPageNumber(), ffont));
			ColumnText.showTextAligned(cb, Element.ALIGN_RIGHT, footer,
					(document.right() - document.left()) / 2 + document.leftMargin(), document.bottom() - 10, 0);
		}
	}

	private DAOBASE dao = new DAOBASE();

	public Map<String, String> getMapOfSegmentTags() {
		Map<String, String> segTagMap = new LinkedHashMap<String, String>();

		segTagMap.put("TUEF", "TUEF12");
		segTagMap.put("PN", "PN03N");
		segTagMap.put("ID", "ID03I");
		segTagMap.put("PT", "PT03T");
		segTagMap.put("EC", "EC03C");
		segTagMap.put("EM", "EM03E");
		segTagMap.put("PI", "PI03I");
		segTagMap.put("SC", "SC10");
		segTagMap.put("PA", "PA03A");
		segTagMap.put("TL", "TL04T");
		segTagMap.put("IQ", "IQ04I");
		segTagMap.put("DR", "DR03D");
		segTagMap.put("ERRR", "ERRR");
		segTagMap.put("UR", "UR03U01");
		segTagMap.put("ES", "ES07");

		return segTagMap;
	}

	public List<String> getSegmentTagList(String inputString) {

		Map<String, String> segmentTagMap = getMapOfSegmentTags();
		Map<String, Integer> segmentCount = new LinkedHashMap<String, Integer>();

		List<String> segmentTagList = new ArrayList<String>();

		for (Map.Entry<String, String> entry : segmentTagMap.entrySet()) {
			String key = entry.getKey();
			String value = entry.getValue();

			int count = checkSegmentCount(inputString, value);

			segmentCount.put(key, count);
		}

		if (segmentCount.get("ES") > 1) {
			segmentCount.put("ES", 1);
		}

		for (Map.Entry<String, Integer> entry : segmentCount.entrySet()) {
			for (int i = 1; i <= entry.getValue(); i++) {
				if (entry.getKey().equals("TUEF") || entry.getKey().equals("PN") || entry.getKey().equals("EM")
						|| entry.getKey().equals("SC") || entry.getKey().equals("DR") || entry.getKey().equals("ERRR")
						|| entry.getKey().equals("UR") || entry.getKey().equals("ES")) {
					segmentTagList.add(segmentTagMap.get(entry.getKey()));

				} else if (entry.getKey().equals("PA")) {
					if (i > 99) {
						segmentTagList.add(segmentTagMap.get(entry.getKey()) + String.format("%02d", 99));
					} else {
						segmentTagList.add(segmentTagMap.get(entry.getKey()) + String.format("%02d", i));
					}
				} else if (entry.getKey().equals("TL") || entry.getKey().equals("IQ")) {
					if (i > 999) {
						segmentTagList.add(segmentTagMap.get(entry.getKey()) + String.format("%03d", 999));
					} else {
						segmentTagList.add(segmentTagMap.get(entry.getKey()) + String.format("%03d", i));
					}
				} else {
					segmentTagList.add(segmentTagMap.get(entry.getKey()) + String.format("%02d", i));

				}
			}
		}
		return segmentTagList;
	}

	public static int checkSegmentCount(String input, String pattern) {

		int lastIndex = 0;
		int count = 0;

		while (lastIndex != -1) {

			lastIndex = input.indexOf(pattern, lastIndex);

			if (lastIndex != -1) {
				count++;
				lastIndex += pattern.length();
			}
		}

		return count;
	}

	/**
	 * 
	 * @param inputString Input cibil response to be parsed
	 * @return This method parse cibil response based on segment tag list. Segment
	 *         tag list contains list of segments in cibil segments order and
	 *         returns parsed data model.
	 */
	public CibilResponse getParsedCibilResponse(StringBuilder inputString) {
		// This line of code get segment tag list as per no of segments present in cibil
		// response.
		List<String> tagList = this.getSegmentTagList(inputString.toString());
		CibilResponse cibilResponse = null;

		System.out.println("Start parse" + new java.util.Date().getTime());
		if (!inputString.toString().startsWith("ERRR")) {
			System.out.println("inside correct response");
			// If cibil String starts with TUEF12
			cibilResponse = this.parseCorrectResponse(inputString, tagList);
		} else {
			System.out.println("inside error response");
			// If cibil string starts with ERR
			cibilResponse = this.parseErrorResponse(inputString, tagList);
		}
		System.out.println("End parse" + new java.util.Date().getTime());

		return cibilResponse;
	}

	public void storeParsedCibilResponse(CibilResponse cibilResponse, String mobRefNo, String leadId, long srNo) {
		dao.storeParsedCibilResponse(cibilResponse, mobRefNo, leadId, srNo);
	}

	/**
	 * 
	 * This method is used to generate cibil report in Base64 string presentation.
	 * 
	 * @return It returns cibil report in ByteArrayOutputStream.
	 *
	 */
	public ByteArrayOutputStream getCibilReport(List<CibilResponse> cibilRespList) {

		ByteArrayOutputStream baosPDF = new ByteArrayOutputStream();

		Map<String, String> accTypeAndEnqPurposeMap = getAccountTypeAndEnquiryPurposeMap();
		try {
			System.out.println("Started pdf generation");

			Document iText_Create_Table = new Document();
			PdfWriter writer = PdfWriter.getInstance(iText_Create_Table, baosPDF);

			MyFooter event = new MyFooter();
			writer.setPageEvent(event);

			iText_Create_Table.open();

			BaseColor myColor = WebColors.getRGBColor("#ededed");
			BaseColor consumerCIR = WebColors.getRGBColor("#fad600");
			BaseColor skyBlueColor = WebColors.getRGBColor("#03a0d0");
			BaseColor grayColor = WebColors.getRGBColor("#7e7e7e");
			BaseColor royalBlueColor = WebColors.getRGBColor("#006685");
			BaseColor redColor = WebColors.getRGBColor("#DC143C");

			Font consumerCIRfont = new Font(FontFamily.UNDEFINED, 16);
			consumerCIRfont.setColor(WebColors.getRGBColor("#000000"));

			Font titleFont = new Font(FontFamily.TIMES_ROMAN, 30, Font.BOLD);
			titleFont.setColor(skyBlueColor);

			Font consCIRInfofont = new Font(FontFamily.UNDEFINED, 8);

			Font segNamefont = new Font(FontFamily.HELVETICA, 12, Font.BOLD);
			segNamefont.setColor(WebColors.getRGBColor("#666666"));

			Font summaryHeadersFont = new Font(FontFamily.UNDEFINED, 12, Font.BOLD);
			summaryHeadersFont.setColor(skyBlueColor);

			Font segHeadersFont = new Font(FontFamily.HELVETICA, 9, Font.BOLD);
			segHeadersFont.setColor(skyBlueColor);

			Font addRecordsFont = new Font(FontFamily.HELVETICA, 9, Font.BOLD);
			addRecordsFont.setColor(redColor);

			Font scoreNameFont = new Font(FontFamily.UNDEFINED, 10);
			scoreNameFont.setColor(royalBlueColor);

			Font scoreFont = new Font(FontFamily.UNDEFINED, 22);
			scoreFont.setColor(royalBlueColor);

			Font font = new Font(FontFamily.HELVETICA, 9);

			Font contentFont = new Font(FontFamily.HELVETICA, 9, Font.NORMAL);
			contentFont.setColor(WebColors.getRGBColor("#676767"));
			Font contentFontBold = new Font(FontFamily.HELVETICA, 9, Font.BOLD);

			PdfPTable headerTable = new PdfPTable(6);
			headerTable.setTotalWidth(PageSize.A4.getWidth() - 50);
			headerTable.setLockedWidth(true);
			headerTable.setSpacingAfter(10);

			PdfPCell table_Heading_cell = new PdfPCell();
			PdfPCell cell1, cell3, cell4, cell5;
			PdfPCell blankCell;

			Paragraph contentParagraph = new Paragraph("");
			String fullName = "", fullName2 = "";

			Paragraph title = new Paragraph("CIBIL REPORT", titleFont);
			title.setAlignment(Element.ALIGN_CENTER);
			table_Heading_cell = new PdfPCell();
			table_Heading_cell.addElement(title);
			table_Heading_cell.setMinimumHeight(35);
			table_Heading_cell.setUseAscender(true);
			table_Heading_cell.setVerticalAlignment(Element.ALIGN_LEFT);
			table_Heading_cell.setBorder(Rectangle.NO_BORDER);
			table_Heading_cell.setColspan(6);
			headerTable.addCell(table_Heading_cell);

			iText_Create_Table.add(headerTable);

			/* Table heading */

			for (int j = 0; j < cibilRespList.size(); j++) {
				CibilResponse cibilResponse = cibilRespList.get(j);

				PdfPTable dataTable = new PdfPTable(6);
				dataTable.setTotalWidth(PageSize.A4.getWidth() - 50);
				dataTable.setLockedWidth(true);
				dataTable.setSpacingAfter(10);

				PdfPTable dataTable2 = new PdfPTable(4);
				dataTable2.setTotalWidth(PageSize.A4.getWidth() - 50);
				dataTable2.setLockedWidth(true);
				dataTable2.setSpacingBefore(10);

				PdfPTable dataTable3 = new PdfPTable(4);
				dataTable3.setTotalWidth(PageSize.A4.getWidth() - 50);
				dataTable3.setLockedWidth(true);
				dataTable3.setSpacingBefore(10);

				PdfPTable endTable = new PdfPTable(4);
				endTable.setTotalWidth(PageSize.A4.getWidth() - 50);
				endTable.setLockedWidth(true);
				endTable.setSpacingBefore(10);

				if (j == 0 && cibilResponse.getNameSegment() != null && cibilResponse.getHeaderSegment() != null) {
					title = new Paragraph("CONSUMER CIR", consumerCIRfont);
					title.setAlignment(Element.ALIGN_LEFT);
					table_Heading_cell = new PdfPCell();
					table_Heading_cell.addElement(title);
					table_Heading_cell.setBackgroundColor(consumerCIR);
					table_Heading_cell.setMinimumHeight(25);
					table_Heading_cell.setUseAscender(true);
					table_Heading_cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
					table_Heading_cell.setBorder(Rectangle.NO_BORDER);
					table_Heading_cell.setColspan(6);
					dataTable.addCell(table_Heading_cell);

					String fName = cibilResponse.getNameSegment().getfName() != null
							? cibilResponse.getNameSegment().getfName()
							: "";
					String mName = cibilResponse.getNameSegment().getmName() != null
							? cibilResponse.getNameSegment().getmName()
							: "";
					String lName = cibilResponse.getNameSegment().getlName() != null
							? cibilResponse.getNameSegment().getlName()
							: "";
					String nameField4 = cibilResponse.getNameSegment().getNameField4() != null
							? cibilResponse.getNameSegment().getNameField4()
							: "";
					String nameField5 = cibilResponse.getNameSegment().getNameField5() != null
							? cibilResponse.getNameSegment().getNameField5()
							: "";

					fullName = fName + " " + mName + " " + lName + " " + nameField4 + " " + nameField5;
					fullName2 = fName + " " + mName + " " + lName;

					contentParagraph = new Paragraph("CONSUMER:" + fullName2, consCIRInfofont);
					cell1 = new PdfPCell();
					cell1.setBorder(Rectangle.NO_BORDER);
					cell1.setColspan(3);
					cell1.addElement(contentParagraph);
					dataTable.addCell(cell1);

					contentParagraph = new Paragraph("", consCIRInfofont);
					cell1 = new PdfPCell();
					cell1.setBorder(Rectangle.NO_BORDER);
					cell1.addElement(contentParagraph);
					dataTable.addCell(cell1);

					String date = cibilResponse.getHeaderSegment().getDateProcessed() != null
							? getFormattedDateOrTime(cibilResponse.getHeaderSegment().getDateProcessed(), "ddMMyyyy")
							: "";

					contentParagraph = new Paragraph("DATE:" + date, consCIRInfofont);
					cell1 = new PdfPCell();
					cell1.setColspan(2);
					cell1.setBorder(Rectangle.NO_BORDER);
					cell1.addElement(contentParagraph);
					dataTable.addCell(cell1);

					String memberID = cibilResponse.getHeaderSegment().getEnqMemberUserId() != null
							? cibilResponse.getHeaderSegment().getEnqMemberUserId()
							: "";
					contentParagraph = new Paragraph("MEMBER ID:" + memberID, consCIRInfofont);
					cell1 = new PdfPCell();
					cell1.setBorder(Rectangle.NO_BORDER);
					cell1.setColspan(3);
					cell1.addElement(contentParagraph);
					dataTable.addCell(cell1);

					contentParagraph = new Paragraph("", consCIRInfofont);
					cell1 = new PdfPCell();
					cell1.setBorder(Rectangle.NO_BORDER);
					cell1.addElement(contentParagraph);
					dataTable.addCell(cell1);

					String time = cibilResponse.getHeaderSegment().getTimeProcessed() != null
							? getFormattedDateOrTime(cibilResponse.getHeaderSegment().getTimeProcessed(), "hhmmss")
							: "";

					contentParagraph = new Paragraph("TIME:" + time, consCIRInfofont);
					cell1 = new PdfPCell();
					cell1.setBorder(Rectangle.NO_BORDER);
					cell1.setColspan(2);
					cell1.addElement(contentParagraph);
					dataTable.addCell(cell1);

					String memberRefNo = cibilResponse.getHeaderSegment().getMemberRefNumber() != null
							? cibilResponse.getHeaderSegment().getMemberRefNumber()
							: "";
					contentParagraph = new Paragraph("MEMBER REFERENCE NUMBER:" + memberRefNo, consCIRInfofont);
					cell1 = new PdfPCell();
					cell1.setBorder(Rectangle.NO_BORDER);
					cell1.setColspan(3);
					cell1.addElement(contentParagraph);
					dataTable.addCell(cell1);

					contentParagraph = new Paragraph("", consCIRInfofont);
					cell1 = new PdfPCell();
					cell1.setBorder(Rectangle.NO_BORDER);
					cell1.addElement(contentParagraph);
					dataTable.addCell(cell1);

					String controlNum = cibilResponse.getHeaderSegment().getEnqControlNumber() != null
							? cibilResponse.getHeaderSegment().getEnqControlNumber()
							: "";

					contentParagraph = new Paragraph("CONTROL NUMBER:" + controlNum, consCIRInfofont);
					cell1 = new PdfPCell();
					cell1.setBorder(Rectangle.NO_BORDER);
					cell1.setColspan(2);
					cell1.addElement(contentParagraph);
					dataTable.addCell(cell1);

					blankCell = new PdfPCell();
					blankCell.setBorder(Rectangle.BOTTOM);
					blankCell.setBorderColor(skyBlueColor);
					blankCell.setBorderWidth(2);
					blankCell.setColspan(6);
					blankCell.addElement(new Phrase(""));
					blankCell.setMinimumHeight(10);
					dataTable.addCell(blankCell);

					blankCell = new PdfPCell();
					blankCell.setColspan(6);
					blankCell.setBorder(Rectangle.NO_BORDER);
					blankCell.addElement(new Phrase(""));
					blankCell.setMinimumHeight(10);
					dataTable.addCell(blankCell);

					blankCell = new PdfPCell();
					blankCell.setColspan(6);
					blankCell.setBorder(Rectangle.NO_BORDER);
					blankCell.addElement(new Phrase(""));
					blankCell.setMinimumHeight(10);
					dataTable.addCell(blankCell);

					blankCell = new PdfPCell();
					blankCell.setColspan(6);
					blankCell.setBorder(Rectangle.NO_BORDER);
					blankCell.addElement(new Phrase(""));
					blankCell.setMinimumHeight(10);
					dataTable.addCell(blankCell);
				}

				if (j > 0) {
					contentParagraph = new Paragraph("ADDITIONAL MATCHES - YOUR ENQUIRY ON " + fullName2
							+ " RETURNED MULTIPLE FILES. SEE INFORMATION RELATED TO ADDITIONAL SUBJECT " + fullName2
							+ " BELOW.", addRecordsFont);
					cell1 = new PdfPCell();
					cell1.setColspan(6);
					cell1.setBorder(Rectangle.NO_BORDER);
					cell1.addElement(contentParagraph);
					dataTable.addCell(cell1);

					blankCell = new PdfPCell();
					blankCell.setBorder(Rectangle.BOTTOM);
					blankCell.setColspan(6);
					blankCell.addElement(new Phrase(""));
					blankCell.setMinimumHeight(10);
					dataTable.addCell(blankCell);
				}

				if (cibilResponse.getNameSegment() != null) {
					title = new Paragraph("CONSUMER INFORMATION:", segNamefont);
					title.setAlignment(Element.ALIGN_LEFT);
					table_Heading_cell = new PdfPCell();
					table_Heading_cell.addElement(title);
					table_Heading_cell.setMinimumHeight(20);
					table_Heading_cell.setUseAscender(true);
					table_Heading_cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
					table_Heading_cell.setBorder(Rectangle.NO_BORDER);
					table_Heading_cell.setColspan(6);
					dataTable.addCell(table_Heading_cell);

					contentParagraph = new Paragraph("NAME : " + fullName, contentFont);
					cell1 = new PdfPCell();
					cell1.setBorder(Rectangle.NO_BORDER);
					cell1.setColspan(6);
					cell1.addElement(contentParagraph);
					dataTable.addCell(cell1);

					String dob = cibilResponse.getNameSegment().getDob() != null
							? getFormattedDateOrTime(cibilResponse.getNameSegment().getDob(), "ddMMyyyy")
							: null;

					contentParagraph = new Paragraph("DATE OF BIRTH : " + dob, contentFont);
					cell1 = new PdfPCell();
					cell1.setBorder(Rectangle.NO_BORDER);
					cell1.setColspan(2);
					cell1.addElement(contentParagraph);
					dataTable.addCell(cell1);

					contentParagraph = new Paragraph("");
					cell3 = new PdfPCell();
					cell3.setBorder(Rectangle.NO_BORDER);
					cell1.setColspan(2);
					cell3.addElement(contentParagraph);
					dataTable.addCell(cell3);

					String gender = cibilResponse.getNameSegment().getGender() != null
							? cibilResponse.getNameSegment().getGender()
							: "";

					if (gender.equals("1")) {
						gender = "FEMALE";
					} else if (gender.equals("2")) {
						gender = "MALE";
					}

					contentParagraph = new Paragraph("GENDER : " + gender, contentFont);
					cell1 = new PdfPCell();
					cell1.setBorder(Rectangle.NO_BORDER);
					cell1.setColspan(2);
					cell1.addElement(contentParagraph);
					dataTable.addCell(cell1);

					blankCell = new PdfPCell();
					blankCell.setBorder(Rectangle.NO_BORDER);
					blankCell.setColspan(6);
					blankCell.addElement(new Phrase(""));
					blankCell.setMinimumHeight(10);
					dataTable.addCell(blankCell);

					blankCell = new PdfPCell();
					blankCell.setBorder(Rectangle.BOTTOM);
					blankCell.setBorderColor(grayColor);
					blankCell.setBorderWidth(1);
					blankCell.setColspan(6);
					blankCell.addElement(new Phrase(""));
					blankCell.setMinimumHeight(10);
					dataTable.addCell(blankCell);

				}
				if (cibilResponse.getScoreSegment() != null) {

					title = new Paragraph("CIBIL TRANSUNION SCORE(S):", segNamefont);
					title.setAlignment(Element.ALIGN_LEFT);
					table_Heading_cell = new PdfPCell();
					table_Heading_cell.addElement(title);
					table_Heading_cell.setMinimumHeight(20);
					table_Heading_cell.setUseAscender(true);
					table_Heading_cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
					table_Heading_cell.setBorder(Rectangle.NO_BORDER);
					table_Heading_cell.setColspan(6);
					dataTable.addCell(table_Heading_cell);

					contentParagraph = new Paragraph("SCORE NAME", segHeadersFont);
					cell1 = new PdfPCell();
					cell1.setBorder(Rectangle.NO_BORDER);
					cell1.setColspan(3);
					cell1.addElement(contentParagraph);
					dataTable.addCell(cell1);

					contentParagraph = new Paragraph("SCORE", segHeadersFont);
					cell3 = new PdfPCell();
					cell3.setBorder(Rectangle.NO_BORDER);
					cell3.setUseAscender(true);
					cell3.setVerticalAlignment(Element.ALIGN_MIDDLE);
					cell3.setColspan(2);
					cell3.addElement(contentParagraph);
					dataTable.addCell(cell3);

					contentParagraph = new Paragraph("", contentFont);
					cell4 = new PdfPCell();
					cell4.setBorder(Rectangle.NO_BORDER);
					cell4.addElement(contentParagraph);
					dataTable.addCell(cell4);

					blankCell = new PdfPCell();
					blankCell.setBorder(Rectangle.NO_BORDER);
					blankCell.setColspan(6);
					blankCell.addElement(new Phrase(""));
					blankCell.setMinimumHeight(10);
					dataTable.addCell(blankCell);

					String scoreCardName = null;

					for (int i = 0; i < cibilResponse.getScoreSegment().getScoreBeanList().size(); i++) {
						scoreCardName = cibilResponse.getScoreSegment().getScoreBeanList().get(i).getScoreCardName();

						if (scoreCardName.equals("01")) {
							scoreCardName = "CIBIL TRANSUNION SCORE VERSION 1.0";
						} else if (scoreCardName.equals("02")) {
							scoreCardName = "Personal Loan Score";
						} else if (scoreCardName.equals("04")) {
							scoreCardName = "CIBIL TRANSUNION SCORE VERSION 2.0";
						} else {
							scoreCardName = "CIBIL TRANSUNION SCORE VERSION 3.0";
						}

						String score = cibilResponse.getScoreSegment().getScoreBeanList().get(i).getScore();

						Integer score2 = -1;
						if (!score.equals("000-1")) {
							score2 = Integer.parseInt(score);
						}

						contentParagraph = new Paragraph(scoreCardName, scoreNameFont);
						cell1 = new PdfPCell();
						cell1.setBorder(Rectangle.NO_BORDER);
						cell1.setColspan(3);
						cell1.addElement(contentParagraph);
						cell1.setBackgroundColor(myColor);
						dataTable.addCell(cell1);

						contentParagraph = new Paragraph(score2.toString(), scoreFont);
						cell3 = new PdfPCell();
						cell3.setColspan(2);
						cell3.setUseAscender(true);
						cell3.setBackgroundColor(myColor);
						cell3.setVerticalAlignment(Element.ALIGN_CENTER);
						cell3.setBorder(Rectangle.NO_BORDER);
						cell3.addElement(contentParagraph);
						dataTable.addCell(cell3);

						contentParagraph = new Paragraph("", scoreNameFont);
						cell5 = new PdfPCell();
						cell5.setBackgroundColor(myColor);
						cell5.setBorder(Rectangle.NO_BORDER);
						cell5.addElement(contentParagraph);
						dataTable.addCell(cell5);

					}

					blankCell = new PdfPCell();
					blankCell.setBorder(Rectangle.BOTTOM);
					blankCell.setBorderColor(grayColor);
					blankCell.setBorderWidth(1);
					blankCell.setColspan(6);
					blankCell.addElement(new Phrase(""));
					blankCell.setMinimumHeight(1);
					dataTable.addCell(blankCell);

					blankCell = new PdfPCell();
					blankCell.setBorder(Rectangle.NO_BORDER);
					blankCell.setColspan(6);
					blankCell.addElement(new Phrase(""));
					blankCell.setMinimumHeight(10);
					dataTable.addCell(blankCell);

					title = new Paragraph("POSSIBLE RANGE FOR " + scoreCardName, scoreNameFont);
					title.setAlignment(Element.ALIGN_LEFT);
					table_Heading_cell = new PdfPCell();
					table_Heading_cell.addElement(title);
					table_Heading_cell.setBackgroundColor(myColor);
					table_Heading_cell.setMinimumHeight(15);
					table_Heading_cell.setUseAscender(true);
					table_Heading_cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
					table_Heading_cell.setBorder(Rectangle.NO_BORDER);
					table_Heading_cell.setColspan(6);
					dataTable.addCell(table_Heading_cell);

					contentParagraph = new Paragraph("Consumers with more than 6 months credit history*", contentFont);
					cell1 = new PdfPCell();
					cell1.setBorder(Rectangle.NO_BORDER);
					cell1.setColspan(3);
					cell1.setBackgroundColor(myColor);
					cell1.addElement(contentParagraph);
					dataTable.addCell(cell1);

					contentParagraph = new Paragraph("", contentFont);
					cell1 = new PdfPCell();
					cell1.setBackgroundColor(myColor);
					cell1.setBorder(Rectangle.NO_BORDER);
					cell1.addElement(contentParagraph);
					dataTable.addCell(cell1);

					contentParagraph = new Paragraph(": 300 (high risk) to 900 (low risk)", contentFont);
					cell1 = new PdfPCell();
					cell1.setBorder(Rectangle.NO_BORDER);
					cell1.setBackgroundColor(myColor);
					cell1.setColspan(2);
					cell1.addElement(contentParagraph);
					dataTable.addCell(cell1);

					contentParagraph = new Paragraph("Consumers having less than 6 months credit history*",
							contentFont);
					cell1 = new PdfPCell();
					cell1.setBorder(Rectangle.NO_BORDER);
					cell1.setColspan(3);
					cell1.setBackgroundColor(myColor);
					cell1.addElement(contentParagraph);
					dataTable.addCell(cell1);

					contentParagraph = new Paragraph("", contentFont);
					cell1 = new PdfPCell();
					cell1.setBackgroundColor(myColor);
					cell1.setBorder(Rectangle.NO_BORDER);
					cell1.addElement(contentParagraph);
					dataTable.addCell(cell1);

					contentParagraph = new Paragraph(": 1 (high risk) to 5 (low risk)", contentFont);
					cell1 = new PdfPCell();
					cell1.setBorder(Rectangle.NO_BORDER);
					cell1.setBackgroundColor(myColor);
					cell1.setColspan(2);
					cell1.addElement(contentParagraph);
					dataTable.addCell(cell1);

					contentParagraph = new Paragraph(
							"Consumers not in CIBIL database or with insufficient information for scoring*",
							contentFont);
					cell1 = new PdfPCell();
					cell1.setBorder(Rectangle.NO_BORDER);
					cell1.setColspan(3);
					cell1.setBackgroundColor(myColor);
					cell1.addElement(contentParagraph);
					dataTable.addCell(cell1);

					contentParagraph = new Paragraph("", contentFont);
					cell1 = new PdfPCell();
					cell1.setBackgroundColor(myColor);
					cell1.setBorder(Rectangle.NO_BORDER);
					cell1.addElement(contentParagraph);
					dataTable.addCell(cell1);

					contentParagraph = new Paragraph(": -1", contentFont);
					cell1 = new PdfPCell();
					cell1.setBorder(Rectangle.NO_BORDER);
					cell1.setBackgroundColor(myColor);
					cell1.setColspan(2);
					cell1.addElement(contentParagraph);
					dataTable.addCell(cell1);

					blankCell = new PdfPCell();
					blankCell.setBorder(Rectangle.NO_BORDER);
					blankCell.setColspan(6);
					blankCell.setBackgroundColor(myColor);
					blankCell.addElement(new Phrase(""));
					blankCell.setMinimumHeight(10);
					dataTable.addCell(blankCell);

					contentParagraph = new Paragraph(
							"* At least one tradeline with information updated in last 24 months is required.In case of error in scoring a value of '0' is returned.",
							contentFont);
					cell1 = new PdfPCell();
					cell1.setBorder(Rectangle.NO_BORDER);
					cell1.setColspan(6);
					cell1.setBackgroundColor(myColor);
					cell1.addElement(contentParagraph);
					dataTable.addCell(cell1);

					blankCell = new PdfPCell();
					blankCell.setBorder(Rectangle.NO_BORDER);
					blankCell.setColspan(6);
					blankCell.addElement(new Phrase(""));
					blankCell.setMinimumHeight(10);
					dataTable.addCell(blankCell);

					blankCell = new PdfPCell();
					blankCell.setBorder(Rectangle.BOTTOM);
					blankCell.setBorderColor(grayColor);
					blankCell.setBorderWidth(1);
					blankCell.setColspan(6);
					blankCell.addElement(new Phrase(""));
					blankCell.setMinimumHeight(10);
					dataTable.addCell(blankCell);
				}

				if (cibilResponse.getIdentificationSegment() != null) {

					title = new Paragraph("IDENTIFICATION(S):", segNamefont);
					title.setAlignment(Element.ALIGN_LEFT);
					table_Heading_cell = new PdfPCell();
					table_Heading_cell.addElement(title);
					table_Heading_cell.setMinimumHeight(20);
					table_Heading_cell.setUseAscender(true);
					table_Heading_cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
					table_Heading_cell.setBorder(Rectangle.NO_BORDER);
					table_Heading_cell.setColspan(6);
					dataTable.addCell(table_Heading_cell);

					contentParagraph = new Paragraph("IDENTIFICATION TYPE", segHeadersFont);
					cell1 = new PdfPCell();
					cell1.setBorder(Rectangle.NO_BORDER);
					cell1.setColspan(2);
					cell1.addElement(contentParagraph);
					dataTable.addCell(cell1);

					contentParagraph = new Paragraph("IDENTIFICATION NUMBER", segHeadersFont);
					cell1 = new PdfPCell();
					cell1.setBorder(Rectangle.NO_BORDER);
					cell1.setColspan(2);
					cell1.addElement(contentParagraph);
					dataTable.addCell(cell1);

					contentParagraph = new Paragraph("ISSUE DATE", segHeadersFont);
					cell1 = new PdfPCell();
					cell1.setBorder(Rectangle.NO_BORDER);

					cell1.addElement(contentParagraph);
					dataTable.addCell(cell1);

					contentParagraph = new Paragraph("EXPIRATION DATE", segHeadersFont);
					cell1 = new PdfPCell();
					cell1.setBorder(Rectangle.NO_BORDER);
					cell1.addElement(contentParagraph);
					dataTable.addCell(cell1);

					for (IDBean idBean : cibilResponse.getIdentificationSegment().getIdBeanList()) {
						String idType = "";
						switch (idBean.getIdType()) {
						case "01":
							idType = "INCOME TAX ID NUMBER (PAN)";
							break;

						case "02":
							idType = "PASSPORT NUMBER";
							break;

						case "03":
							idType = "VOTER ID NUMBER";
							break;

						case "04":
							idType = "DRIVER'S LICENSE NUMBER";
							break;

						case "05":
							idType = "RATION CARD NUMBER";
							break;

						case "06":
							idType = "UNIVERSAL ID NUMBER (UID)";
							break;
						}

						String idNumber = idBean.getIdNumber() != null ? idBean.getIdNumber() : "";
						String issueDate = idBean.getIssueDate() != null
								? getFormattedDateOrTime(idBean.getIssueDate(), "ddMMyyyy")
								: "";

						String expireDate = idBean.getExpirationDate() != null
								? getFormattedDateOrTime(idBean.getExpirationDate(), "ddMMyyyy")
								: "";

						contentParagraph = new Paragraph(idType, contentFont);
						cell1 = new PdfPCell();
						cell1.setBorder(Rectangle.NO_BORDER);
						cell1.setColspan(2);
						cell1.addElement(contentParagraph);
						dataTable.addCell(cell1);

						contentParagraph = new Paragraph(idNumber, contentFont);
						cell1 = new PdfPCell();
						cell1.setBorder(Rectangle.NO_BORDER);
						cell1.setColspan(2);
						cell1.addElement(contentParagraph);
						dataTable.addCell(cell1);

						contentParagraph = new Paragraph(issueDate, contentFont);
						cell1 = new PdfPCell();
						cell1.setBorder(Rectangle.NO_BORDER);

						cell1.addElement(contentParagraph);
						dataTable.addCell(cell1);

						contentParagraph = new Paragraph(expireDate, contentFont);
						cell1 = new PdfPCell();
						cell1.setBorder(Rectangle.NO_BORDER);
						cell1.addElement(contentParagraph);
						dataTable.addCell(cell1);
					}

					blankCell = new PdfPCell();
					blankCell.setBorder(Rectangle.NO_BORDER);
					blankCell.setColspan(6);
					blankCell.addElement(new Phrase(""));
					blankCell.setMinimumHeight(10);
					dataTable.addCell(blankCell);

					blankCell = new PdfPCell();
					blankCell.setBorder(Rectangle.BOTTOM);
					blankCell.setBorderColor(grayColor);
					blankCell.setBorderWidth(1);
					blankCell.setColspan(6);
					blankCell.addElement(new Phrase(""));
					blankCell.setMinimumHeight(10);
					dataTable.addCell(blankCell);
				}

				if (cibilResponse.getTelephoneSegment() != null) {
					title = new Paragraph("TELEPHONE(S):", segNamefont);
					title.setAlignment(Element.ALIGN_LEFT);
					table_Heading_cell = new PdfPCell();
					table_Heading_cell.addElement(title);
					table_Heading_cell.setMinimumHeight(20);
					table_Heading_cell.setUseAscender(true);
					table_Heading_cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
					table_Heading_cell.setBorder(Rectangle.NO_BORDER);
					table_Heading_cell.setColspan(6);
					dataTable.addCell(table_Heading_cell);

					contentParagraph = new Paragraph("TELEPHONE TYPE", segHeadersFont);
					cell1 = new PdfPCell();
					cell1.setBorder(Rectangle.NO_BORDER);
					cell1.setColspan(2);
					cell1.addElement(contentParagraph);
					dataTable.addCell(cell1);

					contentParagraph = new Paragraph("TELEPHONE NUMBER", segHeadersFont);
					cell1 = new PdfPCell();
					cell1.setBorder(Rectangle.NO_BORDER);
					cell1.setColspan(2);
					cell1.addElement(contentParagraph);
					dataTable.addCell(cell1);

					contentParagraph = new Paragraph("TELEPHONE EXTENSION", segHeadersFont);
					cell1 = new PdfPCell();
					cell1.setBorder(Rectangle.NO_BORDER);
					cell1.setColspan(2);
					cell1.addElement(contentParagraph);
					dataTable.addCell(cell1);

					for (TelephoneBean telBean : cibilResponse.getTelephoneSegment().getTelBeanList()) {
						String telType = "";
						switch (telBean.getTelephoneType()) {
						case "00":
							telType = "NOT CLASSIFIED";
							break;

						case "01":
							telType = "MOBILE PHONE";
							break;

						case "02":
							telType = "HOME PHONE";
							break;

						case "03":
							telType = "OFFICE PHONE";
							break;
						}

						String telNumber = telBean.getTelephoneNumber() != null ? telBean.getTelephoneNumber() : "";
						String telExt = telBean.getTelephoneExt() != null ? telBean.getTelephoneExt() : "";

						contentParagraph = new Paragraph(telType, contentFont);
						cell1 = new PdfPCell();
						cell1.setBorder(Rectangle.NO_BORDER);
						cell1.setColspan(2);
						cell1.addElement(contentParagraph);
						dataTable.addCell(cell1);

						contentParagraph = new Paragraph(telNumber, contentFont);
						cell1 = new PdfPCell();
						cell1.setBorder(Rectangle.NO_BORDER);
						cell1.setColspan(2);
						cell1.addElement(contentParagraph);
						dataTable.addCell(cell1);

						contentParagraph = new Paragraph(telExt, contentFont);
						cell1 = new PdfPCell();
						cell1.setBorder(Rectangle.NO_BORDER);
						cell1.setColspan(2);
						cell1.addElement(contentParagraph);
						dataTable.addCell(cell1);

					}

					blankCell = new PdfPCell();
					blankCell.setBorder(Rectangle.NO_BORDER);
					blankCell.setColspan(6);
					blankCell.addElement(new Phrase(""));
					blankCell.setMinimumHeight(10);
					dataTable.addCell(blankCell);

					blankCell = new PdfPCell();
					blankCell.setBorder(Rectangle.BOTTOM);
					blankCell.setBorderColor(grayColor);
					blankCell.setBorderWidth(1);
					blankCell.setColspan(6);
					blankCell.addElement(new Phrase(""));
					blankCell.setMinimumHeight(10);
					dataTable.addCell(blankCell);
				}

				if (cibilResponse.getEmailContactSegment() != null) {
					title = new Paragraph("EMAIL CONTACT(S):", segNamefont);
					title.setAlignment(Element.ALIGN_LEFT);
					table_Heading_cell = new PdfPCell();
					table_Heading_cell.addElement(title);
					table_Heading_cell.setMinimumHeight(20);
					table_Heading_cell.setUseAscender(true);
					table_Heading_cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
					table_Heading_cell.setBorder(Rectangle.NO_BORDER);
					table_Heading_cell.setColspan(6);
					dataTable.addCell(table_Heading_cell);

					contentParagraph = new Paragraph("EMAIL ADDRESS", segHeadersFont);
					cell1 = new PdfPCell();
					cell1.setBorder(Rectangle.NO_BORDER);
					cell1.setColspan(6);
					cell1.addElement(contentParagraph);
					dataTable.addCell(cell1);

					for (String emailAddress : cibilResponse.getEmailContactSegment().getEmailList()) {

						contentParagraph = new Paragraph(emailAddress, contentFont);
						cell1 = new PdfPCell();
						cell1.setBorder(Rectangle.NO_BORDER);
						cell1.setColspan(6);
						cell1.addElement(contentParagraph);
						dataTable.addCell(cell1);
					}

					blankCell = new PdfPCell();
					blankCell.setBorder(Rectangle.NO_BORDER);
					blankCell.setColspan(6);
					blankCell.addElement(new Phrase(""));
					blankCell.setMinimumHeight(10);
					dataTable.addCell(blankCell);

					blankCell = new PdfPCell();
					blankCell.setBorder(Rectangle.BOTTOM);
					blankCell.setBorderColor(grayColor);
					blankCell.setBorderWidth(1);
					blankCell.setColspan(6);
					blankCell.addElement(new Phrase(""));
					blankCell.setMinimumHeight(10);
					dataTable.addCell(blankCell);
				}

				if (cibilResponse.getAddressSegment() != null) {
					title = new Paragraph("ADDRESS(ES):", segNamefont);
					title.setAlignment(Element.ALIGN_LEFT);
					table_Heading_cell = new PdfPCell();
					table_Heading_cell.addElement(title);
					table_Heading_cell.setMinimumHeight(20);
					table_Heading_cell.setUseAscender(true);
					table_Heading_cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
					table_Heading_cell.setBorder(Rectangle.NO_BORDER);
					table_Heading_cell.setColspan(6);
					dataTable.addCell(table_Heading_cell);

					for (AddressBean addressBean : cibilResponse.getAddressSegment().getAddBeanList()) {
						String addLine1 = addressBean.getAddLine1() != null ? addressBean.getAddLine1() : "";
						String addLine2 = addressBean.getAddLine2() != null ? addressBean.getAddLine2() : "";
						String addLine3 = addressBean.getAddLine3() != null ? addressBean.getAddLine3() : "";
						String addLine4 = addressBean.getAddLine4() != null ? addressBean.getAddLine4() : "";
						String addLine5 = addressBean.getAddLine5() != null ? addressBean.getAddLine5() : "";
						String pinCode = addressBean.getPinCode() != null ? addressBean.getPinCode() : "";

						String fullAddress = addLine1 + " " + addLine2 + " " + addLine3 + " " + addLine4 + " "
								+ addLine5 + " " + pinCode;

						contentParagraph = new Paragraph("ADDRESS :" + fullAddress, contentFont);
						cell1 = new PdfPCell();
						cell1.setBorder(Rectangle.NO_BORDER);
						cell1.setColspan(6);
						cell1.addElement(contentParagraph);
						dataTable.addCell(cell1);

						String addCategory = "";
						if (addressBean.getAddCategory() != null) {
							switch (addressBean.getAddCategory()) {
							case "01":
								addCategory = "PERMANENT ADDRESS";
								break;

							case "02":
								addCategory = "RESIDENCE ADDRESS";
								break;

							case "03":
								addCategory = "OFFICE ADDRESS";
								break;

							case "04":
								addCategory = "NOT CATEGORIZED";
								break;
							}
						}

						String resiCode = "";
						if (addressBean.getResidenceCode() != null) {
							switch (addressBean.getResidenceCode()) {
							case "01":
								resiCode = "OWNED";
								break;

							case "02":
								resiCode = "RENTED";
								break;

							}
						}

						String dateReported = addressBean.getDateReported() != null
								? getFormattedDateOrTime(addressBean.getDateReported(), "ddMMyyyy")
								: "";

						contentParagraph = new Paragraph("CATEGORY:" + addCategory, contentFont);
						cell1 = new PdfPCell();
						cell1.setBorder(Rectangle.NO_BORDER);
						cell1.setColspan(2);
						cell1.addElement(contentParagraph);
						dataTable.addCell(cell1);

						contentParagraph = new Paragraph("RESIDENCE CODE:" + resiCode, contentFont);
						cell1 = new PdfPCell();
						cell1.setBorder(Rectangle.NO_BORDER);
						cell1.setColspan(2);
						cell1.addElement(contentParagraph);
						dataTable.addCell(cell1);

						contentParagraph = new Paragraph("DATE REPORTED:" + dateReported, contentFont);
						cell1 = new PdfPCell();
						cell1.setBorder(Rectangle.NO_BORDER);
						cell1.setColspan(2);
						cell1.addElement(contentParagraph);
						dataTable.addCell(cell1);

						blankCell = new PdfPCell();
						blankCell.setBorder(Rectangle.NO_BORDER);
						blankCell.setColspan(6);
						blankCell.addElement(new Phrase(""));
						blankCell.setMinimumHeight(10);
						dataTable.addCell(blankCell);
					}

					blankCell = new PdfPCell();
					blankCell.setBorder(Rectangle.NO_BORDER);
					blankCell.setColspan(6);
					blankCell.addElement(new Phrase(""));
					blankCell.setMinimumHeight(10);
					dataTable.addCell(blankCell);

					blankCell = new PdfPCell();
					blankCell.setBorder(Rectangle.BOTTOM);
					blankCell.setBorderColor(grayColor);
					blankCell.setBorderWidth(1);
					blankCell.setColspan(6);
					blankCell.addElement(new Phrase(""));
					blankCell.setMinimumHeight(10);
					dataTable.addCell(blankCell);
				}

				if (cibilResponse.getEmploymentSegment() != null) {
					title = new Paragraph("EMPLOYMENT INFORMATION:", segNamefont);
					title.setAlignment(Element.ALIGN_LEFT);
					table_Heading_cell = new PdfPCell();
					table_Heading_cell.addElement(title);
					table_Heading_cell.setMinimumHeight(20);
					table_Heading_cell.setUseAscender(true);
					table_Heading_cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
					table_Heading_cell.setBorder(Rectangle.NO_BORDER);
					table_Heading_cell.setColspan(6);
					dataTable.addCell(table_Heading_cell);

					contentParagraph = new Paragraph("ACCOUNT TYPE", segHeadersFont);
					cell1 = new PdfPCell();
					cell1.setBorder(Rectangle.NO_BORDER);
					cell1.addElement(contentParagraph);
					dataTable.addCell(cell1);

					contentParagraph = new Paragraph("DATE REPORTED", segHeadersFont);
					cell1 = new PdfPCell();
					cell1.setBorder(Rectangle.NO_BORDER);
					cell1.addElement(contentParagraph);
					dataTable.addCell(cell1);

					contentParagraph = new Paragraph("OCCUPATION CODE", segHeadersFont);
					cell1 = new PdfPCell();
					cell1.setBorder(Rectangle.NO_BORDER);
					cell1.addElement(contentParagraph);
					dataTable.addCell(cell1);

					contentParagraph = new Paragraph("INCOME", segHeadersFont);
					cell1 = new PdfPCell();
					cell1.setBorder(Rectangle.NO_BORDER);
					cell1.addElement(contentParagraph);
					dataTable.addCell(cell1);

					contentParagraph = new Paragraph("NET / GROSS INCOME INDICATOR", segHeadersFont);
					cell1 = new PdfPCell();
					cell1.setBorder(Rectangle.NO_BORDER);
					cell1.addElement(contentParagraph);
					dataTable.addCell(cell1);

					contentParagraph = new Paragraph("MONTHLY / ANNUAL INCOME INDICATOR", segHeadersFont);
					cell1 = new PdfPCell();
					cell1.setBorder(Rectangle.NO_BORDER);
					cell1.addElement(contentParagraph);
					dataTable.addCell(cell1);

					String accType = cibilResponse.getEmploymentSegment().getAccountType() != null
							? accTypeAndEnqPurposeMap.get(cibilResponse.getEmploymentSegment().getAccountType())
							: "";
					contentParagraph = new Paragraph(accType, contentFont);
					cell1 = new PdfPCell();
					cell1.setBorder(Rectangle.NO_BORDER);
					cell1.addElement(contentParagraph);
					dataTable.addCell(cell1);

					String dateReported = cibilResponse.getEmploymentSegment().getDateReportedAndCertified() != null
							? getFormattedDateOrTime(cibilResponse.getEmploymentSegment().getDateReportedAndCertified(),
									"ddMMyyyy")
							: "";
					contentParagraph = new Paragraph(dateReported, contentFont);
					cell1 = new PdfPCell();
					cell1.setBorder(Rectangle.NO_BORDER);
					cell1.addElement(contentParagraph);
					dataTable.addCell(cell1);

					String occCode = cibilResponse.getEmploymentSegment().getOccupationCode();
					if (occCode != null) {
						switch (occCode) {
						case "01":
							occCode = "SALARIED";
							break;

						case "02":
							occCode = "SELF EMPLOYED PROFESSIONAL";
							break;

						case "03":
							occCode = "SELF EMPLOYED";
							break;

						case "04":
							occCode = "OTHERS";
							break;
						}
					}

					contentParagraph = new Paragraph(occCode, contentFont);
					cell1 = new PdfPCell();
					cell1.setBorder(Rectangle.NO_BORDER);
					cell1.addElement(contentParagraph);
					dataTable.addCell(cell1);

					String income = cibilResponse.getEmploymentSegment().getIncome() != null
							? cibilResponse.getEmploymentSegment().getIncome()
							: "";

					contentParagraph = new Paragraph(income, contentFont);
					cell1 = new PdfPCell();
					cell1.setBorder(Rectangle.NO_BORDER);
					cell1.addElement(contentParagraph);
					dataTable.addCell(cell1);

					String netOrGrossIncomeIndicator = cibilResponse.getEmploymentSegment()
							.getNetOrGrossIncomeIndicator();
					if (netOrGrossIncomeIndicator != null) {
						switch (netOrGrossIncomeIndicator) {
						case "G":
							netOrGrossIncomeIndicator = "GROSS INCOME";
							break;

						case "N":
							netOrGrossIncomeIndicator = "NET INCOME";
							break;
						}
					}

					contentParagraph = new Paragraph(netOrGrossIncomeIndicator, contentFont);
					cell1 = new PdfPCell();
					cell1.setBorder(Rectangle.NO_BORDER);
					cell1.addElement(contentParagraph);
					dataTable.addCell(cell1);

					String monthlyOrAnnualIncomeIndicator = cibilResponse.getEmploymentSegment()
							.getMonthlyOrAnnualIncomeIndicator();
					if (monthlyOrAnnualIncomeIndicator != null) {
						switch (monthlyOrAnnualIncomeIndicator) {
						case "M":
							monthlyOrAnnualIncomeIndicator = "MONTHLY";
							break;

						case "A":
							monthlyOrAnnualIncomeIndicator = "ANNUAL";
							break;
						}
					}

					contentParagraph = new Paragraph(monthlyOrAnnualIncomeIndicator, contentFont);
					cell1 = new PdfPCell();
					cell1.setBorder(Rectangle.NO_BORDER);
					cell1.addElement(contentParagraph);
					dataTable.addCell(cell1);

					blankCell = new PdfPCell();
					blankCell.setBorder(Rectangle.NO_BORDER);
					blankCell.setColspan(6);
					blankCell.addElement(new Phrase(""));
					blankCell.setMinimumHeight(10);
					dataTable.addCell(blankCell);

					blankCell = new PdfPCell();
					blankCell.setBorder(Rectangle.BOTTOM);
					blankCell.setBorderColor(grayColor);
					blankCell.setBorderWidth(1);
					blankCell.setColspan(6);
					blankCell.addElement(new Phrase(""));
					blankCell.setMinimumHeight(10);
					dataTable.addCell(blankCell);
				}

				int highCreditSum = 0;
				int currentBalanceSum = 0;
				int overdueSum = 0;
				int overdueCount = 0;
				int zeroBalanceCount = 0;
				int past30daysEnqCount = 0;
				int past12MonthsEnqCount = 0;
				int past24MonthsEnqCount = 0;
				String mostRecentEnq = "";
				List<Date> openedDateList = new ArrayList<Date>();

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

				if (cibilResponse.getAccountSegment() != null) {

					title = new Paragraph("ACCOUNT(S)", segNamefont);
					title.setAlignment(Element.ALIGN_LEFT);
					table_Heading_cell = new PdfPCell();
					table_Heading_cell.addElement(title);
					table_Heading_cell.setMinimumHeight(20);
					table_Heading_cell.setUseAscender(true);
					table_Heading_cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
					table_Heading_cell.setBorder(Rectangle.NO_BORDER);
					table_Heading_cell.setColspan(6);
					dataTable2.addCell(table_Heading_cell);

					for (OwnAccountBean ownAccBean : cibilResponse.getAccountSegment().getOwnAccountBeanList()) {
						contentParagraph = new Paragraph("ACCOUNT", segHeadersFont);
						cell1 = new PdfPCell();
						cell1.setBorder(Rectangle.NO_BORDER);
						cell1.addElement(contentParagraph);
						dataTable2.addCell(cell1);

						contentParagraph = new Paragraph("DATES", segHeadersFont);
						cell1 = new PdfPCell();
						cell1.setBorder(Rectangle.NO_BORDER);
						cell1.addElement(contentParagraph);
						dataTable2.addCell(cell1);

						contentParagraph = new Paragraph("AMOUNTS", segHeadersFont);
						cell1 = new PdfPCell();
						cell1.setBorder(Rectangle.NO_BORDER);
						cell1.addElement(contentParagraph);
						dataTable2.addCell(cell1);

						contentParagraph = new Paragraph("STATUS", segHeadersFont);
						cell1 = new PdfPCell();
						cell1.setBorder(Rectangle.NO_BORDER);
						cell1.addElement(contentParagraph);
						dataTable2.addCell(cell1);

						String memberName = ownAccBean.getReportingMemberShortName() != null
								? ownAccBean.getReportingMemberShortName()
								: "";
						contentParagraph = new Paragraph("MEMBER NAME: " + memberName, contentFont);
						cell1 = new PdfPCell();
						cell1.setBorder(Rectangle.NO_BORDER);
						cell1.addElement(contentParagraph);
						dataTable2.addCell(cell1);

						String openedDate = ownAccBean.getDateOpenedOrDisbursed() != null
								? getFormattedDateOrTime(ownAccBean.getDateOpenedOrDisbursed(), "ddMMyyyy")
								: "";

						if (openedDate != null && !openedDate.equals("")) {
							Date openDate = new SimpleDateFormat("dd-MM-yyyy").parse(openedDate);

							openedDateList.add(openDate);
						}

						contentParagraph = new Paragraph("OPENED: " + openedDate, contentFont);
						cell1 = new PdfPCell();
						cell1.setBorder(Rectangle.NO_BORDER);
						cell1.addElement(contentParagraph);
						dataTable2.addCell(cell1);

						String sanctionedAmt = ownAccBean.getHighCreditOrSanctionedAmount() != null
								? ownAccBean.getHighCreditOrSanctionedAmount()
								: "";

						if (!sanctionedAmt.equals("")) {
							highCreditSum += Integer.parseInt(sanctionedAmt);
						}
						contentParagraph = new Paragraph("SANCTIONED: " + sanctionedAmt, contentFont);
						cell1 = new PdfPCell();
						cell1.setBorder(Rectangle.NO_BORDER);
						cell1.addElement(contentParagraph);
						dataTable2.addCell(cell1);

						String writtenOffAndsettledStatus = ownAccBean.getWrittenOffAndSettledStatus() != null
								? ownAccBean.getWrittenOffAndSettledStatus()
								: "";

						String suitFiledOrWilfulDefault = ownAccBean.getSuitFiledOrWilfulDefault() != null
								? ownAccBean.getSuitFiledOrWilfulDefault()
								: "";

						switch (writtenOffAndsettledStatus) {
						case "00":
							writtenOffAndsettledStatus = "RESTRUCTURED LOAN";
							break;

						case "01":
							writtenOffAndsettledStatus = "RESTRUCTURED LOAN(GOVT. MANDATED)";
							break;

						case "02":
							writtenOffAndsettledStatus = "WRITTEN-OFF";
							break;

						case "03":
							writtenOffAndsettledStatus = "SETTLED";
							break;

						case "04":
							writtenOffAndsettledStatus = "POST(WO) SETTLED";
							break;

						case "05":
							writtenOffAndsettledStatus = "ACCOUNT SOLD";
							break;

						case "06":
							writtenOffAndsettledStatus = "WRITTEN OFF AND ACCOUNT SOLD";
							break;

						case "07":
							writtenOffAndsettledStatus = "ACCOUNT PURCHASED";
							break;

						case "08":
							writtenOffAndsettledStatus = "ACCOUNT PURCHASED AND WRITTEN OFF";
							break;

						case "09":
							writtenOffAndsettledStatus = "ACCOUNT PURCHASED AND SETTLED";
							break;

						case "10":
							writtenOffAndsettledStatus = "ACCOUNT PURCHASED AND RESTRUCTURED";
							break;
						}

						switch (suitFiledOrWilfulDefault) {
						case "00":
							writtenOffAndsettledStatus = "NO SUIT FILED";
							break;

						case "01":
							suitFiledOrWilfulDefault = "SUIT FILED";
							break;

						case "02":
							suitFiledOrWilfulDefault = "WILFUL DEFAULT";
							break;

						case "03":
							suitFiledOrWilfulDefault = "SUIT FILED(WILFUL DEFAULT)";
							break;
						}

						if (!writtenOffAndsettledStatus.equals("")) {
							contentParagraph = new Paragraph("WRITTEN OFF/SETTLED STATUS:", contentFont);
						} else if (!suitFiledOrWilfulDefault.equals("")) {
							contentParagraph = new Paragraph("SUIT FILED/WILFUL DEFAULT:", contentFont);
						} else {
							contentParagraph = new Paragraph("", contentFont);
						}

						cell1 = new PdfPCell();
						cell1.setBorder(Rectangle.NO_BORDER);
						cell1.addElement(contentParagraph);
						dataTable2.addCell(cell1);

						String accNumber = ownAccBean.getAccountNumber() != null ? ownAccBean.getAccountNumber() : "";
						contentParagraph = new Paragraph("ACCOUNT NUMBER: " + accNumber, contentFont);
						cell1 = new PdfPCell();
						cell1.setBorder(Rectangle.NO_BORDER);
						cell1.addElement(contentParagraph);
						dataTable2.addCell(cell1);

						String lastPaymentDate = ownAccBean.getDateOfLastPayment() != null
								? getFormattedDateOrTime(ownAccBean.getDateOfLastPayment(), "ddMMyyyy")
								: "";
						contentParagraph = new Paragraph("LAST PAYMENT: " + lastPaymentDate, contentFont);
						cell1 = new PdfPCell();
						cell1.setBorder(Rectangle.NO_BORDER);
						cell1.addElement(contentParagraph);
						dataTable2.addCell(cell1);

						contentParagraph = new Paragraph("", contentFont);
						cell1 = new PdfPCell();
						cell1.setBorder(Rectangle.NO_BORDER);
						cell1.addElement(contentParagraph);
						dataTable2.addCell(cell1);

						if (!writtenOffAndsettledStatus.equals("")) {
							contentParagraph = new Paragraph(writtenOffAndsettledStatus, contentFont);
						} else if (!suitFiledOrWilfulDefault.equals("")) {
							contentParagraph = new Paragraph(suitFiledOrWilfulDefault, contentFont);
						} else {
							contentParagraph = new Paragraph("", contentFont);
						}

						cell1 = new PdfPCell();
						cell1.setBorder(Rectangle.NO_BORDER);
						cell1.addElement(contentParagraph);
						dataTable2.addCell(cell1);

						contentParagraph = new Paragraph("", contentFont);
						cell1 = new PdfPCell();
						cell1.setBorder(Rectangle.NO_BORDER);
						cell1.addElement(contentParagraph);
						dataTable2.addCell(cell1);

						String closedDate = ownAccBean.getDateClosed() != null
								? getFormattedDateOrTime(ownAccBean.getDateClosed(), "ddMMyyyy")
								: "";
						contentParagraph = new Paragraph("CLOSED: " + closedDate, contentFont);
						cell1 = new PdfPCell();
						cell1.setBorder(Rectangle.NO_BORDER);
						cell1.addElement(contentParagraph);
						dataTable2.addCell(cell1);

						String currBalance = ownAccBean.getCurrentBalance() != null ? ownAccBean.getCurrentBalance()
								: "";
						if (!currBalance.equals("")) {
							currentBalanceSum += Integer.parseInt(currBalance);
							if (Integer.parseInt(currBalance) == 0) {
								zeroBalanceCount++;
							}
						}
						contentParagraph = new Paragraph("CURRENT BALANCE: " + currBalance, contentFont);
						cell1 = new PdfPCell();
						cell1.setBorder(Rectangle.NO_BORDER);
						cell1.addElement(contentParagraph);
						dataTable2.addCell(cell1);

						String writtenOffTotal = ownAccBean.getWrittenOffAmountTotal() != null
								? ownAccBean.getWrittenOffAmountTotal()
								: "";

						if (!writtenOffTotal.equals("")) {
							contentParagraph = new Paragraph("WRITTEN OFF(TOTAL): " + writtenOffTotal, contentFont);
						} else {
							contentParagraph = new Paragraph("", contentFont);
						}

						cell1 = new PdfPCell();
						cell1.setBorder(Rectangle.NO_BORDER);
						cell1.addElement(contentParagraph);
						dataTable2.addCell(cell1);

						String accType = ownAccBean.getAccountType() != null
								? accTypeAndEnqPurposeMap.get(ownAccBean.getAccountType())
								: "";

						contentParagraph = new Paragraph("TYPE: " + accType, contentFont);
						cell1 = new PdfPCell();
						cell1.setBorder(Rectangle.NO_BORDER);
						cell1.addElement(contentParagraph);
						dataTable2.addCell(cell1);

						String dateReportedCertified = ownAccBean.getDateReportedAndCertified() != null
								? getFormattedDateOrTime(ownAccBean.getDateReportedAndCertified(), "ddMMyyyy")
								: "";
						contentParagraph = new Paragraph("REPORTED AND CERTIFIED: " + dateReportedCertified,
								contentFont);
						cell1 = new PdfPCell();
						cell1.setBorder(Rectangle.NO_BORDER);
						cell1.addElement(contentParagraph);
						dataTable2.addCell(cell1);

						String overdue = ownAccBean.getAmountOverdue() != null ? ownAccBean.getAmountOverdue() : "";

						if (!overdue.equals("")) {
							overdueSum += Integer.parseInt(overdue);
							++overdueCount;
						}
						contentParagraph = new Paragraph("OVERDUE: " + overdue, contentFont);
						cell1 = new PdfPCell();
						cell1.setBorder(Rectangle.NO_BORDER);
						cell1.addElement(contentParagraph);
						dataTable2.addCell(cell1);

						String writtenOffPrinc = ownAccBean.getWrittenOffAmountPrincipal() != null
								? ownAccBean.getWrittenOffAmountPrincipal()
								: "";

						if (!writtenOffPrinc.equals("")) {
							contentParagraph = new Paragraph("WRITTEN OFF (PRINCIPAL): " + writtenOffPrinc,
									contentFont);
						} else {
							contentParagraph = new Paragraph("", contentFont);
						}

						cell1 = new PdfPCell();
						cell1.setBorder(Rectangle.NO_BORDER);
						cell1.addElement(contentParagraph);
						dataTable2.addCell(cell1);

						String ownership = ownAccBean.getOwnershipIndicator() != null
								? ownAccBean.getOwnershipIndicator()
								: "";

						switch (ownership) {
						case "1":
							ownership = "INDIVIDUAL";
							break;

						case "2":
							ownership = "AUTHORISED USER (REFERS TO SUPPLEMENTARY CREDIT CARD HOLDERR)";
							break;

						case "3":
							ownership = "GUARANTOR";
							break;

						case "4":
							ownership = "JOINT";
							break;
						}

						contentParagraph = new Paragraph("OWNERSHIP: " + ownership, contentFont);
						cell1 = new PdfPCell();
						cell1.setBorder(Rectangle.NO_BORDER);
						cell1.addElement(contentParagraph);
						dataTable2.addCell(cell1);

						String payHistStart = ownAccBean.getPaymentHistoryStartDate() != null
								? getFormattedDateOrTime(ownAccBean.getPaymentHistoryStartDate(), "ddMMyyyy")
								: "";
						contentParagraph = new Paragraph("PMT HIST START: " + payHistStart, contentFont);
						cell1 = new PdfPCell();
						cell1.setBorder(Rectangle.NO_BORDER);
						cell1.addElement(contentParagraph);
						dataTable2.addCell(cell1);

						String emi = ownAccBean.getEMIAmount() != null ? ownAccBean.getEMIAmount() : "";
						contentParagraph = new Paragraph("EMI: " + emi, contentFont);
						cell1 = new PdfPCell();
						cell1.setBorder(Rectangle.NO_BORDER);
						cell1.addElement(contentParagraph);
						dataTable2.addCell(cell1);

						contentParagraph = new Paragraph("", contentFont);
						cell1 = new PdfPCell();
						cell1.setBorder(Rectangle.NO_BORDER);
						cell1.addElement(contentParagraph);
						dataTable2.addCell(cell1);

						String collatType = ownAccBean.getTypeOfCollateral() != null ? ownAccBean.getTypeOfCollateral()
								: "";

						switch (collatType) {
						case "00":
							collatType = "NO COLLATERAL";
							break;

						case "01":
							collatType = "PROPERTY";
							break;

						case "02":
							collatType = "GOLD";
							break;

						case "03":
							collatType = "SHARES";
							break;

						case "04":
							collatType = "SAVING ACCOUNT AND FIXED DEPOSIT";
							break;
						}

						contentParagraph = new Paragraph("COLLATERAL TYPE: " + collatType, contentFont);
						cell1 = new PdfPCell();
						cell1.setBorder(Rectangle.NO_BORDER);
						cell1.addElement(contentParagraph);
						dataTable2.addCell(cell1);

						String payHistEnd = ownAccBean.getPaymentHistoryEndDate() != null
								? getFormattedDateOrTime(ownAccBean.getPaymentHistoryEndDate(), "ddMMyyyy")
								: "";
						contentParagraph = new Paragraph("PMT HIST END: " + payHistEnd, contentFont);
						cell1 = new PdfPCell();
						cell1.setBorder(Rectangle.NO_BORDER);
						cell1.addElement(contentParagraph);
						dataTable2.addCell(cell1);

						String payFrequency = ownAccBean.getPaymentFrequency() != null
								? ownAccBean.getPaymentFrequency()
								: "";

						switch (payFrequency) {
						case "01":
							payFrequency = "WEEKLY";
							break;

						case "02":
							payFrequency = "FORTNIGHTLY";
							break;

						case "03":
							payFrequency = "MONTHLY";
							break;

						case "04":
							payFrequency = "QUARTERLY";
							break;
						}

						contentParagraph = new Paragraph("PMT FREQ: " + payFrequency, contentFont);
						cell1 = new PdfPCell();
						cell1.setBorder(Rectangle.NO_BORDER);
						cell1.addElement(contentParagraph);
						dataTable2.addCell(cell1);

						contentParagraph = new Paragraph("", contentFont);
						cell1 = new PdfPCell();
						cell1.setBorder(Rectangle.NO_BORDER);
						cell1.addElement(contentParagraph);
						dataTable2.addCell(cell1);

						contentParagraph = new Paragraph("", contentFont);
						cell1 = new PdfPCell();
						cell1.setBorder(Rectangle.NO_BORDER);
						cell1.addElement(contentParagraph);
						dataTable2.addCell(cell1);

						contentParagraph = new Paragraph("", contentFont);
						cell1 = new PdfPCell();
						cell1.setBorder(Rectangle.NO_BORDER);
						cell1.addElement(contentParagraph);
						dataTable2.addCell(cell1);

						String repayTenure = ownAccBean.getRepaymentTenure() != null ? ownAccBean.getRepaymentTenure()
								: "";
						contentParagraph = new Paragraph("REPAYMENT TENURE: " + repayTenure, contentFont);
						cell1 = new PdfPCell();
						cell1.setBorder(Rectangle.NO_BORDER);
						cell1.addElement(contentParagraph);
						dataTable2.addCell(cell1);

						contentParagraph = new Paragraph("", contentFont);
						cell1 = new PdfPCell();
						cell1.setBorder(Rectangle.NO_BORDER);
						cell1.addElement(contentParagraph);
						dataTable2.addCell(cell1);

						blankCell = new PdfPCell();
						blankCell.setBorder(Rectangle.NO_BORDER);
						blankCell.setColspan(4);
						blankCell.addElement(new Phrase(""));
						blankCell.setMinimumHeight(10);
						dataTable2.addCell(blankCell);

						String paymentHistory1 = ownAccBean.getPaymentHistory1() != null
								? ownAccBean.getPaymentHistory1()
								: "";
						String paymentHistory2 = ownAccBean.getPaymentHistory2() != null
								? ownAccBean.getPaymentHistory2()
								: "";
						String paymentHistory = paymentHistory1 + paymentHistory2;

						Map<String, String> historyMapData = getPaymentHistoryMap(paymentHistory, payHistStart,
								payHistEnd);

						if (!historyMapData.isEmpty()) {

							contentParagraph = new Paragraph(
									"DAYS PAST DUE/ASSET CLASSIFICATION (UP TO 36 MONTHS; LEFT TO RIGHT)",
									segHeadersFont);
							cell1 = new PdfPCell();
							cell1.setColspan(4);
							cell1.setBorder(Rectangle.NO_BORDER);
							cell1.addElement(contentParagraph);
							dataTable2.addCell(cell1);

							blankCell = new PdfPCell();
							blankCell.setBorder(Rectangle.NO_BORDER);
							blankCell.setColspan(4);
							blankCell.addElement(new Phrase(""));
							blankCell.setMinimumHeight(10);
							dataTable2.addCell(blankCell);

							buildHistoryPaymentTable(dataTable2, historyMapData);
						}
						blankCell = new PdfPCell();
						blankCell.setBorder(Rectangle.BOTTOM);
						blankCell.setBorderColor(grayColor);
						blankCell.setBorderWidth(1);
						blankCell.setColspan(6);
						blankCell.addElement(new Phrase(""));
						blankCell.setMinimumHeight(10);
						dataTable2.addCell(blankCell);

					}
				}

				if (cibilResponse.getEnquirySegment() != null) {

					title = new Paragraph("ENQUIRIES:", segNamefont);
					title.setAlignment(Element.ALIGN_LEFT);
					table_Heading_cell = new PdfPCell();
					table_Heading_cell.addElement(title);
					table_Heading_cell.setMinimumHeight(20);
					table_Heading_cell.setUseAscender(true);
					table_Heading_cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
					table_Heading_cell.setBorder(Rectangle.NO_BORDER);
					table_Heading_cell.setColspan(6);
					dataTable3.addCell(table_Heading_cell);

					contentParagraph = new Paragraph("MEMBER", segHeadersFont);
					cell1 = new PdfPCell();
					cell1.setBorder(Rectangle.NO_BORDER);
					cell1.addElement(contentParagraph);
					dataTable3.addCell(cell1);

					contentParagraph = new Paragraph("ENQUIRY DATE", segHeadersFont);
					cell1 = new PdfPCell();
					cell1.setBorder(Rectangle.NO_BORDER);
					cell1.addElement(contentParagraph);
					dataTable3.addCell(cell1);

					contentParagraph = new Paragraph("ENQUIRY PURPOSE", segHeadersFont);
					cell1 = new PdfPCell();
					cell1.setBorder(Rectangle.NO_BORDER);
					cell1.addElement(contentParagraph);
					dataTable3.addCell(cell1);

					contentParagraph = new Paragraph("ENQUIRY AMOUNT", segHeadersFont);
					cell1 = new PdfPCell();
					cell1.setBorder(Rectangle.NO_BORDER);
					cell1.addElement(contentParagraph);
					dataTable3.addCell(cell1);

					if (cibilResponse.getEnquirySegment().getEnquiryBeanList() != null) {
						String dateOfEnquiry = cibilResponse.getEnquirySegment().getEnquiryBeanList().get(0)
								.getDateOfEnquiry();
						mostRecentEnq = dateOfEnquiry != null ? getFormattedDateOrTime(dateOfEnquiry, "ddMMyyyy") : "";

					}

					for (EnquiryBean enquiryBean : cibilResponse.getEnquirySegment().getEnquiryBeanList()) {
						String memberName = enquiryBean.getEnquiringMemberShortName() != null
								? enquiryBean.getEnquiringMemberShortName()
								: "";
						contentParagraph = new Paragraph(memberName, contentFont);
						cell1 = new PdfPCell();
						cell1.setBorder(Rectangle.NO_BORDER);
						cell1.addElement(contentParagraph);
						dataTable3.addCell(cell1);

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

						contentParagraph = new Paragraph(enquiryDate, contentFont);
						cell1 = new PdfPCell();
						cell1.setBorder(Rectangle.NO_BORDER);
						cell1.addElement(contentParagraph);
						dataTable3.addCell(cell1);

						String enquiryPurpose = enquiryBean.getEnquiryPurpose() != null
								? accTypeAndEnqPurposeMap.get(enquiryBean.getEnquiryPurpose())
								: "";

						contentParagraph = new Paragraph(enquiryPurpose, contentFont);
						cell1 = new PdfPCell();
						cell1.setBorder(Rectangle.NO_BORDER);
						cell1.addElement(contentParagraph);
						dataTable3.addCell(cell1);

						String enquiryAmt = enquiryBean.getEnquiryAmount() != null ? enquiryBean.getEnquiryAmount()
								: "";

						contentParagraph = new Paragraph(enquiryAmt, contentFont);
						cell1 = new PdfPCell();
						cell1.setBorder(Rectangle.NO_BORDER);
						cell1.addElement(contentParagraph);
						dataTable3.addCell(cell1);
					}
				}

				if (cibilResponse.getAccountSegment() != null) {
					title = new Paragraph("SUMMARY:", segNamefont);
					title.setAlignment(Element.ALIGN_LEFT);
					table_Heading_cell = new PdfPCell();
					table_Heading_cell.addElement(title);
					table_Heading_cell.setMinimumHeight(20);
					table_Heading_cell.setUseAscender(true);
					table_Heading_cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
					table_Heading_cell.setBorder(Rectangle.NO_BORDER);
					table_Heading_cell.setColspan(6);
					dataTable.addCell(table_Heading_cell);

					title = new Paragraph("ACCOUNT(S)", summaryHeadersFont);
					title.setAlignment(Element.ALIGN_LEFT);
					table_Heading_cell = new PdfPCell();
					table_Heading_cell.addElement(title);
					table_Heading_cell.setMinimumHeight(20);
					table_Heading_cell.setUseAscender(true);
					table_Heading_cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
					table_Heading_cell.setBorder(Rectangle.NO_BORDER);
					table_Heading_cell.setColspan(6);
					dataTable.addCell(table_Heading_cell);

					contentParagraph = new Paragraph("ACCOUNT TYPE", segHeadersFont);
					cell1 = new PdfPCell();
					cell1.setBorder(Rectangle.NO_BORDER);
					cell1.addElement(contentParagraph);
					dataTable.addCell(cell1);

					contentParagraph = new Paragraph("ACCOUNTS", segHeadersFont);
					cell1 = new PdfPCell();
					cell1.setBorder(Rectangle.NO_BORDER);
					cell1.addElement(contentParagraph);
					dataTable.addCell(cell1);

					contentParagraph = new Paragraph("ADVANCES", segHeadersFont);
					cell1 = new PdfPCell();
					cell1.setBorder(Rectangle.NO_BORDER);
					cell1.setColspan(2);
					cell1.addElement(contentParagraph);
					dataTable.addCell(cell1);

					contentParagraph = new Paragraph("BALANCES", segHeadersFont);
					cell1 = new PdfPCell();
					cell1.setBorder(Rectangle.NO_BORDER);
					cell1.addElement(contentParagraph);
					dataTable.addCell(cell1);

					contentParagraph = new Paragraph("DATE OPENED", segHeadersFont);
					cell1 = new PdfPCell();
					cell1.setBorder(Rectangle.NO_BORDER);
					cell1.addElement(contentParagraph);
					dataTable.addCell(cell1);

					int totalAcc = cibilResponse.getAccountSegment().getOwnAccountBeanList().size()
							+ cibilResponse.getAccountSegment().getOthersAccountBeanList().size();

					contentParagraph = new Paragraph("All Accounts", contentFontBold);
					cell1 = new PdfPCell();
					cell1.setBorder(Rectangle.NO_BORDER);
					cell1.setBackgroundColor(myColor);
					cell1.addElement(contentParagraph);
					dataTable.addCell(cell1);

					contentParagraph = new Paragraph("TOTAL:" + totalAcc, contentFont);
					cell1 = new PdfPCell();
					cell1.setBorder(Rectangle.NO_BORDER);
					cell1.setBackgroundColor(myColor);
					cell1.addElement(contentParagraph);
					dataTable.addCell(cell1);

					contentParagraph = new Paragraph("HIGH CR/SANC. AMT:" + highCreditSum, contentFont);
					cell1 = new PdfPCell();
					cell1.setBorder(Rectangle.NO_BORDER);
					cell1.setBackgroundColor(myColor);
					cell1.setColspan(2);
					cell1.addElement(contentParagraph);
					dataTable.addCell(cell1);

					contentParagraph = new Paragraph("CURRENT:" + currentBalanceSum, contentFont);
					cell1 = new PdfPCell();
					cell1.setBorder(Rectangle.NO_BORDER);
					cell1.setBackgroundColor(myColor);
					cell1.addElement(contentParagraph);
					dataTable.addCell(cell1);

					Date recentDate = Collections.max(openedDateList);
					String recentDateString = "";

					if (recentDate != null && !recentDate.equals("")) {
						recentDateString = new SimpleDateFormat("dd-MM-yyyy").format(recentDate);
					}
					contentParagraph = new Paragraph("RECENT:" + recentDateString, contentFont);
					cell1 = new PdfPCell();
					cell1.setBorder(Rectangle.NO_BORDER);
					cell1.setBackgroundColor(myColor);
					cell1.addElement(contentParagraph);
					dataTable.addCell(cell1);

					contentParagraph = new Paragraph("", contentFont);
					cell1 = new PdfPCell();
					cell1.setBorder(Rectangle.NO_BORDER);
					cell1.setBackgroundColor(myColor);
					cell1.addElement(contentParagraph);
					dataTable.addCell(cell1);

					contentParagraph = new Paragraph("OVERDUE:" + overdueCount, contentFont);
					cell1 = new PdfPCell();
					cell1.setBorder(Rectangle.NO_BORDER);
					cell1.setBackgroundColor(myColor);
					cell1.addElement(contentParagraph);
					dataTable.addCell(cell1);

					contentParagraph = new Paragraph("", contentFont);
					cell1 = new PdfPCell();
					cell1.setBorder(Rectangle.NO_BORDER);
					cell1.setBackgroundColor(myColor);
					cell1.setColspan(2);
					cell1.addElement(contentParagraph);
					dataTable.addCell(cell1);

					contentParagraph = new Paragraph("OVERDUE:" + overdueSum, contentFont);
					cell1 = new PdfPCell();
					cell1.setBorder(Rectangle.NO_BORDER);
					cell1.setBackgroundColor(myColor);
					cell1.addElement(contentParagraph);
					dataTable.addCell(cell1);

					Date oldestDate = Collections.min(openedDateList);
					String oldestDateString = "";

					if (oldestDate != null && !oldestDate.equals("")) {
						oldestDateString = new SimpleDateFormat("dd-MM-yyyy").format(oldestDate);
					}

					contentParagraph = new Paragraph("OLDEST:" + oldestDateString, contentFont);
					cell1 = new PdfPCell();
					cell1.setBorder(Rectangle.NO_BORDER);
					cell1.setBackgroundColor(myColor);
					cell1.addElement(contentParagraph);
					dataTable.addCell(cell1);

					contentParagraph = new Paragraph("", contentFont);
					cell1 = new PdfPCell();
					cell1.setBorder(Rectangle.NO_BORDER);
					cell1.setBackgroundColor(myColor);
					cell1.addElement(contentParagraph);
					dataTable.addCell(cell1);

					contentParagraph = new Paragraph("ZERO-BALANCE:" + zeroBalanceCount, contentFont);
					cell1 = new PdfPCell();
					cell1.setBorder(Rectangle.NO_BORDER);
					cell1.setBackgroundColor(myColor);
					cell1.addElement(contentParagraph);
					dataTable.addCell(cell1);

					contentParagraph = new Paragraph("", contentFont);
					cell1 = new PdfPCell();
					cell1.setBorder(Rectangle.NO_BORDER);
					cell1.setBackgroundColor(myColor);
					cell1.setColspan(2);
					cell1.addElement(contentParagraph);
					dataTable.addCell(cell1);

					contentParagraph = new Paragraph("", contentFont);
					cell1 = new PdfPCell();
					cell1.setBorder(Rectangle.NO_BORDER);
					cell1.setBackgroundColor(myColor);
					cell1.addElement(contentParagraph);
					dataTable.addCell(cell1);

					contentParagraph = new Paragraph("", contentFont);
					cell1 = new PdfPCell();
					cell1.setBorder(Rectangle.NO_BORDER);
					cell1.setBackgroundColor(myColor);
					cell1.addElement(contentParagraph);
					dataTable.addCell(cell1);

				}

				if (cibilResponse.getEnquirySegment() != null) {
					title = new Paragraph("ENQUIRIES", summaryHeadersFont);
					title.setAlignment(Element.ALIGN_LEFT);
					table_Heading_cell = new PdfPCell();
					table_Heading_cell.addElement(title);
					table_Heading_cell.setMinimumHeight(20);
					table_Heading_cell.setUseAscender(true);
					table_Heading_cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
					table_Heading_cell.setBorder(Rectangle.NO_BORDER);
					table_Heading_cell.setColspan(6);
					dataTable.addCell(table_Heading_cell);

					contentParagraph = new Paragraph("ENQUIRY PURPOSE", segHeadersFont);
					cell1 = new PdfPCell();
					cell1.setBorder(Rectangle.NO_BORDER);
					cell1.addElement(contentParagraph);
					dataTable.addCell(cell1);

					contentParagraph = new Paragraph("TOTAL", segHeadersFont);
					cell1 = new PdfPCell();
					cell1.setBorder(Rectangle.NO_BORDER);
					cell1.addElement(contentParagraph);
					dataTable.addCell(cell1);

					contentParagraph = new Paragraph("PAST 30 DAYS", segHeadersFont);
					cell1 = new PdfPCell();
					cell1.setBorder(Rectangle.NO_BORDER);
					cell1.addElement(contentParagraph);
					dataTable.addCell(cell1);

					contentParagraph = new Paragraph("PAST 12 MONTHS", segHeadersFont);
					cell1 = new PdfPCell();
					cell1.setBorder(Rectangle.NO_BORDER);
					cell1.addElement(contentParagraph);
					dataTable.addCell(cell1);

					contentParagraph = new Paragraph("PAST 24 MONTHS", segHeadersFont);
					cell1 = new PdfPCell();
					cell1.setBorder(Rectangle.NO_BORDER);
					cell1.addElement(contentParagraph);
					dataTable.addCell(cell1);

					contentParagraph = new Paragraph("RECENT", segHeadersFont);
					cell1 = new PdfPCell();
					cell1.setBorder(Rectangle.NO_BORDER);
					cell1.addElement(contentParagraph);
					dataTable.addCell(cell1);

					int totalEnquires = cibilResponse.getEnquirySegment().getEnquiryBeanList().size();

					contentParagraph = new Paragraph("All Enquiries", contentFontBold);
					cell1 = new PdfPCell();
					cell1.setBorder(Rectangle.NO_BORDER);
					cell1.setBackgroundColor(myColor);
					cell1.addElement(contentParagraph);
					dataTable.addCell(cell1);

					contentParagraph = new Paragraph(new Integer(totalEnquires).toString(), contentFont);
					cell1 = new PdfPCell();
					cell1.setBorder(Rectangle.NO_BORDER);
					cell1.setBackgroundColor(myColor);
					cell1.addElement(contentParagraph);
					dataTable.addCell(cell1);

					contentParagraph = new Paragraph(Integer.toString(past30daysEnqCount), contentFont);
					cell1 = new PdfPCell();
					cell1.setBorder(Rectangle.NO_BORDER);
					cell1.setBackgroundColor(myColor);
					cell1.addElement(contentParagraph);
					dataTable.addCell(cell1);

					contentParagraph = new Paragraph(Integer.toString(past12MonthsEnqCount), contentFont);
					cell1 = new PdfPCell();
					cell1.setBorder(Rectangle.NO_BORDER);
					cell1.setBackgroundColor(myColor);
					cell1.addElement(contentParagraph);
					dataTable.addCell(cell1);

					contentParagraph = new Paragraph(Integer.toString(past24MonthsEnqCount), contentFont);
					cell1 = new PdfPCell();
					cell1.setBorder(Rectangle.NO_BORDER);
					cell1.setBackgroundColor(myColor);
					cell1.addElement(contentParagraph);
					dataTable.addCell(cell1);

					contentParagraph = new Paragraph(mostRecentEnq, contentFont);
					cell1 = new PdfPCell();
					cell1.setBorder(Rectangle.NO_BORDER);
					cell1.setBackgroundColor(myColor);
					cell1.addElement(contentParagraph);
					dataTable.addCell(cell1);

					blankCell = new PdfPCell();
					blankCell.setBorder(Rectangle.NO_BORDER);
					blankCell.setColspan(6);
					blankCell.addElement(new Phrase(""));
					blankCell.setMinimumHeight(10);
					dataTable.addCell(blankCell);

					blankCell = new PdfPCell();
					blankCell.setBorder(Rectangle.BOTTOM);
					blankCell.setBorderColor(grayColor);
					blankCell.setBorderWidth(1);
					blankCell.setColspan(6);
					blankCell.addElement(new Phrase(""));
					blankCell.setMinimumHeight(10);
					dataTable.addCell(blankCell);
				}

				if (cibilResponse.getErrorSegment() != null
						&& cibilResponse.getErrorSegment().getUserReferenceErrorSegment() != null) {
					int i = 0;
					title = new Paragraph("CIBIL INPUT ERRORS", segNamefont);
					title.setAlignment(Element.ALIGN_LEFT);
					table_Heading_cell = new PdfPCell();
					table_Heading_cell.addElement(title);
					table_Heading_cell.setMinimumHeight(20);
					table_Heading_cell.setUseAscender(true);
					table_Heading_cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
					table_Heading_cell.setBorder(Rectangle.NO_BORDER);
					table_Heading_cell.setColspan(6);
					dataTable.addCell(table_Heading_cell);

					contentParagraph = new Paragraph(
							"Hello User, Your Cibil Report have following input errors. Kindly please contact IT team",
							contentFont);
					cell1 = new PdfPCell();
					cell1.setBorder(Rectangle.NO_BORDER);
					cell1.setColspan(6);
					cell1.addElement(contentParagraph);
					dataTable.addCell(cell1);

					blankCell = new PdfPCell();
					blankCell.setBorder(Rectangle.NO_BORDER);
					blankCell.setColspan(6);
					blankCell.addElement(new Phrase(""));
					blankCell.setMinimumHeight(10);
					dataTable.addCell(blankCell);

					contentParagraph = new Paragraph("SR. No.", segHeadersFont);
					cell1 = new PdfPCell();
					cell1.setBorder(Rectangle.BOX);
					cell1.addElement(contentParagraph);
					dataTable.addCell(cell1);

					contentParagraph = new Paragraph("FIELD NAME", segHeadersFont);
					cell1 = new PdfPCell();
					cell1.setBorder(Rectangle.BOX);
					cell1.setColspan(2);
					cell1.addElement(contentParagraph);
					dataTable.addCell(cell1);

					contentParagraph = new Paragraph("FIELD VALUE", segHeadersFont);
					cell1 = new PdfPCell();
					cell1.setBorder(Rectangle.BOX);
					cell1.setColspan(3);
					cell1.addElement(contentParagraph);
					dataTable.addCell(cell1);

					UserReferenceErrorSegment ures = cibilResponse.getErrorSegment().getUserReferenceErrorSegment();

					if (ures.getInvalidVersion() != null) {
						contentParagraph = new Paragraph(Integer.toString(++i), font);
						cell1 = new PdfPCell();
						cell1.setBorder(Rectangle.BOX);
						cell1.addElement(contentParagraph);
						dataTable.addCell(cell1);

						contentParagraph = new Paragraph("Invalid Version", font);
						cell1 = new PdfPCell();
						cell1.setBorder(Rectangle.BOX);
						cell1.setColspan(2);
						cell1.addElement(contentParagraph);
						dataTable.addCell(cell1);

						contentParagraph = new Paragraph(ures.getInvalidVersion(), font);
						cell1 = new PdfPCell();
						cell1.setBorder(Rectangle.BOX);
						cell1.setColspan(3);
						cell1.addElement(contentParagraph);
						dataTable.addCell(cell1);
					}

					if (ures.getInvalidFieldLength() != null) {
						contentParagraph = new Paragraph(Integer.toString(++i), font);
						cell1 = new PdfPCell();
						cell1.setBorder(Rectangle.BOX);
						cell1.addElement(contentParagraph);
						dataTable.addCell(cell1);

						contentParagraph = new Paragraph("Invalid Field Length", font);
						cell1 = new PdfPCell();
						cell1.setBorder(Rectangle.BOX);
						cell1.setColspan(2);
						cell1.addElement(contentParagraph);
						dataTable.addCell(cell1);

						contentParagraph = new Paragraph(ures.getInvalidFieldLength(), font);
						cell1 = new PdfPCell();
						cell1.setBorder(Rectangle.BOX);
						cell1.setColspan(3);
						cell1.addElement(contentParagraph);
						dataTable.addCell(cell1);
					}

					if (ures.getInvalidTotalLength() != null) {
						contentParagraph = new Paragraph(Integer.toString(++i), font);
						cell1 = new PdfPCell();
						cell1.setBorder(Rectangle.BOX);
						cell1.addElement(contentParagraph);
						dataTable.addCell(cell1);

						contentParagraph = new Paragraph("Invalid Total Length", font);
						cell1 = new PdfPCell();
						cell1.setBorder(Rectangle.BOX);
						cell1.setColspan(2);
						cell1.addElement(contentParagraph);
						dataTable.addCell(cell1);

						contentParagraph = new Paragraph(ures.getInvalidTotalLength(), font);
						cell1 = new PdfPCell();
						cell1.setBorder(Rectangle.BOX);
						cell1.setColspan(3);
						cell1.addElement(contentParagraph);
						dataTable.addCell(cell1);
					}

					if (ures.getInvalidEnquiryPurpose() != null) {
						contentParagraph = new Paragraph(Integer.toString(++i), font);
						cell1 = new PdfPCell();
						cell1.setBorder(Rectangle.BOX);
						cell1.addElement(contentParagraph);
						dataTable.addCell(cell1);

						contentParagraph = new Paragraph("Invalid Enquiry Purpose", font);
						cell1 = new PdfPCell();
						cell1.setBorder(Rectangle.BOX);
						cell1.setColspan(2);
						cell1.addElement(contentParagraph);
						dataTable.addCell(cell1);

						contentParagraph = new Paragraph(ures.getInvalidEnquiryPurpose(), font);
						cell1 = new PdfPCell();
						cell1.setBorder(Rectangle.BOX);
						cell1.setColspan(3);
						cell1.addElement(contentParagraph);
						dataTable.addCell(cell1);
					}

					if (ures.getInvalidEnquiryAmount() != null) {
						contentParagraph = new Paragraph(Integer.toString(++i), font);
						cell1 = new PdfPCell();
						cell1.setBorder(Rectangle.BOX);
						cell1.addElement(contentParagraph);
						dataTable.addCell(cell1);

						contentParagraph = new Paragraph("Invalid Enquiry Amount", font);
						cell1 = new PdfPCell();
						cell1.setBorder(Rectangle.BOX);
						cell1.setColspan(2);
						cell1.addElement(contentParagraph);
						dataTable.addCell(cell1);

						contentParagraph = new Paragraph(ures.getInvalidEnquiryAmount(), font);
						cell1 = new PdfPCell();
						cell1.setBorder(Rectangle.BOX);
						cell1.setColspan(3);
						cell1.addElement(contentParagraph);
						dataTable.addCell(cell1);
					}

					if (ures.getInvalidEnquiryMemberUserIDOrPassword() != null) {
						contentParagraph = new Paragraph(Integer.toString(++i), font);
						cell1 = new PdfPCell();
						cell1.setBorder(Rectangle.BOX);
						cell1.addElement(contentParagraph);
						dataTable.addCell(cell1);

						contentParagraph = new Paragraph("Invalid Enquiry Member User ID/Password", font);
						cell1 = new PdfPCell();
						cell1.setBorder(Rectangle.BOX);
						cell1.setColspan(2);
						cell1.addElement(contentParagraph);
						dataTable.addCell(cell1);

						contentParagraph = new Paragraph(ures.getInvalidEnquiryMemberUserIDOrPassword(), font);
						cell1 = new PdfPCell();
						cell1.setBorder(Rectangle.BOX);
						cell1.setColspan(3);
						cell1.addElement(contentParagraph);
						dataTable.addCell(cell1);
					}

					if (ures.getRequiredEnquirySegmentMissing() != null) {
						contentParagraph = new Paragraph(Integer.toString(++i), font);
						cell1 = new PdfPCell();
						cell1.setBorder(Rectangle.BOX);
						cell1.addElement(contentParagraph);
						dataTable.addCell(cell1);

						contentParagraph = new Paragraph("Required Enquiry Segment Missing", font);
						cell1 = new PdfPCell();
						cell1.setBorder(Rectangle.BOX);
						cell1.setColspan(2);
						cell1.addElement(contentParagraph);
						dataTable.addCell(cell1);

						contentParagraph = new Paragraph(ures.getRequiredEnquirySegmentMissing(), font);
						cell1 = new PdfPCell();
						cell1.setBorder(Rectangle.BOX);
						cell1.setColspan(3);
						cell1.addElement(contentParagraph);
						dataTable.addCell(cell1);
					}

					if (ures.getInvalidEnquiryData() != null) {
						for (String invalidData : ures.getInvalidEnquiryData()) {
							contentParagraph = new Paragraph(Integer.toString(++i), font);
							cell1 = new PdfPCell();
							cell1.setBorder(Rectangle.BOX);
							cell1.addElement(contentParagraph);
							dataTable.addCell(cell1);

							contentParagraph = new Paragraph("Invalid Enquiry Data", font);
							cell1 = new PdfPCell();
							cell1.setBorder(Rectangle.BOX);
							cell1.setColspan(2);
							cell1.addElement(contentParagraph);
							dataTable.addCell(cell1);

							contentParagraph = new Paragraph(invalidData, font);
							cell1 = new PdfPCell();
							cell1.setBorder(Rectangle.BOX);
							cell1.setColspan(3);
							cell1.addElement(contentParagraph);
							dataTable.addCell(cell1);
						}
					}

					if (ures.getCibilSystemError() != null) {
						contentParagraph = new Paragraph(Integer.toString(++i), font);
						cell1 = new PdfPCell();
						cell1.setBorder(Rectangle.BOX);
						cell1.addElement(contentParagraph);
						dataTable.addCell(cell1);

						contentParagraph = new Paragraph("CIBIL System Error", font);
						cell1 = new PdfPCell();
						cell1.setBorder(Rectangle.BOX);
						cell1.setColspan(2);
						cell1.addElement(contentParagraph);
						dataTable.addCell(cell1);

						contentParagraph = new Paragraph(ures.getCibilSystemError()
								+ ".Contains the value Y. The member should contact CIBIL if this error condition is encountered.",
								font);
						cell1 = new PdfPCell();
						cell1.setBorder(Rectangle.BOX);
						cell1.setColspan(3);
						cell1.addElement(contentParagraph);
						dataTable.addCell(cell1);
					}

					if (ures.getInvalidSegmentTag() != null) {
						contentParagraph = new Paragraph(Integer.toString(++i), font);
						cell1 = new PdfPCell();
						cell1.setBorder(Rectangle.BOX);
						cell1.addElement(contentParagraph);
						dataTable.addCell(cell1);

						contentParagraph = new Paragraph("Invalid Segment Tag", font);
						cell1 = new PdfPCell();
						cell1.setBorder(Rectangle.BOX);
						cell1.setColspan(2);
						cell1.addElement(contentParagraph);
						dataTable.addCell(cell1);

						contentParagraph = new Paragraph(ures.getInvalidSegmentTag(), font);
						cell1 = new PdfPCell();
						cell1.setBorder(Rectangle.BOX);
						cell1.setColspan(3);
						cell1.addElement(contentParagraph);
						dataTable.addCell(cell1);
					}

					if (ures.getInvalidSegmentOrder() != null) {
						contentParagraph = new Paragraph(Integer.toString(++i), font);
						cell1 = new PdfPCell();
						cell1.setBorder(Rectangle.BOX);
						cell1.addElement(contentParagraph);
						dataTable.addCell(cell1);

						contentParagraph = new Paragraph("Invalid Segment Order", font);
						cell1 = new PdfPCell();
						cell1.setBorder(Rectangle.BOX);
						cell1.setColspan(2);
						cell1.addElement(contentParagraph);
						dataTable.addCell(cell1);

						contentParagraph = new Paragraph(ures.getInvalidSegmentOrder(), font);
						cell1 = new PdfPCell();
						cell1.setBorder(Rectangle.BOX);
						cell1.setColspan(3);
						cell1.addElement(contentParagraph);
						dataTable.addCell(cell1);
					}

					if (ures.getInvalidFieldTagOrder() != null) {
						contentParagraph = new Paragraph(Integer.toString(++i), font);
						cell1 = new PdfPCell();
						cell1.setBorder(Rectangle.BOX);
						cell1.addElement(contentParagraph);
						dataTable.addCell(cell1);

						contentParagraph = new Paragraph("Invalid Field Tag Order", font);
						cell1 = new PdfPCell();
						cell1.setBorder(Rectangle.BOX);
						cell1.setColspan(2);
						cell1.addElement(contentParagraph);
						dataTable.addCell(cell1);

						contentParagraph = new Paragraph(ures.getInvalidFieldTagOrder(), font);
						cell1 = new PdfPCell();
						cell1.setBorder(Rectangle.BOX);
						cell1.setColspan(3);
						cell1.addElement(contentParagraph);
						dataTable.addCell(cell1);
					}

					if (ures.getMissingRequiredField() != null) {
						for (String missingField : ures.getMissingRequiredField()) {
							contentParagraph = new Paragraph(Integer.toString(++i), font);
							cell1 = new PdfPCell();
							cell1.setBorder(Rectangle.BOX);
							cell1.addElement(contentParagraph);
							dataTable.addCell(cell1);

							contentParagraph = new Paragraph("Missing Required Field", font);
							cell1 = new PdfPCell();
							cell1.setBorder(Rectangle.BOX);
							cell1.setColspan(2);
							cell1.addElement(contentParagraph);
							dataTable.addCell(cell1);

							contentParagraph = new Paragraph(missingField, font);
							cell1 = new PdfPCell();
							cell1.setBorder(Rectangle.BOX);
							cell1.setColspan(3);
							cell1.addElement(contentParagraph);
							dataTable.addCell(cell1);
						}
					}

					if (ures.getRequestedResponseSizeExceeded() != null) {
						contentParagraph = new Paragraph(Integer.toString(++i), font);
						cell1 = new PdfPCell();
						cell1.setBorder(Rectangle.BOX);
						cell1.addElement(contentParagraph);
						dataTable.addCell(cell1);

						contentParagraph = new Paragraph("Requested Response Size Exceeded", font);
						cell1 = new PdfPCell();
						cell1.setBorder(Rectangle.BOX);
						cell1.setColspan(2);
						cell1.addElement(contentParagraph);
						dataTable.addCell(cell1);

						contentParagraph = new Paragraph(ures.getRequestedResponseSizeExceeded(), font);
						cell1 = new PdfPCell();
						cell1.setBorder(Rectangle.BOX);
						cell1.setColspan(3);
						cell1.addElement(contentParagraph);
						dataTable.addCell(cell1);
					}

					if (ures.getInvalidInputOrOutputMedia() != null) {
						contentParagraph = new Paragraph(Integer.toString(++i), font);
						cell1 = new PdfPCell();
						cell1.setBorder(Rectangle.BOX);
						cell1.addElement(contentParagraph);
						dataTable.addCell(cell1);

						contentParagraph = new Paragraph("Invalid Input/Output Media", font);
						cell1 = new PdfPCell();
						cell1.setBorder(Rectangle.BOX);
						cell1.setColspan(2);
						cell1.addElement(contentParagraph);
						dataTable.addCell(cell1);

						contentParagraph = new Paragraph(ures.getInvalidInputOrOutputMedia(), font);
						cell1 = new PdfPCell();
						cell1.setBorder(Rectangle.BOX);
						cell1.setColspan(3);
						cell1.addElement(contentParagraph);
						dataTable.addCell(cell1);
					}
				}

				if (!fullName2.equals("")) {
					blankCell = new PdfPCell();
					blankCell.setBorder(Rectangle.BOTTOM);
					blankCell.setColspan(4);
					blankCell.addElement(new Phrase(""));
					blankCell.setMinimumHeight(10);
					endTable.addCell(blankCell);

					contentParagraph = new Paragraph("END OF REPORT ON " + fullName2, segHeadersFont);
					cell1 = new PdfPCell();
					cell1.setColspan(4);
					cell1.setBorder(Rectangle.NO_BORDER);
					cell1.addElement(contentParagraph);
					endTable.addCell(cell1);

					blankCell = new PdfPCell();
					blankCell.setBorder(Rectangle.BOTTOM);
					blankCell.setColspan(4);
					blankCell.addElement(new Phrase(""));
					blankCell.setMinimumHeight(10);
					endTable.addCell(blankCell);
				}

				iText_Create_Table.add(dataTable);
				iText_Create_Table.add(dataTable2);
				iText_Create_Table.add(dataTable3);
				iText_Create_Table.add(endTable);

			}

			iText_Create_Table.close();

			System.out.println("End of pdf generation");

		} catch (Exception i) {
			i.printStackTrace();
			return null;
		}
		return baosPDF;

	}

	private void buildHistoryPaymentTable(PdfPTable outerTable, Map<String, String> historyMapData) {
		try {
			PdfPTable innerTable1, innerTable2;

			PdfPCell cell, blankCell;
			Paragraph contentParagraph;

			Font contentFont = new Font(FontFamily.HELVETICA, 9, Font.NORMAL);
			contentFont.setColor(WebColors.getRGBColor("#676767"));

			List<String> keyList = new ArrayList<String>(historyMapData.keySet());
			List<String> valueList = new ArrayList<String>(historyMapData.values());

			int j = 0;
			int counter = 0;
			int totalColumnsCount = 15;
			int mapTotalSize = historyMapData.size();

			while (!historyMapData.isEmpty()) {
				innerTable1 = new PdfPTable(totalColumnsCount);
				innerTable1.setWidthPercentage(100);

				innerTable2 = new PdfPTable(totalColumnsCount);
				innerTable2.setWidthPercentage(100);

				for (int i = j; i < mapTotalSize; i++, counter++) {

					if (counter == totalColumnsCount) {
						j = i;
						// i=0;
						counter = 0;
						break;

					}

					String key = keyList.get(i);
					String value = valueList.get(i);

					contentParagraph = new Paragraph(value, contentFont);
					cell = new PdfPCell();
					cell.setBorder(Rectangle.NO_BORDER);
					cell.addElement(contentParagraph);
					innerTable1.addCell(cell);

					contentParagraph = new Paragraph(key, contentFont);
					cell = new PdfPCell();
					cell.setBorder(Rectangle.NO_BORDER);
					cell.addElement(contentParagraph);
					innerTable2.addCell(cell);

					historyMapData.remove(key, value);

				}

				if (counter > 0) {
					int remainingColumns = totalColumnsCount - counter;

					for (int k = 0; k < remainingColumns; k++) {
						contentParagraph = new Paragraph("", contentFont);
						cell = new PdfPCell();
						cell.setBorder(Rectangle.NO_BORDER);
						cell.addElement(contentParagraph);
						innerTable1.addCell(cell);

						contentParagraph = new Paragraph("", contentFont);
						cell = new PdfPCell();
						cell.setBorder(Rectangle.NO_BORDER);
						cell.addElement(contentParagraph);
						innerTable2.addCell(cell);
					}
				}

				cell = new PdfPCell(innerTable1);
				cell.setBorder(Rectangle.NO_BORDER);
				cell.setColspan(4);
				outerTable.addCell(cell);

				cell = new PdfPCell(innerTable2);
				cell.setBorder(Rectangle.NO_BORDER);
				cell.setColspan(4);
				outerTable.addCell(cell);

				blankCell = new PdfPCell();
				blankCell.setBorder(Rectangle.NO_BORDER);
				blankCell.setColspan(6);
				blankCell.addElement(new Phrase(""));
				blankCell.setMinimumHeight(10);
				outerTable.addCell(blankCell);

				innerTable1 = null;
				innerTable2 = null;

			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public Map<String, String> getPaymentHistoryMap(String paymentHistory, String paymentHistStart,
			String paymentHistEnd) {
		Map<String, String> historyMapData = new LinkedHashMap<String, String>();
		try {

			String[] payHistArray = paymentHistory.split("(?<=\\G...)");

			int startMonth = Integer.parseInt(paymentHistStart.substring(3, 5));
			int endMonth = Integer.parseInt(paymentHistEnd.substring(3, 5));
			int startYear = Integer.parseInt(paymentHistStart.substring(8));
			int endYear = Integer.parseInt(paymentHistEnd.substring(8));
			int lastMonth = 1;
			int index = 0;

			for (int i = startYear; i >= endYear; i--) {

				if (i == endYear) {
					lastMonth = endMonth;
				}
				for (int j = startMonth; j >= lastMonth; j--) {
					if (j == 1) {
						startMonth = 12;
					}

					if (payHistArray.length - 1 < index) {
						break;
					}
					historyMapData.put(String.format("%02d", j) + "-" + String.format("%02d", i),
							payHistArray[index++]);

				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return historyMapData;
	}

	public String getFormattedDateOrTime(String input, String format) {
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
			e.printStackTrace();
		}

		return output;
	}

	public String storeCibilReport(List<CibilResponse> cibilRespList, long id) {
		String pdfContent = null;
		// This line of code generate cibil report
		ByteArrayOutputStream out = getCibilReport(cibilRespList);
		System.out.println("Generating pdf end:" + new java.util.Date().getTime());
		if (out != null) {
			System.out.println("Storing pdf in db start:" + new java.util.Date().getTime());
			// This line of code store cibil report to db
			dao.storeCibilReportToDB(out, id);
			System.out.println("Storing pdf in db end:" + new java.util.Date().getTime());
			pdfContent = new sun.misc.BASE64Encoder().encode(out.toByteArray());

		}

		return pdfContent;
	}

	public void storeResponseToPdf(ByteArrayOutputStream byteOutStream) {
		OutputStream outStream = null;
		try {
			outStream = new FileOutputStream(PropertyReader.getProperty("pdfPath") + "CibilReport.pdf");
			byteOutStream.writeTo(outStream);
			outStream.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public Map<String, String> getAccountTypeAndEnquiryPurposeMap() {

		return dao.getAccountTypeAndEnquiryPurposeData();

	}

	public String getInputJson(Object requestObject) {

		Gson gson = new Gson();
		String json = gson.toJson(requestObject);
		return json;
	}

	public CibilResponse parseErrorResponse(StringBuilder input, List<String> tagList) {
		String segmentString = null;
		CibilResponse cibilResponse = new CibilResponse();
		ErrorSegment errorSegment = null;

		for (int i = 0; i < tagList.size(); i++) {
			String segmentTag = tagList.get(i).substring(0, 4);
			switch (segmentTag) {
			case "ERRR":
				segmentString = input.substring(input.indexOf(tagList.get(i)), input.indexOf(tagList.get(i + 1)));
				errorSegment = parseErrorSegment(segmentString);
				input.replace(0, segmentString.length(), "");
				cibilResponse.setErrorSegment(errorSegment);
				break;

			case "UR03":
				segmentString = input.substring(input.indexOf(tagList.get(i)), input.indexOf(tagList.get(i + 1)));
				UserReferenceErrorSegment userReferenceErrorSegment = parseUserReferenceErrorSegment(segmentString);
				input.replace(0, segmentString.length(), "");
				cibilResponse.setUserReferenceErrorSegment(userReferenceErrorSegment);
				cibilResponse.getErrorSegment().setUserReferenceErrorSegment(userReferenceErrorSegment);
				break;

			case "ES07":

				segmentString = input.substring(input.indexOf(tagList.get(i)));
				EndSegment endSegment = parseEndSegment(segmentString);
				input.replace(0, segmentString.length(), "");
				cibilResponse.setEndSegment(endSegment);
				break;
			}
		}

		return cibilResponse;

	}

	public CibilResponse parseCorrectResponse(StringBuilder input, List<String> tagList) {
		int identCount = 0, telCount = 0, emailCount = 0, accNumCount = 0, scoreCount = 0, addressCount = 0,
				accountCount = 0, enquiryCount = 0;
		String segmentString = null;
		CibilResponse cibilResponse = new CibilResponse();
		List<IDBean> idBeanList = new ArrayList<IDBean>();
		List<TelephoneBean> telBeanList = new ArrayList<TelephoneBean>();
		List<String> emailList = new ArrayList<String>();
		List<AccountNumberBean> accNumBeanList = new ArrayList<AccountNumberBean>();
		List<ScoreBean> scoreBeanList = new ArrayList<ScoreBean>();
		List<AddressBean> addressBeanList = new ArrayList<AddressBean>();
		List<EnquiryBean> enquiryBeanList = new ArrayList<EnquiryBean>();
		List<OwnAccountBean> ownAccountBeanList = new ArrayList<OwnAccountBean>();
		List<OthersAccountBean> othersAccountBeanList = new ArrayList<OthersAccountBean>();
		try {

			for (int i = 0; i < tagList.size(); i++) {
				String segmentTag = tagList.get(i).substring(0, 4);
				switch (segmentTag) {
				case "TUEF":

					segmentString = input.substring(input.indexOf(tagList.get(i)), input.indexOf(tagList.get(i + 1)));
					HeaderSegment headerSegment = parseHeaderSegment(segmentString);
					input.replace(0, segmentString.length(), "");
					cibilResponse.setHeaderSegment(headerSegment);
					break;

				case "PN03":
					segmentString = input.substring(input.indexOf(tagList.get(i)), input.indexOf(tagList.get(i + 1)));
					NameSegment nameSegment = parseNameSegment(segmentString);
					input.replace(0, segmentString.length(), "");
					cibilResponse.setNameSegment(nameSegment);
					break;

				case "ID03":

					++identCount;
					segmentString = input.substring(input.indexOf(tagList.get(i)), input.indexOf(tagList.get(i + 1)));
					IDBean idbean = parseIdentificationSegment(segmentString, identCount);
					idBeanList.add(idbean);
					input.replace(0, segmentString.length(), "");
					break;

				case "PT03":

					++telCount;
					segmentString = input.substring(input.indexOf(tagList.get(i)), input.indexOf(tagList.get(i + 1)));
					TelephoneBean telbean = parseTelephoneSegment(segmentString, telCount);
					telBeanList.add(telbean);
					input.replace(0, segmentString.length(), "");
					break;

				case "EC03":

					++emailCount;
					segmentString = input.substring(input.indexOf(tagList.get(i)), input.indexOf(tagList.get(i + 1)));
					String email = parseEmailContactSegment(segmentString, emailCount);
					emailList.add(email);
					input.replace(0, segmentString.length(), "");
					break;

				case "EM03":
					segmentString = input.substring(input.indexOf(tagList.get(i)), input.indexOf(tagList.get(i + 1)));
					EmploymentSegment employmentSegment = parseEmploymentSegment(segmentString);
					input.replace(0, segmentString.length(), "");
					cibilResponse.setEmploymentSegment(employmentSegment);
					break;

				case "PI03":

					++accNumCount;
					segmentString = input.substring(input.indexOf(tagList.get(i)), input.indexOf(tagList.get(i + 1)));
					AccountNumberBean accNumBean = parseAccountNumberSegment(segmentString, accNumCount);
					accNumBeanList.add(accNumBean);
					input.replace(0, segmentString.length(), "");
					break;

				case "SC10":

					++scoreCount;

					String nextTag = tagList.get(i + 1);
					if (nextTag.equals("SC10")) {
						segmentString = input.substring(input.indexOf(tagList.get(i)),
								input.lastIndexOf(tagList.get(i + 1)));
					} else {
						segmentString = input.substring(input.indexOf(tagList.get(i)),
								input.indexOf(tagList.get(i + 1)));
					}
					ScoreBean scoreBean = parseScoreSegment(segmentString, scoreCount);
					scoreBeanList.add(scoreBean);
					input.replace(0, segmentString.length(), "");
					break;

				case "PA03":

					++addressCount;
					if (addressCount >= 99) {
						segmentString = input.substring(input.indexOf(tagList.get(i)),
								input.indexOf(tagList.get(i + 1), 6));
					} else {
						segmentString = input.substring(input.indexOf(tagList.get(i)),
								input.indexOf(tagList.get(i + 1)));
					}

					AddressBean addressBean = null;

					if (addressCount >= 99) {
						addressBean = parseAddressSegment(segmentString, 99);
					} else {
						addressBean = parseAddressSegment(segmentString, addressCount);
					}

					addressBeanList.add(addressBean);
					input.replace(0, segmentString.length(), "");
					break;

				case "TL04":
					++accountCount;
					if (accountCount >= 999) {
						segmentString = input.substring(input.indexOf(tagList.get(i)),
								input.indexOf(tagList.get(i + 1), 7));
					} else {
						segmentString = input.substring(input.indexOf(tagList.get(i)),
								input.indexOf(tagList.get(i + 1)));
					}
					Map<String, String> accountSegmentMap = null;
					if (accountCount >= 999) {
						accountSegmentMap = parseAccountSegment(segmentString, 999);
					} else {
						accountSegmentMap = parseAccountSegment(segmentString, accountCount);
					}
					String reportingMemberShortName = accountSegmentMap.get("02");
					if (reportingMemberShortName != null) {
						if (reportingMemberShortName.equals("ACCTREVIEW_SUMM")) {
							OthersAccountBean othersAccountBean = getOthersAccountBean(accountSegmentMap);
							othersAccountBeanList.add(othersAccountBean);
						} else {
							OwnAccountBean ownAccountBean = getOwnAccountBean(accountSegmentMap);
							ownAccountBeanList.add(ownAccountBean);

						}
					} else {
						OthersAccountBean othersAccountBean = getOthersAccountBean(accountSegmentMap);
						othersAccountBeanList.add(othersAccountBean);
					}

					input.replace(0, segmentString.length(), "");
					break;

				case "IQ04":

					++enquiryCount;
					if (enquiryCount >= 999) {
						segmentString = input.substring(input.indexOf(tagList.get(i)),
								input.indexOf(tagList.get(i + 1), 7));
					} else {
						segmentString = input.substring(input.indexOf(tagList.get(i)),
								input.indexOf(tagList.get(i + 1)));
					}

					EnquiryBean enquiryBean = null;

					if (enquiryCount >= 999) {
						enquiryBean = parseEnquirySegment(segmentString, 999);
					} else {
						enquiryBean = parseEnquirySegment(segmentString, enquiryCount);
					}

					enquiryBeanList.add(enquiryBean);
					input.replace(0, segmentString.length(), "");
					break;

				case "DR03":

					segmentString = input.substring(input.indexOf(tagList.get(i)), input.indexOf(tagList.get(i + 1)));
					ConsumerDisputeRemarksSegment consumerDisputeRemarksSegment = parseConsumerDisputeRemarksSegment(
							segmentString);
					input.replace(0, segmentString.length(), "");
					cibilResponse.setConsumerDisputeRemarksSegment(consumerDisputeRemarksSegment);
					break;

				case "ES07":

					segmentString = input.substring(input.indexOf(tagList.get(i)));
					EndSegment endSegment = parseEndSegment(segmentString);
					input.replace(0, segmentString.length(), "");
					cibilResponse.setEndSegment(endSegment);
					break;
				}
			}
			if (identCount > 0) {
				IdentificationSegment identificationSegment = new IdentificationSegment();
				identificationSegment.setIdBeanList(idBeanList);
				cibilResponse.setIdentificationSegment(identificationSegment);
			}

			if (telCount > 0) {
				TelephoneSegment telephoneSegment = new TelephoneSegment();
				telephoneSegment.setTelBeanList(telBeanList);
				cibilResponse.setTelephoneSegment(telephoneSegment);
			}

			if (emailCount > 0) {
				EmailContactSegment emailContactSegment = new EmailContactSegment();
				emailContactSegment.setEmailList(emailList);
				cibilResponse.setEmailContactSegment(emailContactSegment);

			}

			if (accNumCount > 0) {
				AccountNumberSegment accNumSegment = new AccountNumberSegment();
				accNumSegment.setAccNumBeanList(accNumBeanList);
				cibilResponse.setAccNumSegment(accNumSegment);
			}

			if (scoreCount > 0) {
				ScoreSegment scoreSegment = new ScoreSegment();
				scoreSegment.setScoreBeanList(scoreBeanList);
				cibilResponse.setScoreSegment(scoreSegment);
			}

			if (addressCount > 0) {
				AddressSegment addressSegment = new AddressSegment();
				addressSegment.setAddBeanList(addressBeanList);
				cibilResponse.setAddressSegment(addressSegment);
			}

			if (enquiryCount > 0) {
				EnquirySegment enquirySegment = new EnquirySegment();
				enquirySegment.setEnquiryBeanList(enquiryBeanList);
				cibilResponse.setEnquirySegment(enquirySegment);
			}

			if (accountCount > 0) {
				AccountSegment accountSegment = new AccountSegment();
				accountSegment.setOwnAccountBeanList(ownAccountBeanList);
				accountSegment.setOthersAccountBeanList(othersAccountBeanList);
				cibilResponse.setAccountSegment(accountSegment);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}

		return cibilResponse;
	}

	public UserReferenceErrorSegment parseUserReferenceErrorSegment(String input) {

		UserReferenceErrorSegment userReferenceErrorSegment = new UserReferenceErrorSegment();
		List<String> invalidEnquiryData = new ArrayList<String>();
		List<String> missingRequiredField = new ArrayList<String>();
		StringBuilder temp = new StringBuilder(input.replace("UR03U01", ""));

		while (temp.length() > 0) {

			String fieldTag = temp.substring(0, 2);

			String fieldValue = null, toBeChanged = null;

			switch (fieldTag) {
			case "01":
				fieldValue = parseFieldValue(temp.toString());
				toBeChanged = fieldTag + String.format("%02d", fieldValue.length()) + fieldValue;
				temp.replace(0, toBeChanged.length(), "");
				userReferenceErrorSegment.setMemberReferenceNumber(fieldValue);
				break;

			case "03":
				fieldValue = parseFieldValue(temp.toString());
				toBeChanged = fieldTag + String.format("%02d", fieldValue.length()) + fieldValue;
				temp.replace(0, toBeChanged.length(), "");

				userReferenceErrorSegment.setInvalidVersion(fieldValue);
				break;

			case "04":
				fieldValue = parseFieldValue(temp.toString());
				toBeChanged = fieldTag + String.format("%02d", fieldValue.length()) + fieldValue;
				temp.replace(0, toBeChanged.length(), "");
				userReferenceErrorSegment.setInvalidFieldLength(fieldValue);
				break;

			case "05":
				fieldValue = parseFieldValue(temp.toString());
				toBeChanged = fieldTag + String.format("%02d", fieldValue.length()) + fieldValue;
				temp.replace(0, toBeChanged.length(), "");
				userReferenceErrorSegment.setInvalidTotalLength(fieldValue);
				break;

			case "06":
				fieldValue = parseFieldValue(temp.toString());
				toBeChanged = fieldTag + String.format("%02d", fieldValue.length()) + fieldValue;
				temp.replace(0, toBeChanged.length(), "");
				userReferenceErrorSegment.setInvalidEnquiryPurpose(fieldValue);
				break;

			case "07":
				fieldValue = parseFieldValue(temp.toString());
				toBeChanged = fieldTag + String.format("%02d", fieldValue.length()) + fieldValue;
				temp.replace(0, toBeChanged.length(), "");
				userReferenceErrorSegment.setInvalidEnquiryAmount(fieldValue);
				break;

			case "08":
				fieldValue = parseFieldValue(temp.toString());
				toBeChanged = fieldTag + String.format("%02d", fieldValue.length()) + fieldValue;
				temp.replace(0, toBeChanged.length(), "");
				userReferenceErrorSegment.setInvalidEnquiryMemberUserIDOrPassword(fieldValue);
				break;

			case "09":
				fieldValue = parseFieldValue(temp.toString());
				toBeChanged = fieldTag + String.format("%02d", fieldValue.length()) + fieldValue;
				temp.replace(0, toBeChanged.length(), "");
				userReferenceErrorSegment.setRequiredEnquirySegmentMissing(fieldValue);
				break;

			case "10":
				fieldValue = parseFieldValue(temp.toString());
				toBeChanged = fieldTag + String.format("%02d", fieldValue.length()) + fieldValue;
				temp.replace(0, toBeChanged.length(), "");
				invalidEnquiryData.add(fieldValue);
				break;

			case "11":
				fieldValue = parseFieldValue(temp.toString());
				toBeChanged = fieldTag + String.format("%02d", fieldValue.length()) + fieldValue;
				temp.replace(0, toBeChanged.length(), "");
				userReferenceErrorSegment.setCibilSystemError(fieldValue);
				break;

			case "12":
				fieldValue = parseFieldValue(temp.toString());
				toBeChanged = fieldTag + String.format("%02d", fieldValue.length()) + fieldValue;
				temp.replace(0, toBeChanged.length(), "");
				userReferenceErrorSegment.setInvalidSegmentTag(fieldValue);
				break;

			case "13":
				fieldValue = parseFieldValue(temp.toString());
				toBeChanged = fieldTag + String.format("%02d", fieldValue.length()) + fieldValue;
				temp.replace(0, toBeChanged.length(), "");
				userReferenceErrorSegment.setInvalidSegmentOrder(fieldValue);
				break;

			case "14":
				fieldValue = parseFieldValue(temp.toString());
				toBeChanged = fieldTag + String.format("%02d", fieldValue.length()) + fieldValue;
				temp.replace(0, toBeChanged.length(), "");
				userReferenceErrorSegment.setInvalidFieldTagOrder(fieldValue);
				break;

			case "15":
				fieldValue = parseFieldValue(temp.toString());
				toBeChanged = fieldTag + String.format("%02d", fieldValue.length()) + fieldValue;
				temp.replace(0, toBeChanged.length(), "");
				missingRequiredField.add(fieldValue);
				break;

			case "16":
				fieldValue = parseFieldValue(temp.toString());
				toBeChanged = fieldTag + String.format("%02d", fieldValue.length()) + fieldValue;
				temp.replace(0, toBeChanged.length(), "");
				userReferenceErrorSegment.setRequestedResponseSizeExceeded(fieldValue);
				break;

			case "17":
				fieldValue = parseFieldValue(temp.toString());
				toBeChanged = fieldTag + String.format("%02d", fieldValue.length()) + fieldValue;
				temp.replace(0, toBeChanged.length(), "");
				userReferenceErrorSegment.setInvalidInputOrOutputMedia(fieldValue);
				break;
			}
		}

		userReferenceErrorSegment.setInvalidEnquiryData(invalidEnquiryData);
		userReferenceErrorSegment.setMissingRequiredField(missingRequiredField);

		return userReferenceErrorSegment;

	}

	public ErrorSegment parseErrorSegment(String input) {

		ErrorSegment errorSegment = new ErrorSegment();

		int i = 4;
		String dateProcessed = input.substring(i, i += 8).trim();
		String timeProcessed = input.substring(i, i += 6).trim();

		errorSegment.setDateProcessed(dateProcessed);
		errorSegment.setTimeProcessed(timeProcessed);

		return errorSegment;

	}

	public OthersAccountBean getOthersAccountBean(Map<String, String> segmentTagMap) {
		OthersAccountBean othersAccountBean = new OthersAccountBean();

		othersAccountBean.setReportingMemberShortName(segmentTagMap.get("02"));
		othersAccountBean.setNumberOfAccounts(segmentTagMap.get("03"));
		othersAccountBean.setAccountGroup(segmentTagMap.get("04"));
		othersAccountBean.setLiveOrClosedIndicator(segmentTagMap.get("05"));
		othersAccountBean.setDateOpenedOrDisbursed(segmentTagMap.get("08"));
		othersAccountBean.setDateOfLastPayment(segmentTagMap.get("09"));
		othersAccountBean.setDateClosed(segmentTagMap.get("10"));
		othersAccountBean.setDateReportedAndCertified(segmentTagMap.get("11"));
		othersAccountBean.setCreditLimitOrHighCreditOrSanctionedAmount(segmentTagMap.get("12"));
		othersAccountBean.setCurrentBalance(segmentTagMap.get("13"));
		othersAccountBean.setAmountOverdue(segmentTagMap.get("14"));

		othersAccountBean.setPaymentHistory1(segmentTagMap.get("28"));
		othersAccountBean.setPaymentHistory2(segmentTagMap.get("29"));
		othersAccountBean.setPaymentHistoryStartDate(segmentTagMap.get("30"));
		othersAccountBean.setPaymentHistoryEndDate(segmentTagMap.get("31"));
		othersAccountBean.setSuitFiledOrWilfulDefault(segmentTagMap.get("32"));
		othersAccountBean.setWrittenOffAndSettledStatus(segmentTagMap.get("33"));

		othersAccountBean.setDateOfEntryForErrorCode(segmentTagMap.get("80"));
		othersAccountBean.setErrorCode(segmentTagMap.get("82"));
		othersAccountBean.setDateOfEntryForCibilRemarksCode(segmentTagMap.get("83"));
		othersAccountBean.setCibilRemarksCode(segmentTagMap.get("84"));
		othersAccountBean.setDateOfEntryForErrorOrDisputeRemarksCode(segmentTagMap.get("85"));
		othersAccountBean.setErrorOrDisputeRemarksCode1(segmentTagMap.get("86"));
		othersAccountBean.setErrorOrDisputeRemarksCode2(segmentTagMap.get("87"));

		return othersAccountBean;
	}

	public OwnAccountBean getOwnAccountBean(Map<String, String> segmentTagMap) {
		OwnAccountBean ownAccountBean = new OwnAccountBean();

		ownAccountBean.setReportingMemberShortName(segmentTagMap.get("02"));
		ownAccountBean.setAccountNumber(segmentTagMap.get("03"));
		ownAccountBean.setAccountType(segmentTagMap.get("04"));
		ownAccountBean.setOwnershipIndicator(segmentTagMap.get("05"));
		ownAccountBean.setDateOpenedOrDisbursed(segmentTagMap.get("08"));
		ownAccountBean.setDateOfLastPayment(segmentTagMap.get("09"));
		ownAccountBean.setDateClosed(segmentTagMap.get("10"));
		ownAccountBean.setDateReportedAndCertified(segmentTagMap.get("11"));
		ownAccountBean.setHighCreditOrSanctionedAmount(segmentTagMap.get("12"));
		ownAccountBean.setCurrentBalance(segmentTagMap.get("13"));
		ownAccountBean.setAmountOverdue(segmentTagMap.get("14"));

		ownAccountBean.setPaymentHistory1(segmentTagMap.get("28"));
		ownAccountBean.setPaymentHistory2(segmentTagMap.get("29"));
		ownAccountBean.setPaymentHistoryStartDate(segmentTagMap.get("30"));
		ownAccountBean.setPaymentHistoryEndDate(segmentTagMap.get("31"));
		ownAccountBean.setSuitFiledOrWilfulDefault(segmentTagMap.get("32"));
		ownAccountBean.setWrittenOffAndSettledStatus(segmentTagMap.get("33"));
		ownAccountBean.setValueOfCollateral(segmentTagMap.get("34"));
		ownAccountBean.setTypeOfCollateral(segmentTagMap.get("35"));
		ownAccountBean.setCreditLimit(segmentTagMap.get("36"));
		ownAccountBean.setCashLimit(segmentTagMap.get("37"));
		ownAccountBean.setRateOfInterest(segmentTagMap.get("38"));
		ownAccountBean.setRepaymentTenure(segmentTagMap.get("39"));
		ownAccountBean.setEMIAmount(segmentTagMap.get("40"));
		ownAccountBean.setWrittenOffAmountTotal(segmentTagMap.get("41"));
		ownAccountBean.setWrittenOffAmountPrincipal(segmentTagMap.get("42"));
		ownAccountBean.setSettlementAmount(segmentTagMap.get("43"));
		ownAccountBean.setPaymentFrequency(segmentTagMap.get("44"));
		ownAccountBean.setActualPaymentAmount(segmentTagMap.get("45"));

		ownAccountBean.setDateOfEntryForErrorCode(segmentTagMap.get("80"));
		ownAccountBean.setErrorCode(segmentTagMap.get("82"));
		ownAccountBean.setDateOfEntryForCibilRemarksCode(segmentTagMap.get("83"));
		ownAccountBean.setCibilRemarksCode(segmentTagMap.get("84"));
		ownAccountBean.setDateOfEntryForErrorOrDisputeRemarksCode(segmentTagMap.get("85"));
		ownAccountBean.setErrorOrDisputeRemarksCode1(segmentTagMap.get("86"));
		ownAccountBean.setErrorOrDisputeRemarksCode2(segmentTagMap.get("87"));

		return ownAccountBean;
	}

	public Map<String, String> parseAccountSegment(String input, int i) {

		Map<String, String> segmentTagMap = new HashMap<String, String>();
		StringBuilder temp = new StringBuilder(input.replace("TL04T" + String.format("%03d", i), ""));

		while (temp.length() > 0) {

			String fieldTag = temp.substring(0, 2);

			String fieldValue = null, toBeChanged = null;
			switch (fieldTag) {

			case "02":
				fieldValue = parseFieldValue(temp.toString());
				toBeChanged = fieldTag + String.format("%02d", fieldValue.length()) + fieldValue;
				temp.replace(0, toBeChanged.length(), "");
				segmentTagMap.put("02", fieldValue);
				break;

			case "03":
				fieldValue = parseFieldValue(temp.toString());
				toBeChanged = fieldTag + String.format("%02d", fieldValue.length()) + fieldValue;
				temp.replace(0, toBeChanged.length(), "");
				segmentTagMap.put("03", fieldValue);
				break;

			case "04":
				fieldValue = parseFieldValue(temp.toString());
				toBeChanged = fieldTag + String.format("%02d", fieldValue.length()) + fieldValue;
				temp.replace(0, toBeChanged.length(), "");
				segmentTagMap.put("04", fieldValue);
				break;

			case "05":
				fieldValue = parseFieldValue(temp.toString());
				toBeChanged = fieldTag + String.format("%02d", fieldValue.length()) + fieldValue;
				temp.replace(0, toBeChanged.length(), "");
				segmentTagMap.put("05", fieldValue);
				break;

			case "08":
				fieldValue = parseFieldValue(temp.toString());
				toBeChanged = fieldTag + String.format("%02d", fieldValue.length()) + fieldValue;
				temp.replace(0, toBeChanged.length(), "");
				segmentTagMap.put("08", fieldValue);
				break;

			case "09":
				fieldValue = parseFieldValue(temp.toString());
				toBeChanged = fieldTag + String.format("%02d", fieldValue.length()) + fieldValue;
				temp.replace(0, toBeChanged.length(), "");
				segmentTagMap.put("09", fieldValue);
				break;

			case "10":
				fieldValue = parseFieldValue(temp.toString());
				toBeChanged = fieldTag + String.format("%02d", fieldValue.length()) + fieldValue;
				temp.replace(0, toBeChanged.length(), "");
				segmentTagMap.put("10", fieldValue);
				break;

			case "11":
				fieldValue = parseFieldValue(temp.toString());
				toBeChanged = fieldTag + String.format("%02d", fieldValue.length()) + fieldValue;
				temp.replace(0, toBeChanged.length(), "");
				segmentTagMap.put("11", fieldValue);
				break;

			case "12":
				fieldValue = parseFieldValue(temp.toString());
				toBeChanged = fieldTag + String.format("%02d", fieldValue.length()) + fieldValue;
				temp.replace(0, toBeChanged.length(), "");
				segmentTagMap.put("12", fieldValue);
				break;

			case "13":
				fieldValue = parseFieldValue(temp.toString());
				toBeChanged = fieldTag + String.format("%02d", fieldValue.length()) + fieldValue;
				temp.replace(0, toBeChanged.length(), "");
				segmentTagMap.put("13", fieldValue);
				break;

			case "14":
				fieldValue = parseFieldValue(temp.toString());
				toBeChanged = fieldTag + String.format("%02d", fieldValue.length()) + fieldValue;
				temp.replace(0, toBeChanged.length(), "");
				segmentTagMap.put("14", fieldValue);
				break;

			case "28":
				fieldValue = parseFieldValue(temp.toString());
				toBeChanged = fieldTag + String.format("%02d", fieldValue.length()) + fieldValue;
				temp.replace(0, toBeChanged.length(), "");
				segmentTagMap.put("28", fieldValue);
				break;

			case "29":
				fieldValue = parseFieldValue(temp.toString());
				toBeChanged = fieldTag + String.format("%02d", fieldValue.length()) + fieldValue;
				temp.replace(0, toBeChanged.length(), "");
				segmentTagMap.put("29", fieldValue);
				break;

			case "30":
				fieldValue = parseFieldValue(temp.toString());
				toBeChanged = fieldTag + String.format("%02d", fieldValue.length()) + fieldValue;
				temp.replace(0, toBeChanged.length(), "");
				segmentTagMap.put("30", fieldValue);
				break;

			case "31":
				fieldValue = parseFieldValue(temp.toString());
				toBeChanged = fieldTag + String.format("%02d", fieldValue.length()) + fieldValue;
				temp.replace(0, toBeChanged.length(), "");
				segmentTagMap.put("31", fieldValue);
				break;

			case "32":
				fieldValue = parseFieldValue(temp.toString());
				toBeChanged = fieldTag + String.format("%02d", fieldValue.length()) + fieldValue;
				temp.replace(0, toBeChanged.length(), "");
				segmentTagMap.put("32", fieldValue);
				break;

			case "33":
				fieldValue = parseFieldValue(temp.toString());
				toBeChanged = fieldTag + String.format("%02d", fieldValue.length()) + fieldValue;
				temp.replace(0, toBeChanged.length(), "");
				segmentTagMap.put("33", fieldValue);
				break;

			case "34":
				fieldValue = parseFieldValue(temp.toString());
				toBeChanged = fieldTag + String.format("%02d", fieldValue.length()) + fieldValue;
				temp.replace(0, toBeChanged.length(), "");
				segmentTagMap.put("34", fieldValue);
				break;

			case "35":
				fieldValue = parseFieldValue(temp.toString());
				toBeChanged = fieldTag + String.format("%02d", fieldValue.length()) + fieldValue;
				temp.replace(0, toBeChanged.length(), "");
				segmentTagMap.put("35", fieldValue);
				break;

			case "36":
				fieldValue = parseFieldValue(temp.toString());
				toBeChanged = fieldTag + String.format("%02d", fieldValue.length()) + fieldValue;
				temp.replace(0, toBeChanged.length(), "");
				segmentTagMap.put("36", fieldValue);
				break;

			case "37":
				fieldValue = parseFieldValue(temp.toString());
				toBeChanged = fieldTag + String.format("%02d", fieldValue.length()) + fieldValue;
				temp.replace(0, toBeChanged.length(), "");
				segmentTagMap.put("37", fieldValue);
				break;

			case "38":
				fieldValue = parseFieldValue(temp.toString());
				toBeChanged = fieldTag + String.format("%02d", fieldValue.length()) + fieldValue;
				temp.replace(0, toBeChanged.length(), "");
				segmentTagMap.put("38", fieldValue);
				break;

			case "39":
				fieldValue = parseFieldValue(temp.toString());
				toBeChanged = fieldTag + String.format("%02d", fieldValue.length()) + fieldValue;
				temp.replace(0, toBeChanged.length(), "");
				segmentTagMap.put("39", fieldValue);
				break;

			case "40":
				fieldValue = parseFieldValue(temp.toString());
				toBeChanged = fieldTag + String.format("%02d", fieldValue.length()) + fieldValue;
				temp.replace(0, toBeChanged.length(), "");
				segmentTagMap.put("40", fieldValue);
				break;

			case "41":
				fieldValue = parseFieldValue(temp.toString());
				toBeChanged = fieldTag + String.format("%02d", fieldValue.length()) + fieldValue;
				temp.replace(0, toBeChanged.length(), "");
				segmentTagMap.put("41", fieldValue);
				break;

			case "42":
				fieldValue = parseFieldValue(temp.toString());
				toBeChanged = fieldTag + String.format("%02d", fieldValue.length()) + fieldValue;
				temp.replace(0, toBeChanged.length(), "");
				segmentTagMap.put("42", fieldValue);
				break;

			case "43":
				fieldValue = parseFieldValue(temp.toString());
				toBeChanged = fieldTag + String.format("%02d", fieldValue.length()) + fieldValue;
				temp.replace(0, toBeChanged.length(), "");
				segmentTagMap.put("43", fieldValue);
				break;

			case "44":
				fieldValue = parseFieldValue(temp.toString());
				toBeChanged = fieldTag + String.format("%02d", fieldValue.length()) + fieldValue;
				temp.replace(0, toBeChanged.length(), "");
				segmentTagMap.put("44", fieldValue);
				break;

			case "45":
				fieldValue = parseFieldValue(temp.toString());
				toBeChanged = fieldTag + String.format("%02d", fieldValue.length()) + fieldValue;
				temp.replace(0, toBeChanged.length(), "");
				segmentTagMap.put("45", fieldValue);
				break;

			case "80":
				fieldValue = parseFieldValue(temp.toString());
				toBeChanged = fieldTag + String.format("%02d", fieldValue.length()) + fieldValue;
				temp.replace(0, toBeChanged.length(), "");
				segmentTagMap.put("80", fieldValue);
				break;

			case "82":
				fieldValue = parseFieldValue(temp.toString());
				toBeChanged = fieldTag + String.format("%02d", fieldValue.length()) + fieldValue;
				temp.replace(0, toBeChanged.length(), "");
				segmentTagMap.put("82", fieldValue);
				break;

			case "83":
				fieldValue = parseFieldValue(temp.toString());
				toBeChanged = fieldTag + String.format("%02d", fieldValue.length()) + fieldValue;
				temp.replace(0, toBeChanged.length(), "");
				segmentTagMap.put("83", fieldValue);
				break;

			case "84":
				fieldValue = parseFieldValue(temp.toString());
				toBeChanged = fieldTag + String.format("%02d", fieldValue.length()) + fieldValue;
				temp.replace(0, toBeChanged.length(), "");
				segmentTagMap.put("84", fieldValue);
				break;

			case "85":
				fieldValue = parseFieldValue(temp.toString());
				toBeChanged = fieldTag + String.format("%02d", fieldValue.length()) + fieldValue;
				temp.replace(0, toBeChanged.length(), "");
				segmentTagMap.put("85", fieldValue);
				break;

			case "86":
				fieldValue = parseFieldValue(temp.toString());
				toBeChanged = fieldTag + String.format("%02d", fieldValue.length()) + fieldValue;
				temp.replace(0, toBeChanged.length(), "");
				segmentTagMap.put("86", fieldValue);
				break;

			case "87":
				fieldValue = parseFieldValue(temp.toString());
				toBeChanged = fieldTag + String.format("%02d", fieldValue.length()) + fieldValue;
				temp.replace(0, toBeChanged.length(), "");
				segmentTagMap.put("87", fieldValue);
				break;
			}
		}

		return segmentTagMap;
	}

	public EndSegment parseEndSegment(String input) {
		EndSegment endSegment = new EndSegment();
		int i = 4;
		String lengthOfTransmission = input.substring(i, i += 7).trim();
		endSegment.setLenOfRecord(Integer.parseInt(lengthOfTransmission));
		return endSegment;
	}

	public ConsumerDisputeRemarksSegment parseConsumerDisputeRemarksSegment(String input) {

		ConsumerDisputeRemarksSegment consumerDisputeRemarksSegment = new ConsumerDisputeRemarksSegment();
		StringBuilder temp = new StringBuilder(input.replace("DR03D01", ""));
		while (temp.length() > 0) {

			String fieldTag = temp.substring(0, 2);
			String fieldValue = null, toBeChanged = null;
			switch (fieldTag) {
			case "01":
				fieldValue = parseFieldValue(temp.toString());
				toBeChanged = fieldTag + String.format("%02d", fieldValue.length()) + fieldValue;
				temp.replace(0, toBeChanged.length(), "");
				consumerDisputeRemarksSegment.setDateOfEntry(fieldValue);
				break;

			case "02":
				fieldValue = parseFieldValue(temp.toString());
				toBeChanged = fieldTag + String.format("%02d", fieldValue.length()) + fieldValue;
				temp.replace(0, toBeChanged.length(), "");
				consumerDisputeRemarksSegment.setDisputeRemarksLine1(fieldValue);
				break;

			case "03":
				fieldValue = parseFieldValue(temp.toString());
				toBeChanged = fieldTag + String.format("%02d", fieldValue.length()) + fieldValue;
				temp.replace(0, toBeChanged.length(), "");
				consumerDisputeRemarksSegment.setDisputeRemarksLine2(fieldValue);
				break;

			case "04":
				fieldValue = parseFieldValue(temp.toString());
				toBeChanged = fieldTag + String.format("%02d", fieldValue.length()) + fieldValue;
				temp.replace(0, toBeChanged.length(), "");
				consumerDisputeRemarksSegment.setDisputeRemarksLine3(fieldValue);
				break;

			case "05":
				fieldValue = parseFieldValue(temp.toString());
				toBeChanged = fieldTag + String.format("%02d", fieldValue.length()) + fieldValue;
				temp.replace(0, toBeChanged.length(), "");
				consumerDisputeRemarksSegment.setDisputeRemarksLine4(fieldValue);
				break;

			case "06":
				fieldValue = parseFieldValue(temp.toString());
				toBeChanged = fieldTag + String.format("%02d", fieldValue.length()) + fieldValue;
				temp.replace(0, toBeChanged.length(), "");
				consumerDisputeRemarksSegment.setDisputeRemarksLine5(fieldValue);
				break;

			case "07":
				fieldValue = parseFieldValue(temp.toString());
				toBeChanged = fieldTag + String.format("%02d", fieldValue.length()) + fieldValue;
				temp.replace(0, toBeChanged.length(), "");
				consumerDisputeRemarksSegment.setDisputeRemarksLine6(fieldValue);
				break;
			}
		}

		return consumerDisputeRemarksSegment;
	}

	public EnquiryBean parseEnquirySegment(String input, int i) {

		EnquiryBean enquiryBean = new EnquiryBean();
		StringBuilder temp = new StringBuilder(input.replace("IQ04I" + String.format("%03d", i), ""));
		while (temp.length() > 0) {

			String fieldTag = temp.substring(0, 2);

			String fieldValue = null, toBeChanged = null;
			switch (fieldTag) {
			case "01":
				fieldValue = parseFieldValue(temp.toString());
				toBeChanged = fieldTag + String.format("%02d", fieldValue.length()) + fieldValue;
				temp.replace(0, toBeChanged.length(), "");
				enquiryBean.setDateOfEnquiry(fieldValue);
				break;

			case "04":
				fieldValue = parseFieldValue(temp.toString());
				toBeChanged = fieldTag + String.format("%02d", fieldValue.length()) + fieldValue;
				temp.replace(0, toBeChanged.length(), "");
				enquiryBean.setEnquiringMemberShortName(fieldValue);
				break;

			case "05":
				fieldValue = parseFieldValue(temp.toString());
				toBeChanged = fieldTag + String.format("%02d", fieldValue.length()) + fieldValue;
				temp.replace(0, toBeChanged.length(), "");
				enquiryBean.setEnquiryPurpose(fieldValue);
				break;

			case "06":
				fieldValue = parseFieldValue(temp.toString());
				toBeChanged = fieldTag + String.format("%02d", fieldValue.length()) + fieldValue;
				temp.replace(0, toBeChanged.length(), "");
				enquiryBean.setEnquiryAmount(fieldValue);
				break;

			}
		}

		return enquiryBean;
	}

	public String getAccountSegmentReportingMemberShortName(String input, int i) {
		String temp = input.replace("TL04T" + String.format("%03d", i), "");
		String fieldValue = null;

		if (temp.length() > 0) {
			String fieldTag = temp.substring(0, 2);
			switch (fieldTag) {
			case "02":
				fieldValue = parseFieldValue(temp);
				break;
			}
		}
		return fieldValue;
	}

	public AddressBean parseAddressSegment(String input, int i) {
		AddressBean addressBean = new AddressBean();
		StringBuilder temp = new StringBuilder(input.replace("PA03A" + String.format("%02d", i), ""));
		while (temp.length() > 0) {

			String fieldTag = temp.substring(0, 2);

			String fieldValue = null, toBeChanged = null;
			switch (fieldTag) {
			case "01":
				fieldValue = parseFieldValue(temp.toString());
				toBeChanged = fieldTag + String.format("%02d", fieldValue.length()) + fieldValue;
				temp.replace(0, toBeChanged.length(), "");
				addressBean.setAddLine1(fieldValue);
				break;

			case "02":
				fieldValue = parseFieldValue(temp.toString());
				toBeChanged = fieldTag + String.format("%02d", fieldValue.length()) + fieldValue;
				temp.replace(0, toBeChanged.length(), "");
				addressBean.setAddLine2(fieldValue);
				break;

			case "03":
				fieldValue = parseFieldValue(temp.toString());
				toBeChanged = fieldTag + String.format("%02d", fieldValue.length()) + fieldValue;
				temp.replace(0, toBeChanged.length(), "");
				addressBean.setAddLine3(fieldValue);
				break;

			case "04":
				fieldValue = parseFieldValue(temp.toString());
				toBeChanged = fieldTag + String.format("%02d", fieldValue.length()) + fieldValue;
				temp.replace(0, toBeChanged.length(), "");
				addressBean.setAddLine4(fieldValue);
				break;

			case "05":
				fieldValue = parseFieldValue(temp.toString());
				toBeChanged = fieldTag + String.format("%02d", fieldValue.length()) + fieldValue;
				temp.replace(0, toBeChanged.length(), "");
				addressBean.setAddLine5(fieldValue);
				break;

			case "06":
				fieldValue = parseFieldValue(temp.toString());
				toBeChanged = fieldTag + String.format("%02d", fieldValue.length()) + fieldValue;
				temp.replace(0, toBeChanged.length(), "");
				addressBean.setStateCode(fieldValue);
				break;

			case "07":
				fieldValue = parseFieldValue(temp.toString());
				toBeChanged = fieldTag + String.format("%02d", fieldValue.length()) + fieldValue;
				temp.replace(0, toBeChanged.length(), "");
				addressBean.setPinCode(fieldValue);
				break;

			case "08":
				fieldValue = parseFieldValue(temp.toString());
				toBeChanged = fieldTag + String.format("%02d", fieldValue.length()) + fieldValue;
				temp.replace(0, toBeChanged.length(), "");
				addressBean.setAddCategory(fieldValue);
				break;

			case "09":
				fieldValue = parseFieldValue(temp.toString());
				toBeChanged = fieldTag + String.format("%02d", fieldValue.length()) + fieldValue;
				temp.replace(0, toBeChanged.length(), "");
				addressBean.setResidenceCode(fieldValue);
				break;

			case "10":
				fieldValue = parseFieldValue(temp.toString());
				toBeChanged = fieldTag + String.format("%02d", fieldValue.length()) + fieldValue;
				temp.replace(0, toBeChanged.length(), "");
				addressBean.setDateReported(fieldValue);
				break;

			case "11":
				fieldValue = parseFieldValue(temp.toString());
				toBeChanged = fieldTag + String.format("%02d", fieldValue.length()) + fieldValue;
				temp.replace(0, toBeChanged.length(), "");
				addressBean.setMemberShortName(fieldValue);
				break;

			case "90":
				fieldValue = parseFieldValue(temp.toString());
				toBeChanged = fieldTag + String.format("%02d", fieldValue.length()) + fieldValue;
				temp.replace(0, toBeChanged.length(), "");
				addressBean.setEnrichedThroughEnquiry(fieldValue);
				break;

			}
		}

		return addressBean;

	}

	public ScoreBean parseScoreSegment(String input, int i) {

		ScoreBean scoreBean = new ScoreBean();
		StringBuilder temp = new StringBuilder(input);

		while (temp.length() > 0) {

			String fieldTag = temp.substring(0, 2);

			String fieldValue = null, toBeChanged = null;

			switch (fieldTag) {
			case "SC":
				fieldValue = parseFieldValue(temp.toString());
				toBeChanged = fieldTag + String.format("%02d", fieldValue.length()) + fieldValue;
				temp.replace(0, toBeChanged.length(), "");
				scoreBean.setScoreName(fieldValue);
				break;

			case "01":
				fieldValue = parseFieldValue(temp.toString());
				toBeChanged = fieldTag + String.format("%02d", fieldValue.length()) + fieldValue;
				temp.replace(0, toBeChanged.length(), "");
				scoreBean.setScoreCardName(fieldValue);
				break;

			case "02":
				fieldValue = parseFieldValue(temp.toString());
				toBeChanged = fieldTag + String.format("%02d", fieldValue.length()) + fieldValue;
				temp.replace(0, toBeChanged.length(), "");
				scoreBean.setScoreCardVersion(fieldValue);
				break;

			case "03":
				fieldValue = parseFieldValue(temp.toString());
				toBeChanged = fieldTag + String.format("%02d", fieldValue.length()) + fieldValue;
				temp.replace(0, toBeChanged.length(), "");
				scoreBean.setScoreDate(fieldValue);
				break;

			case "04":
				fieldValue = parseFieldValue(temp.toString());
				toBeChanged = fieldTag + String.format("%02d", fieldValue.length()) + fieldValue;
				temp.replace(0, toBeChanged.length(), "");
				scoreBean.setScore(fieldValue);
				break;

			case "05":
				fieldValue = parseFieldValue(temp.toString());
				toBeChanged = fieldTag + String.format("%02d", fieldValue.length()) + fieldValue;
				temp.replace(0, toBeChanged.length(), "");
				scoreBean.setExclusionCode1(fieldValue);
				break;

			case "06":
				fieldValue = parseFieldValue(temp.toString());
				toBeChanged = fieldTag + String.format("%02d", fieldValue.length()) + fieldValue;
				temp.replace(0, toBeChanged.length(), "");
				scoreBean.setExclusionCode2(fieldValue);
				break;

			case "07":
				fieldValue = parseFieldValue(temp.toString());
				input = input.replace(fieldTag + String.format("%02d", fieldValue.length()) + fieldValue, "");
				toBeChanged = fieldTag + String.format("%02d", fieldValue.length()) + fieldValue;
				temp.replace(0, toBeChanged.length(), "");
				scoreBean.setExclusionCode3(fieldValue);
				break;

			case "08":
				fieldValue = parseFieldValue(temp.toString());
				toBeChanged = fieldTag + String.format("%02d", fieldValue.length()) + fieldValue;
				temp.replace(0, toBeChanged.length(), "");
				scoreBean.setExclusionCode4(fieldValue);
				break;

			case "09":
				fieldValue = parseFieldValue(temp.toString());
				toBeChanged = fieldTag + String.format("%02d", fieldValue.length()) + fieldValue;
				temp.replace(0, toBeChanged.length(), "");
				scoreBean.setExclusionCode5(fieldValue);
				break;

			case "10":
				fieldValue = parseFieldValue(temp.toString());
				toBeChanged = fieldTag + String.format("%02d", fieldValue.length()) + fieldValue;
				temp.replace(0, toBeChanged.length(), "");
				scoreBean.setExclusionCode6(fieldValue);
				break;

			case "11":
				fieldValue = parseFieldValue(temp.toString());
				toBeChanged = fieldTag + String.format("%02d", fieldValue.length()) + fieldValue;
				temp.replace(0, toBeChanged.length(), "");
				scoreBean.setExclusionCode7(fieldValue);
				break;

			case "12":
				fieldValue = parseFieldValue(temp.toString());
				toBeChanged = fieldTag + String.format("%02d", fieldValue.length()) + fieldValue;
				temp.replace(0, toBeChanged.length(), "");
				scoreBean.setExclusionCode8(fieldValue);
				break;

			case "13":
				fieldValue = parseFieldValue(temp.toString());
				toBeChanged = fieldTag + String.format("%02d", fieldValue.length()) + fieldValue;
				temp.replace(0, toBeChanged.length(), "");
				scoreBean.setExclusionCode9(fieldValue);
				break;

			case "14":
				fieldValue = parseFieldValue(temp.toString());
				toBeChanged = fieldTag + String.format("%02d", fieldValue.length()) + fieldValue;
				temp.replace(0, toBeChanged.length(), "");
				scoreBean.setExclusionCode10(fieldValue);
				break;

			case "25":
				fieldValue = parseFieldValue(temp.toString());
				toBeChanged = fieldTag + String.format("%02d", fieldValue.length()) + fieldValue;
				temp.replace(0, toBeChanged.length(), "");
				scoreBean.setReasonCode1(fieldValue);
				break;

			case "26":
				fieldValue = parseFieldValue(temp.toString());
				toBeChanged = fieldTag + String.format("%02d", fieldValue.length()) + fieldValue;
				temp.replace(0, toBeChanged.length(), "");
				scoreBean.setReasonCode2(fieldValue);
				break;

			case "27":
				fieldValue = parseFieldValue(temp.toString());
				toBeChanged = fieldTag + String.format("%02d", fieldValue.length()) + fieldValue;
				temp.replace(0, toBeChanged.length(), "");
				scoreBean.setReasonCode3(fieldValue);
				break;

			case "28":
				fieldValue = parseFieldValue(temp.toString());
				toBeChanged = fieldTag + String.format("%02d", fieldValue.length()) + fieldValue;
				temp.replace(0, toBeChanged.length(), "");
				scoreBean.setReasonCode4(fieldValue);
				break;

			case "29":
				fieldValue = parseFieldValue(temp.toString());
				toBeChanged = fieldTag + String.format("%02d", fieldValue.length()) + fieldValue;
				temp.replace(0, toBeChanged.length(), "");
				scoreBean.setReasonCode5(fieldValue);
				break;

			case "70":
				fieldValue = parseFieldValue(temp.toString());
				toBeChanged = fieldTag + String.format("%02d", fieldValue.length()) + fieldValue;
				temp.replace(0, toBeChanged.length(), "");
				scoreBean.setErrorCode(fieldValue);
				break;

			}
		}
		return scoreBean;

	}

	public AccountNumberBean parseAccountNumberSegment(String input, int i) {

		AccountNumberBean accNumBean = new AccountNumberBean();
		StringBuilder temp = new StringBuilder(input.replace("PI03I" + String.format("%02d", i), ""));

		while (temp.length() > 0) {

			String fieldTag = temp.substring(0, 2);

			String fieldValue = null, toBeChanged = null;

			switch (fieldTag) {
			case "01":
				fieldValue = parseFieldValue(temp.toString());
				toBeChanged = fieldTag + String.format("%02d", fieldValue.length()) + fieldValue;
				temp.replace(0, toBeChanged.length(), "");
				accNumBean.setAccountNumber(fieldValue);
				break;
			}
		}
		return accNumBean;

	}

	public EmploymentSegment parseEmploymentSegment(String input) {

		EmploymentSegment employmentSegment = new EmploymentSegment();
		StringBuilder temp = new StringBuilder(input.replace("EM03E01", ""));

		while (temp.length() > 0) {

			String fieldTag = temp.substring(0, 2);

			String fieldValue = null, toBeChanged = null;
			switch (fieldTag) {
			case "01":
				fieldValue = parseFieldValue(temp.toString());
				toBeChanged = fieldTag + String.format("%02d", fieldValue.length()) + fieldValue;
				temp.replace(0, toBeChanged.length(), "");
				employmentSegment.setAccountType(fieldValue);
				break;

			case "02":
				fieldValue = parseFieldValue(temp.toString());
				toBeChanged = fieldTag + String.format("%02d", fieldValue.length()) + fieldValue;
				temp.replace(0, toBeChanged.length(), "");
				employmentSegment.setDateReportedAndCertified(fieldValue);
				break;

			case "03":
				fieldValue = parseFieldValue(temp.toString());
				toBeChanged = fieldTag + String.format("%02d", fieldValue.length()) + fieldValue;
				temp.replace(0, toBeChanged.length(), "");
				employmentSegment.setOccupationCode(fieldValue);
				break;

			case "04":
				fieldValue = parseFieldValue(temp.toString());
				toBeChanged = fieldTag + String.format("%02d", fieldValue.length()) + fieldValue;
				temp.replace(0, toBeChanged.length(), "");
				employmentSegment.setIncome(fieldValue);
				break;

			case "05":
				fieldValue = parseFieldValue(temp.toString());
				toBeChanged = fieldTag + String.format("%02d", fieldValue.length()) + fieldValue;
				temp.replace(0, toBeChanged.length(), "");
				employmentSegment.setNetOrGrossIncomeIndicator(fieldValue);
				break;

			case "06":
				fieldValue = parseFieldValue(temp.toString());
				toBeChanged = fieldTag + String.format("%02d", fieldValue.length()) + fieldValue;
				temp.replace(0, toBeChanged.length(), "");
				employmentSegment.setMonthlyOrAnnualIncomeIndicator(fieldValue);
				break;

			case "80":
				fieldValue = parseFieldValue(temp.toString());
				toBeChanged = fieldTag + String.format("%02d", fieldValue.length()) + fieldValue;
				temp.replace(0, toBeChanged.length(), "");
				employmentSegment.setDateOfEntryForErrorCode(fieldValue);
				break;

			case "82":
				fieldValue = parseFieldValue(temp.toString());
				toBeChanged = fieldTag + String.format("%02d", fieldValue.length()) + fieldValue;
				temp.replace(0, toBeChanged.length(), "");
				employmentSegment.setErrorCode(fieldValue);
				break;

			case "83":
				fieldValue = parseFieldValue(temp.toString());
				toBeChanged = fieldTag + String.format("%02d", fieldValue.length()) + fieldValue;
				temp.replace(0, toBeChanged.length(), "");
				employmentSegment.setDateOfEntryForCibilRemarksCode(fieldValue);
				break;

			case "84":
				fieldValue = parseFieldValue(temp.toString());
				toBeChanged = fieldTag + String.format("%02d", fieldValue.length()) + fieldValue;
				temp.replace(0, toBeChanged.length(), "");
				employmentSegment.setCibilRemarksCode(fieldValue);
				break;

			case "85":
				fieldValue = parseFieldValue(temp.toString());
				toBeChanged = fieldTag + String.format("%02d", fieldValue.length()) + fieldValue;
				temp.replace(0, toBeChanged.length(), "");
				employmentSegment.setDateOfEntryForErrorOrDisputeRemarksCode(fieldValue);
				break;

			case "86":
				fieldValue = parseFieldValue(temp.toString());
				toBeChanged = fieldTag + String.format("%02d", fieldValue.length()) + fieldValue;
				temp.replace(0, toBeChanged.length(), "");
				employmentSegment.setErrorOrDisputeRemarksCode1(fieldValue);
				break;

			case "87":
				fieldValue = parseFieldValue(temp.toString());
				toBeChanged = fieldTag + String.format("%02d", fieldValue.length()) + fieldValue;
				temp.replace(0, toBeChanged.length(), "");
				employmentSegment.setErrorOrDisputeRemarksCode2(fieldValue);
				break;
			}
		}

		return employmentSegment;

	}

	public String parseEmailContactSegment(String input, int i) {

		StringBuilder temp = new StringBuilder(input.replace("EC03C" + String.format("%02d", i), ""));
		String fieldValue = null, toBeChanged = null;

		while (temp.length() > 0) {

			String fieldTag = temp.substring(0, 2);

			switch (fieldTag) {
			case "01":
				fieldValue = parseFieldValue(temp.toString());
				toBeChanged = fieldTag + String.format("%02d", fieldValue.length()) + fieldValue;
				temp.replace(0, toBeChanged.length(), "");
				break;
			}
		}
		return fieldValue;

	}

	public TelephoneBean parseTelephoneSegment(String input, int i) {

		TelephoneBean telBean = new TelephoneBean();
		StringBuilder temp = new StringBuilder(input.replace("PT03T" + String.format("%02d", i), ""));
		while (temp.length() > 0) {

			String fieldTag = temp.substring(0, 2);

			String fieldValue = null, toBeChanged = null;
			switch (fieldTag) {
			case "01":
				fieldValue = parseFieldValue(temp.toString());
				toBeChanged = fieldTag + String.format("%02d", fieldValue.length()) + fieldValue;
				temp.replace(0, toBeChanged.length(), "");
				telBean.setTelephoneNumber(fieldValue);
				break;

			case "02":
				fieldValue = parseFieldValue(temp.toString());
				toBeChanged = fieldTag + String.format("%02d", fieldValue.length()) + fieldValue;
				temp.replace(0, toBeChanged.length(), "");
				telBean.setTelephoneExt(fieldValue);
				break;

			case "03":
				fieldValue = parseFieldValue(temp.toString());
				toBeChanged = fieldTag + String.format("%02d", fieldValue.length()) + fieldValue;
				temp.replace(0, toBeChanged.length(), "");
				telBean.setTelephoneType(fieldValue);
				break;

			case "90":
				fieldValue = parseFieldValue(temp.toString());
				toBeChanged = fieldTag + String.format("%02d", fieldValue.length()) + fieldValue;
				temp.replace(0, toBeChanged.length(), "");
				telBean.setEnrichedThroughEnquiry(fieldValue);
				break;
			}
		}
		return telBean;

	}

	public IDBean parseIdentificationSegment(String identification, int i) {
		IDBean idBean = new IDBean();
		StringBuilder temp = new StringBuilder(identification.replace("ID03I" + String.format("%02d", i), ""));

		while (temp.length() > 0) {
			String fieldTag = temp.substring(0, 2);
			String fieldValue = null, toBeChanged = null;
			switch (fieldTag) {
			case "01":
				fieldValue = parseFieldValue(temp.toString());
				toBeChanged = fieldTag + String.format("%02d", fieldValue.length()) + fieldValue;
				temp.replace(0, toBeChanged.length(), "");
				idBean.setIdType(fieldValue);
				break;

			case "02":
				fieldValue = parseFieldValue(temp.toString());
				toBeChanged = fieldTag + String.format("%02d", fieldValue.length()) + fieldValue;
				temp.replace(0, toBeChanged.length(), "");
				idBean.setIdNumber(fieldValue);
				break;

			case "03":
				fieldValue = parseFieldValue(temp.toString());
				toBeChanged = fieldTag + String.format("%02d", fieldValue.length()) + fieldValue;
				temp.replace(0, toBeChanged.length(), "");
				idBean.setIssueDate(fieldValue);
				break;

			case "04":
				fieldValue = parseFieldValue(temp.toString());
				toBeChanged = fieldTag + String.format("%02d", fieldValue.length()) + fieldValue;
				temp.replace(0, toBeChanged.length(), "");
				idBean.setExpirationDate(fieldValue);
				break;

			case "90":
				fieldValue = parseFieldValue(temp.toString());
				toBeChanged = fieldTag + String.format("%02d", fieldValue.length()) + fieldValue;
				temp.replace(0, toBeChanged.length(), "");
				idBean.setEnrichedThroughEnquiry(fieldValue);
				break;
			}
		}
		return idBean;

	}

	public NameSegment parseNameSegment(String name) {
		NameSegment nameSegment = new NameSegment();
		StringBuilder temp = new StringBuilder(name.replace("PN03N01", ""));

		while (temp.length() > 0) {

			String fieldTag = temp.substring(0, 2);

			String fieldValue = null, toBeChanged = null;
			switch (fieldTag) {

			case "01":
				fieldValue = parseFieldValue(temp.toString());
				toBeChanged = fieldTag + String.format("%02d", fieldValue.length()) + fieldValue;
				temp.replace(0, toBeChanged.length(), "");
				nameSegment.setfName(fieldValue);
				break;

			case "02":
				fieldValue = parseFieldValue(temp.toString());
				toBeChanged = fieldTag + String.format("%02d", fieldValue.length()) + fieldValue;
				temp.replace(0, toBeChanged.length(), "");
				nameSegment.setmName(fieldValue);
				break;

			case "03":
				fieldValue = parseFieldValue(temp.toString());
				toBeChanged = fieldTag + String.format("%02d", fieldValue.length()) + fieldValue;
				temp.replace(0, toBeChanged.length(), "");
				nameSegment.setlName(fieldValue);
				break;

			case "04":
				fieldValue = parseFieldValue(temp.toString());
				toBeChanged = fieldTag + String.format("%02d", fieldValue.length()) + fieldValue;
				temp.replace(0, toBeChanged.length(), "");
				nameSegment.setNameField4(fieldValue);
				break;

			case "05":
				fieldValue = parseFieldValue(temp.toString());
				toBeChanged = fieldTag + String.format("%02d", fieldValue.length()) + fieldValue;
				temp.replace(0, toBeChanged.length(), "");
				nameSegment.setNameField5(fieldValue);
				break;

			case "07":
				fieldValue = parseFieldValue(temp.toString());
				toBeChanged = fieldTag + String.format("%02d", fieldValue.length()) + fieldValue;
				temp.replace(0, toBeChanged.length(), "");
				nameSegment.setDob(fieldValue);
				break;

			case "08":
				fieldValue = parseFieldValue(temp.toString());
				toBeChanged = fieldTag + String.format("%02d", fieldValue.length()) + fieldValue;
				temp.replace(0, toBeChanged.length(), "");
				nameSegment.setGender(fieldValue);
				break;

			case "80":
				fieldValue = parseFieldValue(temp.toString());
				toBeChanged = fieldTag + String.format("%02d", fieldValue.length()) + fieldValue;
				temp.replace(0, toBeChanged.length(), "");
				nameSegment.setDateOfEntryForErrorCode(fieldValue);
				break;

			case "81":
				fieldValue = parseFieldValue(temp.toString());
				toBeChanged = fieldTag + String.format("%02d", fieldValue.length()) + fieldValue;
				temp.replace(0, toBeChanged.length(), "");
				nameSegment.setErrorSegmentTag(fieldValue);
				break;

			case "82":
				fieldValue = parseFieldValue(temp.toString());
				toBeChanged = fieldTag + String.format("%02d", fieldValue.length()) + fieldValue;
				temp.replace(0, toBeChanged.length(), "");
				nameSegment.setErrorCode(fieldValue);
				break;

			case "83":
				fieldValue = parseFieldValue(temp.toString());
				toBeChanged = fieldTag + String.format("%02d", fieldValue.length()) + fieldValue;
				temp.replace(0, toBeChanged.length(), "");
				nameSegment.setDateOfEntryForCibilRemarksCode(fieldValue);
				break;

			case "84":
				fieldValue = parseFieldValue(temp.toString());
				toBeChanged = fieldTag + String.format("%02d", fieldValue.length()) + fieldValue;
				temp.replace(0, toBeChanged.length(), "");
				nameSegment.setCibilRemarksCode(fieldValue);
				break;

			case "85":
				fieldValue = parseFieldValue(temp.toString());
				toBeChanged = fieldTag + String.format("%02d", fieldValue.length()) + fieldValue;
				temp.replace(0, toBeChanged.length(), "");
				nameSegment.setDateOfEntryForErrorOrDisputeRemarksCode(fieldValue);
				break;

			case "86":
				fieldValue = parseFieldValue(temp.toString());
				toBeChanged = fieldTag + String.format("%02d", fieldValue.length()) + fieldValue;
				temp.replace(0, toBeChanged.length(), "");
				nameSegment.setErrorOrDisputeRemarksCode1(fieldValue);
				break;

			case "87":
				fieldValue = parseFieldValue(temp.toString());
				toBeChanged = fieldTag + String.format("%02d", fieldValue.length()) + fieldValue;
				temp.replace(0, toBeChanged.length(), "");
				nameSegment.setErrorOrDisputeRemarksCode2(fieldValue);
				break;
			}
		}

		return nameSegment;
	}

	public String parseFieldValue(String temp) {
		Integer length = Integer.parseInt(temp.substring(2, 4));
		String fieldValue = temp.substring(4, length + 4);

		return fieldValue;
	}

	public HeaderSegment parseHeaderSegment(String header) {
		HeaderSegment headerSegment = new HeaderSegment();

		int i = 6;
		String memberRefNumber = header.substring(i, i += 25).trim();
		i += 6;
		String enqMemberUserId = header.substring(i, i += 30).trim();
		String subReasonCode = header.substring(i, i += 1).trim();
		String enqControlNumber = header.substring(i, i += 12).trim();
		String dateProcessed = header.substring(i, i += 8).trim();
		String timeProcessed = header.substring(i, header.length()).trim();

		headerSegment.setMemberRefNumber(memberRefNumber);
		headerSegment.setEnqMemberUserId(enqMemberUserId);
		headerSegment.setSubReasonCode(subReasonCode);
		headerSegment.setEnqControlNumber(enqControlNumber);
		headerSegment.setDateProcessed(dateProcessed);
		headerSegment.setTimeProcessed(timeProcessed);

		return headerSegment;
	}
}
