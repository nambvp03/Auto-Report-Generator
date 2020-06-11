package edu.cpp.admissions.util;

import java.text.SimpleDateFormat;
import java.util.*;

import javax.mail.internet.MimeMessage;

import edu.cpp.admissions.bean.StatusType;
import edu.cpp.admissions.data.StatusReportRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import edu.cpp.admissions.bean.AuditReportBean;
import edu.cpp.admissions.data.FirstTimeFreshmenAuditRepo;
import edu.cpp.admissions.data.UpperDivisionTransferAuditRepo;
import edu.cpp.admissions.data.UpperDivisionTransferCompleteAuditRepo;

@Component
public class AuditReportEmailUtil {

	@Autowired
	JavaMailSender sender;

	@Autowired
	UpperDivisionTransferAuditRepo upperDivisionTransferAuditRepo;

	@Autowired
	UpperDivisionTransferCompleteAuditRepo upperDivisionTransferCompleteAuditRepo;

	@Autowired
	FirstTimeFreshmenAuditRepo firstTimeFreshmenAuditRepo;

	@Autowired
	StatusReportRepo statusReportRepo;

	@Value("${spring.mail.username}")
	private String from;

	@Value("${cpp.admissions.to}")
	private String toGeneral;

	String lastWeekSaturday;
	String lastWeekSunday;
	String lastWeekSaturdayM1;
	String lastWeekSundayM1;
	String lastWeekSaturdayM2;
	String lastWeekSundayM2;
	String lastWeekSaturdayM3;
	String lastWeekSundayM3;

	List<Object[]> udtProcessorReport;
	List<Object[]> udtWamReport;
	List<Object[]> ftfProcessorReport;
	List<Object[]> statusReport;


	String initialBody;
	String footer;
	String tableHeader;
	String tableHeaderStatus;

	HashMap<String, AuditReportBean> userAndReportBeanMap;
	LinkedHashMap<String, StatusType> userStatusCountMap = new LinkedHashMap<>();

	public void getDataAndPrepareForEmail() {
		boolean status = false;
		statusReport = statusReportRepo.getReportByStatus();
		udtProcessorReport = upperDivisionTransferAuditRepo.getProcessorCountOfLastWeek();
		udtWamReport = upperDivisionTransferCompleteAuditRepo.getWAMCountOfLastWeek();
		ftfProcessorReport = firstTimeFreshmenAuditRepo.getProcessorCountOfLastWeek();

		Calendar cal = Calendar.getInstance();
		cal.add( Calendar.DAY_OF_WEEK, -(cal.get(Calendar.DAY_OF_WEEK)+6)); 
		lastWeekSunday = cal.get(Calendar.MONTH)+1 + "/" + cal.get(Calendar.DATE);
		cal.add( Calendar.DAY_OF_WEEK, -(cal.get(Calendar.DAY_OF_WEEK)+6)); 
		lastWeekSundayM1 = cal.get(Calendar.MONTH)+1 + "/" + cal.get(Calendar.DATE);
		cal.add( Calendar.DAY_OF_WEEK, -(cal.get(Calendar.DAY_OF_WEEK)+6)); 
		lastWeekSundayM2 = cal.get(Calendar.MONTH)+1 + "/" + cal.get(Calendar.DATE);
		cal.add( Calendar.DAY_OF_WEEK, -(cal.get(Calendar.DAY_OF_WEEK)+6)); 
		lastWeekSundayM3 = cal.get(Calendar.MONTH)+1 + "/" + cal.get(Calendar.DATE);

		cal = Calendar.getInstance();
		cal.add( Calendar.DAY_OF_WEEK, -(cal.get(Calendar.DAY_OF_WEEK))); 
		lastWeekSaturday = cal.get(Calendar.MONTH)+1 + "/" + cal.get(Calendar.DATE);
		cal.add( Calendar.DAY_OF_WEEK, -(cal.get(Calendar.DAY_OF_WEEK))); 
		lastWeekSaturdayM1 = cal.get(Calendar.MONTH)+1 + "/" + cal.get(Calendar.DATE);
		cal.add( Calendar.DAY_OF_WEEK, -(cal.get(Calendar.DAY_OF_WEEK))); 
		lastWeekSaturdayM2 = cal.get(Calendar.MONTH)+1 + "/" + cal.get(Calendar.DATE);
		cal.add( Calendar.DAY_OF_WEEK, -(cal.get(Calendar.DAY_OF_WEEK))); 
		lastWeekSaturdayM3 = cal.get(Calendar.MONTH)+1 + "/" + cal.get(Calendar.DATE);

		String[] toArrayGeneral = toGeneral.split(",");
		String subjectGeneral = "AutoEmail: Completed audit for "
				+ lastWeekSunday + " to " + lastWeekSaturday;
		String emailBodyGeneral = getGeneralHtmlEmailBody();
		status = sendEmail(toArrayGeneral, subjectGeneral, emailBodyGeneral);

//		HashMap<String, String> individualEmailData = generateIndividualEmailDate();
//		for (Map.Entry<String, String> entry : individualEmailData.entrySet()) {
//			//if(!excludeFromEmail.contains(entry.getKey())) {
//				String[] toArrayIndividual = { entry.getKey().toLowerCase() + "@cpp.edu" };
//				//String[] toArrayIndividual = { "" };
//				String emailBodyIndividual = entry.getValue();
//				//String[] toArrayIndividual = { "crhayden@cpp.edu" };
//			System.out.println(toArrayIndividual[0]);
//				//toArrayIndividual[0] = "jspatil@cpp.edu";
//				toArrayIndividual[0] = "crhayden@cpp.edu";
//				status = sendEmail(toArrayIndividual, subjectGeneral, emailBodyIndividual);
//			//}
//		}

	}

