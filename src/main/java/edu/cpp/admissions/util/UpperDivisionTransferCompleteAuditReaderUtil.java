package edu.cpp.admissions.util;

import java.io.BufferedReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import edu.cpp.admissions.controller.AuditReportController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import edu.cpp.admissions.bean.UpperDivisionTranscriptAuditBean;
import edu.cpp.admissions.bean.UpperDivisionTranscriptCompleteAuditBean;
import edu.cpp.admissions.data.UpperDivisionTransferAuditRepo;
import edu.cpp.admissions.data.UpperDivisionTransferCompleteAuditRepo;

@Component
public class UpperDivisionTransferCompleteAuditReaderUtil {

	@Autowired
	UpperDivisionTransferCompleteAuditRepo upperDivisionTransferCompleteAuditRepo;
	
	@Autowired
	UpperDivisionTransferAuditRepo upperDivisionTransferAuditRepo;
	
	public void readCsvFileAndStore() throws Exception {

		List<UpperDivisionTranscriptCompleteAuditBean> auditCompleteList = new ArrayList<>();
		List<UpperDivisionTranscriptAuditBean> auditList = new ArrayList<>();
		
		Calendar cal = Calendar.getInstance();
		cal.add( Calendar.DAY_OF_WEEK, -(cal.get(Calendar.DAY_OF_WEEK)));
		Date lastWeekSaturdayDate = cal.getTime();
		
		Path pathToFile = Paths.get("/Users/jayavardhanpatil/projects/Admission_Enrolment/AEP_Docs/"+ AuditReportController.udtFileName);
		BufferedReader br = Files.newBufferedReader(pathToFile, StandardCharsets.UTF_8);
		// skip header line
		String line = br.readLine();
		line = br.readLine();

		while (line != null) {
			String[] row = line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)", -1);
			
			UpperDivisionTranscriptCompleteAuditBean auditCompleteBean = new UpperDivisionTranscriptCompleteAuditBean();
			auditCompleteBean.setBroncoId(row[1]);

			if(!row[32].equals("")) {
				if (!"".equals(row[27])) {
					auditCompleteBean.setProcessor(row[27]);
				}
				if (!"".equals(row[28])) {
					auditCompleteBean.setDateStart(new SimpleDateFormat("yyyy-MM-dd").parse(row[28]));
				}

				auditCompleteBean.setFinalDate(new SimpleDateFormat("yyyy-MM-dd").parse(row[32]));
				auditCompleteBean.setStatus(row[40]);
				auditCompleteBean.setWam(row[44]);


				if (null != auditCompleteBean.getFinalDate() && !lastWeekSaturdayDate.before(auditCompleteBean.getFinalDate())) {
					auditCompleteList.add(auditCompleteBean);
					if ("".equals(row[28])) {
						UpperDivisionTranscriptAuditBean auditBean = new UpperDivisionTranscriptAuditBean();
						auditBean.setBroncoId(auditCompleteBean.getBroncoId());
						auditBean.setProcessor(auditCompleteBean.getWam());
						auditBean.setDateStart(auditCompleteBean.getFinalDate());
						auditBean.setFinalDate(auditCompleteBean.getFinalDate());
						auditBean.setWam(auditCompleteBean.getWam());
						auditBean.setStatus(auditCompleteBean.getStatus());
						auditList.add(auditBean);
					}
				}
			}
			
			// if end of file reached, line would be null
			line = br.readLine();
		}
		if(!auditCompleteList.isEmpty()) {
			upperDivisionTransferCompleteAuditRepo.saveAll(auditCompleteList);
			upperDivisionTransferAuditRepo.saveAll(auditList);
		}
	}
}
