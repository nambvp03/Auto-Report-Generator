package edu.cpp.admissions.data;

import edu.cpp.admissions.bean.StatusAuditReport;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StatusReportRepo extends CrudRepository<StatusAuditReport, Long> {
    @Query(value="select \"processor\",\"status\", count(\"status\") as count\n" +
            "from \"status_report\" group by \"processor\",\"status\" order by \"processor\" asc" , nativeQuery = true)

    List<Object[]> getReportByStatus();

}
