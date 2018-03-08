package com.DH.Entity;

import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;

import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@NamedQueries( {
	@NamedQuery(name = "Report.findByName", query = "select o from Report o where o.reportName=:reportName"),
})

@Entity
@EntityListeners(AuditingEntityListener.class)
public class Report {
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private int id;
	
	private String reportName;
	private int numberOfSales;
	private double totalSales;
	private double fees;
	private int numberOfPurchases;
	private double totalPurchases;
	private double postageCosts;

	public Report(){
		
	}
	
	public Report(String reportName)
	{
		this.reportName = reportName;
	}
	
	
	
	public Report( String reportName, int numberOfSales, double totalSales, double fees, int numberOfPurchases, double totalPurchases, double postageCosts) {
		this.reportName = reportName;
		this.numberOfSales = numberOfSales;
		this.totalSales = totalSales;
		this.fees = fees;
		this.numberOfPurchases = numberOfPurchases;
		this.totalPurchases = totalPurchases;
		this.postageCosts = postageCosts;
	}



	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
	
	public String getReportName()
	{
		return reportName;
	}
	
	public void setReportName(String reportName)
	{
		this.reportName = reportName;
	}

	public int getNumberOfSales() {
		return numberOfSales;
	}

	public void setNumberOfSales(int numberOfSales) {
		this.numberOfSales = numberOfSales;
	}

	public double getTotalSales() {
		return totalSales;
	}

	public void setTotalSales(double totalSales) {
		this.totalSales = totalSales;
	}

	public double getFees() {
		return fees;
	}

	public void setFees(double fees) {
		this.fees = fees;
	}

	public int getNumberOfPurchases() {
		return numberOfPurchases;
	}

	public void setNumberOfPurchases(int numberOfPurchases) {
		this.numberOfPurchases = numberOfPurchases;
	}

	public double getTotalPurchases() {
		return totalPurchases;
	}

	public void setTotalPurchases(double totalPurchases) {
		this.totalPurchases = totalPurchases;
	}

	public double getPostageCosts() {
		return postageCosts;
	}

	public void setPostageCosts(double postageCosts) {
		this.postageCosts = postageCosts;
	}


}