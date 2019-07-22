package edu.cpp.admissions.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.mail.internet.MimeMessage;

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

	String initialBody;
	String footer;
	String tableHeader;

	HashMap<String, AuditReportBean> userAndReportBeanMap;

	public boolean getDataAndPrepareForEmail() {
		boolean status = false;

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

		HashMap<String, String> individualEmailData = generateIndividualEmailDate();
		for (Map.Entry<String, String> entry : individualEmailData.entrySet()) {
			//if(!excludeFromEmail.contains(entry.getKey())) {
				String[] toArrayIndividual = { entry.getKey().toLowerCase() + "@cpp.edu" };
				//String[] toArrayIndividual = { "" };
				String emailBodyIndividual = entry.getValue();
				status = sendEmail(toArrayIndividual, subjectGeneral, emailBodyIndividual);
			//}
		}

		return status;
	}

	private boolean sendEmail(String[] to, String subject, String emailBody) {
		boolean status = false;
		try {
			MimeMessage message = sender.createMimeMessage();
			MimeMessageHelper helper = new MimeMessageHelper(message);

			helper.setTo(to);
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

		userAndReportBeanMap = new HashMap<>();

		StringBuffer emailBody = new StringBuffer();

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

		for(int i = 0; i < udtProcessorReport.size(); i++) {

			AuditReportBean bean = new AuditReportBean();
			bean.setUser(udtProcessorReport.get(i)[0].toString());
			bean.setUdtGrandProcessCount(Integer.parseInt(udtProcessorReport.get(i)[1].toString()));
			bean.setUdtCurrProcessCount(Integer.parseInt(udtProcessorReport.get(i)[2].toString()));
			bean.setUdtCurrProcessCountM1(Integer.parseInt(udtProcessorReport.get(i)[3].toString()));
			bean.setUdtCurrProcessCountM2(Integer.parseInt(udtProcessorReport.get(i)[4].toString()));
			bean.setUdtCurrProcessCountM3(Integer.parseInt(udtProcessorReport.get(i)[5].toString()));
			userAndReportBeanMap.put(udtProcessorReport.get(i)[0].toString(), bean);

			udtTotalProcessCount += Integer.parseInt(udtProcessorReport.get(i)[1].toString());
			udtCurrProcessCount += Integer.parseInt(udtProcessorReport.get(i)[2].toString());
			udtCurrProcessCountM1 += Integer.parseInt(udtProcessorReport.get(i)[3].toString());
			udtCurrProcessCountM2 += Integer.parseInt(udtProcessorReport.get(i)[4].toString());
			udtCurrProcessCountM3 += Integer.parseInt(udtProcessorReport.get(i)[5].toString());
			emailBody.append("<tr><td>" + udtProcessorReport.get(i)[0] 
					+ "</td><td align=\"center\">" + udtProcessorReport.get(i)[5] 
							+ "</td><td align=\"center\">" + udtProcessorReport.get(i)[4] 
									+ "</td><td align=\"center\">" + udtProcessorReport.get(i)[3] 
											+ "</td><td align=\"center\">" + udtProcessorReport.get(i)[2]
													+ "</td><td align=\"center\">" + udtProcessorReport.get(i)[1]
															+ "</td></tr>");
		}

		emailBody.append("<tr><td><b>Total</b></td><td align=\"center\"><b>"
				+ udtCurrProcessCountM3 + "</b></td><td align=\"center\"><b>"
				+ udtCurrProcessCountM2 + "</b></td><td align=\"center\"><b>"
				+ udtCurrProcessCountM1 + "</b></td><td align=\"center\"><b>"
				+ udtCurrProcessCount + "</b></td><td align=\"center\"><b>" 
				+ udtTotalProcessCount + "</b></td></tr></table><br/><br/>");

		emailBody.append(
				"<table border=\"1px solid #ddd\" cellspacing=\"0\" cellpadding=\"0\" style=\"width:420pt;\"><tr bgcolor=\"#0069c0\"><th colspan=6><b><font color=\"#FFFFFF\">Upper Division Transfer</font></b></th></tr>"
						+ "<tr bgcolor=\"#6ec6ff\"><th>Transfer Credit</th>");

		emailBody.append(tableHeader);

		for(int i = 0; i < udtWamReport.size(); i++) {

			AuditReportBean bean;
			if (userAndReportBeanMap.containsKey(udtWamReport.get(i)[0].toString())) {
				bean = userAndReportBeanMap.get(udtWamReport.get(i)[0].toString());
			}
			else {
				bean = new AuditReportBean();
			}
			bean.setUser(udtWamReport.get(i)[0].toString());
			bean.setUdtGrandWAMCount(Integer.parseInt(udtWamReport.get(i)[1].toString()));
			bean.setUdtCurrWAMCount(Integer.parseInt(udtWamReport.get(i)[2].toString()));
			bean.setUdtCurrWAMCountM1(Integer.parseInt(udtWamReport.get(i)[3].toString()));
			bean.setUdtCurrWAMCountM2(Integer.parseInt(udtWamReport.get(i)[4].toString()));
			bean.setUdtCurrWAMCountM3(Integer.parseInt(udtWamReport.get(i)[5].toString()));
			userAndReportBeanMap.put(udtWamReport.get(i)[0].toString(), bean);

			udtTotalWAMCount += Integer.parseInt(udtWamReport.get(i)[1].toString());
			udtCurrWAMCount += Integer.parseInt(udtWamReport.get(i)[2].toString());
			udtCurrWAMCountM1 += Integer.parseInt(udtWamReport.get(i)[3].toString());
			udtCurrWAMCountM2 += Integer.parseInt(udtWamReport.get(i)[4].toString());
			udtCurrWAMCountM3 += Integer.parseInt(udtWamReport.get(i)[5].toString());
			emailBody.append("<tr><td>" + udtWamReport.get(i)[0] 
					+ "</td><td align=\"center\">" + udtWamReport.get(i)[5] 
							+ "</td><td align=\"center\">" + udtWamReport.get(i)[4]
									+ "</td><td align=\"center\">" + udtWamReport.get(i)[3]
											+ "</td><td align=\"center\">" + udtWamReport.get(i)[2]
													+ "</td><td align=\"center\">" + udtWamReport.get(i)[1] 
															+ "</td></tr>");
		}

		emailBody.append("<tr><td><b>Total</b></td><td align=\"center\"><b>" 
				+ udtCurrWAMCountM3 + "</b></td><td align=\"center\"><b>"
				+ udtCurrWAMCountM2 + "</b></td><td align=\"center\"><b>"
				+ udtCurrWAMCountM1 + "</b></td><td align=\"center\"><b>"
				+ udtCurrWAMCount + "</b></td><td align=\"center\"><b>" 
				+ udtTotalWAMCount + "</b></td></tr></table><br/><br/>");

		emailBody.append(
				"<table border=\"1px solid #ddd\" cellspacing=\"0\" cellpadding=\"0\" style=\"width:420pt;\"><tr bgcolor=\"#0069c0\"><th colspan=6><b><font color=\"#FFFFFF\">First Time Freshmen</font></b></th></tr>"
						+ "<tr bgcolor=\"#6ec6ff\"><th>Audit</th>");

		emailBody.append(tableHeader);

		for(int i = 0; i < ftfProcessorReport.size(); i++) {

			AuditReportBean bean;
			if (userAndReportBeanMap.containsKey(ftfProcessorReport.get(i)[0].toString())) {
				bean = userAndReportBeanMap.get(ftfProcessorReport.get(i)[0].toString());
			}
			else {
				bean = new AuditReportBean();
			}
			bean.setUser(ftfProcessorReport.get(i)[0].toString());
			bean.setFtfGrandProcessCount(Integer.parseInt(ftfProcessorReport.get(i)[1].toString()));
			bean.setFtfCurrProcessCount(Integer.parseInt(ftfProcessorReport.get(i)[2].toString()));
			bean.setFtfCurrProcessCountM1(Integer.parseInt(ftfProcessorReport.get(i)[3].toString()));
			bean.setFtfCurrProcessCountM2(Integer.parseInt(ftfProcessorReport.get(i)[4].toString()));
			bean.setFtfCurrProcessCountM3(Integer.parseInt(ftfProcessorReport.get(i)[5].toString()));
			userAndReportBeanMap.put(ftfProcessorReport.get(i)[0].toString(), bean);

			ftfTotalProcessCount += Integer.parseInt(ftfProcessorReport.get(i)[1].toString());
			ftfCurrProcessCount += Integer.parseInt(ftfProcessorReport.get(i)[2].toString());
			ftfCurrProcessCountM1 += Integer.parseInt(ftfProcessorReport.get(i)[3].toString());
			ftfCurrProcessCountM2 += Integer.parseInt(ftfProcessorReport.get(i)[4].toString());
			ftfCurrProcessCountM3 += Integer.parseInt(ftfProcessorReport.get(i)[5].toString());
			emailBody.append("<tr><td>" + ftfProcessorReport.get(i)[0] 
					+ "</td><td align=\"center\">" + ftfProcessorReport.get(i)[5] 
							+ "</td><td align=\"center\">" + ftfProcessorReport.get(i)[4]
									+ "</td><td align=\"center\">" + ftfProcessorReport.get(i)[3]
											+ "</td><td align=\"center\">" + ftfProcessorReport.get(i)[2]
													+ "</td><td align=\"center\">" + ftfProcessorReport.get(i)[1] 
															+ "</td></tr>");
		}

		emailBody.append("<tr><td><b>Total</b></td><td align=\"center\"><b>" 
				+ ftfCurrProcessCountM3 + "</b></td><td align=\"center\"><b>"
				+ ftfCurrProcessCountM2 + "</b></td><td align=\"center\"><b>"
				+ ftfCurrProcessCountM1 + "</b></td><td align=\"center\"><b>"
				+ ftfCurrProcessCount + "</b></td><td align=\"center\"><b>" 
				+ ftfTotalProcessCount + "</b></td></tr></table><br/><br/>");

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
			StringBuffer emailBody = new StringBuffer();
			emailBody.append(initialBody);

			if(0 < entry.getValue().getUdtGrandProcessCount()) {
				emailBody.append(
						"<table border=\"1px solid #ddd\" cellspacing=\"0\" cellpadding=\"0\" style=\"width:420pt;\"><tr bgcolor=\"#0069c0\"><th colspan=6><b><font color=\"#FFFFFF\">Upper Division Transfer</font></b></th></tr>"
								+ "<tr bgcolor=\"#6ec6ff\"><th>Audit</th>");
				emailBody.append(tableHeader);
				emailBody.append("<tr><td>" + entry.getKey() + "</td><td align=\"center\">"
						+ entry.getValue().getUdtCurrProcessCountM3() + "</td><td align=\"center\">"
						+ entry.getValue().getUdtCurrProcessCountM2() + "</td><td align=\"center\">"
						+ entry.getValue().getUdtCurrProcessCountM1() + "</td><td align=\"center\">"
						+ entry.getValue().getUdtCurrProcessCount() + "</td><td align=\"center\">"
						+ entry.getValue().getUdtGrandProcessCount() + "</td></tr>");
				emailBody.append("</table><br/><br/>");
			}

			if(0 < entry.getValue().getUdtGrandWAMCount()) {
				emailBody.append(
						"<table border=\"1px solid #ddd\" cellspacing=\"0\" cellpadding=\"0\" style=\"width:420pt;\"><tr bgcolor=\"#0069c0\"><th colspan=6><b><font color=\"#FFFFFF\">Upper Division Transfer</font></b></th></tr>"
								+ "<tr bgcolor=\"#6ec6ff\"><th>Transfer Credit</th>");
				emailBody.append(tableHeader);
				emailBody.append("<tr><td>" + entry.getKey() + "</td><td align=\"center\">"
						+ entry.getValue().getUdtCurrWAMCountM3() + "</td><td align=\"center\">"
						+ entry.getValue().getUdtCurrWAMCountM2() + "</td><td align=\"center\">"
						+ entry.getValue().getUdtCurrWAMCountM1() + "</td><td align=\"center\">"
						+ entry.getValue().getUdtCurrWAMCount() + "</td><td align=\"center\">"
						+ entry.getValue().getUdtGrandWAMCount() + "</td></tr>");
				emailBody.append("</table><br/><br/>");
			}

			if(0 < entry.getValue().getFtfGrandProcessCount()) {
				emailBody.append(
						"<table border=\"1px solid #ddd\" cellspacing=\"0\" cellpadding=\"0\" style=\"width:420pt;\"><tr bgcolor=\"#0069c0\"><th colspan=6><b><font color=\"#FFFFFF\">First Time Freshmen</font></b></th></tr>"
								+ "<tr bgcolor=\"#6ec6ff\"><th>Audit</th>");
				emailBody.append(tableHeader);
				emailBody.append("<tr><td>" + entry.getKey() + "</td><td align=\"center\">"
						+ entry.getValue().getFtfCurrProcessCountM3() + "</td><td align=\"center\">"
						+ entry.getValue().getFtfCurrProcessCountM2() + "</td><td align=\"center\">"
						+ entry.getValue().getFtfCurrProcessCountM1() + "</td><td align=\"center\">"
						+ entry.getValue().getFtfCurrProcessCount() + "</td><td align=\"center\">"
						+ entry.getValue().getFtfGrandProcessCount() + "</td></tr>");
				emailBody.append("</table><br/><br/>");
			}

			emailBody.append(footer);

			individualEmailData.put(entry.getKey(), emailBody.toString());
		}
		return individualEmailData;
	}
}
