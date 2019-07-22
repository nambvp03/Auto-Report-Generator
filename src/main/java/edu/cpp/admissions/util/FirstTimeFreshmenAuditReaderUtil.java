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

import edu.cpp.admissions.bean.FirstTimeFreshmenAuditBean;
import edu.cpp.admissions.data.FirstTimeFreshmenAuditRepo;

@Component
public class FirstTimeFreshmenAuditReaderUtil {

	@Autowired
	FirstTimeFreshmenAuditRepo firstTimeFreshmenAuditRepo;
	
	public void readCsvFileAndStore() throws Exception {

		List<FirstTimeFreshmenAuditBean> auditList = new ArrayList<>();
		
		Calendar cal = Calendar.getInstance();
		cal.add( Calendar.DAY_OF_WEEK, -(cal.get(Calendar.DAY_OF_WEEK)));
		//cal.add( Calendar.DAY_OF_WEEK, -(cal.get(Calendar.DAY_OF_WEEK)+7));
		Date lastWeekSaturdayDate = cal.getTime();
		
		Path pathToFile = Paths.get("C:\\Users\\Dell G5\\Desktop\\FTF.csv");
		BufferedReader br = Files.newBufferedReader(pathToFile, StandardCharsets.UTF_8);
		// skip header line
		String line = br.readLine();
		line = br.readLine();

		while (line != null) {
			String[] row = line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)", -1);
			
			FirstTimeFreshmenAuditBean auditBean = new FirstTimeFreshmenAuditBean();
			auditBean.setBroncoId(Long.parseLong(row[1].substring(1, row[1].length()-1)));
			auditBean.setProcessor(row[91].substring(1, row[91].length()-1));
			auditBean.setFinalDate(new SimpleDateFormat("yyyy-MM-dd").parse(row[103].substring(1, row[103].length()-1)));
			auditBean.setStatus(row[102].substring(1, row[102].length()-1));

			if(!lastWeekSaturdayDate.before(auditBean.getFinalDate())) {
				auditList.add(auditBean);
			}
			// if end of file reached, line would be null
			line = br.readLine();
		}
		if(!auditList.isEmpty()) {
			firstTimeFreshmenAuditRepo.saveAll(auditList);
			//System.out.println(auditList.size());
		}
	}
}
