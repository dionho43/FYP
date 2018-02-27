package com.DH.Entity;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

@MappedSuperclass
public class Item {
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private int id;
	private String title;
	private String price;
	private String postage;
	private String location;
	private String image;
	private boolean sold;
	
	public Item() {
		
	}
	
	public Item(String title, String price, String postage, String location, String image)
	{
		this.title = title;
		this.price = price;
		this.image = image;
		this.postage = postage;
		this.location = location;
	}
	
	public Item(String title, String price, String postage, String location, String image, boolean sold)
	{
		this.title = title;
		this.price = price;
		this.image = image;
		this.sold = sold;
		this.postage = postage;
		this.location = location;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getPrice() {
		return price;
	}

	public void setPrice(String price) {
		this.price = price;
	}

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}
	
	public boolean getSold() {
		return sold;
	}

	public void setSold(boolean sold) {
		this.sold = sold;
	}
	
	public String getPostage() {
		return postage;
	}

	public void setPostage(String postage) {
		this.postage = postage;
	}
	
	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

}