	private boolean sendEmail(String[] to, String subject, String emailBody) {
		boolean status = false;
		try {
			MimeMessage message = sender.createMimeMessage();
			MimeMessageHelper helper = new MimeMessageHelper(message);

			helper.setTo(to);
			for(String t : to) {
				System.out.println("To : "+t);
			}
			helper.setSubject(subject);
			helper.setText(emailBody, true);
			helper.setFrom(from);
			sender.send(message);
			status = true;
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return status;
	}

	private String getGeneralHtmlEmailBody(/*List<Object[]> processorReport, List<Object[]> wamReport*/) {

		int udtTotalProcessCount = 0;
		int udtTotalWAMCount = 0;
		int ftfTotalProcessCount = 0;

		int udtCurrProcessCount = 0;
		int udtCurrWAMCount = 0;
		int ftfCurrProcessCount = 0;

		int udtCurrProcessCountM1 = 0;
		int udtCurrWAMCountM1 = 0;
		int ftfCurrProcessCountM1 = 0;

		int udtCurrProcessCountM2 = 0;
		int udtCurrWAMCountM2 = 0;
		int ftfCurrProcessCountM2 = 0;

		int udtCurrProcessCountM3 = 0;
		int udtCurrWAMCountM3 = 0;
		int ftfCurrProcessCountM3 = 0;

		int totalanalystCount = 0;
		int totalinitialTCRCount = 0;
		int totalfinalTCRCount = 0;
		int totalcompletedCount = 0;
		int totaldrReviewCount = 0;
		int totalreferDirCount = 0;
		int totalgrandTotal = 0;

		userAndReportBeanMap = new HashMap<>();

		StringBuilder emailBody = new StringBuilder();

		initialBody = "Hi,<br/><br/>Please find below audit report generated on "
				+ new SimpleDateFormat("MM/dd/yyyy").format(new Date())
				+ " for previous week's (from Sunday to Saturday) data.<br/><br/>";
		emailBody.append(initialBody);

		emailBody.append(
				"<table border=\"1px solid #ddd\" cellspacing=\"0\" cellpadding=\"0\" style=\"width:420pt;\"><tr bgcolor=\"#0069c0\"><th colspan=6><b><font color=\"#FFFFFF\">Upper Division Transfer</font></b></th></tr>"
						+ "<tr bgcolor=\"#6ec6ff\"><th>Audit</th>");

		tableHeader = "<th>" + lastWeekSundayM3 + "-" + lastWeekSaturdayM3
				+ "</th><th>" + lastWeekSundayM2 + "-" + lastWeekSaturdayM2
				+ "</th><th>" + lastWeekSundayM1 + "-" + lastWeekSaturdayM1
				+ "</th><th>" + lastWeekSunday + "-" + lastWeekSaturday 
				+ "</th><th>Grand total</th></tr>";
		emailBody.append(tableHeader);

		for (Object[] value : udtProcessorReport) {

			AuditReportBean bean = new AuditReportBean();
			bean.setUser(value[0].toString());
			bean.setUdtGrandProcessCount(Integer.parseInt(value[1].toString()));
			bean.setUdtCurrProcessCount(Integer.parseInt(value[2].toString()));
			bean.setUdtCurrProcessCountM1(Integer.parseInt(value[3].toString()));
			bean.setUdtCurrProcessCountM2(Integer.parseInt(value[4].toString()));
			bean.setUdtCurrProcessCountM3(Integer.parseInt(value[5].toString()));
			userAndReportBeanMap.put(value[0].toString(), bean);

			udtTotalProcessCount += Integer.parseInt(value[1].toString());
			udtCurrProcessCount += Integer.parseInt(value[2].toString());
			udtCurrProcessCountM1 += Integer.parseInt(value[3].toString());
			udtCurrProcessCountM2 += Integer.parseInt(value[4].toString());
			udtCurrProcessCountM3 += Integer.parseInt(value[5].toString());
			emailBody.append("<tr><td>").append(value[0]).append("</td><td align=\"center\">").append(value[5]).append("</td><td align=\"center\">").append(value[4]).append("</td><td align=\"center\">").append(value[3]).append("</td><td align=\"center\">").append(value[2]).append("</td><td align=\"center\">").append(value[1]).append("</td></tr>");
		}

		emailBody.append("<tr><td><b>Total</b></td><td align=\"center\"><b>").append(udtCurrProcessCountM3).append("</b></td><td align=\"center\"><b>").append(udtCurrProcessCountM2).append("</b></td><td align=\"center\"><b>").append(udtCurrProcessCountM1).append("</b></td><td align=\"center\"><b>").append(udtCurrProcessCount).append("</b></td><td align=\"center\"><b>").append(udtTotalProcessCount).append("</b></td></tr></table><br/><br/>");

		emailBody.append(
				"<table border=\"1px solid #ddd\" cellspacing=\"0\" cellpadding=\"0\" style=\"width:420pt;\"><tr bgcolor=\"#0069c0\"><th colspan=6><b><font color=\"#FFFFFF\">Upper Division Transfer</font></b></th></tr>"
						+ "<tr bgcolor=\"#6ec6ff\"><th>Transfer Credit</th>");

		emailBody.append(tableHeader);

		for (Object[] objects : udtWamReport) {

			AuditReportBean bean;
			if (userAndReportBeanMap.containsKey(objects[0].toString())) {
				bean = userAndReportBeanMap.get(objects[0].toString());
			} else {
				bean = new AuditReportBean();
			}
			bean.setUser(objects[0].toString());
			bean.setUdtGrandWAMCount(Integer.parseInt(objects[1].toString()));
			bean.setUdtCurrWAMCount(Integer.parseInt(objects[2].toString()));
			bean.setUdtCurrWAMCountM1(Integer.parseInt(objects[3].toString()));
			bean.setUdtCurrWAMCountM2(Integer.parseInt(objects[4].toString()));
			bean.setUdtCurrWAMCountM3(Integer.parseInt(objects[5].toString()));
			userAndReportBeanMap.put(objects[0].toString(), bean);

			udtTotalWAMCount += Integer.parseInt(objects[1].toString());
			udtCurrWAMCount += Integer.parseInt(objects[2].toString());
			udtCurrWAMCountM1 += Integer.parseInt(objects[3].toString());
			udtCurrWAMCountM2 += Integer.parseInt(objects[4].toString());
			udtCurrWAMCountM3 += Integer.parseInt(objects[5].toString());
			emailBody.append("<tr><td>").append(objects[0]).append("</td><td align=\"center\">").append(objects[5]).append("</td><td align=\"center\">").append(objects[4]).append("</td><td align=\"center\">").append(objects[3]).append("</td><td align=\"center\">").append(objects[2]).append("</td><td align=\"center\">").append(objects[1]).append("</td></tr>");
		}

		emailBody.append("<tr><td><b>Total</b></td><td align=\"center\"><b>").append(udtCurrWAMCountM3).append("</b></td><td align=\"center\"><b>").append(udtCurrWAMCountM2).append("</b></td><td align=\"center\"><b>").append(udtCurrWAMCountM1).append("</b></td><td align=\"center\"><b>").append(udtCurrWAMCount).append("</b></td><td align=\"center\"><b>").append(udtTotalWAMCount).append("</b></td></tr></table><br/><br/>");

		emailBody.append(
				"<table border=\"1px solid #ddd\" cellspacing=\"0\" cellpadding=\"0\" style=\"width:420pt;\"><tr bgcolor=\"#0069c0\"><th colspan=6><b><font color=\"#FFFFFF\">First Time Freshmen</font></b></th></tr>"
						+ "<tr bgcolor=\"#6ec6ff\"><th>Audit</th>");

		emailBody.append(tableHeader);

		for (Object[] objects : ftfProcessorReport) {

			AuditReportBean bean;
			if (userAndReportBeanMap.containsKey(objects[0].toString())) {
				bean = userAndReportBeanMap.get(objects[0].toString());
			} else {
				bean = new AuditReportBean();
			}
			bean.setUser(objects[0].toString());
			bean.setFtfGrandProcessCount(Integer.parseInt(objects[1].toString()));
			bean.setFtfCurrProcessCount(Integer.parseInt(objects[2].toString()));
			bean.setFtfCurrProcessCountM1(Integer.parseInt(objects[3].toString()));
			bean.setFtfCurrProcessCountM2(Integer.parseInt(objects[4].toString()));
			bean.setFtfCurrProcessCountM3(Integer.parseInt(objects[5].toString()));
			userAndReportBeanMap.put(objects[0].toString(), bean);

			ftfTotalProcessCount += Integer.parseInt(objects[1].toString());
			ftfCurrProcessCount += Integer.parseInt(objects[2].toString());
			ftfCurrProcessCountM1 += Integer.parseInt(objects[3].toString());
			ftfCurrProcessCountM2 += Integer.parseInt(objects[4].toString());
			ftfCurrProcessCountM3 += Integer.parseInt(objects[5].toString());
			emailBody.append("<tr><td>").append(objects[0]).append("</td><td align=\"center\">").append(objects[5]).append("</td><td align=\"center\">").append(objects[4]).append("</td><td align=\"center\">").append(objects[3]).append("</td><td align=\"center\">").append(objects[2]).append("</td><td align=\"center\">").append(objects[1]).append("</td></tr>");
		}

		emailBody.append("<tr><td><b>Total</b></td><td align=\"center\"><b>").append(ftfCurrProcessCountM3).append("</b></td><td align=\"center\"><b>").append(ftfCurrProcessCountM2).append("</b></td><td align=\"center\"><b>").append(ftfCurrProcessCountM1).append("</b></td><td align=\"center\"><b>").append(ftfCurrProcessCount).append("</b></td><td align=\"center\"><b>").append(ftfTotalProcessCount).append("</b></td></tr></table><br/><br/>");



		emailBody.append(
				"<table border=\"1px solid #ddd\" cellspacing=\"0\" cellpadding=\"0\" style=\"width:500pt;\"><tr bgcolor=\"#0069c0\"><th colspan=8><b><font color=\"#FFFFFF\">Upper Division Transfer</font></b></th></tr>"
						+ "<tr bgcolor=\"#6ec6ff\"><th>Status Report</th>");


		tableHeaderStatus = "<th> ANALYST START"
				+ "</th><th> INITIAL TCR"
				+ "</th><th> FINAL TCR"
				+ "</th><th> COMPLETE"
				+ "</th><th> D/R REVIEW"
				+ "</th><th> REFER DIR"
				+ "</th><th>Grand total</th></tr>";

		emailBody.append(tableHeaderStatus);

		for (Object[] objects : statusReport) {
			StatusType statusType = new StatusType();
			if(userStatusCountMap.containsKey(objects[0])){
				statusType = userStatusCountMap.get(objects[0]);
			}
			String status = objects[1].toString();
			int grandTotal = statusType.getGrandTotal();
			statusType.setGrandTotal(grandTotal + Integer.parseInt(objects[2].toString()));
			switch (status){
				case "ANALYST START":
					statusType.setAnalystCount(Integer.parseInt(objects[2].toString()));
					totalanalystCount += Integer.parseInt(objects[2].toString());
					break;

				case "INITIAL TCR":
					statusType.setInitialTCRCount(Integer.parseInt(objects[2].toString()));
					totalinitialTCRCount += Integer.parseInt(objects[2].toString());
					break;

				case "FINAL TCR":
					statusType.setFinalTCRCount(Integer.parseInt(objects[2].toString()));
					totalfinalTCRCount += Integer.parseInt(objects[2].toString());
					break;

				case "COMPLETE":
					statusType.setCompletedCount(Integer.parseInt(objects[2].toString()));
					totalcompletedCount += Integer.parseInt(objects[2].toString());
					break;

				case "D/R REVIEW":
					statusType.setDrReviewCount(Integer.parseInt(objects[2].toString()));
					totaldrReviewCount += Integer.parseInt(objects[2].toString());
					break;

				case "REFER DIR":
					statusType.setReferDirCount(Integer.parseInt(objects[2].toString()));
					totalreferDirCount += Integer.parseInt(objects[2].toString());
					break;

				default:
					break;
			}
			userStatusCountMap.put(objects[0].toString(), statusType);
		}

		for(Map.Entry<String, StatusType> data : userStatusCountMap.entrySet()){
			emailBody.append("<tr><td>").append(data.getKey())
					.append("</td><td align=\"center\">")
					.append(data.getValue().getAnalystCount())
					.append("</td><td align=\"center\">")
					.append(data.getValue().getInitialTCRCount())
					.append("</td><td align=\"center\">")
					.append(data.getValue().getFinalTCRCount())
					.append("</td><td align=\"center\">")
					.append(data.getValue().getCompletedCount())
					.append("</td><td align=\"center\">")
					.append(data.getValue().getDrReviewCount())
					.append("</td><td align=\"center\">")
					.append(data.getValue().getReferDirCount())
					.append("</td><td align=\"center\">")
					.append(data.getValue().getGrandTotal())
					.append("</td></tr>");
		}

		totalgrandTotal = totalanalystCount + totalinitialTCRCount + totalfinalTCRCount + totalcompletedCount + totaldrReviewCount + totalreferDirCount;

		emailBody.append("<tr><td><b>Total</b></td><td align=\"center\"><b>")
				.append(totalanalystCount)
				.append("</b></td><td align=\"center\"><b>")
				.append(totalinitialTCRCount)
				.append("</b></td><td align=\"center\"><b>")
				.append(totalfinalTCRCount)
				.append("</b></td><td align=\"center\"><b>")
				.append(totalcompletedCount)
				.append("</b></td><td align=\"center\"><b>")
				.append(totaldrReviewCount)
				.append("</b></td><td align=\"center\"><b>")
				.append(totalreferDirCount)
				.append("</b></td><td align=\"center\"><b>")
				.append(totalgrandTotal)
				.append("</b></td></tr></table><br/><br/>");


		footer = "Thank you.<br/><br/>"
				+ "<b>Report Generator System</b><br/>"
				+ "Office of Admissions & Enrollment Planning<br/>"
				+ "Building 121 - E</br></br>"
				+ "<b>California State Polytechnic University</b></br>"
				+ "3801 West Temple Ave. | Pomona, CA 91768</br>"
				+ "<a href=\"http://www.cpp.edu/admissions/\">www.cpp.edu/admissions</a></br>"
				+ "<b>Follow AEP: </b><a href=\"http://www.instagram.com/cpp_admissions/\">Instagram</a> | <a href=\"http://www.facebook.com/cppadmissions\">Facebook</a> | <a href=\"http://twitter.com/cpp_admissions\">Twitter</a></br>"
				+ "";

		emailBody.append(footer);

		return emailBody.toString();
	}

	private HashMap<String, String> generateIndividualEmailDate() {

		HashMap<String, String> individualEmailData = new HashMap<>();

		for (Map.Entry<String, AuditReportBean> entry : userAndReportBeanMap.entrySet()) {
			StringBuilder emailBody = new StringBuilder();
			emailBody.append(initialBody);

			if(0 < entry.getValue().getUdtGrandProcessCount()) {
				emailBody.append(
						"<table border=\"1px solid #ddd\" cellspacing=\"0\" cellpadding=\"0\" style=\"width:420pt;\"><tr bgcolor=\"#0069c0\"><th colspan=6><b><font color=\"#FFFFFF\">Upper Division Transfer</font></b></th></tr>"
								+ "<tr bgcolor=\"#6ec6ff\"><th>Audit</th>");
				emailBody.append(tableHeader);
				emailBody.append("<tr><td>").append(entry.getKey()).append("</td><td align=\"center\">").append(entry.getValue().getUdtCurrProcessCountM3()).append("</td><td align=\"center\">").append(entry.getValue().getUdtCurrProcessCountM2()).append("</td><td align=\"center\">").append(entry.getValue().getUdtCurrProcessCountM1()).append("</td><td align=\"center\">").append(entry.getValue().getUdtCurrProcessCount()).append("</td><td align=\"center\">").append(entry.getValue().getUdtGrandProcessCount()).append("</td></tr>");
				emailBody.append("</table><br/><br/>");
			}

			if(0 < entry.getValue().getUdtGrandWAMCount()) {
				emailBody.append(
						"<table border=\"1px solid #ddd\" cellspacing=\"0\" cellpadding=\"0\" style=\"width:420pt;\"><tr bgcolor=\"#0069c0\"><th colspan=6><b><font color=\"#FFFFFF\">Upper Division Transfer</font></b></th></tr>"
								+ "<tr bgcolor=\"#6ec6ff\"><th>Transfer Credit</th>");
				emailBody.append(tableHeader);
				emailBody.append("<tr><td>").append(entry.getKey()).append("</td><td align=\"center\">").append(entry.getValue().getUdtCurrWAMCountM3()).append("</td><td align=\"center\">").append(entry.getValue().getUdtCurrWAMCountM2()).append("</td><td align=\"center\">").append(entry.getValue().getUdtCurrWAMCountM1()).append("</td><td align=\"center\">").append(entry.getValue().getUdtCurrWAMCount()).append("</td><td align=\"center\">").append(entry.getValue().getUdtGrandWAMCount()).append("</td></tr>");
				emailBody.append("</table><br/><br/>");
			}

			if(0 < entry.getValue().getFtfGrandProcessCount()) {
				emailBody.append(
						"<table border=\"1px solid #ddd\" cellspacing=\"0\" cellpadding=\"0\" style=\"width:420pt;\"><tr bgcolor=\"#0069c0\"><th colspan=6><b><font color=\"#FFFFFF\">First Time Freshmen</font></b></th></tr>"
								+ "<tr bgcolor=\"#6ec6ff\"><th>Audit</th>");
				emailBody.append(tableHeader);
				emailBody.append("<tr><td>").append(entry.getKey()).append("</td><td align=\"center\">").append(entry.getValue().getFtfCurrProcessCountM3()).append("</td><td align=\"center\">").append(entry.getValue().getFtfCurrProcessCountM2()).append("</td><td align=\"center\">").append(entry.getValue().getFtfCurrProcessCountM1()).append("</td><td align=\"center\">").append(entry.getValue().getFtfCurrProcessCount()).append("</td><td align=\"center\">").append(entry.getValue().getFtfGrandProcessCount()).append("</td></tr>");
				emailBody.append("</table><br/><br/>");
			}

			StatusType statusType = userStatusCountMap.get(entry.getKey());
			if(statusType != null){
				emailBody.append(
						"<table border=\"1px solid #ddd\" cellspacing=\"0\" cellpadding=\"0\" style=\"width:500pt;\"><tr bgcolor=\"#0069c0\"><th colspan=8><b><font color=\"#FFFFFF\">Upper Division Transfer</font></b></th></tr>"
								+ "<tr bgcolor=\"#6ec6ff\"><th>Status Report</th>");
				emailBody.append(tableHeaderStatus);
				emailBody.append("<tr><td><b>"+entry.getKey()+"</b></td><td align=\"center\"><b>")
						.append(statusType.getAnalystCount())
						.append("</b></td><td align=\"center\"><b>")
						.append(statusType.getInitialTCRCount())
						.append("</b></td><td align=\"center\"><b>")
						.append(statusType.getFinalTCRCount())
						.append("</b></td><td align=\"center\"><b>")
						.append(statusType.getCompletedCount())
						.append("</b></td><td align=\"center\"><b>")
						.append(statusType.getDrReviewCount())
						.append("</b></td><td align=\"center\"><b>")
						.append(statusType.getReferDirCount())
						.append("</b></td><td align=\"center\"><b>")
						.append(statusType.getGrandTotal())
						.append("</b></td></tr></table><br/><br/>");
			}

			emailBody.append(footer);

			individualEmailData.put(entry.getKey(), emailBody.toString());
		}
		return individualEmailData;
	}
}
