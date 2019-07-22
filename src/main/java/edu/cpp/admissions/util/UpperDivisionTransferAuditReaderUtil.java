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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import edu.cpp.admissions.bean.UpperDivisionTranscriptAuditBean;
import edu.cpp.admissions.data.UpperDivisionTransferAuditRepo;

@Component
public class UpperDivisionTransferAuditReaderUtil {

	@Autowired
	UpperDivisionTransferAuditRepo upperDivisionTransferAuditRepo;
	
	public void readCsvFileAndStore() throws Exception {

		List<UpperDivisionTranscriptAuditBean> auditList = new ArrayList<>();
		
		Calendar cal=Calendar.getInstance();
		cal.add( Calendar.DAY_OF_WEEK, -(cal.get(Calendar.DAY_OF_WEEK)));
		//cal.add( Calendar.DAY_OF_WEEK, -(cal.get(Calendar.DAY_OF_WEEK)+7));
		Date lastWeekSaturdayDate = cal.getTime();
		
		Path pathToFile = Paths.get("C:\\Users\\Dell G5\\Desktop\\UDT_DATE_START.csv");
		BufferedReader br = Files.newBufferedReader(pathToFile, StandardCharsets.UTF_8);
		// skip header line
		String line = br.readLine();
		line = br.readLine();

		while (line != null) {
			String[] row = line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)", -1);
			
			UpperDivisionTranscriptAuditBean auditBean = new UpperDivisionTranscriptAuditBean();
			auditBean.setBroncoId(Long.parseLong(row[1].substring(1, row[1].length()-1)));
			auditBean.setProcessor(row[27].substring(1, row[27].length()-1));
			auditBean.setDateStart(new SimpleDateFormat("yyyy-MM-dd").parse(row[28].substring(1, row[28].length()-1)));
			if(!"".equals(row[32])) {
				//System.out.println(row[32]);
				auditBean.setFinalDate(new SimpleDateFormat("yyyy-MM-dd").parse(row[32].substring(1, row[32].length()-1)));
			}
			auditBean.setStatus(row[40].substring(1, row[40].length()-1));
			if (!"".equals(row[44])) {
				auditBean.setWam(row[44].substring(1, row[44].length()-1));
			}
			else {
				auditBean.setWam("");
			}

			if(!lastWeekSaturdayDate.before(auditBean.getDateStart())) {
				auditList.add(auditBean);
			}
			
			// if end of file reached, line would be null
			line = br.readLine();
		}
		if(!auditList.isEmpty()) {
			upperDivisionTransferAuditRepo.saveAll(auditList);
			//System.out.println(auditList.size());
		}
	}
}
