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
public class Sale extends Item{
	private String itemCondition;
	private String link;
	private String fee;
	private String date;
	
	public Sale() {
		
	}
	
	public Sale(String title, String price, String image, String itemCondition, String postage, String location, String link, String fee, String date)
	{
		super(title, price, postage, location, image);
		this.itemCondition = itemCondition;
		this.link = link;
		this.fee = fee;
		this.date = date;
	}

	public String getItemCondition() {
		return itemCondition;
	}

	public void setitemCondition(String itemCondition) {
		this.itemCondition = itemCondition;
	}

	public String getLink() {
		return link;
	}

	public void setLink(String link) {
		this.link = link;
	}

	public String getFee() {
		return fee;
	}

	public void setFee(String fee) {
		this.fee = fee;
	}
	
	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

}
