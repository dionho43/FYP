package com.DH.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.DH.Entity.Report;
import com.DH.Entity.User;


@Repository
public interface ReportRepository extends JpaRepository<Report, Long> {
	
	List<Report> findById(@Param("id") int id);
	
	List<Report> findByReportName(@Param("reportName") String reportName);
	
}