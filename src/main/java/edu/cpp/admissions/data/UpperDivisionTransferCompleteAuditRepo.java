package edu.cpp.admissions.data;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import edu.cpp.admissions.bean.UpperDivisionTranscriptCompleteAuditBean;

@Repository
public interface UpperDivisionTransferCompleteAuditRepo extends CrudRepository<UpperDivisionTranscriptCompleteAuditBean, Long> {
	
	@Query(value="select " + 
			"	one.wam, " + 
			"	one.grand,  " + 
			"	CASE WHEN two.curr is NULL THEN 0 ELSE two.curr END AS curr, " + 
			"	CASE WHEN three.currm1 is NULL THEN 0 ELSE three.currm1 END AS currm1, " + 
			"	CASE WHEN four.currm2 is NULL THEN 0 ELSE four.currm2 END AS currm2,	 " + 
			"	CASE WHEN five.currm3 is NULL THEN 0 ELSE five.currm3 END AS currm3 " + 
			"	from  " + 
			"	(   " + 
			"		select wam, count(wam) as grand from upper_div_transcript_complete_report  " + 
			"			where final_date <= date_trunc('week', now())- INTERVAL '2days'   " + 
			"			and status = 'COMPLETE' " + 
			"			group by wam  " + 
			"	) as one  " + 
			"	left join   " + 
			"	( " + 
			"		select wam, count(wam) as curr from upper_div_transcript_complete_report   " + 
			"			where final_date between date_trunc('week', now())- INTERVAL '8days' and date_trunc('week', now())- INTERVAL '2days'  " + 
			"			and status = 'COMPLETE' " + 
			"			group by wam   " + 
			"	) as two   " + 
			"	on one.wam = two.wam " + 
			"	left join   " + 
			"	( " + 
			"		select wam, count(wam) as currm1 from upper_div_transcript_complete_report   " + 
			"			where final_date between date_trunc('week', now())- INTERVAL '15days' and date_trunc('week', now())- INTERVAL '9days'  " + 
			"			and status = 'COMPLETE' " + 
			"			group by wam   " + 
			"	) as three  " + 
			"	on one.wam = three.wam " + 
			"	left join   " + 
			"	( " + 
			"		select wam, count(wam) as currm2 from upper_div_transcript_complete_report   " + 
			"			where final_date between date_trunc('week', now())- INTERVAL '22days' and date_trunc('week', now())- INTERVAL '16days'  " + 
			"			and status = 'COMPLETE' " + 
			"			group by wam   " + 
			"	) as four  " + 
			"	on one.wam = four.wam " + 
			"	left join   " + 
			"	( " + 
			"		select wam, count(wam) as currm3 from upper_div_transcript_complete_report   " + 
			"			where final_date between date_trunc('week', now())- INTERVAL '29days' and date_trunc('week', now())- INTERVAL '23days'  " + 
			"			and status = 'COMPLETE' " + 
			"			group by wam   " + 
			"	) as five  " + 
			"	on one.wam = five.wam " + 
			"	order by one.wam", nativeQuery = true)
	List<Object[]> getWAMCountOfLastWeek();
}
