package com.DH.Service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.DH.Entity.Report;
import com.DH.Entity.Search;
import com.DH.Entity.User;
import com.DH.Repository.ReportRepository;

@Service
public class ReportService {

@Autowired
protected ReportRepository reportRepository;

public void save(Report report) {
    reportRepository.save(report);
} 

public List<Report> findByReportName(String reportName)
{
	return reportRepository.findByReportName(reportName);
}

public List<Report> findById(int id)
{
	return reportRepository.findById(id);
}
}