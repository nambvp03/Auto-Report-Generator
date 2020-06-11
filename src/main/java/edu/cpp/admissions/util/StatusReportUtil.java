package edu.cpp.admissions.util;
import edu.cpp.admissions.bean.StatusAuditReport;
import edu.cpp.admissions.data.StatusReportRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

@Component
public class StatusReportUtil {


    @Autowired
    StatusReportRepo status_report_repo;

    public void readCsvFileAndStore() throws Exception {

        List<StatusAuditReport> auditList = new ArrayList<>();

        Path pathToFile = Paths.get("../AEP_Docs/OnBase Worksheets UDT_20200608.csv");
        BufferedReader br = Files.newBufferedReader(pathToFile, StandardCharsets.UTF_8);
        // skip header line
        String line = br.readLine();
        //line = br.readLine();

        while ((line = br.readLine()) != null){
            String[] data = line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)");
            StatusAuditReport report = new StatusAuditReport();
            report.setBroncoId(data[1]);
            report.setProcessor(data[27]);
            report.setStatus(data[40]);
            try {
                if (!data[44].equals("")) {
                    report.setWam(data[44]);
                } else {
                    report.setWam("");
                }
            }catch (Exception e){
                System.out.println("error : "+e.getLocalizedMessage());
            }
            auditList.add(report);
        }

        if(!auditList.isEmpty()) {
            //status_report_repo.deleteAll();
            status_report_repo.saveAll(auditList);
            //System.out.println(auditList.size());
        }

    }

}
