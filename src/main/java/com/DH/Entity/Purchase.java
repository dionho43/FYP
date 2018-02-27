package com.DH.Entity;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@EntityListeners(AuditingEntityListener.class)
public class Purchase extends Item {
	
	private String itemCondition;
	private String sellerName;
	private String link;
	private String date;
	public Purchase() {
		
	}
	
	public Purchase(String title, String price, String image, String itemCondition, String postage, String location, String sellerName, String link, String date)
	{
		super(title, price, postage, location, image);
		this.itemCondition = itemCondition;
		this.sellerName = sellerName;
		this.link = link;
		this.date = date;
	}

	public String getItemCondition() {
		return itemCondition;
	}

	public void setItemCondition(String itemCondition) {
		this.itemCondition = itemCondition;
	}

	public String getSellerName() {
		return sellerName;
	}

	public void setSellerName(String sellerName) {
		this.sellerName = sellerName;
	}

	public String getLink() {
		return link;
	}

	public void setLink(String link) {
		this.link = link;
	}
	
	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}
	

}
