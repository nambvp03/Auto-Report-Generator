package edu.cpp.admissions.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import edu.cpp.admissions.data.UpperDivisionTransferCompleteAuditRepo;
import edu.cpp.admissions.util.AuditReportEmailUtil;
import edu.cpp.admissions.util.FirstTimeFreshmenAuditReaderUtil;
import edu.cpp.admissions.util.UpperDivisionTransferAuditReaderUtil;
import edu.cpp.admissions.util.UpperDivisionTransferCompleteAuditReaderUtil;

@RestController
public class AuditReportController {
	
	@Autowired
	AuditReportEmailUtil emailUtil;
	
	@Autowired
	UpperDivisionTransferAuditReaderUtil upperDivisionTransferAuditReaderUtil;
	
	@Autowired
	UpperDivisionTransferCompleteAuditReaderUtil upperDivisionTransferCompleteAuditReaderUtil;
	
	@Autowired
	FirstTimeFreshmenAuditReaderUtil firstTimeFreshmenAuditReaderUtil;

	@RequestMapping(value = "/admissions/statuscheck", method = RequestMethod.GET)
	String statusCheck() {
		return "<center><h1><font color=#28B463>OK: System is running</font></h1></center>";
	}

	@RequestMapping(value = "/admissions/readUDTCSVFile", method = RequestMethod.GET)
	Map<String, Object> readUDTCSVFileAndStore() {
		Map<String, Object> response = new HashMap<String, Object>();
		try {
			upperDivisionTransferAuditReaderUtil.readCsvFileAndStore();
			response.put("success", "Success msg");
			response.put("message", "Audit report generated");
		} catch (Exception e) {
			e.printStackTrace();
			response.put("success", false);
			response.put("message", e.getMessage());
		}
		return response;
	}
	
	@RequestMapping(value = "/admissions/readUDTCompleteCSVFile", method = RequestMethod.GET)
	Map<String, Object> readUDTCompleteCSVFileAndStore() {
		Map<String, Object> response = new HashMap<String, Object>();
		try {
			upperDivisionTransferCompleteAuditReaderUtil.readCsvFileAndStore();
			response.put("success", "Success msg");
			response.put("message", "Audit report generated");
		} catch (Exception e) {
			e.printStackTrace();
			response.put("success", false);
			response.put("message", e.getMessage());
		}
		return response;
	}
	
	@RequestMapping(value = "/admissions/readFTFCSVFile", method = RequestMethod.GET)
	Map<String, Object> readFTFCSVFileAndStore() {
		Map<String, Object> response = new HashMap<String, Object>();
		try {
			firstTimeFreshmenAuditReaderUtil.readCsvFileAndStore();
			response.put("success", "Success msg");
			response.put("message", "Audit report generated");
		} catch (Exception e) {
			e.printStackTrace();
			response.put("success", false);
			response.put("message", e.getMessage());
		}
		return response;
	}
	
	@RequestMapping(value = "/admissions/generateAuditReport", method = RequestMethod.GET)
	Map<String, Object> generateAuditReport() {
		Map<String, Object> response = new HashMap<String, Object>();
		try {
			//upperDivisionTransferAuditReaderUtil.readCsvFileAndStore();
			emailUtil.getDataAndPrepareForEmail();
			response.put("success", "Success msg");
			response.put("message", "Audit report generated");
		} catch (Exception e) {
			e.printStackTrace();
			response.put("success", false);
			response.put("message", e.getMessage());
		}
		return response;
	}
}