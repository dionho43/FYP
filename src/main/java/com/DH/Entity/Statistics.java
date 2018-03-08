package com.DH.Entity;

import java.util.ArrayList;

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
	private ArrayList<String> conditions;
	private ArrayList<Double> prices;

	public Statistics(){
		
	}
	
	public Statistics(int numberListings, int numberSold, double averagePrice, double averagePostage, int numberAuctions, int numberPictures, double highestPrice, double lowestPrice, String range, ArrayList<String> conditions,  ArrayList<Double> prices) {
		this.numberListings = numberListings;
		this.numberSold = numberSold;
		this.averagePrice = averagePrice;
		this.averagePostage = averagePostage;
		this.numberAuctions = numberAuctions;
		this.numberPictures = numberPictures;
		this.highestPrice = highestPrice;
		this.lowestPrice = lowestPrice;
		this.range = range;
		this.conditions = conditions;
		this.prices = prices;
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
	
	public ArrayList<String> getConditions()
	{
		return conditions;
	}
	
	public void setConditions(ArrayList<String> conditions)
	{
		this.conditions = conditions;
	}
	
	public ArrayList<Double> getPrices()
	{
		return prices;
	}
	
	public void setPrices(ArrayList<Double> prices)
	{
		this.prices = prices;
	}

}
