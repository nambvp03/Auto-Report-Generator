package edu.cpp.admissions.data;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import edu.cpp.admissions.bean.UpperDivisionTranscriptAuditBean;

@Repository
public interface UpperDivisionTransferAuditRepo extends CrudRepository<UpperDivisionTranscriptAuditBean, Long> {

	@Query(value="select  " + 
			"	one.processor, " + 
			"	one.grand, " + 
			"	CASE WHEN two.curr is NULL THEN 0 ELSE two.curr END AS curr, " + 
			"	CASE WHEN three.currm1 is NULL THEN 0 ELSE three.currm1 END AS currm1, " + 
			"	CASE WHEN four.currm2 is NULL THEN 0 ELSE four.currm2 END AS currm2, " + 
			"	CASE WHEN five.currm3 is NULL THEN 0 ELSE five.currm3 END AS currm3 " + 
			"	from " + 
			"	(  " + 
			"		select processor, count(processor) as grand from upper_div_transcript_report " + 
			"			where date_start <= date_trunc('week', now())- INTERVAL '2days'  " + 
			"			and status <> 'ANALYST START' " + 
			"			group by processor  " + 
			"	) as one  " + 
			"	left join   " + 
			"	( " + 
			"		select processor, count(processor) as curr from upper_div_transcript_report  " + 
			"			where date_start between date_trunc('week', now())- INTERVAL '8days' and date_trunc('week', now())- INTERVAL '2days' " + 
			"			and status <> 'ANALYST START' " + 
			"			group by processor  " + 
			"	) as two  " + 
			"	on one.processor = two.processor " + 
			"	left join   " + 
			"	( " + 
			"		select processor, count(processor) as currm1 from upper_div_transcript_report  " + 
			"			where date_start between date_trunc('week', now())- INTERVAL '15days' and date_trunc('week', now())- INTERVAL '9days' " + 
			"			and status <> 'ANALYST START' " + 
			"			group by processor  " + 
			"	) as three  " + 
			"	on one.processor = three.processor " + 
			"	left join   " + 
			"	( " + 
			"		select processor, count(processor) as currm2 from upper_div_transcript_report  " + 
			"			where date_start between date_trunc('week', now())- INTERVAL '22days' and date_trunc('week', now())- INTERVAL '16days' " + 
			"			and status <> 'ANALYST START' " + 
			"			group by processor  " + 
			"	) as four  " + 
			"	on one.processor = four.processor " + 
			"	left join   " + 
			"	( " + 
			"		select processor, count(processor) as currm3 from upper_div_transcript_report  " + 
			"			where date_start between date_trunc('week', now())- INTERVAL '29days' and date_trunc('week', now())- INTERVAL '23days' " + 
			"			and status <> 'ANALYST START' " + 
			"			group by processor  " + 
			"	) as five  " + 
			"	on one.processor = five.processor " + 
			"	order by one.processor", nativeQuery = true)
	List<Object[]> getProcessorCountOfLastWeek();
}
