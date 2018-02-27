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
	@NamedQuery(name = "Search.findById", query = "select o from Search o where o.id=:id"),
})

@Entity
@EntityListeners(AuditingEntityListener.class)
public class Search {
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private int id;
	private String keyword;
	private String website;
	private boolean sold;
	private boolean freePostage;
	private String category;
	private String searchURL;
	private String listingType;
	private String numberOfListings;

	public Search(){
		
	}
	
	public Search(String keyword) {
		this.keyword = keyword;
	}
	
	public int getId() {
		return id;
	}
	
	public void setId(int id) {
		this.id = id;
	}
	
	public String getKeyword() {
		return keyword;
	}

	public void setKeyword(String keyword) {
		this.keyword = keyword;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}
	
	public String getWebsite() {
		return website;
	}

	public void setWebsite(String website) {
		this.website = website;
	}
	
	public boolean isFreePostage() {
		return freePostage;
	}

	public void setFreePostage(boolean freePostage) {
		this.freePostage = freePostage;
	}
	
	public boolean isSold() {
		return sold;
	}

	public void setSold(boolean sold) {
		this.sold = sold;
	}

	public String getSearchURL() {
		return searchURL;
	}

	public void setSearchURL(String searchURL) {
		this.searchURL = searchURL;
	}
	

	public String getListingType() {
		return listingType;
	}

	public void setListingType(String listingType) {
		this.listingType = listingType;
	}
	

	public String getNumberOfListings() {
		return numberOfListings;
	}

	public void setNumberOfListings(String numberOfListings) {
		this.numberOfListings = numberOfListings;
	}

}
