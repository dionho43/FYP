package com.DH.Entity;

public class Statistics {
	private int numberListings;
	private int numberSold;
	private double averagePrice;
	private double averagePostage;
	private int numberAuctions;
	private int numberPictures;
	private double highestPrice;
	private double lowestPrice;
	private String range;

	public Statistics(){
		
	}
	
	public Statistics(int numberListings, int numberSold, double averagePrice, double averagePostage, int numberAuctions, int numberPictures, double highestPrice, double lowestPrice, String range) {
		this.numberListings = numberListings;
		this.numberSold = numberSold;
		this.averagePrice = averagePrice;
		this.averagePostage = averagePostage;
		this.numberAuctions = numberAuctions;
		this.numberPictures = numberPictures;
		this.highestPrice = highestPrice;
		this.lowestPrice = lowestPrice;
		this.range = range;
	}
	
	

	public int getNumberListings() {
		return numberListings;
	}

	public void setNumberListings(int numberListings) {
		this.numberListings = numberListings;
	}

	public int getNumberSold() {
		return numberSold;
	}

	public void setNumberSold(int numberSold) {
		this.numberSold = numberSold;
	}

	public double getAveragePrice() {
		return averagePrice;
	}

	public void setAveragePrice(double averagePrice) {
		this.averagePrice = averagePrice;
	}

	public double getAveragePostage() {
		return averagePostage;
	}

	public void setAveragePostage(double averagePostage) {
		this.averagePostage = averagePostage;
	}

	public int getNumberAuctions() {
		return numberAuctions;
	}

	public void setNumberAuctions(int numberAuctions) {
		this.numberAuctions = numberAuctions;
	}

	public int getNumberPictures() {
		return numberPictures;
	}

	public void setNumberPictures(int numberPictures) {
		this.numberPictures = numberPictures;
	}

	public double getHighestPrice() {
		return highestPrice;
	}

	public void setHighestPrice(double highestPrice) {
		this.highestPrice = highestPrice;
	}

	public double getLowestPrice() {
		return lowestPrice;
	}

	public void setLowestPrice(double lowestPrice) {
		this.lowestPrice = lowestPrice;
	}

	public String getRange() {
		return range;
	}

	public void setRange(String range) {
		this.range = range;
	}

}
