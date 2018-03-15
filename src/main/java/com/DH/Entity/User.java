package com.DH.Entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.*;

import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;


@NamedQueries( {
	@NamedQuery(name = "User.findAll", query = "select o from User o"),
	@NamedQuery(name = "User.findById", query = "select o from User o where o.id=:id"),
	@NamedQuery(name = "User.findByUsername", query = "select o from User o where o.username=:username"),
})

@Entity
@EntityListeners(AuditingEntityListener.class)
public class User implements Serializable{
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private int id;
	private String username;
	private String password;
	private String ebayUsername;
	@LazyCollection(LazyCollectionOption.FALSE)
	@OneToMany(fetch=FetchType.LAZY, cascade=CascadeType.ALL)
	private List<Purchase> purchases = new ArrayList<Purchase>();
	@LazyCollection(LazyCollectionOption.FALSE)
	@OneToMany(fetch=FetchType.LAZY, cascade=CascadeType.ALL)
	private List<Sale> sales = new ArrayList<Sale>();
	@LazyCollection(LazyCollectionOption.FALSE)
	@OneToMany(fetch=FetchType.LAZY, cascade=CascadeType.ALL)
	private List<Search> searches = new ArrayList<Search>();
	@LazyCollection(LazyCollectionOption.FALSE)
	@OneToMany(fetch=FetchType.LAZY, cascade=CascadeType.ALL)
	private List<Report> reports = new ArrayList<Report>();
	
	public User(){
		
	}
	
	public User(String username, String ebayUsername, String password) {
		super();
		this.username = username;
		this.ebayUsername = ebayUsername;
		this.password = password;
	}
	
	public int getId() {
		return id;
	}
	
	public void setId(int id) {
		this.id = id;
	}
	
	public String getUsername() {
		return username;
	}
	
	public void setUsername(String username) {
		this.username = username;
	}
	
	public String getEbayUsername() {
		return ebayUsername;
	}

	public void setEbayUsername(String ebayUsername) {
		this.ebayUsername = ebayUsername;
	}

	public String getPassword() {
		return password;
	}
	
	public void setPassword(String password) {
		this.password = password;
	}
	
	public void addPurchase(Purchase p)
	{
		this.purchases.add(p);
	}
	
	public void removePurchase(Purchase p)
	{
		this.purchases.remove(p);
	}
	
	public List<Purchase> getPurchases(){
		return this.purchases;
	}
	
	public void addSale(Sale s)
	{
		this.sales.add(s);
	}
	
	public void removeSale(Sale s)
	{
		this.sales.remove(s);
	}
	
	public List<Sale> getSales(){
		return this.sales;
	}
	
	public void addSearch(Search s)
	{
		this.searches.add(s);
	}
	
	public void removeSearch(Search s)
	{
		this.searches.remove(s);
	}
	
	public List<Search> getSearches(){
		return this.searches;
	}
	
	public List<Report> getReports(){
		return this.reports;
	}
	
	public void addReport(Report r)
	{
		this.reports.add(r);
	}
	

	

	


}